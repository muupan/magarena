[
    new MagicWhenTargetedTrigger() {
        @Override
        public MagicEvent executeTrigger(
                final MagicGame game,
                final MagicPermanent permanent,
                final MagicItemOnStack itemOnStack) {
            if (!itemOnStack.isInstantOrSorcerySpell()) {
                return MagicEvent.NONE;
            }

            final Collection<MagicPermanent> targets = game.filterPermanents(
                    permanent.getController(),
                    MagicTargetFilterFactory.CREATURE_YOU_CONTROL);
            MagicPermanent targetedPerm = MagicPermanent.NONE;
            for (final MagicPermanent perm : targets) {
                if (itemOnStack.containsInChoiceResults(perm) &&
                    perm.isCreature() &&
                    perm.getController() == permanent.getController()) {
                    targetedPerm = perm;
                    break;
                }
            }
            return (targetedPerm != MagicPermanent.NONE) ?
                new MagicEvent(
                    permanent,
                    permanent.getController(),
                    targetedPerm,
                    this,
                    "RN gets +3/+3 until end of turn."
                ):
                MagicEvent.NONE;
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            game.doAction(new MagicChangeTurnPTAction(event.getRefPermanent(),3,3));
        }
    }
]
