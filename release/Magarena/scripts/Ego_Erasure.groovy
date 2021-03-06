def ST = new MagicStatic(MagicLayer.Type, MagicStatic.UntilEOT) {
    @Override
    public void modSubTypeFlags(final MagicPermanent P, final Set<MagicSubType> flags) {
        flags.removeAll(MagicSubType.ALL_CREATURES);
    }
};

[
    new MagicSpellCardEvent() {
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack,final MagicPayedCost payedCost) {
            return new MagicEvent(
                cardOnStack,
                MagicTargetChoice.NEG_TARGET_PLAYER,
                this,
                "Creatures PN controls get -2/-0 and lose all creature types until the end of the turn."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPlayer(game, {
                final MagicPlayer player ->
                final Collection<MagicPermanent> targets = game.filterPermanents(player, MagicTargetFilterFactory.CREATURE_YOU_CONTROL);
                for (final MagicPermanent creature : targets) {
                    game.doAction(new MagicChangeTurnPTAction(creature,-2,0));
                    game.doAction(new MagicBecomesCreatureAction(creature,ST));
                }
            });
        }
    }
]
