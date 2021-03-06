[
    new MagicSpellCardEvent() {
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack,final MagicPayedCost payedCost) {
            return new MagicEvent(
                cardOnStack,
                MagicTargetChoice.NEG_WHITE_OR_BLUE_CREATURE,
                new MagicDamageTargetPicker(5,true),
                this,
                "SN deals 5 damage to target white or blue creature\$. " +
                "The damage can't be prevented."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPermanent(game, {
                final MagicPermanent target ->
                final MagicDamage damage = new MagicDamage(event.getSource(),target,5);
                damage.setUnpreventable();
                game.doAction(new MagicDealDamageAction(damage));
            });
        }
    }
]
