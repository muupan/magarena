[
    new MagicPermanentActivation(
        [MagicConditionFactory.ManaCost("{1}{B}")],
        new MagicActivationHints(MagicTiming.Pump,true),
        "Deathtouch"
    ) {
        
        @Override
        public MagicEvent[] getCostEvent(final MagicPermanent source) {
            return [new MagicPayManaCostEvent(source,"{1}{B}")];
        }

        @Override
        public MagicEvent getPermanentEvent(final MagicPermanent source,final MagicPayedCost payedCost) {
            return new MagicEvent(
                source,
                MagicTargetChoice.TARGET_CREATURE_YOU_CONTROL,
                MagicDeathtouchTargetPicker.getInstance(),
                this,
                "Target creature\$ you control gains deathtouch until end of turn."
            );
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPermanent(game,new MagicPermanentAction() {
                public void doAction(final MagicPermanent creature) {
                    game.doAction(new MagicSetAbilityAction(creature,MagicAbility.Deathtouch));
                }
            });
        }
    }
]