[
    new MagicPermanentActivation(
        new MagicActivationHints(MagicTiming.Token),
        "Put"
    ) {

        @Override
        public Iterable<MagicEvent> getCostEvent(final MagicPermanent source) {
            return [new MagicTapEvent(source)];
        }

        @Override
        public MagicEvent getPermanentEvent(final MagicPermanent source,final MagicPayedCost payedCost) {
            final MagicTargetChoice targetChoice = new MagicTargetChoice(
                new MagicCMCCardFilter(
                    MagicTargetFilterFactory.CREATURE_CARD_FROM_HAND,
                    Operator.EQUAL,
                    source.getCounters(MagicCounterType.Charge)
                ),
                MagicTargetHint.None,
                "a creature card from your hand"
            );
            return new MagicEvent(
                source,
                targetChoice,
                MagicGraveyardTargetPicker.PutOntoBattlefield,
                this,
                "PN puts a creature card\$ with converted mana cost equal to " +
                "the number of charge counters on SN from his or her hand onto the battlefield."
            );
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetCard(game, {
                final MagicCard card ->
                game.doAction(new MagicRemoveCardAction(card,MagicLocationType.OwnersHand));
                game.doAction(new MagicPlayCardAction(card,event.getPlayer()));
            });
        }
    }
]
