[
    new MagicWhenAttacksTrigger() {
        @Override
        public MagicEvent executeTrigger(
                final MagicGame game,
                final MagicPermanent permanent,
                final MagicPermanent attacker) {
            return (permanent==attacker) ?
                new MagicEvent(
                    permanent,
                    this,
                    "Attacking creatures gain first strike until end of turn."
                ):
                MagicEvent.NONE;
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            final Collection<MagicPermanent> targets = game.filterPermanents(
                    event.getPlayer(),
                    MagicTargetFilterFactory.ATTACKING_CREATURE);
            for (final MagicPermanent target : targets) {
                game.doAction(new MagicGainAbilityAction(
                    target,
                    MagicAbility.FirstStrike
                ));
            }
        }
    }
]
