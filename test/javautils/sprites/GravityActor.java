package javautils.sprites;

import javautils.actors.Actor;
import javautils.actors.ActorFlagId;
import javautils.actors.IActor;
import javautils.flags.Flags;
import javautils.flags.HasFlags;

import java.awt.geom.Point2D;

public class GravityActor extends Actor {
    public final Sprite sprite;
    public final double mass;
    private Long lastTime = null;

    public GravityActor(final Sprite sprite, final double mass) {
        super(true, true, false);
        this.sprite = sprite;
        this.mass = mass;
    }

    public Point2D.Double getCenterOfGravity() {
        double centerX = sprite.getShape().getBounds2D().getCenterX();
        double centerY = sprite.getShape().getBounds2D().getCenterY();
        return new Point2D.Double(centerX, centerY);
    }

    @Override
    public void affectActor(IActor actor, long time) {
        if (this.lastTime==null) {
            this.lastTime = time;
        } else if (actor instanceof GravityActor) {
            long dTime = time - this.lastTime;
            this.lastTime = time;

            GravityActor otherGravityActor = (GravityActor) actor;
            Point2D.Double thisCenter = this.getCenterOfGravity();
            Point2D.Double otherCenter = otherGravityActor.getCenterOfGravity();
            double distance = thisCenter.distance(otherCenter);
            if (distance > 0) {
                double forceMagnitude = dTime * (this.mass * otherGravityActor.mass) / (distance * distance);
                double forceX = forceMagnitude * (otherCenter.x - thisCenter.x) / distance;
                double forceY = forceMagnitude * (otherCenter.y - thisCenter.y) / distance;

                // Here you would apply the calculated force to the other actor's velocity or position
                otherGravityActor.sprite.translate(forceX, forceY, 0D);
            }
        }
    }
}
