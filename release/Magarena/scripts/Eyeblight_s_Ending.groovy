def NON_ELF_CREATURE = new MagicPermanentFilterImpl() {
    public boolean accept(final MagicGame game,final MagicPlayer player,final MagicPermanent target) {
        return target.isCreature() &&
               !target.hasSubType(MagicSubType.Elf);
    }
};

def TARGET_NON_ELF_CREATURE = new MagicTargetChoice(
    NON_ELF_CREATURE,
    MagicTargetHint.Negative,
    "target non-Elf creature"
);

[
    new MagicSpellCardEvent() {
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack,final MagicPayedCost payedCost) {
            return new MagicEvent(
                cardOnStack,
                TARGET_NON_ELF_CREATURE,
                MagicDestroyTargetPicker.Destroy,
                this,
                "Destroy target non-Elf creature\$."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPermanent(game, {
                final MagicPermanent creature ->
                game.doAction(new MagicDestroyAction(creature));
            });
        }
    }
]
