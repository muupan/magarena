[
    new MagicStatic(MagicLayer.ModPT) {
        @Override
        public void modPowerToughness(
                final MagicPermanent source,
                final MagicPermanent permanent,
                final MagicPowerToughness pt) {
            if (MagicCondition.THRESHOLD_CONDITION.accept(permanent)) {
                pt.add(2,2);
            }
        }
    },

    new MagicStatic(MagicLayer.Ability) {
        @Override
        public void modAbilityFlags(
                final MagicPermanent source,
                final MagicPermanent permanent,
                final Set<MagicAbility> flags) {
            if (MagicCondition.THRESHOLD_CONDITION.accept(permanent)) {
                permanent.addAbility(MagicAbility.SacWhenTargeted, flags);
            }
        }
    }
]
