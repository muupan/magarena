[
    new MagicPermanentActivation(
        MagicActivation.NO_COND,
        new MagicActivationHints(MagicTiming.Removal),
        "Destroy"
    ) {

        @Override
        public MagicEvent[] getCostEvent(final MagicPermanent source) {
            return [new MagicSacrificeEvent(source)];
        }

        @Override
        public MagicEvent getPermanentEvent(final MagicPermanent source,final MagicPayedCost payedCost) {
            return new MagicEvent(
                source,
                MagicTargetChoice.NEG_TARGET_NONBLACK_CREATURE,
                new MagicDestroyTargetPicker(true),
                this,
                "Destroy target nonblack creature\$. It can't be regenerated."
            );
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPermanent(game,new MagicPermanentAction() {
                public void doAction(final MagicPermanent creature) {
                    game.doAction(new MagicChangeStateAction(creature,MagicPermanentState.CannotBeRegenerated,true));
                    game.doAction(new MagicDestroyAction(creature));
                }
            });
        }
    }
]