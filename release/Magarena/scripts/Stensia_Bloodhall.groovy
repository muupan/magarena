[
    new MagicPermanentActivation(
        [
            MagicConditionFactory.ManaCost("{4}{B}{R}"), //add ONE for the card itself
            MagicCondition.CAN_TAP_CONDITION
        ],
        new MagicActivationHints(MagicTiming.Removal),
        "Damage"
    ) {

        @Override
        public MagicEvent[] getCostEvent(final MagicPermanent source) {
            return [
                new MagicTapEvent(source),
                new MagicPayManaCostEvent(source,"{3}{B}{R}")
            ];
        }

        @Override
        public MagicEvent getPermanentEvent(final MagicPermanent source,final MagicPayedCost payedCost) {
            return new MagicEvent(
                source,
                MagicTargetChoice.NEG_TARGET_PLAYER,
                new MagicDamageTargetPicker(2),
                this,
                "SN deals 2 damage to target player\$."
            );
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPlayer(game,new MagicPlayerAction() {
                public void doAction(final MagicPlayer player) {
                    final MagicDamage damage = new MagicDamage(event.getPermanent(),player,2);
                    game.doAction(new MagicDealDamageAction(damage));
                }
            });
        }
    }
]