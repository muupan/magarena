[
    new MagicSpellCardEvent() {
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack,final MagicPayedCost payedCost) {
            return new MagicEvent(
                cardOnStack,
                MagicTargetChoice.NEG_TARGET_CREATURE,
                MagicExileTargetPicker.create(),
                this,
                "Gain control of target creature\$ until end of turn. Untap it. " +
                "It gains trample and haste until end of turn."
            );
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPermanent(game, {
                final MagicPermanent creature ->
                game.doAction(new MagicGainControlAction(event.getPlayer(),creature,MagicStatic.UntilEOT));
                game.doAction(new MagicUntapAction(creature));
                game.doAction(new MagicGainAbilityAction(creature,MagicAbility.Trample));
                game.doAction(new MagicGainAbilityAction(creature,MagicAbility.Haste));
            });
        }
    }
]
