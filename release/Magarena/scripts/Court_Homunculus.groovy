[
    new MagicStatic(MagicLayer.ModPT) {
        @Override
        public void modPowerToughness(final MagicPermanent source,final MagicPermanent permanent,final MagicPowerToughness pt) {
            final MagicPermanentFilterImpl filter = new MagicOtherPermanentTargetFilter(MagicTargetFilterFactory.ARTIFACT, source);
            if (source.getController().controlsPermanent(filter)) {
                pt.add(1,1);
            }
        }
    }
]
