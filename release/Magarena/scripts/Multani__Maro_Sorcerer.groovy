[
    new MagicCDA() {
        @Override
        public void modPowerToughness(final MagicGame game,final MagicPlayer player,final MagicPowerToughness pt) {
            final int amount = player.getHandSize() + player.getOpponent().getHandSize();
            pt.set(amount,amount);
        }
    }
]
