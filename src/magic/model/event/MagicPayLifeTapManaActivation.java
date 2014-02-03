package magic.model.event;

import magic.model.MagicManaType;
import magic.model.MagicPermanent;
import magic.model.MagicSource;
import magic.model.condition.MagicCondition;
import magic.model.event.MagicPainTapEvent;

import java.util.List;
import java.util.Arrays;

public class MagicPayLifeTapManaActivation extends MagicManaActivation {

    public MagicPayLifeTapManaActivation(final List<MagicManaType> manaTypes) {
        super(manaTypes);
    }

    @Override
    public Iterable<? extends MagicEvent> getCostEvent(final MagicPermanent perm) {
        return Arrays.asList(new MagicPayLifeTapEvent(perm));
    }
}