package magic.model.action;

import magic.model.MagicCard;
import magic.model.MagicGame;
import magic.model.choice.MagicFromCardListChoice;
import magic.model.event.MagicEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MagicRevealAction extends MagicAction {
    
    private final List<MagicCard> cards = new ArrayList<MagicCard>();

    public MagicRevealAction(final MagicCard aCard) {
        cards.add(aCard);
    }

    public MagicRevealAction(final Collection<MagicCard> aCards) {
        cards.addAll(aCards);
    }

    public void doAction(final MagicGame game) {
        if (cards.isEmpty()) {
            return;
        }
        game.addEvent(new MagicEvent(
            MagicEvent.NO_SOURCE,
            cards.get(0).getOpponent(),
            new MagicFromCardListChoice(cards),
            MagicEvent.NO_ACTION,
            ""
        ));
    }

    public void undoAction(final MagicGame game) {
        //do nothing for now
    }
}
