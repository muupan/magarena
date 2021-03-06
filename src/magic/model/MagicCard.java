package magic.model;

import magic.data.HighQualityCardImagesProvider;
import magic.model.event.MagicActivation;
import magic.model.event.MagicEvent;
import magic.model.event.MagicSourceActivation;
import magic.model.target.MagicTarget;
import magic.model.target.MagicTargetFilter;
import magic.model.target.MagicTargetType;
import magic.ui.canvas.cards.ICardCanvas;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class MagicCard
    extends MagicObjectImpl
    implements MagicSource,MagicTarget,Comparable<MagicCard>,MagicMappable<MagicCard>,ICardCanvas {

    public static final MagicCard NONE = new MagicCard(MagicCardDefinition.UNKNOWN, MagicPlayer.NONE, 0) {
        @Override
        public MagicCard copy(final MagicCopyMap copyMap) {
            return this;
        }
        @Override
        public String getName() {
            return "NONE";
        }
        @Override
        public boolean isInHand() {
            final boolean found = getOwner().getHand().contains(this);
            if (found) {
                throw new RuntimeException("Card NONE is in hand");
            }
            return found;
        }
        @Override
        public boolean isInGraveyard() {
            final boolean found = getOwner().getGraveyard().contains(this);
            if (found) {
                throw new RuntimeException("Card NONE is in graveyard");
            }
            return found;
        }
        @Override
        public boolean isInExile() {
            final boolean found = getOwner().getExile().contains(this);
            if (found) {
                throw new RuntimeException("Card NONE is in exile");
            }
            return found;
        }
    };

    private static final int TOKEN_ID=-1;

    private final MagicCardDefinition cardDefinition;
    private final MagicPlayer owner;
    private final long id;
    private final int imageIndex;
    private final boolean token;
    private boolean known=true;

    public MagicCard(final MagicCardDefinition aCardDefinition,final MagicPlayer aOwner,final long aId) {
        this(aCardDefinition, aOwner, aId, false);
    }

    public static MagicCard createTokenCard(final MagicCardDefinition cardDefinition, final MagicPlayer owner) {
        return new MagicCard(cardDefinition, owner, MagicCard.TOKEN_ID, true);
    }

    public static MagicCard createTokenCard(final MagicObject obj, final MagicPlayer owner) {
        return new MagicCard(obj.getCardDefinition(), owner, MagicCard.TOKEN_ID, true);
    }

    private MagicCard(final MagicCardDefinition aCardDefinition,final MagicPlayer aOwner,final long aId, final boolean aToken) {
        aCardDefinition.loadAbilities();
        cardDefinition = aCardDefinition;
        owner = aOwner;
        id = aId;
        imageIndex = (int)Math.abs(id % 1000);
        token = aToken;
    }

    private MagicCard(final MagicCopyMap copyMap, final MagicCard sourceCard) {
        copyMap.put(sourceCard, this);

        cardDefinition = sourceCard.cardDefinition;
        owner = copyMap.copy(sourceCard.owner);
        id = sourceCard.id;
        imageIndex = sourceCard.imageIndex;
        token = sourceCard.token;
        known = sourceCard.known;
    }

    @Override
    public MagicCard copy(final MagicCopyMap copyMap) {
        return new MagicCard(copyMap, this);
    }

    @Override
    public MagicCard map(final MagicGame game) {
        final MagicPlayer mappedOwner=owner.map(game);
        MagicCard card = MagicCard.NONE;
        if (card == MagicCard.NONE) {
            card = mappedOwner.getPrivateHand().getCard(id);
        }
        if (card == MagicCard.NONE) {
            card = mappedOwner.getGraveyard().getCard(id);
        }
        if (card == MagicCard.NONE) {
            card = mappedOwner.getExile().getCard(id);
        }
        if (card == MagicCard.NONE) {
            card = mappedOwner.getLibrary().getCard(id);
        }
        if (card == MagicCard.NONE) {
            throw new RuntimeException("Mapping card failed, card " + getName() + " " + id + " not found");
        }
        return card;
    }

    public long getId() {
        return id;
    }

    public long getStateId() {
        return getCardDefinition().getIndex();
    }

    public int getImageIndex() {
        return imageIndex;
    }

    public MagicCardDefinition getCardDefinition() {
        return known ? cardDefinition : MagicCardDefinition.UNKNOWN;
    }

    public MagicPlayer getOwner() {
        return owner;
    }

    public boolean isToken() {
        return token;
    }

    public int getPower() {
        return genPowerToughness().power();
    }

    public int getToughness() {
        return genPowerToughness().toughness();
    }

    public MagicPowerToughness genPowerToughness() {
        return genPowerToughness(MagicPermanent.NONE);
    }

    public MagicPowerToughness genPowerToughness(final MagicPermanent perm) {
        final MagicPowerToughness pt = getCardDefinition().genPowerToughness();
        getCardDefinition().applyCDAPowerToughness(
            getGame(),
            perm.isValid() ? perm.getController() : getOwner(),
            perm,
            pt
        );
        return pt;
    }

    public int getConvertedCost() {
        return getCost().getConvertedCost();
    }

    public MagicManaCost getCost() {
        return getCardDefinition().getCost();
    }

    public Iterable<? extends MagicEvent> getCostEvent() {
        return getCardDefinition().getCostEvent(this);
    }

    public void reveal() {
        setKnown(true);
    }

    void setKnown(final boolean known) {
        this.known=known;
    }

    public boolean isKnown() {
        return known;
    }

    public boolean isInHand() {
        return getOwner().getHand().contains(this);
    }

    public boolean isInGraveyard() {
        return getOwner().getGraveyard().contains(this);
    }

    public boolean isInExile() {
        return getOwner().getExile().contains(this);
    }

    @Override
    public String getName() {
        return getCardDefinition().getName();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public MagicPlayer getController() {
        return owner;
    }

    @Override
    public int getPreventDamage() {
        return 0;
    }

    @Override
    public void setPreventDamage(final int amount) {

    }

    @Override
    public boolean isValidTarget(final MagicSource source) {
        return true;
    }

    @Override
    public boolean isPermanent() {
        return false;
    }

    @Override
    public boolean isPlayer() {
        return false;
    }

    @Override
    public boolean isSpell() {
        return true;
    }

    private int getColorFlags() {
        return getCardDefinition().getColorFlags();
    }

    @Override
    public boolean hasColor(final MagicColor color) {
        return color.hasColor(getColorFlags());
    }

    @Override
    public boolean hasAbility(final MagicAbility ability) {
        return getCardDefinition().hasAbility(ability);
    }

    public Set<MagicAbility> getAbilityFlags() {
        return getCardDefinition().genAbilityFlags();
    }

    @Override
    public boolean hasType(final MagicType type) {
        return getCardDefinition().hasType(type);
    }

    @Override
    public boolean hasSubType(final MagicSubType subType) {
        return getCardDefinition().hasSubType(subType);
    }

    public Set<MagicSubType> getSubTypeFlags() {
        return getCardDefinition().getSubTypeFlags();
    }

    @Override
    public Collection<MagicSourceActivation<? extends MagicSource>> getSourceActivations() {
        List<MagicSourceActivation<? extends MagicSource>> sourceActs = new LinkedList<>();
        final Collection<MagicActivation<MagicCard>> activations = isInHand() ? 
            getCardDefinition().getCardActivations() :
            getCardDefinition().getGraveyardActivations();
        for (final MagicActivation<MagicCard> act : activations) {
            sourceActs.add(MagicSourceActivation.create(this, act));
        }
        return sourceActs;
    }

    @Override
    public int compareTo(final MagicCard card) {
        final int diff = getCardDefinition().getIndex() - card.getCardDefinition().getIndex();
        if (diff != 0) {
            return diff;
        } else {
            return Long.signum(id - card.id);
        }
    }

    @Override
    public MagicGame getGame() {
        return owner.getGame();
    }

    @Override
    public boolean isLegalTarget(final MagicPlayer player, final MagicTargetFilter<? extends MagicTarget> targetFilter) {
        // Card in graveyard
        if (targetFilter.acceptType(MagicTargetType.Graveyard) &&
            player.getGraveyard().contains(this)) {
            return true;
        }

        // Card in opponent's graveyard
        if (targetFilter.acceptType(MagicTargetType.OpponentsGraveyard) &&
            player.getOpponent().getGraveyard().contains(this)) {
            return true;
        }

        // Card in hand
        if (targetFilter.acceptType(MagicTargetType.Hand) &&
            player.getHand().contains(this)) {
            return true;
        }

        // Card in library
        if (targetFilter.acceptType(MagicTargetType.Library) &&
            player.getLibrary().contains(this)) {
            return true;
        }

        return false;
    }

    /* (non-Javadoc)
     * @see magic.ui.canvas.cards.ICardCanvas#getFrontImage()
     */
    @Override
    public BufferedImage getFrontImage() {
        return HighQualityCardImagesProvider.getInstance().getImage(
                cardDefinition, getImageIndex(), true);
    }

    /* (non-Javadoc)
     * @see magic.ui.canvas.cards.ICardCanvas#getBackImage()
     */
    @Override
    public BufferedImage getBackImage() {
        return null;
    }
}
