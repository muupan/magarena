[
    new MagicWhenLifeIsGainedTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game, final MagicPermanent permanent, final MagicLifeChangeTriggerData lifeChange) {
            return permanent.isController(lifeChange.player) ?
                new MagicEvent(
                    permanent,
                    this,
                    "PN puts a +1/+1 counter on each creature he or she controls."
                ) :
                MagicEvent.NONE;
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            final Collection<MagicPermanent> targets = game.filterPermanents(
                event.getPlayer(),
                MagicTargetFilterFactory.CREATURE_YOU_CONTROL
            );
            for (final MagicPermanent target : targets) {
                game.doAction(new MagicChangeCountersAction(
                    target,
                    MagicCounterType.PlusOne,
                    1,
                    true
                ));
            }
        }
    }
]
