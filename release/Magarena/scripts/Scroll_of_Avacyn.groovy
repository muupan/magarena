[
    new MagicPermanentActivation(
        [MagicConditionFactory.ManaCost("{1}")],
        new MagicActivationHints(MagicTiming.Draw),
        "Draw"
    ) {
        @Override
        public MagicEvent[] getCostEvent(final MagicPermanent source) {
            return [
                new MagicPayManaCostEvent(source,"{1}"),
                new MagicSacrificeEvent(source)
            ];
        }

        @Override
        public MagicEvent getPermanentEvent(final MagicPermanent source, final MagicPayedCost payedCost) {
            return new MagicEvent(
                source,
                this,
                "PN draws a card. If he or she controls an Angel, PN gains 5 life."
            );
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            final MagicPlayer player = event.getPlayer();
            game.doAction(new MagicDrawAction(player,1));
            if (player.controlsPermanentWithSubType(MagicSubType.Angel)) {
                game.doAction(new MagicChangeLifeAction(player,5));
            }
        }
    }
]