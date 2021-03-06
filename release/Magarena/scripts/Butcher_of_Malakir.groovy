def action = {
    final MagicGame game, final MagicEvent event ->
    final MagicPlayer opponent=event.getPlayer().getOpponent();
    if (opponent.controlsPermanent(MagicType.Creature)) {
        game.addEvent(new MagicSacrificePermanentEvent(
            event.getPermanent(),
            opponent,
            MagicTargetChoice.SACRIFICE_CREATURE
        ));
    }
}

def event = {
    final MagicPermanent permanent ->
    return new MagicEvent(
        permanent,
        action,
        "PN's opponent sacrifices a creature."
    );
}

[
    new MagicWhenDiesTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game, final MagicPermanent permanent, final MagicPermanent died) {
            return event(permanent);
        }
    },
    new MagicWhenOtherDiesTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent,final MagicPermanent otherPermanent) {
            return (otherPermanent != permanent &&
                    otherPermanent.isFriend(permanent) &&
                    otherPermanent.isCreature()) ?
                event(permanent) : MagicEvent.NONE;
        }
    }
]
