[
    new MagicWhenComesIntoPlayTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent, final MagicPayedCost payedCost) {
            return new MagicEvent(
                permanent,
                MagicTargetChoice.TARGET_CREATURE,
                MagicExileTargetPicker.create(),
                this,
                "Exile target creature\$."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            final MagicPermanent permanent = event.getPermanent();
            event.processTargetPermanent(game, {
                final MagicPermanent creature ->
                game.doAction(new MagicExileLinkAction(permanent,creature));
            });
        }
    }
]
