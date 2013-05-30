[
    new MagicPermanentActivation(
        [
            MagicCondition.CAN_TAP_CONDITION,
            MagicConditionFactory.ManaCost("{2}")
        ],
        new MagicActivationHints(MagicTiming.Token),
        "Put"
    ) {
        @Override
        public MagicEvent[] getCostEvent(final MagicPermanent source) {
            return [
                new MagicTapEvent(source),
                new MagicPayManaCostEvent(source,"{2}")
            ];
        }

        @Override
        public MagicEvent getPermanentEvent(final MagicPermanent source, final MagicPayedCost payedCost) {
            return new MagicEvent(
                source,
                MagicTargetChoice.TARGET_MULTICOLOR_CREATURE_CARD_FROM_HAND,
                new MagicGraveyardTargetPicker(true),
                this,
                "Put a multicolored creature card\$ from your hand onto the battlefield."
            );
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetCard(game,new MagicCardAction() {
                public void doAction(final MagicCard card) {
                    game.doAction(new MagicRemoveCardAction(card,MagicLocationType.OwnersHand));
                    game.doAction(new MagicPlayCardAction(card,event.getPlayer(),MagicPlayCardAction.NONE));
                }
            });
        }
    }
]