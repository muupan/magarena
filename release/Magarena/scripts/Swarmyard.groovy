[
    new MagicPermanentActivation(
        [MagicCondition.CAN_TAP_CONDITION],
        new MagicActivationHints(MagicTiming.Pump,false),
        "Regen"
    ) {

        @Override
        public MagicEvent[] getCostEvent(final MagicPermanent source) {
            return [new MagicTapEvent(source)];
        }

        @Override
        public MagicEvent getPermanentEvent(final MagicPermanent source,final MagicPayedCost payedCost) {
            return new MagicEvent(
                source,
                MagicTargetChoice.POS_TARGET_INSECT_RAT_SPIDER_OR_SQUIRREL,
                MagicRegenerateTargetPicker.getInstance(),
                this,
                "Regenerate target Insect, Rat, Spider, or Squirrel\$."
            );
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPermanent(game,new MagicPermanentAction() {
                public void doAction(final MagicPermanent creature) {
                    game.doAction(new MagicRegenerateAction(creature));
                }
            });
        }
    }
]