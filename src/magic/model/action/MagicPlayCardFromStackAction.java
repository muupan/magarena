package magic.model.action;

import magic.model.MagicCardDefinition;
import magic.model.MagicGame;
import magic.model.MagicLocationType;
import magic.model.MagicObject;
import magic.model.MagicPermanent;
import magic.model.stack.MagicCardOnStack;

public class MagicPlayCardFromStackAction extends MagicPutIntoPlayAction {

    private final MagicCardOnStack cardOnStack;
    private final MagicCardDefinition cardDef;

    public MagicPlayCardFromStackAction(final MagicCardOnStack aCardOnStack) {
        this(aCardOnStack, aCardOnStack.getCardDefinition());
    }
    
    public MagicPlayCardFromStackAction(final MagicCardOnStack aCardOnStack, final MagicCardDefinition aCardDef) {
        cardOnStack = aCardOnStack;
        cardDef = aCardDef; 
        setPayedCost(aCardOnStack.getPayedCost());
    }

    public MagicPlayCardFromStackAction(final MagicCardOnStack cardOnStack,final MagicPermanent enchantedPermanent) {
        this(cardOnStack);
        setEnchantedPermanent(enchantedPermanent);
    }

    static final public MagicPlayCardFromStackAction EnterAsCopy(final MagicCardOnStack cardOnStack, final MagicObject obj) {
        return new MagicPlayCardFromStackAction(cardOnStack, obj.getCardDefinition());
    }

    @Override
    protected MagicPermanent createPermanent(final MagicGame game) {
        cardOnStack.setMoveLocation(MagicLocationType.Play);
        return game.createPermanent(cardOnStack.getCard(),cardDef,cardOnStack.getController());
    }
}
