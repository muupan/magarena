
[
    new MagicWhenOtherDiesTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent,final MagicPermanent otherPermanent) {
            return (otherPermanent.isCreature() &&
                    otherPermanent.isFriend(permanent)) ?
                new MagicEvent(
                    permanent,
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
                game.doAction(new MagicPlayTokensAction(
                    player,
                    TokenCardDefinitions.get("1/1 colorless Spirit creature token"),
                    4
                ));
            }
        }
    }
]
