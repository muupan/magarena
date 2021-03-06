def DrawPump = new MagicWhenOtherDrawnTrigger() {
    @Override
    public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent,final MagicCard card) {
        return permanent.isFriend(card) ?
            new MagicEvent(
                permanent,
                this,
                "SN gets +1/+1 and gains flying until end of turn."
            ):
            MagicEvent.NONE;
    }
    @Override
    public void executeEvent(final MagicGame game, final MagicEvent event) {
        game.doAction(new MagicChangeTurnPTAction(event.getPermanent(),1,1));
        game.doAction(new MagicGainAbilityAction(event.getPermanent(),MagicAbility.Flying));
    }
};

[
    new MagicStatic(MagicLayer.Ability) {
        @Override
        public void modAbilityFlags(final MagicPermanent source, final MagicPermanent permanent, final Set<MagicAbility> flags) {
            permanent.addAbility(DrawPump);
        }
        @Override
        public boolean accept(final MagicGame game,final MagicPermanent source,final MagicPermanent target) { 
            return MagicStatic.acceptLinked(game, source, target);
        }
    },
    new MagicWhenOtherComesIntoPlayTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent,final MagicPermanent otherPermanent) {
            return (otherPermanent.isCreature() &&
                    otherPermanent.hasSubType(MagicSubType.Wizard)) ?
                new MagicEvent(
                    permanent,
                    new MagicMayChoice(),
                    otherPermanent,
                    this,
                    "You may\$ attach SN to RN."
                ) :
                MagicEvent.NONE;
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            if (event.isYes()) {
                game.doAction(new MagicAttachAction(
                    event.getPermanent(),
                    event.getRefPermanent()
                ));
            }
        }
    }
]
