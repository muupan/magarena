package magic.model.mstatic;

import magic.model.MagicAbility;
import magic.model.MagicAbilityList;
import magic.model.MagicCardDefinition;
import magic.model.MagicChangeCardDefinition;
import magic.model.MagicColor;
import magic.model.MagicCounterType;
import magic.model.MagicGame;
import magic.model.MagicPermanent;
import magic.model.MagicPlayer;
import magic.model.MagicPowerToughness;
import magic.model.MagicSubType;
import magic.model.MagicType;
import magic.model.action.MagicRemoveStaticAction;
import magic.model.target.MagicPermanentTargetFilter;
import magic.model.target.MagicTargetFilter;
import magic.model.target.MagicTargetFilterFactory;

import java.util.Set;

public abstract class MagicStatic extends MagicDummyModifier implements MagicChangeCardDefinition {

    public static final boolean UntilEOT = true;
    public static final boolean Forever = !UntilEOT;

    //permanents affected by the static effect
    private MagicTargetFilter<MagicPermanent> filter;

    //layer where this effect operate
    private final MagicLayer layer;

    private final boolean isUntilEOT;

    protected MagicStatic(final MagicLayer aLayer, final MagicTargetFilter<MagicPermanent> aFilter, final boolean aIsUntilEOT) {
        filter = aFilter;
        layer = aLayer;
        isUntilEOT = aIsUntilEOT;
    }

    protected MagicStatic(final MagicLayer aLayer, final MagicTargetFilter<MagicPermanent> aFilter) {
        this(aLayer, aFilter, false);
    }

    protected MagicStatic(final MagicLayer aLayer, final boolean aIsUntilEOT) {
        this(aLayer, MagicTargetFilterFactory.NONE, aIsUntilEOT);
    }

    protected MagicStatic(final MagicLayer aLayer) {
        this(aLayer, MagicTargetFilterFactory.NONE, false);
    }

    @Override
    public void change(final MagicCardDefinition cdef) {
        cdef.addStatic(this);
    }

    public final MagicLayer getLayer() {
        return layer;
    }

    public final boolean isUntilEOT() {
        return isUntilEOT;
    }

    public boolean accept(final MagicGame game,final MagicPermanent source,final MagicPermanent target) {
        if (filter == MagicTargetFilterFactory.NONE) {
            return source == target && condition(game, source, target);
        } else {
            return filter.accept(game, source.getController(), target) && condition(game, source, target);
        }
    }

    public boolean condition(final MagicGame game,final MagicPermanent source,final MagicPermanent target) {
        return true;
    }

    public static boolean acceptLinked(final MagicGame game,final MagicPermanent source,final MagicPermanent target) {
        if (source.isEquipment()) {
            return source.getEquippedCreature() == target;
        } else if (source.isEnchantment()) {
            return source.getEnchantedPermanent() == target;
        } else {
            return source.getPairedCreature() == target ||
                (source == target && source.isPaired());
        }
    }
    
    public static MagicStatic genPTStatic(final MagicTargetFilter<MagicPermanent> filter, final int givenPower, final int givenToughness) {
        return new MagicStatic(
            MagicLayer.ModPT,
            filter
        ) {
            @Override
            public void modPowerToughness(
                final MagicPermanent source,
                final MagicPermanent permanent,
                final MagicPowerToughness pt) {
                pt.add(givenPower, givenToughness);
            }
        };
    }
    
    public static MagicStatic genPTStaticOther(final MagicTargetFilter<MagicPermanent> filter, final int givenPower, final int givenToughness) {
        return new MagicStatic(
            MagicLayer.ModPT,
            filter
        ) {
            @Override
            public void modPowerToughness(
                final MagicPermanent source,
                final MagicPermanent permanent,
                final MagicPowerToughness pt) {
                pt.add(givenPower, givenToughness);
            }
            @Override
            public boolean condition(final MagicGame game,final MagicPermanent source,final MagicPermanent target) {
                return source != target;
            }
        };
    }

    public static MagicStatic genPTStatic(final int givenPower, final int givenToughness) {
        return new MagicStatic(MagicLayer.ModPT) {
            @Override
            public void modPowerToughness(
                final MagicPermanent source,
                final MagicPermanent permanent,
                final MagicPowerToughness pt) {
                pt.add(givenPower, givenToughness);
            }
            @Override
            public boolean accept(final MagicGame game,final MagicPermanent source,final MagicPermanent target) {
                return MagicStatic.acceptLinked(game, source, target);
            }
        };
    }
    
    public static MagicStatic genPTSetStatic(final int givenPower, final int givenToughness) {
        return new MagicStatic(MagicLayer.SetPT) {
            @Override
            public void modPowerToughness(
                final MagicPermanent source,
                final MagicPermanent permanent,
                final MagicPowerToughness pt) {
                pt.set(givenPower, givenToughness);
            }
            @Override
            public boolean accept(final MagicGame game,final MagicPermanent source,final MagicPermanent target) {
                return MagicStatic.acceptLinked(game, source, target);
            }
        };
    }
    
    public static MagicStatic genABStatic(final MagicTargetFilter<MagicPermanent> filter, final MagicAbilityList abilityList) {
        return new MagicStatic(
            MagicLayer.Ability,
            filter
        ) {
            @Override
            public void modAbilityFlags(
                final MagicPermanent source,
                final MagicPermanent permanent,
                final Set<MagicAbility> flags) {
                abilityList.giveAbility(permanent, flags);
            }
        };
    }
    
    public static MagicStatic genABStaticOther(final MagicTargetFilter<MagicPermanent> filter, final MagicAbilityList abilityList) {
        return new MagicStatic(
            MagicLayer.Ability,
            filter
        ) {
            @Override
            public void modAbilityFlags(
                final MagicPermanent source,
                final MagicPermanent permanent,
                final Set<MagicAbility> flags) {
                abilityList.giveAbility(permanent, flags);
            }
            @Override
            public boolean condition(final MagicGame game,final MagicPermanent source,final MagicPermanent target) {
                return source != target;
            }
        };
    }
    
    public static MagicStatic genABStatic(final MagicAbilityList abilityList) {
        return new MagicStatic(MagicLayer.Ability) {
            @Override
            public void modAbilityFlags(
                final MagicPermanent source,
                final MagicPermanent permanent,
                final Set<MagicAbility> flags) {
                abilityList.giveAbility(permanent, flags);
            }
            @Override
            public boolean accept(final MagicGame game,final MagicPermanent source,final MagicPermanent target) {
                return MagicStatic.acceptLinked(game, source, target);
            }
        };
    }

    public static MagicStatic genTypeStatic(final int givenTypeFlags) {
        return new MagicStatic(MagicLayer.Type) {
            @Override
            public int getTypeFlags(final MagicPermanent permanent,final int flags) {
                return flags | givenTypeFlags;
            }
            @Override
            public boolean accept(final MagicGame game,final MagicPermanent source,final MagicPermanent target) {
                return MagicStatic.acceptLinked(game, source, target);
            }
        };
    }

    public static MagicStatic genSTStatic(final Set<MagicSubType> givenSubTypeFlags) {
        return new MagicStatic(MagicLayer.Type) {
            @Override
            public void modSubTypeFlags(
                final MagicPermanent permanent,
                final Set<MagicSubType> flags) {
                flags.addAll(givenSubTypeFlags);
            }
            @Override
            public boolean accept(final MagicGame game,final MagicPermanent source,final MagicPermanent target) {
                return MagicStatic.acceptLinked(game, source, target);
            }
        };
    }

    public static MagicStatic AddLinkedColor(final int givenColorFlags) {
        return new MagicStatic(MagicLayer.Color) {
            @Override
            public int getColorFlags(
                final MagicPermanent permanent,
                final int flags) {
                return flags | givenColorFlags;
            }
            @Override
            public boolean accept(final MagicGame game,final MagicPermanent source,final MagicPermanent target) {
                return MagicStatic.acceptLinked(game, source, target);
            }
        };
    }

    public static MagicStatic SetLinkedColor(final int givenColorFlags) {
        return new MagicStatic(MagicLayer.Color) {
            @Override
            public int getColorFlags(
                final MagicPermanent permanent,
                final int flags) {
                return givenColorFlags;
            }
            @Override
            public boolean accept(final MagicGame game,final MagicPermanent source,final MagicPermanent target) {
                return MagicStatic.acceptLinked(game, source, target);
            }
        };
    }

    public static MagicStatic ControlEnchanted = new MagicStatic(MagicLayer.Control) {
        @Override
        public MagicPlayer getController(
                final MagicPermanent source,
                final MagicPermanent target,
                final MagicPlayer player) {
            return source.getController();
        }
        @Override
        public boolean accept(
                final MagicGame game,
                final MagicPermanent source,
                final MagicPermanent target) {
            return source.getEnchantedPermanent() == target;
        }
    };

    public static MagicStatic ControlAsLongAsYouControlSource(final MagicPlayer you, final MagicPermanent target) {
        final MagicTargetFilter<MagicPermanent> filter = new MagicPermanentTargetFilter(target);
        return new MagicStatic(MagicLayer.Control,filter) {
            @Override
            public MagicPlayer getController(
                final MagicPermanent source,
                final MagicPermanent permanent,
                final MagicPlayer player) {
                return source.getController();
            }
            @Override
            public boolean condition(
                final MagicGame game,
                final MagicPermanent source,
                final MagicPermanent target) {
                if (you.getIndex() != source.getController().getIndex()) {
                    //remove this static after the update
                    game.addDelayedAction(new MagicRemoveStaticAction(source, this));
                    return false;
                } else {
                    return true;
                }
            }
        };
    }

    public static MagicStatic Unleash = new MagicStatic(MagicLayer.Ability) {
        @Override
        public void modAbilityFlags(
            final MagicPermanent source,
            final MagicPermanent permanent,
            final Set<MagicAbility> flags) {
            flags.add(MagicAbility.CannotBlock);
        }
        @Override
        public boolean condition(final MagicGame game,final MagicPermanent source,final MagicPermanent target) {
            return source.hasCounters(MagicCounterType.PlusOne);
        }
    };

    public static MagicStatic SwitchPT = new MagicStatic(MagicLayer.SwitchPT, MagicStatic.UntilEOT) {
        @Override
        public void modPowerToughness(
                final MagicPermanent source,
                final MagicPermanent permanent,
                final MagicPowerToughness pt) {
            pt.set(pt.toughness(),pt.power());
        }
    };
    
    public static MagicStatic Zombie = new MagicStatic(MagicLayer.Type) {
         @Override
         public void modSubTypeFlags(final MagicPermanent permanent,final Set<MagicSubType> flags) {
             flags.add(MagicSubType.Zombie);
         }
    };
    
    public static MagicStatic Vampire = new MagicStatic(MagicLayer.Type) {
         @Override
         public void modSubTypeFlags(final MagicPermanent permanent,final Set<MagicSubType> flags) {
             flags.add(MagicSubType.Vampire);
         }
    };

    public static MagicStatic Black = new MagicStatic(MagicLayer.Color) {
         @Override
         public int getColorFlags(final MagicPermanent permanent,final int flags) {
             return flags | MagicColor.Black.getMask();
         }
    };
    
    public static MagicStatic Artifact = new MagicStatic(MagicLayer.Type) {
         @Override
         public int getTypeFlags(final MagicPermanent permanent,final int flags) {
             return flags | MagicType.Artifact.getMask();
         }
    };
            
    public static MagicStatic AllCreatureTypesUntilEOT = new MagicStatic(MagicLayer.Type, MagicStatic.UntilEOT) {
        @Override
        public void modSubTypeFlags(final MagicPermanent permanent, final Set<MagicSubType> flags) {
            flags.addAll(MagicSubType.ALL_CREATURES);
        }
    };
    
    public static MagicStatic Bestowed = new MagicStatic(MagicLayer.Type) {
         @Override
         public int getTypeFlags(final MagicPermanent permanent,final int flags) {
             return flags & ~MagicType.Creature.getMask();
         }
        @Override
        public void modSubTypeFlags(final MagicPermanent permanent,final Set<MagicSubType> flags) {
            flags.removeAll(MagicSubType.ALL_CREATURES);
            flags.add(MagicSubType.Aura);
        }
        @Override
        public boolean condition(final MagicGame game,final MagicPermanent source,final MagicPermanent target) {
            return source.getEnchantedPermanent().isValid();
        }
    };
}
