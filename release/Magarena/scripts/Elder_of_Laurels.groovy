[
    new MagicPermanentActivation(
        [MagicConditionFactory.ManaCost("{3}{G}")],
        new MagicActivationHints(MagicTiming.Pump,true),
        "Pump"
    ) {
        @Override
        public MagicEvent[] getCostEvent(final MagicPermanent source) {
            return [new MagicPayManaCostEvent(source,"{3}{G}")];
        }
        @Override
        public MagicEvent getPermanentEvent(final MagicPermanent source,final MagicPayedCost payedCost) {
            return new MagicEvent(
                source,
                MagicTargetChoice.POS_TARGET_CREATURE,
                MagicPumpTargetPicker.create(),
                this,
                "Target creature gets +X/+X until end of turn, " + 
                "where X is the number of creatures PN controls."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPermanent(game,new MagicPermanentAction() {
                public void doAction(final MagicPermanent creature) {
                    final int amount = event.getPlayer().getNrOfPermanentsWithType(MagicType.Creature);
                    game.doAction(new MagicChangeTurnPTAction(creature,amount,amount));
                }
            });
        }
    }
]