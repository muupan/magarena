[
    new MagicWhenComesIntoPlayTrigger() {
        @Override
        public MagicEvent executeTrigger(
                final MagicGame game,
                final MagicPermanent permanent,
                final MagicPlayer player) {
            return new MagicEvent(
                permanent,
                MagicTargetChoice.TARGET_CREATURE_CARD_FROM_GRAVEYARD,
                new MagicGraveyardTargetPicker(true),
                this,
                "Return target creature card\$ from " +
                "your graveyard to the battlefield."
            );
        }
        
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetCard(game,new MagicCardAction() {
                public void doAction(final MagicCard targetCard) {
                    game.doAction(new MagicReanimateAction(
                        event.getPlayer(),
                        targetCard,
                        MagicPlayCardAction.NONE
                    ));
                }
            });
        }
    }
]