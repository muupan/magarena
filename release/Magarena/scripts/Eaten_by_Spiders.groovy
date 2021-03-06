[
    new MagicSpellCardEvent() {
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack, final MagicPayedCost payedCost) {
            return new MagicEvent(
                cardOnStack,
                MagicTargetChoice.NEG_TARGET_CREATURE_WITH_FLYING,
                MagicDestroyTargetPicker.Destroy,
                this,
                "Destroy target creature\$ with flying and " +
                "all Equipment attached to that creature."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPermanent(game, {
                final MagicPermanent creature ->
                final MagicPermanentList perms = new MagicPermanentList();
                perms.add(creature);
                perms.addAll(creature.getEquipmentPermanents());
                game.doAction(new MagicDestroyAction(perms));
            });
        }
    }
]
