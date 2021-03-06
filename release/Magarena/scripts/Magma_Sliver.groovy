
def MagmaPump = new MagicPermanentActivation(
    new MagicActivationHints(MagicTiming.Pump),
    "Pump"
) {
    @Override
    public Iterable<MagicEvent> getCostEvent(final MagicPermanent source) {
        return [
            new MagicTapEvent(source)
        ];
    }
    @Override
    public MagicEvent getPermanentEvent(final MagicPermanent source,final MagicPayedCost payedCost) {
        return new MagicEvent(
            source,
            MagicTargetChoice.Positive("target Sliver creature"),
            MagicPumpTargetPicker.create(),
            this,
            "Target Sliver creature gets +X/+0 until end of turn, " +
            "where X is the number of Slivers on the battlefield\$."
        );
    }
    @Override
    public void executeEvent(final MagicGame game, final MagicEvent event) {
        event.processTargetPermanent(game, {
            final MagicPermanent creature ->
            final int amount = creature.getController().getNrOfPermanents(MagicSubType.Sliver) +
                               creature.getOpponent().getNrOfPermanents(MagicSubType.Sliver);
            game.doAction(new MagicChangeTurnPTAction(creature,amount,0));
        });
    }
};

[
    new MagicStatic(
        MagicLayer.Ability,
        MagicTargetFilterFactory.SLIVER
    ) {
        @Override
        public void modAbilityFlags(final MagicPermanent source,final MagicPermanent permanent,final Set<MagicAbility> flags) {
            permanent.addAbility(MagmaPump);
        }
    }
]
