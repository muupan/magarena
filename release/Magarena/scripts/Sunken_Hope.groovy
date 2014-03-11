[
    new MagicAtUpkeepTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game, final MagicPermanent permanent, final MagicPlayer upkeepPlayer) {
            return new MagicEvent(
                permanent,
                upkeepPlayer,
                MagicTargetChoice.CREATURE_YOU_CONTROL,
                MagicBounceTargetPicker.create(),
                this,
                "PN returns a creature PN controls\$ to its owner's hand."
            );
        }
        @Override
        public void executeEvent(final MagicGame game,final MagicEvent event) {
            event.processTargetPermanent(game, {
                final MagicPermanent target ->
                game.doAction(new MagicRemoveFromPlayAction(target,MagicLocationType.OwnersHand));
            });
        }
    }
]
