package magic.ui.widget;

import magic.MagicMain;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.Timer;
import java.util.TimerTask;

public class ButtonControlledPopup extends TexturedPanel implements ActionListener, WindowFocusListener {

    private static final long serialVersionUID = 54232L;

    public static final int STARTING_WIDTH = 200;
    public static final int STARTING_HEIGHT = 200;

    private final JDialog dialog;
    private final JButton invokePopupButton;
    private final String hidePopupButtonText;
    private final String showPopupButtonText;
    private final Timer timer;

    private boolean popupJustToggled;

    public ButtonControlledPopup(JButton toggleButton, final String hidePopupButtonText, final String showPopupButtonText) {
        this.invokePopupButton = toggleButton;
        this.hidePopupButtonText = hidePopupButtonText;
        this.showPopupButtonText = showPopupButtonText;
        this.dialog = new JDialog(MagicMain.rootFrame);
        this.timer = new Timer();

        setBorder(FontsAndBorders.UP_BORDER);

        dialog.setUndecorated(true);
        dialog.setSize(STARTING_WIDTH, STARTING_HEIGHT);

        invokePopupButton.addActionListener(this);
        dialog.addWindowFocusListener(this);
        dialog.add(this);
    }

    public void setPopupSize(final int width, final int height) {
        // jdialog will not resize by itself
        dialog.setSize(width, height);
    }

    public void showPopup() {
        // set location relative to button
        final Point location = invokePopupButton.getLocation();
        SwingUtilities.convertPointToScreen(location, invokePopupButton.getParent());
        location.translate(0, invokePopupButton.getHeight());
        dialog.setLocation(location);

        // showPopup the popup if not visible
        invokePopupButton.setText(hidePopupButtonText);
        dialog.setVisible(true);
        dialog.requestFocus();
    }

    public void hidePopup() {
        invokePopupButton.setText(showPopupButtonText);
        dialog.setVisible(false);
    }

    class ResponsePreventer extends TimerTask {
        public void run() {
            popupJustToggled = false;
        }
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        if (popupJustToggled) {
            // focus event just hid popup -> this event is probably from clicking on the hide button -> don't do anything
            return;
        }

        // set popup visibility
        if (!dialog.isVisible()) {
            showPopup();
        } else {
            // hide - taken care of by focusLost
        }
    }

    @Override
    public void windowLostFocus(final WindowEvent e) {
        popupJustToggled = true;
        timer.schedule(new ResponsePreventer(), 300); // don't allow clicks on hide button to reshow popup immediately by disabling response for < 1 s

        if (dialog.isVisible()) {
            hidePopup();
        }
    }

    @Override
    public void windowGainedFocus(final WindowEvent e) {
    }
}
