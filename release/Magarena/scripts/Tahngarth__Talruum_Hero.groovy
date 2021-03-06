[
    new MagicPermanentActivation(
        new MagicActivationHints(MagicTiming.Removal),
        "Damage"
    ) {

        @Override
        public Iterable<MagicEvent> getCostEvent(final MagicPermanent source) {
            return [
                new MagicTapEvent(source), new MagicPayManaCostEvent(source,  "{1}{R}")
            ];
        }

        @Override
        public MagicEvent getPermanentEvent(final MagicPermanent source, final MagicPayedCost payedCost) {
            return new MagicEvent(
                source,
                MagicTargetChoice.NEG_TARGET_CREATURE,
                new MagicDamageTargetPicker(source.getPower()),
                this,
                "SN deals damage equal to its power to target creature. " +
                "That creature deals damage equal to its power to SN."
            );
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPermanent(game, {
                final MagicPermanent target ->
                game.doAction(new MagicDealDamageAction(
                    new MagicDamage(
                        event.getPermanent(),
                        target,
                        event.getPermanent().getPower()
                    )
                ));
                game.doAction(new MagicDealDamageAction(
                    new MagicDamage(
                        target,
                        event.getPermanent(),
                        target.getPower()
                    )
                ));
            });
        }
    }
]
