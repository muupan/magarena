[
    new MagicSpellCardEvent() {
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack,final MagicPayedCost payedCost) {
            return new MagicEvent(
                cardOnStack,
                MagicTargetChoice.TARGET_CREATURE_CARD_FROM_GRAVEYARD,
                MagicGraveyardTargetPicker.ReturnToHand,
                this,
                "Return target creature card\$ from your graveyard to your hand. Draw a card."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetCard(game, {
                final MagicCard targetCard ->
                game.doAction(new MagicRemoveCardAction(targetCard,MagicLocationType.Graveyard));
                game.doAction(new MagicMoveCardAction(targetCard,MagicLocationType.Graveyard,MagicLocationType.OwnersHand));
                game.doAction(new MagicDrawAction(event.getPlayer()));
            });
        }
    }
]
