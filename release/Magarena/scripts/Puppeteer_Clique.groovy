[
    new MagicWhenComesIntoPlayTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent, final MagicPlayer player) {
            return new MagicEvent(
                permanent,
                MagicTargetChoice.TARGET_CREATURE_CARD_FROM_OPPONENTS_GRAVEYARD,
                new MagicGraveyardTargetPicker(true),
                this,
                "Put target creature card\$ in an opponent's graveyard onto the battlefield under your control. "+
                "It has haste. At the end of your turn, exile it."
            );
        }
        
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetCard(game,new MagicCardAction() {
                public void doAction(final MagicCard card) {
                    game.doAction(new MagicReanimateAction(
                        event.getPlayer(),
                        card,
                        MagicPlayCardAction.HASTE_REMOVE_AT_END_OF_YOUR_TURN
                    ));
                }
            });
        }
    }
]