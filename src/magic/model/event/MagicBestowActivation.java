package magic.model.event;

import magic.model.MagicCard;
import magic.model.MagicGame;
import magic.model.MagicLocationType;
import magic.model.MagicManaCost;
import magic.model.MagicPermanent;
import magic.model.MagicSource;
import magic.model.MagicSubType;
import magic.model.MagicType;
import magic.model.action.MagicAddStaticAction;
import magic.model.action.MagicPermanentAction;
import magic.model.action.MagicPlayCardFromStackAction;
import magic.model.action.MagicPutItemOnStackAction;
import magic.model.action.MagicRemoveCardAction;
import magic.model.choice.MagicTargetChoice;
import magic.model.condition.MagicCondition;
import magic.model.mstatic.MagicStatic;
import magic.model.stack.MagicCardOnStack;
import magic.model.target.MagicPumpTargetPicker;

import java.util.Arrays;

public class MagicBestowActivation extends MagicCardActivation {

    final MagicManaCost cost;
    final public static MagicPlayAuraEvent BestowEvent = new MagicPlayAuraEvent(MagicTargetChoice.POS_TARGET_CREATURE, MagicPumpTargetPicker.create()) {
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            final boolean valid = event.processTargetPermanent(game,new MagicPermanentAction() {
                public void doAction(final MagicPermanent creature) {
                    final MagicPlayCardFromStackAction action = new MagicPlayCardFromStackAction(event.getCardOnStack(),creature);
                    game.doAction(action);
                    game.doAction(new MagicAddStaticAction(
                        action.getPermanent(),
                        MagicStatic.Bestowed
                    ));
                }
            });
            if (!valid) {
                game.doAction(new MagicPlayCardFromStackAction(event.getCardOnStack()));
            }
        }
    };

    public MagicBestowActivation(final MagicManaCost aCost) {
        super(
            new MagicCondition[]{MagicCondition.CARD_CONDITION},
            new MagicActivationHints(MagicTiming.Aura, true),
            "Bestow"
        );
        cost = aCost;
    }
    
    @Override
    public Iterable<? extends MagicEvent> getCostEvent(final MagicCard source) {
        return Arrays.asList(new MagicPayManaCostEvent(source, cost));
    }
    
    @Override
    public MagicEvent getEvent(final MagicSource source) {
        return new MagicEvent(
            source,
            EVENT_ACTION,
            "Play SN."
        );
    }
    
    private final MagicEventAction EVENT_ACTION = new MagicEventAction() {
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            final MagicCard card = event.getCard();
            game.doAction(new MagicRemoveCardAction(card,MagicLocationType.OwnersHand));
                
            final MagicCardOnStack cardOnStack=new MagicCardOnStack(
                card,
                BestowEvent,
                game.getPayedCost()
            ) {
                @Override
                public boolean hasType(final MagicType type) {
                    if (type == MagicType.Creature) {
                        return false;
                    } else {
                        return super.hasType(type);
                    }
                }

                @Override
                public boolean hasSubType(final MagicSubType subType) {
                    if (subType == MagicSubType.Aura) {
                        return true;
                    } else if (MagicSubType.ALL_CREATURES.contains(subType)) {
                        return false;
                    } else {
                        return super.hasSubType(subType);
                    }
                }
            };

            game.doAction(new MagicPutItemOnStackAction(cardOnStack));
        }
    };
}
