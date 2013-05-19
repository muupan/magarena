[
    new MagicWhenAttacksTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent,final MagicPermanent creature) {
            return (permanent == creature) ?
                new MagicEvent(
                    permanent,
                    this,
                    "PN puts a 4/4 white Angel creature token with " +
                    "flying onto the battlefield tapped and attacking. " +
                    "Exile that token at end of combat."
                ):
                MagicEvent.NONE;
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            final MagicPlayer player = event.getPlayer();
            final MagicCard card = MagicCard.createTokenCard(TokenCardDefinitions.get("Angel4"),player);
            final MagicPlayCardAction action = new MagicPlayCardAction(card,player,MagicPlayCardAction.TAPPED_ATTACKING);
            game.doAction(action);
            game.doAction(new MagicChangeStateAction(action.getPermanent(),MagicPermanentState.ExileAtEndOfCombat,true));
        }
    }
]