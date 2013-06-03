[
    new MagicPermanentActivation(
        [
            MagicCondition.CAN_TAP_CONDITION,
            MagicCondition.ONE_CREATURE_CONDITION
        ],
        new MagicActivationHints(MagicTiming.Pump),
        "Charge"
    ) {
        @Override
        public MagicEvent[] getCostEvent(final MagicPermanent source) {
            return [
                new MagicTapEvent(source),
                new MagicSacrificePermanentEvent(
                    source,
                    MagicTargetChoice.SACRIFICE_CREATURE
                )
            ];
        }
        @Override
        public MagicEvent getPermanentEvent(final MagicPermanent source,final MagicPayedCost payedCost) {
            return new MagicEvent(
                source,
                this,
                "Put a charge counter on SN."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            game.doAction(new MagicChangeCountersAction(
                event.getPermanent(),
                MagicCounterType.Charge,
                1,
                true
            ));
        }
    },
    new MagicPermanentActivation(
        [
            MagicConditionFactory.ManaCost("{1}")
        ],
        new MagicActivationHints(MagicTiming.Pump),
        "Draw"
    ) {
        @Override
        public MagicEvent[] getCostEvent(final MagicPermanent source) {
            return [
                new MagicPayManaCostEvent(source,"{1}"),
                new MagicSacrificeEvent(source)
            ];
        }

        @Override
        public MagicEvent getPermanentEvent(final MagicPermanent source, final MagicPayedCost payedCost) {
            return new MagicEvent(
                source,
                this,
                "PN draw a card for each charge counter on SN."
            );
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            game.doAction(new MagicDrawAction(
                event.getPlayer(),
                event.getPermanent().getCounters(MagicCounterType.Charge)
            ));
        }
    }
]