[
    new MagicSpellCardEvent() {
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack, final MagicPayedCost payedCost) {
            return new MagicEvent(
                cardOnStack,
                this,
                "PN gains 2 life for each card in his or her " +
                "graveyard, then shuffles his or her graveyard into " +
                "his or her library."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            final MagicPlayer player = event.getPlayer();
            final MagicCardList graveyard = new MagicCardList(player.getGraveyard());
            game.doAction(new MagicChangeLifeAction(player,2 * graveyard.size()));
            for (final MagicCard card : graveyard) {
                game.doAction(new MagicRemoveCardAction(
                    card,
                    MagicLocationType.Graveyard
                ));
                game.doAction(new MagicMoveCardAction(
                    card,
                    MagicLocationType.Graveyard,
                    MagicLocationType.OwnersLibrary
                ));
            }
        }
    }
]
