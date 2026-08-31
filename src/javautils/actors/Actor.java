package javautils.actors;

import javautils.flags.Flags;
import javautils.flags.HasFlags;

public abstract class Actor implements IActor {
    private Flags flags;

    public Actor() {
        this(false, false, false);
    }

    public Actor(boolean affectsOthers, boolean affectedByOthers, boolean affectedBySelf) {
        flags = new Flags();
        if (affectedByOthers) {
            setFlag(ActorFlagId.AFFECTS_OTHERS);
        }
        if (affectedBySelf) {
            setFlag(ActorFlagId.AFFECTS_SELF);
        }
        if (affectsOthers) {
            setFlag(ActorFlagId.AFFECTS_OTHERS);
        }
    }

    public Actor(final ActorFlagId... trueFlags) {
        flags = new Flags();
        for(ActorFlagId actorFlagId : trueFlags) {
            setFlag(actorFlagId);
        }
    }

    @Override
    public int getFlagIndex(ActorFlagId flag) {
        return flag.ordinal();
    }

    @Override
    public int numFlags() {
        return ActorFlagId.values().length;
    }

    @Override
    public Flags getFlags() {
        return flags;
    }

    @Override
    public HasFlags<ActorFlagId> setFlags(HasFlags<ActorFlagId> flags) {
        this.flags = flags.getFlags();
        return this;
    }

}
