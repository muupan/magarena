[    
    new MagicStatic(
        MagicLayer.Ability,
        MagicTargetFilterFactory.KALDRA_EQUIPMENT
    ) {
        @Override
        public void modAbilityFlags(final MagicPermanent source,final MagicPermanent permanent,final Set<MagicAbility> flags) {
            permanent.addAbility(MagicAbility.Indestructible, flags);
        }
    }
]
