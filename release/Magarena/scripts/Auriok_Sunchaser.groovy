[
    new MagicStatic(MagicLayer.Ability) {
        @Override
        public void modAbilityFlags(final MagicPermanent source,final MagicPermanent permanent,final Set<MagicAbility> flags) {
            if (MagicCondition.METALCRAFT_CONDITION.accept(permanent)) {
                permanent.addAbility(MagicAbility.Flying, flags);
            }
        }
    },
    new MagicStatic(MagicLayer.ModPT) {
        @Override
        public void modPowerToughness(final MagicPermanent source,final MagicPermanent permanent,final MagicPowerToughness pt) {
            if (MagicCondition.METALCRAFT_CONDITION.accept(permanent)) {
                pt.add(2,2);
            }
        }
    }
]
