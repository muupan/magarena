package magic.model.event;

import magic.model.MagicCounterType;
import magic.model.MagicGame;
import magic.model.MagicPermanent;
import magic.model.action.MagicChangeCountersAction;
import magic.model.condition.MagicCondition;
import magic.model.condition.MagicConditionFactory;

public class MagicRemoveCounterEvent extends MagicEvent {

    private final MagicCondition[] conds;

    public MagicRemoveCounterEvent(final MagicPermanent permanent,final MagicCounterType counterType,final int amount) {
        super(
            permanent,
            new MagicEventAction() {
                @Override
                public void executeEvent(final MagicGame game, final MagicEvent event) {
                    game.doAction(new MagicChangeCountersAction(
                        event.getPermanent(),
                        counterType,
                        -amount,
                        true
                    ));
                }
            },
            genDescription(permanent,counterType,amount)
        );
        conds = new MagicCondition[]{
            MagicConditionFactory.CounterAtLeast(counterType, amount)
        };
    }

    @Override
    public final MagicCondition[] getConditions() {
        return conds;
    }

    private static String genDescription(final MagicPermanent permanent,final MagicCounterType counterType,final int amount) {
        final StringBuilder description=new StringBuilder("Remove ");
        if (amount==1) {
            description.append("a ").append(counterType.getName()).append(" counter");
        } else {
            description.append(amount).append(' ').append(counterType.getName()).append(" counters");
        }
        description.append(" from ").append(permanent.getName()).append('.');
        return description.toString();
    }
}
