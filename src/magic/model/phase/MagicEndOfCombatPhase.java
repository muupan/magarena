package magic.model.phase;

import magic.model.MagicGame;
import magic.model.MagicPermanent;
import magic.model.MagicPlayer;
import magic.model.action.MagicRemoveFromCombatAction;
import magic.model.trigger.MagicTriggerType;

public class MagicEndOfCombatPhase extends MagicPhase {

    private static final MagicPhase INSTANCE=new MagicEndOfCombatPhase();

    private MagicEndOfCombatPhase() {
        super(MagicPhaseType.EndOfCombat);
    }

    public static MagicPhase getInstance() {
        return INSTANCE;
    }

    @Override
    public void executeBeginStep(final MagicGame game) {
        game.setStep(game.canSkip()?MagicStep.NextPhase:MagicStep.ActivePlayer);
    }

    @Override
    public void executeEndOfPhase(final MagicGame game) {
        // End of combat triggers
        game.executeTrigger(MagicTriggerType.AtEndOfCombat,game.getTurnPlayer());
        
        // Remove permanents from combat
        for (final MagicPlayer player : game.getPlayers()) {
            for (final MagicPermanent permanent : player.getPermanents()) {
                if (permanent.isAttacking()||permanent.isBlocking()) {
                    game.doAction(new MagicRemoveFromCombatAction(permanent));
                }
            }
        }
    }
}
