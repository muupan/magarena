package magic.ui;

import magic.data.CardDefinitions;
import magic.model.MagicCardDefinition;
import magic.model.MagicColor;
import magic.model.MagicRarity;
import magic.model.MagicSubType;
import magic.model.MagicType;
import magic.ui.theme.ThemeFactory;
import magic.ui.widget.ButtonControlledPopup;
import magic.ui.widget.FontsAndBorders;
import magic.ui.widget.TexturedPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ExplorerFilterPanel extends TexturedPanel implements ActionListener, DocumentListener {

    private static final long serialVersionUID = 1L;

    private static final String[] COST_VALUES = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"};
    private static final String[] FILTER_CHOICES = {"Match any selected", "Match all selected", "Exclude selected"};
    private static final int SEARCH_FIELD_WIDTH = 12;
    private static final Color TEXT_COLOR = ThemeFactory.getInstance().getCurrentTheme().getTextColor();
    private static final Dimension POPUP_CHECKBOXES_SIZE = new Dimension(200, 150);
    private static final Dimension BUTTON_HOLDER_PANEL_SIZE = new Dimension(100, 36);
    private static final Dimension SEARCH_HOLDER_PANEL_SIZE = new Dimension(150, 72);

    private final MigLayout layout = new MigLayout();
    private final ExplorerPanel explorerPanel;
    private ButtonControlledPopup typePopup;
    private JCheckBox[] typeCheckBoxes;
    private JRadioButton[] typeFilterChoices;
    private ButtonControlledPopup colorPopup;
    private JCheckBox[] colorCheckBoxes;
    private JRadioButton[] colorFilterChoices;
    private ButtonControlledPopup costPopup;
    private JCheckBox[] costCheckBoxes;
    private JRadioButton[] costFilterChoices;
    private ButtonControlledPopup subtypePopup;
    private JCheckBox[] subtypeCheckBoxes;
    private JRadioButton[] subtypeFilterChoices;
    private ButtonControlledPopup rarityPopup;
    private JCheckBox[] rarityCheckBoxes;
    private JRadioButton[] rarityFilterChoices;
    private JTextField nameTextField;
    private JButton resetButton;
    private boolean disableUpdate; // so when we change several filters, it doesn't update until the end

    public ExplorerFilterPanel(final ExplorerPanel explorerPanel) {

        this.explorerPanel=explorerPanel;

        disableUpdate = false;

        setBackground(FontsAndBorders.TRANSLUCENT_WHITE_STRONG);

        layout.setLayoutConstraints("flowy, wrap 2, gap 4");
        setLayout(layout);

        addCardTypeFilter();
        addCardSubtypeFilter();
        addCardColorFilter();
        addManaCostFilter();
        addCardRarityFilter();
        addResetButton();
        addSearchTextFilter();

    }

    private ButtonControlledPopup addFilterPopupPanel(final String title) {

        final JButton selectButton = new JButton(title);
        selectButton.setFont(FontsAndBorders.FONT1);
        selectButton.setPreferredSize(BUTTON_HOLDER_PANEL_SIZE);
        add(selectButton);

        final ButtonControlledPopup pop = new ButtonControlledPopup(selectButton, title, title);
        pop.setLayout(new BoxLayout(pop, BoxLayout.Y_AXIS));
        selectButton.addActionListener(new PopupCloser(pop));
        return pop;
    }

    private class PopupCloser implements ActionListener {
        private final ButtonControlledPopup p;

        public PopupCloser(final ButtonControlledPopup p) {
            this.p = p;
        }

        @Override
        public void actionPerformed(final ActionEvent event) {
            // close all other popups except for our own button's
            if (p != typePopup) {
                typePopup.hidePopup();
            }
            if (p != colorPopup) {
                colorPopup.hidePopup();
            }
            if (p != costPopup) {
                costPopup.hidePopup();
            }
            if (p != subtypePopup) {
                subtypePopup.hidePopup();
            }
            if (p != rarityPopup) {
                rarityPopup.hidePopup();
            }
        }
    }

    private void populateCheckboxPopup(final ButtonControlledPopup popup, final Object[] checkboxValues, final JCheckBox[] newCheckboxes, final JRadioButton[] newFilterButtons, final boolean hideAND) {
        final JPanel checkboxesPanel = new JPanel(new GridLayout(newCheckboxes.length, 1));
        checkboxesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        checkboxesPanel.setOpaque(false);
        for (int i=0;i<checkboxValues.length;i++) {
            newCheckboxes[i]=new JCheckBox(checkboxValues[i].toString().replace('_', ' '));
            newCheckboxes[i].addActionListener(this);
            newCheckboxes[i].setOpaque(false);
            newCheckboxes[i].setForeground(TEXT_COLOR);
            newCheckboxes[i].setFocusPainted(false);
            newCheckboxes[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            checkboxesPanel.add(newCheckboxes[i]);
        }

        final JScrollPane scrollPane = new JScrollPane(checkboxesPanel);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setBorder(FontsAndBorders.DOWN_BORDER);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setPreferredSize(POPUP_CHECKBOXES_SIZE);
        popup.add(scrollPane);

        final ButtonGroup bg = new ButtonGroup();
        for (int i = 0; i < FILTER_CHOICES.length; i++) {
            newFilterButtons[i] = new JRadioButton(FILTER_CHOICES[i]);
            newFilterButtons[i].addActionListener(this);
            newFilterButtons[i].setOpaque(false);
            newFilterButtons[i].setForeground(TEXT_COLOR);
            newFilterButtons[i].setFocusPainted(false);
            newFilterButtons[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            if (i == 0) {
                newFilterButtons[i].setSelected(true);
            } else if (i == 1) {
                newFilterButtons[i].setVisible(!hideAND);
            }
            bg.add(newFilterButtons[i]);
            popup.add(newFilterButtons[i]);
        }
    }

    private boolean filter(final MagicCardDefinition cardDefinition) {

        if (cardDefinition.isToken()) {
            return false;
        }

        // search text in name, abilities, type, text, etc.
        final String filterString = nameTextField.getText();
        if (filterString.length() > 0) {
            final String[] filters = filterString.split(" ");
            for (int i=0; i<filters.length; i++) {
                if (!cardDefinition.hasText(filters[i])) {
                    return false;
                }
            }
        }

        // type
        if (!filterCheckboxes(cardDefinition, typeCheckBoxes, typeFilterChoices,
            new CardChecker() {
                public boolean checkCard(final MagicCardDefinition card, final int i) {
                    return card.hasType(MagicType.FILTER_TYPES.toArray(new MagicType[0])[i]);
                }
            })) {
            return false;
        }

        // color
        if (!filterCheckboxes(cardDefinition, colorCheckBoxes, colorFilterChoices,
            new CardChecker() {
                public boolean checkCard(final MagicCardDefinition card, final int i) {
                    return card.hasColor(MagicColor.values()[i]);
                }
            })) {
            return false;
        }

        // cost
        if (!filterCheckboxes(cardDefinition, costCheckBoxes, costFilterChoices,
            new CardChecker() {
                public boolean checkCard(final MagicCardDefinition card, final int i) {
                    return card.hasConvertedCost(Integer.parseInt(COST_VALUES[i]));
                }
            })) {
            return false;
        }

        // subtype
        if (!filterCheckboxes(cardDefinition, subtypeCheckBoxes, subtypeFilterChoices,
            new CardChecker() {
                public boolean checkCard(final MagicCardDefinition card, final int i) {
                    return card.hasSubType(MagicSubType.values()[i]);
                }
            })) {
            return false;
        }

        // rarity
        if (!filterCheckboxes(cardDefinition, rarityCheckBoxes, rarityFilterChoices,
            new CardChecker() {
                public boolean checkCard(final MagicCardDefinition card, final int i) {
                    return card.isRarity(MagicRarity.values()[i]);
                }
            })) {
            return false;
        }

        return true;
    }

    private boolean filterCheckboxes(final MagicCardDefinition cardDefinition, final JCheckBox[] checkboxes, final JRadioButton[] filterButtons, final CardChecker func) {
        boolean somethingSelected = false;
        boolean resultOR = false;
        boolean resultAND = true;

        for (int i=0; i < checkboxes.length; i++) {
            if (checkboxes[i].isSelected()) {
                somethingSelected = true;
                if (!func.checkCard(cardDefinition, i)) {
                    resultAND = false;
                } else {
                    resultOR = true;
                }
            }
        }
        if (filterButtons[2].isSelected()) {
            // exclude selected
            return !resultOR;
        }
        if (!somethingSelected) {
            // didn't choose to exclude and nothing selected, so don't filter
            return true;
        } else {
            // otherwise return OR or AND result
            return (filterButtons[0].isSelected() && resultOR) || (filterButtons[1].isSelected() && resultAND);
        }
    }

    private interface CardChecker {
        public boolean checkCard(MagicCardDefinition card, int i);
    }

    public List<MagicCardDefinition> getCardDefinitions() {

        final List<MagicCardDefinition> cardDefinitions=new ArrayList<MagicCardDefinition>();
        for (final MagicCardDefinition cardDefinition : CardDefinitions.getCards()) {

            if (filter(cardDefinition)) {
                cardDefinitions.add(cardDefinition);
            }
        }
        return cardDefinitions;
    }

    public void resetFilters() {
        disableUpdate = true; // ignore any events caused by resetting filters

        closePopups();

        unselectFilterSet(typeCheckBoxes, typeFilterChoices);
        unselectFilterSet(colorCheckBoxes, colorFilterChoices);
        unselectFilterSet(costCheckBoxes, costFilterChoices);
        unselectFilterSet(subtypeCheckBoxes, subtypeFilterChoices);
        unselectFilterSet(rarityCheckBoxes, rarityFilterChoices);

        nameTextField.setText("");

        disableUpdate = false;
    }

    private void unselectFilterSet(final JCheckBox[] boxes, final JRadioButton[] filterButtons) {
        // uncheck all checkboxes
        for (int i = 0; i < boxes.length; i++){
            boxes[i].setSelected(false);
        }

        // reset to first option
        filterButtons[0].setSelected(true);
    }

    public void closePopups() {
        typePopup.hidePopup();
        colorPopup.hidePopup();
        costPopup.hidePopup();
        subtypePopup.hidePopup();
        rarityPopup.hidePopup();
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        final Object source=event.getSource();

        if (source == resetButton) {
            resetFilters();
        }

        if (!disableUpdate) {
            explorerPanel.updateCardPool();
        }
    }

    @Override
    public void insertUpdate(final DocumentEvent arg0) {
        if (!disableUpdate) {
            explorerPanel.updateCardPool();
        }
    }

    @Override
    public void removeUpdate(final DocumentEvent arg0) {
        if (!disableUpdate) {
            explorerPanel.updateCardPool();
        }
    }

    @Override
    public void changedUpdate(final DocumentEvent arg0) {
    }

    private void addCardTypeFilter() {
        typePopup = addFilterPopupPanel("Card Type");
        typeCheckBoxes = new JCheckBox[MagicType.FILTER_TYPES.size()];
        typeFilterChoices = new JRadioButton[FILTER_CHOICES.length];
        populateCheckboxPopup(typePopup, MagicType.FILTER_TYPES.toArray(), typeCheckBoxes, typeFilterChoices, false);
    }

    private void addCardColorFilter() {
        colorPopup = addFilterPopupPanel("Color");
        colorCheckBoxes=new JCheckBox[MagicColor.NR_COLORS];
        final JPanel colorsPanel=new JPanel();
        colorsPanel.setLayout(new BoxLayout(colorsPanel, BoxLayout.X_AXIS));
        colorsPanel.setBorder(FontsAndBorders.DOWN_BORDER);
        colorsPanel.setOpaque(false);
        colorPopup.setPopupSize(280, 90);
        for (int i = 0; i < MagicColor.NR_COLORS; i++) {
            final MagicColor color = MagicColor.values()[i];
            final JPanel colorPanel=new JPanel();
            colorPanel.setOpaque(false);
            colorCheckBoxes[i]=new JCheckBox("",false);
            colorCheckBoxes[i].addActionListener(this);
            colorCheckBoxes[i].setOpaque(false);
            colorCheckBoxes[i].setFocusPainted(false);
            colorCheckBoxes[i].setAlignmentY(Component.CENTER_ALIGNMENT);
            colorCheckBoxes[i].setActionCommand(Character.toString(color.getSymbol()));
            colorPanel.add(colorCheckBoxes[i]);
            colorPanel.add(new JLabel(color.getManaType().getIcon(true)));
            colorsPanel.add(colorPanel);
        }
        colorsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        colorPopup.add(colorsPanel);

        final ButtonGroup colorFilterBg = new ButtonGroup();
        colorFilterChoices = new JRadioButton[FILTER_CHOICES.length];
        for (int i = 0; i < FILTER_CHOICES.length; i++) {
            colorFilterChoices[i] = new JRadioButton(FILTER_CHOICES[i]);
            colorFilterChoices[i].addActionListener(this);
            colorFilterChoices[i].setOpaque(false);
            colorFilterChoices[i].setForeground(TEXT_COLOR);
            colorFilterChoices[i].setFocusPainted(false);
            colorFilterChoices[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            if (i == 0) {
                colorFilterChoices[i].setSelected(true);
            }
            colorFilterBg.add(colorFilterChoices[i]);
            colorPopup.add(colorFilterChoices[i]);
        }
    }

    private void addManaCostFilter() {
        costPopup = addFilterPopupPanel("Mana Cost");
        costCheckBoxes = new JCheckBox[COST_VALUES.length];
        costFilterChoices = new JRadioButton[FILTER_CHOICES.length];
        populateCheckboxPopup(costPopup, COST_VALUES, costCheckBoxes, costFilterChoices, true);
    }

    private void addCardSubtypeFilter() {
        subtypePopup = addFilterPopupPanel("Subtype");
        subtypeCheckBoxes = new JCheckBox[MagicSubType.values().length];
        subtypeFilterChoices = new JRadioButton[FILTER_CHOICES.length];
        populateCheckboxPopup(subtypePopup, MagicSubType.values(), subtypeCheckBoxes, subtypeFilterChoices, false);
    }

    private void addCardRarityFilter() {
        rarityPopup = addFilterPopupPanel("Rarity");
        rarityCheckBoxes = new JCheckBox[MagicRarity.values().length];
        rarityFilterChoices = new JRadioButton[FILTER_CHOICES.length];
        populateCheckboxPopup(rarityPopup, MagicRarity.values(), rarityCheckBoxes, rarityFilterChoices, true);
    }

    private void addSearchTextFilter() {
        final TitledBorder textFilterBorder = BorderFactory.createTitledBorder("Search Text");
        textFilterBorder.setTitleColor(TEXT_COLOR);
        final JPanel textFilterPanel = new JPanel(new MigLayout("insets 0"));
        textFilterPanel.setOpaque(false);
        textFilterPanel.setBorder(textFilterBorder);
        textFilterPanel.setPreferredSize(SEARCH_HOLDER_PANEL_SIZE);
        nameTextField = new JTextField(SEARCH_FIELD_WIDTH);
        nameTextField.addActionListener(this);
        nameTextField.getDocument().addDocumentListener(this);
        textFilterPanel.add(nameTextField);
        add(textFilterPanel, "spany 2");
    }

    private void addResetButton() {
        resetButton = new JButton("Reset");
        resetButton.setFont(new Font("dialog", Font.BOLD + Font.ITALIC, 12));
        resetButton.setForeground(Color.BLUE);
        resetButton.addActionListener(this);
        resetButton.setPreferredSize(BUTTON_HOLDER_PANEL_SIZE);
        add(resetButton);
    }

}
