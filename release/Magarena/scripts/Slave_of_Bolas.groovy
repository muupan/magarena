[
    new MagicSpellCardEvent() {
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack,final MagicPayedCost payedCost) {
            return new MagicEvent(
                cardOnStack,
                MagicTargetChoice.NEG_TARGET_CREATURE,
                MagicExileTargetPicker.create(),
                this,
                "Gain control of target creature\$. Untap that creature. " + 
                "It gains haste until end of turn. Sacrifice it at end of turn."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPermanent(game,new MagicPermanentAction() {
                public void doAction(final MagicPermanent creature) {
                    game.doAction(new MagicGainControlAction(event.getPlayer(),creature));
                    game.doAction(new MagicUntapAction(creature));
                    game.doAction(new MagicSetAbilityAction(creature,MagicAbility.Haste));
                    game.doAction(new MagicChangeStateAction(creature,MagicPermanentState.SacrificeAtEndOfTurn,true));
                }
            });
        }
    }
]