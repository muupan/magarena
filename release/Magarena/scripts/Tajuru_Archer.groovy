[
    new MagicWhenOtherComesIntoPlayTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent,final MagicPermanent other) {
            final int numAllies = permanent.getController().getNrOfPermanents(MagicSubType.Ally);
            return (other.isFriend(permanent) &&
                    other.hasSubType(MagicSubType.Ally)) ?
                new MagicEvent(
                    permanent,
                    new MagicMayChoice(
                        MagicTargetChoice.NEG_TARGET_CREATURE_WITH_FLYING
                    ),
                    // estimated. Amount of damage can be different on resolution
                    new MagicDamageTargetPicker(numAllies),
                    this,
                    "PN may\$ have SN deal " +
                    "damage to target creature with flying\$ equal to " +
                    "the number of Allies he or she controls."
                ) :
                MagicEvent.NONE;
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            if (event.isYes()) {
                event.processTargetPermanent(game, {
                    final MagicPermanent creature ->
                    final MagicPlayer player = event.getPlayer();
                    final int amount =
                            player.getNrOfPermanents(MagicSubType.Ally);
                    final MagicDamage damage = new MagicDamage(
                        event.getPermanent(),
                        creature,
                        amount
                    );
                    game.doAction(new MagicDealDamageAction(damage));
                });
            }
        }
    }
]
