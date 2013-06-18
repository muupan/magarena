[
    new MagicSpellCardEvent() {
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack,final MagicPayedCost payedCost) {
            return new MagicEvent(
                cardOnStack,
                new MagicKickerChoice(
                    MagicTargetChoice.TARGET_NONLAND_PERMANENT,
                    MagicManaCost.create("{1}{U}")
                ),
                MagicBounceTargetPicker.getInstance(),
                this,
                "Return target nonland permanent\$ to its owner's hand. If SN was kicked\$, draw a card."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPermanent(game,new MagicPermanentAction() {
                public void doAction(final MagicPermanent permanent) {
                    game.doAction(new MagicRemoveFromPlayAction(permanent,MagicLocationType.OwnersHand));
                    if (event.isKicked()) {
                        game.doAction(new MagicDrawAction(event.getPlayer(),1));
                    }
                }
            });
        }
    }
]
