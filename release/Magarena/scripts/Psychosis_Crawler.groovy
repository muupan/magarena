[
    new MagicWhenOtherDrawnTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent,final MagicCard card) {
            return permanent.isFriend(card) ?
                new MagicEvent(
                    permanent,
                    this,
                    "Each opponent ("+permanent.getController().getOpponent().getName()+") loses 1 life."
                ):
                MagicEvent.NONE;
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            game.doAction(new MagicChangeLifeAction(event.getPlayer().getOpponent(),-1));
        }
    }
]
