[
    new MagicSpellCardEvent() {
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack, final MagicPayedCost payedCost) {
            return new MagicEvent(
                cardOnStack,
                MagicTargetChoice.POS_TARGET_CREATURE,
                MagicFlyingTargetPicker.create(),
                this,
                "Target creature\$ gains flying until end of turn. " +
                "Prevent all damage that would be dealt to that creature this turn."
            );
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPermanent(game, {
                final MagicPermanent creature ->
                game.doAction(new MagicGainAbilityAction(creature,MagicAbility.Flying));
                game.doAction(new MagicAddTurnTriggerAction(
                    creature,
                    MagicIfDamageWouldBeDealtTrigger.PreventDamageDealtTo
                ));
            });
        }
    }
]
