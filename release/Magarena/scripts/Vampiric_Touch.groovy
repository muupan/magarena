[
    new MagicSpellCardEvent() {
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack,final MagicPayedCost payedCost) {
            return new MagicEvent(
                cardOnStack,
                MagicTargetChoice.TARGET_OPPONENT,
                new MagicDamageTargetPicker(2),
                this,
                "SN deals 2 damage to target opponent\$ and " +
                "PN gains 2 life."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPlayer(game, {
                final MagicPlayer player ->
                final MagicDamage damage = new MagicDamage(event.getSource(),player,2);
                game.doAction(new MagicDealDamageAction(damage));
                game.doAction(new MagicChangeLifeAction(event.getPlayer(),2));
            });
        }
    }
]
