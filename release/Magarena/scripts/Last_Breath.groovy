[
    new MagicSpellCardEvent() {
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack,final MagicPayedCost payedCost) {
            return new MagicEvent(
                cardOnStack,
                MagicTargetChoice.Negative("target creature with power 2 or less"),
                MagicExileTargetPicker.create(),
                this,
                "Exile target creature\$. Its controller gains 4 life."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPermanent(game, {
                final MagicPermanent creature ->
                game.doAction(new MagicRemoveFromPlayAction(creature,MagicLocationType.Exile));
                game.doAction(new MagicChangeLifeAction(creature.getController(),4));
            });
        }
    }
]
