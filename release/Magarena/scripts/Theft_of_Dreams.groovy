[
    new MagicSpellCardEvent() {
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack,final MagicPayedCost payedCost) {
            return new MagicEvent(
                cardOnStack,
                MagicTargetChoice.TARGET_OPPONENT,
                this,
                "Draw a card for each tapped creature target opponent\$ controls."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPlayer(game, {
                final MagicPlayer player ->
                final int amt = game.filterPermanents(player, MagicTargetFilterFactory.TAPPED_CREATURE_YOU_CONTROL).size();
                game.doAction(new MagicDrawAction(event.getPlayer(),amt));
            });
        }
    } 
]
