[
    new MagicSpellCardEvent() {
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack,final MagicPayedCost payedCost) {
            return new MagicEvent(
                cardOnStack,
                MagicTargetChoice.POS_TARGET_CREATURE,
                MagicShadowTargetPicker.create(),
                this,
                "Target creature\$ can't be blocked this turn. PN draws a card."
            );
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPermanent(game, {
                final MagicPermanent creature ->
                game.doAction(new MagicGainAbilityAction(creature,MagicAbility.Unblockable));
                game.doAction(new MagicDrawAction(event.getPlayer()));
            });
        }
    }
]
