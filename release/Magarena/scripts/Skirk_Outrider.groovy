[
    new MagicStatic(MagicLayer.ModPT) {
        @Override
        public void modPowerToughness(final MagicPermanent source,final MagicPermanent permanent,final MagicPowerToughness pt) {
            if (source.getController().controlsPermanent(MagicSubType.Beast)) {
                pt.add(2,2);
            }
        }
    },

    new MagicStatic(MagicLayer.Ability) {
        @Override
        public void modAbilityFlags(final MagicPermanent source,final MagicPermanent permanent,final Set<MagicAbility> flags) {
            if (source.getController().controlsPermanent(MagicSubType.Beast)) {
                permanent.addAbility(MagicAbility.Trample, flags);
            }
        }
    }
]
