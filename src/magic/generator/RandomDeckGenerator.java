package magic.generator;

import magic.data.CardDefinitions;
import magic.model.MagicCardDefinition;
import magic.model.MagicCondensedDeck;
import magic.model.MagicCubeDefinition;
import magic.model.MagicDeck;
import magic.model.MagicDeckProfile;
import magic.model.MagicRandom;

import java.util.ArrayList;
import java.util.List;

public class RandomDeckGenerator {

    private final List<MagicCardDefinition> spellCards = new ArrayList<MagicCardDefinition>();
    private final List<MagicCardDefinition> landCards = new ArrayList<MagicCardDefinition>();

    private MagicCubeDefinition cubeDefinition;

    public RandomDeckGenerator(final MagicCubeDefinition cubeDefinition) {
        this.cubeDefinition = cubeDefinition;
    }

    public void setCubeDefinition(final MagicCubeDefinition cube) {
        cubeDefinition = cube;
    }

    private void genSpells() {
        if (cubeDefinition == null) {
            return;
        }

        spellCards.clear();
        for (int rarity =  getMinRarity(); rarity <= getMaxRarity(); rarity++) {
            for (final MagicCardDefinition card : CardDefinitions.getSpellCards()) {
                if (card.getRarity() >= getMinRarity() && card.getRarity() <= rarity && cubeDefinition.containsCard(card)) {
                    if (acceptPossibleSpellCard(card)) {
                        spellCards.add(card);
                    }
                }
            }
        }
    }

    public int getMinRarity() {
        return 1;
    }

    public int getMaxRarity() {
        return 4;
    }

    public boolean acceptPossibleSpellCard(final MagicCardDefinition card) {
        return true;
    }

    private void genLands() {
        if (cubeDefinition == null) {
            return;
        }

        landCards.clear();
        for (final MagicCardDefinition card : CardDefinitions.getLandCards()) {
            if (cubeDefinition.containsCard(card)) {
                for (int count = 4; count > 0; count--) {
                    if (acceptPossibleLandCard(card)) {
                        landCards.add(card);
                    }
                }
            }
        }
    }

    public boolean acceptPossibleLandCard(final MagicCardDefinition card) {
        return true;
    }

    public void generateDeck(final int size, final MagicDeckProfile profile, final MagicDeck deck) {
        setColors(profile);

        final MagicCondensedDeck condensedDeck = new MagicCondensedDeck();

        genSpells();
        genLands();

        final int spells = (size*3)/5;
        final int lands = profile.getNrOfNonBasicLands(size-spells);

        final int maxCreatures = (spells*2)/3;
        final int maxNonlandNoncreature = spells - maxCreatures;
        final int maxColorless = spells/6;
        final int maxHigh = spells/6;
        final int maxOther = (spells-maxHigh)/2;
        final int[] maxCost = {maxOther,maxOther+1,maxHigh};

        int countCreatures = 0;
        int countColorless = 0;
        int countNonlandNoncreature = 0;
        final int[] countCost = new int[3];

        addRequiredSpells(condensedDeck);

        // count required cards added
        for (final MagicCardDefinition card : condensedDeck.toMagicDeck()) {
            if (card.isCreature()) {
                countCreatures++;
            } else if (!card.isLand()) {
                countNonlandNoncreature++;
            }

            if (card.isColorless()) {
                countColorless++;
            }

            countCost[card.getCostBucket()]++;
        }

        // Add spells to deck.
        while (condensedDeck.getNumCards() < spells && spellCards.size() > 0) {
            final int index=MagicRandom.nextRNGInt(spellCards.size());
            final MagicCardDefinition cardDefinition=spellCards.get(index);
            spellCards.remove(index);

            if (cardDefinition.isPlayable(profile)) {
                final boolean creature = cardDefinition.isCreature();

                if (creature && countCreatures >= maxCreatures) {
                    continue;
                }

                if (!creature && countNonlandNoncreature >= maxNonlandNoncreature) {
                    continue;
                }

                final boolean colorless = cardDefinition.isColorless();
                if (!ignoreMaxColorless() && colorless && countColorless >= maxColorless) {
                    continue;
                }

                final int bucket = cardDefinition.getCostBucket();
                if (!ignoreMaxCost() && countCost[bucket] >= maxCost[bucket]) {
                    continue;
                }

                if (condensedDeck.addCard(cardDefinition, false)) {
                    countCost[bucket]++;
                    if (creature) {
                        countCreatures++;
                    } else if (!cardDefinition.isLand()) {
                        countNonlandNoncreature++;
                    }
                    if (colorless) {
                        countColorless++;
                    }
                }
            }

            if (spellCards.size() == 0) {
                genSpells();
            }
        }

        // Add nonbasic lands to deck.
        addRequiredLands(condensedDeck);

        while (condensedDeck.getNumCards() < spells+lands && landCards.size() > 0) {
            final int index=MagicRandom.nextRNGInt(landCards.size());
            final MagicCardDefinition cardDefinition=landCards.get(index);
            landCards.remove(index);

            if (cardDefinition.isPlayable(profile)) {
                condensedDeck.addCard(cardDefinition, false);
            }
        }

        deck.setContent(condensedDeck.toMagicDeck());
    }

    protected void addRequiredCards(final MagicCondensedDeck deck, final String[] cards) {
        for (final String name : cards) {
            final MagicCardDefinition cardDef = CardDefinitions.getCard(name);
            if (cardDef.isValid()) {
                deck.addCard(cardDef, false);
            } else {
                System.err.println("Cannot find " + name);
            }
        }
    }

    public void addRequiredSpells(final MagicCondensedDeck deck) { }

    public void addRequiredLands(final MagicCondensedDeck deck) { }

    public void setColors(final MagicDeckProfile profile) {    }

    public boolean ignoreMaxColorless() {
        return false;
    }

    public boolean ignoreMaxCost() {
        return false;
    }
}
