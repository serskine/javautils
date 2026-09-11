package javautils.sprites;

import javautils.Logger;
import javautils.time.SystemTimeSource;
import javautils.time.Ticker;
import javautils.time.TimeSource;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;

public class ViewController implements MouseListener, MouseMotionListener, MouseWheelListener, ComponentListener, ChangeListener, Camera2DController {

    private double panX = 0;
    private double panY = 0;
    private double scale = 1.0;
    private double rotationRadians = 0.0;
    private double rotationPivotX = 0.0;
    private double rotationPivotY = 0.0;
    private double rotationSpeedFactor = 0.1D;

    private int lastMouseX = 0;
    private int lastMouseY = 0;
    private boolean isPanning = false;
    
    private JComponent attachedTo;

    private Ticker ticker;
    private boolean changed = false;

    private WheelMode wheelMode = WheelMode.WHEEL_ROTATE;

    @Override
    public double getX() {
        return getPanX();
    }

    @Override
    public double getY() {
        return getPanY();
    }

    @Override
    public void setX(double x) {
        panX = x;
    }

    @Override
    public void setY(double y) {
        panY = y;
    }

    @Override
    public double getRotationRadians() {
        return rotationRadians;
    }

    @Override
    public void setRotationRadians(double rotationRadians) {
        this.rotationRadians = rotationRadians;
    }

    @Override
    public double getZoom() {
        return this.scale;
    }

    @Override
    public void setZoom(double scale) {
        this.scale = scale;
    }

    public enum WheelMode {
        WHEEL_ZOOM,
        WHEEL_ROTATE
    }

    public ViewController() {
        this(new SystemTimeSource(false));
    }
    public ViewController(TimeSource timesource) {
        ticker = new Ticker(timesource, Thread.MIN_PRIORITY);
        ticker.addListener(new Ticker.Listener() {
            @Override
            public void onTick(long tick) {
                repaint();
            }

            @Override
            public void onStop(long tick) {
                repaint();
            }

            @Override
            public void onStart(long tick) {
                repaint();
            }
        });
        ticker.start();
    }

    public final void detach() {
        if (attachedTo != null) {
            attachedTo.removeMouseListener(this);
            attachedTo.removeMouseMotionListener(this);
            attachedTo.removeMouseWheelListener(this);
            attachedTo.removeComponentListener(this);
            attachedTo = null;
        }
    }

    public final void attachTo(final JComponent component) {
        if (this.attachedTo != component) {
            detach();
            this.attachedTo = component;
            component.addMouseListener(this);
            component.addMouseMotionListener(this);
            component.addMouseWheelListener(this);
            component.addComponentListener(this);
        }
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
            isPanning = true;
            lastMouseX = mouseEvent.getX();
            lastMouseY = mouseEvent.getY();
        }
        if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
            this.wheelMode = WheelMode.WHEEL_ROTATE;
            Logger.info("Setting wheelMode = " + this.wheelMode);
        }
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
            isPanning = false;
        }
        if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
            this.wheelMode = WheelMode.WHEEL_ZOOM;
            Logger.info("Setting wheelMode = " + this.wheelMode);
        }

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        if (isPanning) {
            double deltaX = (mouseEvent.getX() - lastMouseX);
            double deltaY = (mouseEvent.getY() - lastMouseY);
            
            double cos = Math.cos(rotationRadians);
            double sin = Math.sin(rotationRadians);

            double worldDeltaX = deltaX * cos + deltaY * sin;
            double worldDeltaY = -deltaX * sin + deltaY * cos;

            setX(getX() + worldDeltaX);
            setY(getY() + worldDeltaY);

//            panX += worldDeltaX;
//            panY += worldDeltaY;
            
            lastMouseX = mouseEvent.getX();
            lastMouseY = mouseEvent.getY();
            
            invalidate();
        }
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {

        switch(wheelMode) {
            case WHEEL_ZOOM -> zoomCamera(mouseWheelEvent);
            case WHEEL_ROTATE -> rotateCamera(mouseWheelEvent);
        }
        
        invalidate();
    }

    private void rotateCamera(MouseWheelEvent mouseWheelEvent) {

        rotationPivotX = mouseWheelEvent.getX();
        rotationPivotY = mouseWheelEvent.getY();

        rotationRadians += mouseWheelEvent.getWheelRotation() * getRotationSpeedFactor();

    }

    private void zoomCamera(MouseWheelEvent mouseWheelEvent) {
        double oldScale = scale;
        double zoomFactor = 1.1;

        if (mouseWheelEvent.getWheelRotation() < 0) {
            scale *= zoomFactor;
        } else {
            scale /= zoomFactor;
        }

        int mouseX = mouseWheelEvent.getX();
        int mouseY = mouseWheelEvent.getY();

        panX = mouseX - (mouseX - panX) * (scale / oldScale);
        panY = mouseY - (mouseY - panY) * (scale / oldScale);
    }
    
    @Override
    public void componentResized(final ComponentEvent componentEvent) {
        invalidate();
    }
    
    @Override
    public void componentMoved(final ComponentEvent componentEvent) {
    }
    
    @Override
    public void componentShown(final ComponentEvent componentEvent) {
    }
    
    @Override
    public void componentHidden(final ComponentEvent componentEvent) {
    }
    
    public double getPanX() {
        return panX;
    }
    
    public double getPanY() {
        return panY;
    }
    
    public double getScale() {
        return scale;
    }

    @Override
    public void setScale(double scale) {

    }

    public double getLastMouseX() {
        return lastMouseX;
    }

    public double getLastMouseY() {
        return lastMouseY;
    }

    public double getCenterX() {
        return (attachedTo==null) ? 0D : attachedTo.getWidth()/2D;
    }

    public double getCenterY() {
        return (attachedTo==null) ? 0D : attachedTo.getHeight()/2D;
    }

    public double getRotationSpeedFactor() {
        return rotationSpeedFactor;
    }

    public void setRotationSpeedFactor(double rotationSpeedFactor) {
        this.rotationSpeedFactor = rotationSpeedFactor;
    }

    public final void applyTransform(final Graphics2D g2d) {
        g2d.translate(rotationPivotX, rotationPivotY);
        g2d.rotate(getRotationRadians());
        g2d.translate(-rotationPivotX, -rotationPivotY);
        g2d.translate(getX(), getY());
        g2d.scale(getScale(), getScale());
    }

    public final void invalidate() {
        changed = true;
    }

    public final void repaint() {
        if (attachedTo != null && changed) {
            attachedTo.repaint();
            changed = false;
        }
    }

    public final void centerOnOrigon() {
        if (attachedTo != null) {
            panX = attachedTo.getX() - (attachedTo.getWidth() / 2);
            panY = attachedTo.getY() - (attachedTo.getHeight() / 2);
        }
    }

    @Override
    public void stateChanged(ChangeEvent changeEvent) {
        invalidate();
    }

    public final Ticker getTicker() {
        if (this.ticker == null) {
            this.ticker = new Ticker(new SystemTimeSource(false), Thread.MIN_PRIORITY);
        }
        return this.ticker;
    }
}
