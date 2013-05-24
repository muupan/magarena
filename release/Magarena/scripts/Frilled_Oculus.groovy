[
    new MagicPermanentActivation(
        [
            MagicCondition.ABILITY_ONCE_CONDITION,
            MagicConditionFactory.ManaCost("{1}{G}")
        ],
        new MagicActivationHints(MagicTiming.Pump),
        "Pump"
    ) {
        @Override
        public MagicEvent[] getCostEvent(final MagicPermanent source) {
            return [
                new MagicPayManaCostEvent(source,"{1}{G}"),
                new MagicPlayAbilityEvent(source)
            ];
        }
        @Override
        public MagicEvent getPermanentEvent(final MagicPermanent source,final MagicPayedCost payedCost) {
            return new MagicEvent(
                source,
                this,
                "SN gets +2/+2 until end of turn."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            game.doAction(new MagicChangeTurnPTAction(event.getPermanent(),2,2));
        }
    }
]