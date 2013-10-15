[
    new MagicPermanentActivation(
        new MagicActivationHints(MagicTiming.Tapping),
        "Untap"
    ) {

        @Override
        public Iterable<MagicEvent> getCostEvent(final MagicPermanent source) {
            return [new MagicTapEvent(source)];
        }

        @Override
        public MagicEvent getPermanentEvent(final MagicPermanent source,final MagicPayedCost payedCost) {
            return new MagicEvent(
                source,
                MagicTargetChoice.POS_TARGET_LAND,
                MagicTapTargetPicker.Untap,
                this,
                "Untap target land\$."
            );
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPermanent(game,new MagicPermanentAction() {
                public void doAction(final MagicPermanent land) {
                    game.doAction(new MagicUntapAction(land));
                }
            });
        }
    }
]