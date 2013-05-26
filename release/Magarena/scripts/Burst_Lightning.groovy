[
    new MagicSpellCardEvent() {
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack,final MagicPayedCost payedCost) {
            return new MagicEvent(
                cardOnStack,
                new MagicKickerChoice(
                    MagicTargetChoice.NEG_TARGET_CREATURE_OR_PLAYER,
                    MagicManaCost.create("{4}"),
                    false
                ),
                new MagicDamageTargetPicker(2),
                this,
                "SN deals 2 damage to target creature or player$. "+
                "If SN was kicked$, it deals 4 damage to that creature or player instead."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTarget(game,new MagicTargetAction() {
                public void doAction(final MagicTarget target) {
                    final int amount = event.isKicked() ? 4 : 2;
                    final MagicDamage damage=new MagicDamage(event.getSource(),target,amount);
                    game.doAction(new MagicDealDamageAction(damage));
                }
            });
        }
    }
]