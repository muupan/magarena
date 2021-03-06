[
    new MagicAtEndOfTurnTrigger() {
        @Override
        public MagicEvent executeTrigger(
                final MagicGame game,
                final MagicPermanent permanent,
                final MagicPlayer player) {
            return (!player.getOpponent().hasState(MagicPlayerState.WasDealtDamage)) ?
                new MagicEvent(
                    permanent,
                    player,
                    this,
                    "SN deals 2 damage to PN."
                ):
                MagicEvent.NONE;
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            final MagicDamage damage = new MagicDamage(
                event.getSource(),
                event.getPlayer(),
                2
            );
            game.doAction(new MagicDealDamageAction(damage));
        }
    }
]
