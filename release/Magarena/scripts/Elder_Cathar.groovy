[
    new MagicWhenDiesTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game, final MagicPermanent permanent, final MagicPermanent died) {
            return new MagicEvent(
                permanent,
                MagicTargetChoice.TARGET_CREATURE_YOU_CONTROL,
                MagicPumpTargetPicker.create(),
                this,
                "PN puts a +1/+1 counter on target creature\$ he or she controls. " +
                "If that creature is a Human, put two +1/+1 counters on it instead."
            );
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPermanent(game, {
                final MagicPermanent creature ->
                final int amount = creature.hasSubType(MagicSubType.Human) ? 2 : 1;
                game.doAction(new MagicChangeCountersAction(
                    creature,
                    MagicCounterType.PlusOne,
                    amount,
                    true
                ));
            });
        }
    }
]
