[
    new MagicWhenOtherSpellIsCastTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent,final MagicCardOnStack spell) {
            return (spell.hasColor(MagicColor.Red)) ?
                new MagicEvent(
                    permanent,
                    new MagicMayChoice(
                        new MagicPayManaCostChoice(MagicManaCost.create("{1}")),
                        MagicTargetChoice.NEG_TARGET_CREATURE
                    ),
                    this,
                    "PN may\$ pay {1}\$. If PN does, target creature\$ can't block this turn."
                ):
                MagicEvent.NONE;
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            if (event.isYes()) {
                event.processTarget(game, {
                    final MagicPermanent target ->
                    game.doAction(new MagicGainAbilityAction(target,MagicAbility.CannotBlock));
                });
            }
        }
    }
]