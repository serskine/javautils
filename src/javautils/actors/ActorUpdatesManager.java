package javautils.actors;

import javautils.time.Ticker;

import java.util.HashSet;
import java.util.Set;

public class ActorUpdatesManager {

    private Set<IActor> allActors = new HashSet<>();
    private Set<IActor> affectedByActors =  new HashSet<>();
    private Set<IActor> affectsActors =  new HashSet<>();

    private Ticker ticker;

    public ActorUpdatesManager(Ticker ticker) {
        this.ticker = ticker;
    }

    public void add(IActor actor) {
        allActors.add(actor);
        if (actor.isAffectedByActors()) {
            affectedByActors.add(actor);
        }
        if (actor.affectsActors()) {
            affectsActors.add(actor);
        }
        checkOnActor(actor);
    }

    public void remove(IActor actor) {
        allActors.remove(actor);
        if (actor.isAffectedByActors()) {
            affectedByActors.remove(actor);
        }
        if (actor.affectsActors()) {
            affectsActors.remove(actor);
        }
    }

    public final void checkAllActorsAffectOnThisActor(IActor actor, long time) {
        for(IActor currentActor : affectsActors) {
            if (currentActor != actor) {
                currentActor.affectActor(actor, time);
            }
        }
    }

    public final void checkActorsAffectOnAllActors(IActor actor, long time) {
        for(IActor affectedActor : affectedByActors) {
            if (affectedActor != actor) {
                affectedActor.affectActor(actor, time);
            }
        }
    }

    public final void checkOnActor(IActor actor) {
        final long time = ticker.getTime();
        if (actor.isAffectedByActors()) {
            checkAllActorsAffectOnThisActor(actor, time);
        }
        if (actor.affectsActors()) {
            checkActorsAffectOnAllActors(actor, time);
        }
    }

    public final void update() {
        final long time = ticker.getTime();
        for(IActor activeActor : this.affectsActors) {
            checkActorsAffectOnAllActors(activeActor, time);
        }
    }

}
