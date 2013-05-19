[
    new MagicWhenOtherPutIntoGraveyardFromPlayTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent,final MagicPermanent otherPermanent) {
            final MagicPlayer player=permanent.getController();
            return (otherPermanent.isCreature()&&otherPermanent.getController()==player) ?
                new MagicEvent(
                    permanent,
                    player,
                    this,
                    "Sacrifice SN. If you do, " + 
                    "put four 1/1 colorless Spirit creature tokens onto the battlefield."
                ):
                MagicEvent.NONE;
        }
        
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            final MagicPermanent permanent=event.getPermanent();
            final MagicPlayer player=event.getPlayer();
            if (player.controlsPermanent(permanent)) {
                game.doAction(new MagicSacrificeAction(permanent));
                game.doAction(new MagicPlayTokensAction(player,TokenCardDefinitions.get("Spirit1"),4));
            }
        }
    }
]