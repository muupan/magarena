[
    new MagicPermanentActivation(
        new MagicActivationHints(MagicTiming.Main),
        "Card"
    ) {
        @Override
        public Iterable<MagicEvent> getCostEvent(final MagicPermanent source) {
            return [
                new MagicPayManaCostEvent(source,"{B}")
            ];
        }
        @Override
        public MagicEvent getPermanentEvent(final MagicPermanent source,final MagicPayedCost payedCost) {
            return new MagicEvent(
                source,
                MagicTargetChoice.TARGET_CREATURE_CARD_FROM_GRAVEYARD,
                MagicGraveyardTargetPicker.ReturnToHand,
                this,
                "Put target creature card\$ from your graveyard on top of your library"
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetCard(game, {
                final MagicCard targetCard ->
                game.doAction(new MagicRemoveCardAction(targetCard,MagicLocationType.Graveyard));
                game.doAction(new MagicMoveCardAction(
                    targetCard,
                    MagicLocationType.Graveyard,
                    MagicLocationType.TopOfOwnersLibrary
                ));
            });
        }
    }
]