[
    new MagicSpellCardEvent() {
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack,final MagicPayedCost payedCost) {
            return new MagicEvent(
                cardOnStack,
                MagicTargetChoice.NEG_TARGET_NONCREATURE_SPELL,
                this,
                "Counter target noncreature spell\$. PN draws a card."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetCardOnStack(game, {
                final MagicCardOnStack targetSpell ->
                game.doAction(new MagicCounterItemOnStackAction(targetSpell));
                game.doAction(new MagicDrawAction(event.getPlayer()));
            });
        }
    }
]
