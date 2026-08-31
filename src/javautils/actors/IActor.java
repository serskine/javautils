package javautils.actors;

import javautils.flags.HasFlags;

public interface IActor extends HasFlags<ActorFlagId> {

    /**
     * This is what we do when we want to affect another actor
     * @param actor is the actor we want to affect
     */
    void affectActor(IActor actor, long time);

    default boolean isAffectedByActors() {
        return !isFlag(ActorFlagId.IMMUNE_TO_AFFECTS);
    }

    default boolean affectsOthers() {
        return isFlag(ActorFlagId.AFFECTS_OTHERS);
    }

    default boolean affectsSelf() {
        return isFlag(ActorFlagId.AFFECTS_SELF);
    }

    default boolean affectsActors() {
        return affectsOthers() || affectsSelf();
    }
}
