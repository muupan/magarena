[
    new MagicSpellCardEvent() {
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack,final MagicPayedCost payedCost) {
            return new MagicEvent(
                cardOnStack,
                MagicTargetChoice.NEG_TARGET_CARD_FROM_ALL_GRAVEYARDS,
                MagicGraveyardTargetPicker.ExileOpp,
                this,
                "Exile target card\$ from a graveyard. " +
                "PN draws a card at the beginning of the next turn's upkeep."
            );
        }
        
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetCard(game, {
                final MagicCard card ->
                game.doAction(new MagicRemoveCardAction(
                    card,
                    MagicLocationType.Graveyard
                ));
                game.doAction(new MagicMoveCardAction(
                    card,
                    MagicLocationType.Graveyard,
                    MagicLocationType.Exile
                ));
                game.doAction(new MagicAddTriggerAction(
                    MagicAtUpkeepTrigger.YouDraw(
                        event.getSource(), 
                        event.getPlayer()
                    )
                ));
            });
        }
    }
]
