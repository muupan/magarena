[
    new MagicPermanentActivation( 
        [
            MagicCondition.ABILITY_ONCE_CONDITION,
            MagicCondition.TWO_LIFE_CONDITION
        ],
        new MagicActivationHints(MagicTiming.Pump),
        "Pump"
    ) {

        @Override
        public MagicEvent[] getCostEvent(final MagicPermanent source) {
            return [
                new MagicPayLifeEvent(source,source.getController(),2),
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
            final MagicPermanent permanent=event.getPermanent();
            game.doAction(new MagicChangeTurnPTAction(permanent,2,2));
        }
    }
]