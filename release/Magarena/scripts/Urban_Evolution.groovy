[
    new MagicSpellCardEvent() {
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack,final MagicPayedCost payedCost) {
            return new MagicEvent(
                cardOnStack,
                this,
                "Draw three cards. PN may play an additional land this turn."
            );
        }

        @Override
        public void executeEvent(final MagicGame outerGame, final MagicEvent event) {
            outerGame.doAction(new MagicAddStaticAction(MagicPermanent.NONE,
                new MagicStatic(MagicLayer.Game, MagicStatic.UntilEOT) {
                    @Override
                    public void modGame(final MagicPermanent source, final MagicGame game) {
                        game.incMaxLand();
                    }
                    @Override
                    public boolean condition(final MagicGame game,final MagicPermanent source,final MagicPermanent target) {
                        return game.getTurnPlayer() == event.getPlayer();
                    }
                }
            ));
            outerGame.doAction(new MagicDrawAction(event.getPlayer(),3));
        }
    }
]
