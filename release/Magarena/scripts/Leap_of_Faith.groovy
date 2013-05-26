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
            event.processTargetPermanent(game,new MagicPermanentAction() {
                public void doAction(final MagicPermanent creature) {
                    game.doAction(new MagicSetAbilityAction(creature,MagicAbility.Flying));
                    game.doAction(new MagicChangeStateAction(
                        creature,
                        MagicPermanentState.PreventAllDamage,
                        true
                    ));
                }
            });
        }
    }
]