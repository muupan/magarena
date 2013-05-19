[
    new MagicPermanentActivation(
        [MagicCondition.ONE_CREATURE_CONDITION],
        new MagicActivationHints(MagicTiming.Pump),
        "Flying"
    ) {
        @Override
        public MagicEvent[] getCostEvent(final MagicPermanent source) {
            return [
                new MagicSacrificePermanentEvent(
                    source,
                    source.getController(),
                    MagicTargetChoice.SACRIFICE_CREATURE
                )
            ];
        }
        @Override
        public MagicEvent getPermanentEvent(final MagicPermanent source,final MagicPayedCost payedCost) {
            return new MagicEvent(
                source,
                this,
                "SN gains flying until end of turn."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            game.doAction(new MagicSetAbilityAction(event.getPermanent(),MagicAbility.Flying));
        }
    }
]