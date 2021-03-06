[
    new MagicSpellCardEvent() {
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack,final MagicPayedCost payedCost) {
            return new MagicEvent(
                cardOnStack,
                MagicTargetChoice.TARGET_CREATURE_YOUR_OPPONENT_CONTROLS,
                MagicPowerTargetPicker.create(),
                this,
                "Target creature\$ an opponent controls deals damage equal to " +
                "its power to each other creature that player controls, then " +
                "each of those creatures deals damage equal to its power to that creature."
            );
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPermanent(game, {
                final MagicPermanent permanent ->
                final MagicPlayer player = event.getPlayer();
                final Collection<MagicPermanent> creatures =
                        game.filterPermanents(player.getOpponent(),MagicTargetFilterFactory.CREATURE_YOU_CONTROL);
                for (final MagicPermanent creature : creatures) {
                    final MagicDamage damage = new MagicDamage(
                        permanent,
                        creature,
                        permanent.getPower()
                    );
                    game.doAction(new MagicDealDamageAction(damage));
                }
                for (final MagicPermanent creature : creatures) {
                    final MagicDamage damage = new MagicDamage(
                        creature,
                        permanent,
                        creature.getPower()
                    );
                    game.doAction(new MagicDealDamageAction(damage));
                }
            });
        }
    }
]
