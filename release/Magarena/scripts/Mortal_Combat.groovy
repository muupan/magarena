[
    new MagicAtUpkeepTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent,final MagicPlayer upkeepPlayer) {
            return permanent.isController(upkeepPlayer) && game.filterCards(upkeepPlayer,MagicTargetFilterFactory.CREATURE_CARD_FROM_GRAVEYARD).size() >= 20 ?
                new MagicEvent(
                    permanent,
                    this,
                    "PN wins the game."
                ):
                MagicEvent.NONE;
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            game.doAction(new MagicLoseGameAction(event.getPermanent().getController().getOpponent()));
        };
    }
]
