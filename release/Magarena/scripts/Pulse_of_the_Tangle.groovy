[
    new MagicSpellCardEvent() {
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack,final MagicPayedCost payedCost) {
            return new MagicEvent(
                cardOnStack,
                this,
                "PN puts a 3/3 green Beast creature token onto the battlefield. " + 
                "Then if your opponent controls more creatures than you, return SN to its owner's hand."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            final MagicPlayer player=event.getPlayer();
            game.doAction(new MagicPlayTokenAction(player,TokenCardDefinitions.get("Beast3")));
            final boolean more = player.getOpponent().getNrOfPermanentsWithType(MagicType.Creature) >
                                 player.getNrOfPermanentsWithType(MagicType.Creature);
            if (more) {
                game.doAction(new MagicChangeCardDestinationAction(event.getCardOnStack(), MagicLocationType.OwnersHand));
            }
        }
    }
]