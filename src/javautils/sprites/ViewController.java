package javautils.sprites;

import javautils.time.SystemTimeSource;
import javautils.time.Ticker;
import javautils.time.TimeSource;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;

public class ViewController implements MouseListener, MouseMotionListener, MouseWheelListener, ComponentListener, ChangeListener {

    private double panX = 0;
    private double panY = 0;
    private double scale = 1.0;
    
    private int lastMouseX = 0;
    private int lastMouseY = 0;
    private boolean isPanning = false;
    
    private JComponent attachedTo;

    private Ticker ticker;
    private boolean changed = false;

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
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
            isPanning = false;
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
            int deltaX = mouseEvent.getX() - lastMouseX;
            int deltaY = mouseEvent.getY() - lastMouseY;
            
            panX += deltaX;
            panY += deltaY;
            
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
        
        invalidate();
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

    public final void applyTransform(final Graphics2D g2d) {
        g2d.translate(getPanX(), getPanY());
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
