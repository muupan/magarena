[
    new MagicStatic(MagicLayer.SetPT) {
        @Override
        public void modPowerToughness(final MagicPermanent source,final MagicPermanent permanent,final MagicPowerToughness pt) {
            final int charges = permanent.getCounters(MagicCounterType.Charge);
            if (charges >= 4) {
                pt.set(7,3);
            } else if (charges >= 1) {
                pt.set(4,2);
            }
        }        
    },
    new MagicPermanentActivation(
        [
            MagicConditionFactory.ChargeCountersAtLeast(4),
            MagicCondition.CAN_REGENERATE_CONDITION,
            MagicConditionFactory.ManaCost("{B}")
        ],
        new MagicActivationHints(MagicTiming.Pump),
        "Regen"
    ) {

        @Override
        public MagicEvent[] getCostEvent(final MagicPermanent source) {
            return [
                new MagicPayManaCostEvent(source,"{B}")
            ];
        }

        @Override
        public MagicEvent getPermanentEvent(final MagicPermanent source,final MagicPayedCost payedCost) {
            return new MagicEvent(
                source,
                this,
                "Regenerate SN."
            );
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            game.doAction(new MagicRegenerateAction(event.getPermanent()));
        }
    }
]