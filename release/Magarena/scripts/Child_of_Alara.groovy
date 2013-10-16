[
    new MagicWhenDiesTrigger() {
        @Override
        public MagicEvent getEvent(final MagicPermanent permanent) {
            return new MagicEvent(
                permanent,
                this,
                "Destroy all nonland permanents. They can't be regenerated."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            final Collection<MagicPermanent> targets =
                game.filterPermanents(event.getPlayer(),MagicTargetFilter.TARGET_NONLAND_PERMANENT);
            for (final MagicPermanent target : targets) {
                game.doAction(MagicChangeStateAction.Set(target,MagicPermanentState.CannotBeRegenerated));
            }
            game.doAction(new MagicDestroyAction(targets));
        }
    }
]