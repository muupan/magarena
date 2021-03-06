[
    new MagicSpellCardEvent() {
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack,final MagicPayedCost payedCost) {
            return new MagicEvent(
                cardOnStack,
                MagicTargetChoice.NEG_TARGET_ATTACKING_CREATURE,
                MagicDestroyTargetPicker.Destroy,
                this,
                "Destroy target attacking creature\$. " +
                "PN gains life equal to its power."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPermanent(game, {
                final MagicPermanent creature ->
                final int power=creature.getPower();
                game.doAction(new MagicDestroyAction(creature));
                game.doAction(new MagicChangeLifeAction(event.getPlayer(),power));
            });
        }
    }
]
