[
    new MagicSpellCardEvent() {
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack,final MagicPayedCost payedCost) {
            final int amount = MagicCondition.METALCRAFT_CONDITION.accept(cardOnStack) ? 4 : 2;
            return new MagicEvent(
                cardOnStack,
                MagicTargetChoice.NEG_TARGET_CREATURE_OR_PLAYER,
                new MagicDamageTargetPicker(amount),
                this,
                "SN deals 2 damage to target creature or player\$. "+
                "If you control three or more artifacts, it deals 4 damage to that creature or player instead."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTarget(game, {
                final MagicTarget target ->
                final int amount = MagicCondition.METALCRAFT_CONDITION.accept(event.getSource()) ? 4 : 2;
                final MagicDamage damage=new MagicDamage(event.getSource(),target,amount);
                game.doAction(new MagicDealDamageAction(damage));
            });
        }
    }
]
