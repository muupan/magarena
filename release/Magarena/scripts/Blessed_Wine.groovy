[
    new MagicSpellCardEvent() {
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack,final MagicPayedCost payedCost) {
            return new MagicEvent(
                cardOnStack,
                this,
                "PN gains 1 life. "+
                "PN draws a card at the beginning of the next turn's upkeep."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            game.doAction(new MagicChangeLifeAction(event.getPlayer(),1));
            game.doAction(new MagicAddTriggerAction(
                MagicAtUpkeepTrigger.YouDraw(
                    event.getSource(), 
                    event.getPlayer()
                )
            ));
        }
    }
]
