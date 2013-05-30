[
    new MagicPermanentActivation(
        [
            MagicCondition.CAN_TAP_CONDITION,
            MagicConditionFactory.ManaCost("{2}{W}")
        ],
        new MagicActivationHints(MagicTiming.Removal),
        "Damage"
    ) {
        @Override
        public MagicEvent[] getCostEvent(final MagicPermanent source) {
            return [new MagicPayManaCostTapEvent(source,"{2}{W}")];
        }
        @Override
        public MagicEvent getPermanentEvent(final MagicPermanent source,final MagicPayedCost payedCost) {
            return new MagicEvent(
                source,
                MagicTargetChoice.NEG_TARGET_ATTACKING_OR_BLOCKING_CREATURE,
                new MagicDamageTargetPicker(3),
                this,
                "SN deals 3 damage to target attacking or blocking creature\$. " +
                "SN doesn't untap during your next untap step."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPermanent(game,new MagicPermanentAction() {
                public void doAction(final MagicPermanent creature) {
                    final MagicDamage damage=new MagicDamage(event.getPermanent(),creature,3);
                    game.doAction(new MagicDealDamageAction(damage));
                    game.doAction(new MagicChangeStateAction(event.getPermanent(),MagicPermanentState.DoesNotUntapDuringNext,true));
                }
            });
        }
    }
]