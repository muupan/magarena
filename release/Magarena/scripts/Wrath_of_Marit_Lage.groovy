[
    new MagicWhenComesIntoPlayTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent,final MagicPayedCost payedCost) {
            return new MagicEvent(
                permanent,
                this,
                "Tap all red creatures."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            final Collection<MagicPermanent> targets = game.filterPermanents(
                    event.getPlayer(),
                    MagicTargetFilterFactory.RED_CREATURE);
            for (final MagicPermanent creature : targets) {
                if (creature != event.getPermanent()) {
                    game.doAction(new MagicTapAction(creature,true));
                }
            }
        }
    }
]
