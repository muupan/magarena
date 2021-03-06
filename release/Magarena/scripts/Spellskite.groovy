def action = {
    final MagicGame game, final MagicEvent event ->
    event.processTargetItemOnStack(game, {
        final MagicItemOnStack item ->
        game.doAction(new MagicChangeTargetAction(item, event.getPermanent()));
    });
}

def event = {
    final MagicPermanent source ->
    return new MagicEvent(
        source,
        new MagicTargetChoice(
            MagicTargetFilterFactory.SPELL_OR_ABILITY_THAT_TARGETS_PERMANENTS, 
            MagicTargetHint.Negative, 
            "target spell or ability"
        ), 
        action,
        "Change the target of target spell or ability\$ to SN."
    );
}

[
    new MagicPermanentActivation(
        new MagicActivationHints(MagicTiming.Spell),
        "Retarget"
    ) {

        @Override
        public Iterable<MagicEvent> getCostEvent(final MagicPermanent source) {
            return [new MagicPayManaCostEvent(source, "{U}")];
        }

        @Override
        public MagicEvent getPermanentEvent(final MagicPermanent source,final MagicPayedCost payedCost) {
            return event(source);
        }
    },
    new MagicPermanentActivation(
        new MagicActivationHints(MagicTiming.Spell),
        "Pay 2 life"
    ) {

        @Override
        public Iterable<MagicEvent> getCostEvent(final MagicPermanent source) {
            return [new MagicPayLifeEvent(source, 2)];
        }

        @Override
        public MagicEvent getPermanentEvent(final MagicPermanent source,final MagicPayedCost payedCost) {
            return event(source);
        }
    }
]
