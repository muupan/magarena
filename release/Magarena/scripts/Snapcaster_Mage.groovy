def A_PAYABLE_INSTANT_OR_SORCERY_CARD_FROM_YOUR_GRAVEYARD = new MagicTargetChoice(
    MagicTargetFilterFactory.PAYABLE_INSTANT_OR_SORCERY_FROM_GRAVEYARD,
    "a instant or sorcery card from your graveyard"
);

[
    new MagicWhenComesIntoPlayTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game, final MagicPermanent permanent, final MagicPayedCost payedCost) {
            return new MagicEvent(
                permanent,
                new MagicMayChoice(
                    A_PAYABLE_INSTANT_OR_SORCERY_CARD_FROM_YOUR_GRAVEYARD
                ),
                MagicGraveyardTargetPicker.PutOntoBattlefield,
                this,
                "PN may\$ cast target instant or sorcery card\$ from his or her graveyard, then exile it."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            if (event.isYes()) {
                event.processTargetCard(game, {
                    final MagicCard card ->
                    game.addEvent(new MagicPayManaCostEvent(card,card.getCost()));
                    game.addEvent(new MagicEvent(
                        card,
                        {
                            final MagicGame G, final MagicEvent E ->
                            G.doAction(new MagicRemoveCardAction(E.getCard(),MagicLocationType.Graveyard));
                            final MagicCardOnStack cardOnStack=new MagicCardOnStack(card,event.getPlayer(),game.getPayedCost());
                            cardOnStack.setMoveLocation(MagicLocationType.Exile);
                            game.doAction(new MagicPutItemOnStackAction(cardOnStack));
                        },
                        "Cast SN."
                    ));
                });
            }
        }
    }
]
