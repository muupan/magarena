[
    new MagicPermanentActivation(
        new MagicActivationHints(MagicTiming.Removal),
        "Bounce"
    ) {
        @Override
        public Iterable<MagicEvent> getCostEvent(final MagicPermanent source) {
            return [
                new MagicPayManaCostEvent(source,"{W}")
            ];
        }
        @Override
        public MagicEvent getPermanentEvent(final MagicPermanent source, final MagicPayedCost payedCost) {
            return new MagicEvent(
                source,
                source.getEnchantedPermanent(),
                this,
                "Return RN to its owner's hand."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            game.doAction(new MagicRemoveFromPlayAction(event.getRefPermanent(),MagicLocationType.OwnersHand));
        }
    }
]
