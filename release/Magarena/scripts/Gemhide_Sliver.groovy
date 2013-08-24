def TapAddAny = new MagicTapManaActivation(MagicManaType.ALL_TYPES);

[
    new MagicStatic(
        MagicLayer.Ability,
        MagicTargetFilter.TARGET_SLIVER
    ) {
        @Override
        public void modAbilityFlags(final MagicPermanent source,final MagicPermanent permanent,final Set<MagicAbility> flags) {
            permanent.addAbility(TapAddAny);
        }
    }
]