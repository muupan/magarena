[
    new MagicStatic(MagicLayer.ModPT) {
        @Override
        public void modPowerToughness(
                final MagicPermanent source,
                final MagicPermanent permanent,
                final MagicPowerToughness pt) {
            if (permanent.isEnchanted()) {
                pt.add(
                    permanent.getAuraPermanents().size(),
                    permanent.getAuraPermanents().size()
                );
            }
        }
    },
    new MagicPermanentActivation(
        new MagicActivationHints(MagicTiming.Pump),
        "Regen"
    ) {
        @Override
        public Iterable<MagicEvent> getCostEvent(final MagicPermanent source) {
            return [
                new MagicSacrificePermanentEvent(source,MagicTargetChoice.SACRIFICE_AURA),
                new MagicRegenerationConditionsEvent(source,this)
            ];
        }
        @Override
        public MagicEvent getPermanentEvent(final MagicPermanent source,final MagicPayedCost payedCost) {
            return new MagicEvent(
                source,
                this,
                "Regenerate SN."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            game.doAction(new MagicRegenerateAction(event.getPermanent()));
        }
    }
]