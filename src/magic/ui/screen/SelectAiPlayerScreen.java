package magic.ui.screen;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JPanel;

import magic.ai.MagicAIImpl;
import magic.model.player.AiPlayer;
import magic.model.player.PlayerProfile;
import magic.model.player.PlayerProfiles;
import magic.ui.dialog.AiPropertiesDialog;
import magic.ui.screen.interfaces.IActionBar;
import magic.ui.screen.interfaces.IPlayerProfileConsumer;
import magic.ui.screen.interfaces.IStatusBar;
import magic.ui.screen.widget.MenuButton;
import magic.ui.widget.AiPlayerJList;

@SuppressWarnings("serial")
public class SelectAiPlayerScreen
    extends SelectPlayerAbstractScreen
    implements IStatusBar, IActionBar {

    private AiPlayerJList profilesJList;
    private IPlayerProfileConsumer consumer;
    private final PlayerProfile playerProfile;

    // CTR
    public SelectAiPlayerScreen(final IPlayerProfileConsumer consumer, final PlayerProfile playerProfile) {
        this.consumer = consumer;
        this.playerProfile = playerProfile;
        refreshProfilesJList(playerProfile.getId());
    }

    /* (non-Javadoc)
     * @see magic.ui.screen.PlayerScreenUtil#getProfilesListPanel()
     */
    @Override
    protected JPanel getProfilesListPanel() {
      profilesJList = new AiPlayerJList();
      profilesJList.addMouseListener(getMouseAdapter());
      return getContainerPanel(profilesJList);
    }

    private MouseAdapter getMouseAdapter() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    doNextAction();
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }
        };
    }

    private void refreshProfilesJList(final String selectedProfileId) {
        profilesJList.setListData(getPlayerProfilesArray());
        setSelectedListItem(selectedProfileId);
    }
    private void refreshProfilesJList() {
        refreshProfilesJList(null);
    }

    private void setSelectedListItem(final String selectedProfileId) {
        if (selectedProfileId == null || selectedProfileId.isEmpty()) {
            profilesJList.setSelectedIndex(0);
        } else {
            profilesJList.setSelectedValue(profilesMap.get(selectedProfileId), true);
        }
        setFocusInProfilesJList(profilesJList);
    }

    private AiPlayer[] getPlayerProfilesArray() {
        setProfilesMap();
        final List<PlayerProfile> profilesByName = new ArrayList<PlayerProfile>(profilesMap.values());
        Collections.sort(profilesByName, new Comparator<PlayerProfile>() {
            @Override
            public int compare(PlayerProfile o1, PlayerProfile o2) {
                return o1.getPlayerName().toLowerCase().compareTo(o2.getPlayerName().toLowerCase());
            }
        });
        return profilesByName.toArray(new AiPlayer[profilesByName.size()]);
    }

    private void setProfilesMap() {
        profilesMap = PlayerProfiles.getAiPlayerProfiles();
    }

    /* (non-Javadoc)
     * @see magic.ui.screen.SelectPlayerAbstractScreen#createDefaultPlayerProfiles()
     */
    @Override
    protected void createDefaultPlayerProfiles() throws IOException {
        // Les Vegas
        AiPlayer profile = new AiPlayer();
        profile.setPlayerName("Les Vegas");
        profile.setAiType(MagicAIImpl.VEGAS);
        profile.setAiLevel(6);
        profile.save();
        // Mini Max
        profile = new AiPlayer();
        profile.setPlayerName("Mini Max");
        profile.setAiType(MagicAIImpl.MMAB);
        profile.setAiLevel(6);
        profile.save();
        // Monty Carlo
        profile = new AiPlayer();
        profile.setPlayerName("Monty Carlo");
        profile.setAiType(MagicAIImpl.MCTS);
        profile.setAiLevel(6);
        profile.save();
    }

    /* (non-Javadoc)
     * @see magic.ui.IMagStatusBar#getScreenCaption()
     */
    @Override
    public String getScreenCaption() {
        return "Select AI Player";
    }

    /* (non-Javadoc)
     * @see magic.ui.IMagActionBar#getLeftAction()
     */
    @Override
    public MenuButton getLeftAction() {
        return new MenuButton("Cancel", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                getFrame().closeActiveScreen(false);
            }
        });
    }

    /* (non-Javadoc)
     * @see magic.ui.IMagActionBar#getRightAction()
     */
    @Override
    public MenuButton getRightAction() {
        return new MenuButton("Select", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                doNextAction();
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    /* (non-Javadoc)
     * @see magic.ui.IMagActionBar#getMiddleActions()
     */
    @Override
    public List<MenuButton> getMiddleActions() {
        final List<MenuButton> buttons = new ArrayList<MenuButton>();
        buttons.add(new MenuButton("New", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createNewPlayerProfile();
            }
        }, "Create a new player profile."));
        buttons.add(new MenuButton("Edit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AiPropertiesDialog(getFrame(), getSelectedPlayerProfile());
                profilesJList.repaint();
                if (getSelectedPlayerProfile().getId().equals(playerProfile.getId())) {
                    consumer.setPlayerProfile(getSelectedPlayerProfile());
                }
            }
        }, "Update name and duel settings for selected player."));
        buttons.add(new MenuButton("Delete", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedPlayerProfile();
            }
        }, "Delete selected player profile."));
        buttons.add(getAvatarActionButton());
        return buttons;
    }

    private void deleteSelectedPlayerProfile() {
        final PlayerProfile profile = getSelectedPlayerProfile();
        if (deleteSelectedPlayerProfile(profile)) {
            PlayerProfiles.getPlayerProfiles().remove(profile.getId());
            refreshProfilesJList();
            if (profile.getId().equals(playerProfile.getId())) {
                consumer.setPlayerProfile(getSelectedPlayerProfile());
            }
        }
    }

    private void createNewPlayerProfile() {
        final AiPropertiesDialog dialog = new AiPropertiesDialog(getFrame());
        final PlayerProfile newProfile = dialog.getPlayerProfile();
        if (newProfile != null) {
            PlayerProfiles.getPlayerProfiles().put(newProfile.getId(), newProfile);
            refreshProfilesJList(newProfile.getId());
        }
    }

    private AiPlayer getSelectedPlayerProfile() {
        return profilesJList.getSelectedValue();
    }

    /* (non-Javadoc)
     * @see magic.ui.MagScreen#canScreenClose()
     */
    @Override
    public boolean isScreenReadyToClose(final AbstractScreen nextScreen) {
        return true;
    }

    /* (non-Javadoc)
     * @see magic.ui.screen.IAvatarImageConsumer#setSelectedAvatarPath(java.nio.file.Path)
     */
    @Override
    public void setSelectedAvatarPath(final Path imagePath) {
        final PlayerProfile profile = getSelectedPlayerProfile();
        updateAvatarImage(imagePath, profile);
        PlayerProfiles.refreshMap();
        refreshProfilesJList(profile.getId());
        consumer.setPlayerProfile(getSelectedPlayerProfile());
    }

    /* (non-Javadoc)
     * @see magic.ui.screen.SelectPlayerScreen#getPlayerType()
     */
    @Override
    protected String getPlayerType() {
        return "ai";
    }

    /* (non-Javadoc)
     * @see magic.ui.screen.SelectPlayerAbstractScreen#doNextAction()
     */
    @Override
    protected void doNextAction() {
        consumer.setPlayerProfile(getSelectedPlayerProfile());
        getFrame().closeActiveScreen(false);
    }

    /* (non-Javadoc)
     * @see magic.ui.screen.interfaces.IStatusBar#getStatusPanel()
     */
    @Override
    public JPanel getStatusPanel() {
        return null;
    }

}