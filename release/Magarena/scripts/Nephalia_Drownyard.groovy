[
    new MagicPermanentActivation(
        [
            MagicConditionFactory.ManaCost("{2}{U}{B}"), //add ONE for the card itself
            MagicCondition.CAN_TAP_CONDITION
        ],
        new MagicActivationHints(MagicTiming.Pump),
        "Pump"
    ) {

        @Override
        public MagicEvent[] getCostEvent(final MagicPermanent source) {
            return [
                new MagicTapEvent(source),
                new MagicPayManaCostEvent(source,"{1}{U}{B}")
            ];
        }

        @Override
        public MagicEvent getPermanentEvent(final MagicPermanent source,final MagicPayedCost payedCost) {
            return new MagicEvent(
                source,
                MagicTargetChoice.TARGET_PLAYER,
                this,
                "Target player\$ puts the top three cards of " +
                "his or her library into his or her graveyard."
            );
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPlayer(game,new MagicPlayerAction() {
                public void doAction(final MagicPlayer player) {
                    game.doAction(new MagicMillLibraryAction(player,3));
                }
            });
        }
    }
]