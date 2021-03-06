package magic.ui.dialog;

import magic.data.GeneralConfig;
import magic.data.IconImages;
import magic.ui.MagicFrame;
import magic.ui.theme.ThemeFactory;
import magic.ui.widget.SliderPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

public class PreferencesDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;
    private final static GeneralConfig config = GeneralConfig.getInstance();

    private final ActionListener actionListener = new ActionListener() {
        public void actionPerformed(final ActionEvent actionEvent) {
            dispose();
        }
    };

    private final MagicFrame frame;
    private JComboBox<String> themeComboBox;
    private JComboBox<String> highlightComboBox;
    private JCheckBox confirmExitCheckBox;
    private JCheckBox soundCheckBox;
    private JCheckBox touchscreenCheckBox;
    private JCheckBox highQualityCheckBox;
    private JCheckBox skipSingleCheckBox;
    private JCheckBox alwaysPassCheckBox;
    private JCheckBox smartTargetCheckBox;
    private JCheckBox mouseWheelPopupCheckBox;
    private SliderPanel popupDelaySlider;
    private SliderPanel messageDelaySlider;
    private JButton okButton;
    private JButton cancelButton;
    private JCheckBox previewCardOnSelectCheckBox;
    private JCheckBox gameLogCheckBox;
    private JCheckBox mulliganScreenCheckbox;
    private boolean isCustomBackground;

    public PreferencesDialog(final MagicFrame frame) {

        super(frame,true);
        this.setTitle("Preferences");
        this.setSize(400,500);
        this.setLocationRelativeTo(frame);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.frame=frame;

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(getTabbedSettingsPane(), BorderLayout.CENTER);
        getContentPane().add(getActionButtonsPanel(), BorderLayout.SOUTH);

        setEscapeKeyAsCancelAction();

        setVisible(true);
    }

    private void setEscapeKeyAsCancelAction() {
        final KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(actionListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private JTabbedPane getTabbedSettingsPane() {
        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("General", getGeneralSettingsPanel());
        tabbedPane.addTab("Gameplay", getDuelSettingsPanel());
        tabbedPane.addTab("Look & Feel", getThemeSettingsPanel());
        tabbedPane.addTab("Deck Editor", getDeckEditorSettingsPanel());
        tabbedPane.addTab("Experimental", getExperimentalSettingsPanel());
        return tabbedPane;
    }

    private Component getExperimentalSettingsPanel() {

        final JPanel panel = new JPanel();
        panel.setLayout(null);

        final int Y=10;
        final int X3=25;
        final int H3=20;
        final int W3=350;

        mulliganScreenCheckbox = new JCheckBox("Use Mulligan screen", config.showMulliganScreen());
        mulliganScreenCheckbox.setBounds(X3,Y,W3,H3);
        mulliganScreenCheckbox.setFocusable(false);
        panel.add(mulliganScreenCheckbox);

        return panel;
    }

    private Component getDeckEditorSettingsPanel() {

        final JPanel panel = new JPanel();
        panel.setLayout(null);

        final int Y=10;
        final int X3=25;
        final int H3=20;
        final int W3=350;

        previewCardOnSelectCheckBox =
                new JCheckBox("Preview card on select (instead of mouse-over)",
                config.isPreviewCardOnSelect());
        previewCardOnSelectCheckBox.setBounds(X3,Y,W3,H3);
        previewCardOnSelectCheckBox.setFocusable(false);
        panel.add(previewCardOnSelectCheckBox);

        return panel;
    }

    private JPanel getDuelSettingsPanel() {

        final JPanel mainPanel=new JPanel();
        mainPanel.setLayout(null);

        int Y=10;
        final int X3=25;
        final int H3=20;
        final int W3=350;

        gameLogCheckBox = new JCheckBox("Show game log messages.", config.isLogMessagesVisible());
        gameLogCheckBox.setToolTipText("<html>Clear this option if you would prefer the game log messages to be hidden by default.<br>You can still toggle visibility during a game by clicking on the log titlebar.</html>");
        gameLogCheckBox.setBounds(X3,Y,W3,H3);
        gameLogCheckBox.setFocusable(false);
        mainPanel.add(gameLogCheckBox);

        Y += 30;
        soundCheckBox = new JCheckBox("Enable sound effects",config.isSound());
        soundCheckBox.setBounds(X3,Y,W3,H3);
        soundCheckBox.setFocusable(false);
        mainPanel.add(soundCheckBox);

        Y += 30;
        touchscreenCheckBox = new JCheckBox("Double-click to cast or activate ability (for touchscreen)",config.isTouchscreen());
        touchscreenCheckBox.setBounds(X3,Y,W3,H3);
        touchscreenCheckBox.setFocusable(false);
        mainPanel.add(touchscreenCheckBox);

        Y += 30;
        skipSingleCheckBox = new JCheckBox("Skip single option choices when appropriate",
                config.getSkipSingle());
        skipSingleCheckBox.setBounds(X3,Y,W3,H3);
        skipSingleCheckBox.setFocusable(false);
        mainPanel.add(skipSingleCheckBox);

        Y += 30;
        alwaysPassCheckBox = new JCheckBox("Always pass during draw and begin of combat step",
                config.getAlwaysPass());
        alwaysPassCheckBox.setBounds(X3,Y,W3,H3);
        alwaysPassCheckBox.setFocusable(false);
        mainPanel.add(alwaysPassCheckBox);

        Y += 30;
        smartTargetCheckBox=new JCheckBox("Remove unusual target choices",
                config.getSmartTarget());
        smartTargetCheckBox.setBounds(X3,Y,W3,H3);
        smartTargetCheckBox.setFocusable(false);
        mainPanel.add(smartTargetCheckBox);

        Y += 30;
        mouseWheelPopupCheckBox = new JCheckBox("Popup card image using mouse wheel (instead of delay)",
                config.isMouseWheelPopup());
        mouseWheelPopupCheckBox.setBounds(X3,Y,W3,H3);
        mouseWheelPopupCheckBox.setFocusable(false);
        mainPanel.add(mouseWheelPopupCheckBox);

        Y += 30;
        popupDelaySlider=new SliderPanel("Popup",
                IconImages.DELAY,
                0,
                500,
                50,
                config.getPopupDelay());
        popupDelaySlider.setBounds(50,Y,280,50);
        mainPanel.add(popupDelaySlider);

        Y += 40;
        messageDelaySlider = new SliderPanel("Message",
                IconImages.DELAY,
                0,
                3000,
                500,
                config.getMessageDelay());
        messageDelaySlider.setBounds(50,Y,280,50);
        mainPanel.add(messageDelaySlider);

        return mainPanel;
    }

    private JPanel getThemeSettingsPanel() {

        final JPanel panel = new JPanel();
        panel.setLayout(null);

        int Y=10;
        final int X=28;
        final int W=70;
        final int H=25;
        final int X2=100;
        final int W2=255;

        final JLabel themeLabel=new JLabel("Theme");
        themeLabel.setBounds(X,Y,W,H);
        themeLabel.setIcon(IconImages.PICTURE);
        panel.add(themeLabel);
        themeComboBox=new JComboBox<String>(ThemeFactory.getInstance().getThemeNames());
        themeComboBox.setFocusable(false);
        themeComboBox.setBounds(X2,Y,W2,H);
        themeComboBox.setSelectedItem(config.getTheme());
        themeComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                isCustomBackground = false;
            }
        });
        panel.add(themeComboBox);

        Y += 35;
        final JLabel highlightLabel = new JLabel("Highlight");
        highlightLabel.setBounds(X,Y,W,H);
        highlightLabel.setIcon(IconImages.PICTURE);
        panel.add(highlightLabel);
        final String[] Highlightchoices = { "none", "overlay", "border", "theme" };
        highlightComboBox = new JComboBox<String>(Highlightchoices);
        highlightComboBox.setFocusable(false);
        highlightComboBox.setBounds(X2,Y,W2,H);
        highlightComboBox.setSelectedItem(config.getHighlight());
        panel.add(highlightComboBox);

        isCustomBackground = config.isCustomBackground();

        return panel;

    }

    private JPanel getGeneralSettingsPanel() {

        final JPanel mainPanel=new JPanel();
        mainPanel.setLayout(null);

        int Y=10;
        final int X3=25;
        final int H3=20;
        final int W3=350;

        confirmExitCheckBox = new JCheckBox("Show confirmation dialog on exit",
                config.isConfirmExit());
        confirmExitCheckBox.setBounds(X3,Y,W3,H3);
        confirmExitCheckBox.setFocusable(false);
        mainPanel.add(confirmExitCheckBox);

        Y += 30;
        highQualityCheckBox = new JCheckBox("Show card images in original size",
                config.isHighQuality());
        highQualityCheckBox.setBounds(X3,Y,W3,H3);
        highQualityCheckBox.setFocusable(false);
        mainPanel.add(highQualityCheckBox);

        return mainPanel;
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        final Object source=event.getSource();
        if (source==okButton) {
            final GeneralConfig config=GeneralConfig.getInstance();
            config.setTheme(themeComboBox.getItemAt(themeComboBox.getSelectedIndex()));
            config.setHighlight(highlightComboBox.getItemAt(highlightComboBox.getSelectedIndex()));
            config.setConfirmExit(confirmExitCheckBox.isSelected());
            config.setSound(soundCheckBox.isSelected());
            config.setTouchscreen(touchscreenCheckBox.isSelected());
            config.setHighQuality(highQualityCheckBox.isSelected());
            config.setSkipSingle(skipSingleCheckBox.isSelected());
            config.setAlwaysPass(alwaysPassCheckBox.isSelected());
            config.setSmartTarget(smartTargetCheckBox.isSelected());
            config.setMouseWheelPopup(mouseWheelPopupCheckBox.isSelected());
            config.setPopupDelay(popupDelaySlider.getValue());
            config.setMessageDelay(messageDelaySlider.getValue());
            config.setPreviewCardOnSelect(previewCardOnSelectCheckBox.isSelected());
            config.setLogMessagesVisible(gameLogCheckBox.isSelected());
            config.setMulliganScreenActive(mulliganScreenCheckbox.isSelected());
            config.setCustomBackground(isCustomBackground);
            config.save();
            ThemeFactory.getInstance().setCurrentTheme(config.getTheme());
            frame.repaint();
            dispose();
        } else if (source==cancelButton) {
            dispose();
        }
    }

    private JPanel getActionButtonsPanel() {
        final JPanel buttonPanel = new JPanel(new MigLayout("insets 5, gapx 5, flowx"));
        // Cancel button
        cancelButton=new JButton("Cancel");
        cancelButton.setFocusable(false);
        cancelButton.setIcon(IconImages.CANCEL);
        cancelButton.addActionListener(this);
        buttonPanel.add(cancelButton, "w 100!, alignx right, pushx");
        // Save button
        okButton=new JButton("Save");
        okButton.setFocusable(false);
        okButton.setIcon(IconImages.OK);
        okButton.addActionListener(this);
        buttonPanel.add(okButton, "w 100!");
        return buttonPanel;
    }

}
