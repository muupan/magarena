[
    new MagicPermanentActivation(
        new MagicActivationHints(MagicTiming.Removal),
        "Damage"
    ) {

        @Override
        public Iterable<MagicEvent> getCostEvent(final MagicPermanent source) {
            return [
                new MagicPayManaCostEvent(source, "{R}"),
                new MagicTapEvent(source)
            ];
        }

        @Override
        public MagicEvent getPermanentEvent(final MagicPermanent source,final MagicPayedCost payedCost) {
            return new MagicEvent(
                source,
                MagicTargetChoice.NEG_TARGET_CREATURE_OR_PLAYER,
                new MagicDamageTargetPicker(1),
                this,
                "SN deals 1 damage to target creature or player\$ and 1 damage to PN."
            );
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            final MagicSource source=event.getSource();
            event.processTarget(game, {
                final MagicTarget target ->
                final MagicDamage damage1=new MagicDamage(source,target,1);
                game.doAction(new MagicDealDamageAction(damage1));
                final MagicDamage damage2=new MagicDamage(source,event.getPlayer(),1);
                game.doAction(new MagicDealDamageAction(damage2));
            });
        }
    }
]
