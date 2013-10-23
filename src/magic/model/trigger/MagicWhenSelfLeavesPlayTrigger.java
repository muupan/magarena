package magic.model.trigger;

import magic.model.MagicGame;
import magic.model.MagicPermanent;
import magic.model.MagicLocationType;
import magic.model.target.MagicTargetPicker;
import magic.model.choice.MagicChoice;
import magic.model.choice.MagicMayChoice;
import magic.model.event.MagicEvent;
import magic.model.event.MagicSourceEvent;
import magic.model.event.MagicEventAction;
import magic.model.event.MagicRuleEventAction;
import magic.model.action.MagicRemoveFromPlayAction;

public abstract class MagicWhenSelfLeavesPlayTrigger extends MagicWhenLeavesPlayTrigger {
    public MagicWhenSelfLeavesPlayTrigger(final int priority) {
        super(priority);
    }
    
    public MagicWhenSelfLeavesPlayTrigger() {}
   
    @Override
    public boolean accept(final MagicPermanent permanent, final MagicRemoveFromPlayAction act) {
        return act.isPermanent(permanent);
    }
    
    public static final MagicWhenSelfLeavesPlayTrigger create(final MagicSourceEvent sourceEvent) {
        return new MagicWhenSelfLeavesPlayTrigger() {
            @Override
            public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent, final MagicRemoveFromPlayAction data) {
                return sourceEvent.getEvent(permanent);
            }
        };
    }

    // replacement effect has priority 1
    public static final MagicWhenSelfLeavesPlayTrigger IfDieExileInstead = new MagicWhenSelfLeavesPlayTrigger(MagicTrigger.REPLACEMENT) {
        @Override
        public MagicEvent executeTrigger(final MagicGame game, final MagicPermanent permanent, final MagicRemoveFromPlayAction act) {
            if (act.getToLocation() == MagicLocationType.Graveyard) {
                act.setToLocation(MagicLocationType.Exile);
            }
            return MagicEvent.NONE;
        }
    };
}