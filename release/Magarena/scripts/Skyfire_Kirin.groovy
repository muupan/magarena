[
    new MagicWhenYouCastSpiritOrArcaneTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent,final MagicCardOnStack spell) {
            final int cmc = spell.getConvertedCost();
            final MagicTargetChoice choice = new MagicTargetChoice(
                new MagicCMCPermanentFilter(
                    MagicTargetFilterFactory.CREATURE,
                    Operator.EQUAL,
                    cmc
                ),
                MagicTargetHint.Negative,
                "target creature with converted mana cost " + cmc
            );
            return new MagicEvent(
                permanent,
                new MagicMayChoice(MagicTargetChoice.NEG_TARGET_CREATURE),
                MagicExileTargetPicker.create(),
                cmc,
                this,
                "PN may\$ gain control of target creature\$ with converted mana cost RN until end of turn."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            if (event.isYes()) {
                event.processTargetPermanent(game, {
                    final MagicPermanent creature ->
                    game.doAction(new MagicGainControlAction(
                        event.getPlayer(),
                        creature,
                        MagicStatic.UntilEOT
                    ));
                });
            }
        }
    }
]
