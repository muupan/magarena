[
    new MagicStatic(
        MagicLayer.Ability,
        MagicTargetFilterFactory.CREATURE
    ) {
        @Override
        public void modAbilityFlags(
                final MagicPermanent source,
                final MagicPermanent permanent,
                final Set<MagicAbility> flags) {
            permanent.addAbility(MagicAbility.CannotAttackOrBlock, flags);
        }
        @Override
        public boolean condition(
                final MagicGame game,
                final MagicPermanent source,
                final MagicPermanent target) {
            return target.getCounters(MagicCounterType.Bribery) > 0;
        }
    },
    new MagicPermanentActivation(
        new MagicActivationHints(MagicTiming.Tapping),
        "Bribe"
    ) {

        @Override
        public Iterable<MagicEvent> getCostEvent(final MagicPermanent source) {
            return [
                new MagicTapEvent(source), new MagicPayManaCostEvent(source, "{W}{U}")
            ];
        }

        @Override
        public MagicEvent getPermanentEvent(final MagicPermanent source, final MagicPayedCost payedCost) {
            return new MagicEvent(
                source,
                MagicTargetChoice.TARGET_CREATURE_YOUR_OPPONENT_CONTROLS,
                new MagicNoCombatTargetPicker(true,true,true),
                this,
                "PN puts a bribery counter on target creature\$. " +
                "Its controller draws a card."
            );
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPermanent(game, {
                final MagicPermanent creature ->
                game.doAction(new MagicChangeCountersAction(
                    creature,
                    MagicCounterType.Bribery,
                    1,
                    true
                ));
                game.doAction(new MagicDrawAction(creature.getController()));
            });
        }
    }
]
