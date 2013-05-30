[
    new MagicPermanentActivation(
        [
            MagicCondition.CAN_TAP_CONDITION,
            MagicConditionFactory.ManaCost("{2}{W}")
        ],
        new MagicActivationHints(MagicTiming.Tapping),
        "Tap"
    ) {

        @Override
        public MagicEvent[] getCostEvent(final MagicPermanent source) {
            return [
                new MagicPayManaCostTapEvent(source,"{2}{W}")
            ];
        }

        @Override
        public MagicEvent getPermanentEvent(final MagicPermanent source, final MagicPayedCost payedCost) {
            return new MagicEvent(
                source,
                MagicTargetChoice.NEG_TARGET_CREATURE,
                new MagicTapTargetPicker(true,false),
                this,
                "Tap target creature\$. If that creature is a Zombie, exile it."
            );
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPermanent(game,new MagicPermanentAction() {
                public void doAction(final MagicPermanent creature) {
                    game.doAction(new MagicTapAction(creature,true));
                    if (creature.hasSubType(MagicSubType.Zombie)) {
                        game.doAction(new MagicRemoveFromPlayAction(
                            creature,
                            MagicLocationType.Exile
                        ));
                    }
                }
            });
        }
    }
]