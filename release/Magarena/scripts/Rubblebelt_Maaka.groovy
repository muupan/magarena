[
    new MagicBloodrushActivation(
        MagicManaCost.create("{R}"),
        "Target attacking creature\$ gets +3/+3 until end of turn."
    ) {
        @Override
        public void executeEvent(final MagicGame game,final MagicEvent event) {
            event.processTargetPermanent(game, {
                final MagicPermanent creature ->
                game.doAction(new MagicChangeTurnPTAction(creature,3,3));
            });
        }
    }
]
