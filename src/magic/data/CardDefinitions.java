package magic.data;

import magic.MagicMain;
import magic.model.MagicCardDefinition;
import magic.model.MagicChangeCardDefinition;
import magic.model.MagicColor;
import magic.model.event.MagicCardActivation;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import groovy.lang.GroovyShell;

/**
 * Load card definitions from cards.txt
 */
public class CardDefinitions {

    public static final String CARD_TEXT_FOLDER = "texts";
    public static final String CARD_IMAGE_FOLDER = "cards";
    public static final String TOKEN_IMAGE_FOLDER = "tokens";
    public static final String CARD_IMAGE_EXT = CardImagesProvider.IMAGE_EXTENSION;
    public static final String CARD_TEXT_EXT = ".txt";

    private static final List<MagicCardDefinition> cards = new ArrayList<MagicCardDefinition>();
    private static final List<MagicCardDefinition> landCards = new ArrayList<MagicCardDefinition>();
    private static final List<MagicCardDefinition> spellCards = new ArrayList<MagicCardDefinition>();
    private static final Map<String,MagicCardDefinition> cardsMap = new HashMap<String, MagicCardDefinition>();
    private static final File cardDir = new File(MagicMain.getScriptsPath());



    // groovy shell for evaluating groovy card scripts with autmatic imports
    private static final GroovyShell shell = new GroovyShell(
        new CompilerConfiguration().addCompilationCustomizers(
            new ImportCustomizer().addStarImports(
                "java.util",
                "magic.data",
                "magic.model",
                "magic.model.action",
                "magic.model.choice",
                "magic.model.condition",
                "magic.model.event",
                "magic.model.mstatic",
                "magic.model.stack",
                "magic.model.target",
                "magic.model.trigger",
                "magic.card"
            ),
            new ASTTransformationCustomizer(groovy.transform.CompileStatic.class)
        )
    );

    private static void setProperty(final MagicCardDefinition card,final String property,final String value) {
        try {
            CardProperty.valueOf(property.toUpperCase()).setProperty(card, value);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Unsupported card property: " + property, e);
        }
    }

    private static void filterCards() {
        for (final MagicCardDefinition card : cards) {
            if (!card.isLand() && !card.isToken()) {
                spellCards.add(card);
            } else if (!card.isBasic() && !card.isToken()) {
                landCards.add(card);
            }
        }
    }

    private static void addDefinition(final MagicCardDefinition cardDefinition) {
        assert cardDefinition != null : "CardDefinitions.addDefinition passed null";
        assert cardDefinition.getIndex() == -1 : "cardDefinition has been assigned index";

        cardDefinition.setIndex(cards.size());
        cards.add(cardDefinition);
        cardsMap.put(cardDefinition.getFullName(),cardDefinition);

        //add to tokens or all (vintage) cube
        if (cardDefinition.isToken()) {
            TokenCardDefinitions.add(cardDefinition);
        } else {
            cardDefinition.add(new MagicCardActivation(cardDefinition));
            CubeDefinitions.getCubeDefinition("all").add(cardDefinition.getName());
        }
    }

    private static MagicCardDefinition prop2carddef(final Properties content) {
        final MagicCardDefinition cardDefinition=new MagicCardDefinition();

        //run through the list of properties
        for (final String key : content.stringPropertyNames()) {
            setProperty(cardDefinition, key, content.getProperty(key));
        }

        return cardDefinition;
    }

    //link to groovy script that returns array of MagicChangeCardDefinition objects
    static void addCardSpecificGroovyCode(final MagicCardDefinition cardDefinition, final String cardName) {
        try {
            @SuppressWarnings("unchecked")
            final List<MagicChangeCardDefinition> defs = (List<MagicChangeCardDefinition>)shell.evaluate(
                new File(cardDir, getCanonicalName(cardName) + ".groovy")
            );
            for (MagicChangeCardDefinition ccd : defs) {
                ccd.change(cardDefinition);
            }
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String getCanonicalName(String fullName) {
        return fullName.replaceAll("[^A-Za-z0-9]", "_");
    }

    private static void loadCardDefinition(final File file) {
        try {
            final MagicCardDefinition cdef = prop2carddef(FileIO.toProp(file));
            cdef.validate();
            addDefinition(cdef);
        } catch (final Throwable cause) {
            throw new RuntimeException("Error loading " + file, cause);
        }
    }

    public static void loadCardDefinitions() {
        //load all files in card directory
        MagicMain.setSplashStatusMessage("Initializing cards database...");
        final File[] files = cardDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".txt");
            }
        });

        //sort files to ensure consistent order
        Arrays.sort(files);

        //load the card definitions
        final int totalTxtCards = files.length;
        final int totalNonTokenCards = getNonTokenCardsCount(files);
        final int totalTokenCards = totalTxtCards - totalNonTokenCards;
        MagicMain.setSplashStatusMessage("Loading " + totalNonTokenCards + " cards, " + totalTokenCards + " tokens...");
        for (final File file : files) {
            loadCardDefinition(file);
        }

        filterCards();
        printStatistics();

        addDefinition(MagicCardDefinition.UNKNOWN);

        System.err.println(getNumberOfCards()+ " card definitions");
    }

    /**
     * Returns the number of non-token cards.
     * <p>
     * Assumes that token card contains "token" in the file name.
     */
    private static int getNonTokenCardsCount(final File[] files) {
        int count = 0;
        final Iterator<File> filesIterator = Arrays.asList(files).iterator();
        while (filesIterator.hasNext()) {
            final File f = filesIterator.next();
            if (!f.getName().toLowerCase().contains("token")) {
                count++;
            }
        }
        return count;
    }

    public static void loadCardAbilities() {
        final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (final MagicCardDefinition cdef : getCards()) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        cdef.loadAbilities();
                    } catch (Throwable cause) {
                        throw new RuntimeException("Unable to load " + cdef, cause);
                    }
                }
            });
        }
        executor.shutdown();
        try {
            executor.awaitTermination(100, TimeUnit.SECONDS);
        } catch (final InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static int getNumberOfCards() {
        return cards.size();
    }

    public static MagicCardDefinition getCard(final int cindex) {
        return cards.get(cindex);
    }

    public static MagicCardDefinition getCard(final String name) {
        final MagicCardDefinition cardDefinition=cardsMap.get(name);
        if (cardDefinition == null) {
            throw new RuntimeException("Unknown card: " + name);
        }
        return cardDefinition;
    }

    public static void loadCardTexts() {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                for (final MagicCardDefinition card : getCards()) {
                    if (card != MagicCardDefinition.UNKNOWN && card.getText().length() == 0) {
                        loadCardText(card);
                    }
                }
            }
        });
        executor.shutdown();
    }

    private static void loadCardText(final MagicCardDefinition card) {
        // try to load text from file
        final StringBuilder buffer = new StringBuilder();
        buffer.append(MagicMain.getGamePath());
        buffer.append(File.separator);
        buffer.append(CARD_TEXT_FOLDER);
        buffer.append(File.separator);
        buffer.append(card.getCardTextName());
        buffer.append(CARD_TEXT_EXT);

        try {
            final String text = FileIO.toStr(new File(buffer.toString()));
            if (text != null) {
                card.setText(text);
            }
        } catch (IOException e) {
            // text not downloaded or missing
        }
    }

    public static MagicCardDefinition getBasicLand(final MagicColor color) {
        if (color == MagicColor.Black) {
            return getCard("Swamp");
        } else if (color == MagicColor.Blue) {
            return getCard("Island");
        } else if (color == MagicColor.Green) {
            return getCard("Forest");
        } else if (color == MagicColor.Red) {
            return getCard("Mountain");
        } else if (color == MagicColor.White) {
            return getCard("Plains");
        }
        throw new RuntimeException("No matching basic land for MagicColor " + color);
    }

    public static List<MagicCardDefinition> getCards() {
        return cards;
    }

    public static List<MagicCardDefinition> getLandCards() {
        return landCards;
    }

    public static List<MagicCardDefinition> getSpellCards() {
        return spellCards;
    }

    private static void printStatistics() {
        final CardStatistics statistics=new CardStatistics(cards);
        statistics.printStatictics(System.err);
    }
}
