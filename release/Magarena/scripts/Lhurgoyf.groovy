[
    new MagicCDA() {
        @Override
        public void modPowerToughness(final MagicGame game,final MagicPlayer player,final MagicPowerToughness pt) {
            final int size = game.filterCards(player,MagicTargetFilterFactory.CREATURE_CARD_FROM_ALL_GRAVEYARDS).size();
            pt.set(size, size + 1);
        }
    }
]
