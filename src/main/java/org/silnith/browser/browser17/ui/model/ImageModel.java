package org.silnith.browser.browser17.ui.model;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


public class ImageModel {
    
    private final class OriginalImageObserver implements ImageObserver {

        @Override
        public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
            if (img != originalImage) {
                System.err.println("Wrong original image.");
                return true;
            }

            if ((infoflags & ImageObserver.WIDTH) == ImageObserver.WIDTH) {
                ImageModel.this.setOriginalWidth(width);
            }
            if ((infoflags & ImageObserver.HEIGHT) == ImageObserver.HEIGHT) {
                ImageModel.this.setOriginalHeight(height);
            }
            if ((infoflags & ImageObserver.PROPERTIES) == ImageObserver.PROPERTIES) {
            }
            if ((infoflags & ImageObserver.SOMEBITS) == ImageObserver.SOMEBITS) {
                final Rectangle newPixelsBoundingBox = new Rectangle(x, y, width, height);
            }
            if ((infoflags & ImageObserver.FRAMEBITS) == ImageObserver.FRAMEBITS) {
            }
            if ((infoflags & ImageObserver.ALLBITS) == ImageObserver.ALLBITS) {
                // redraw the whole originalImage
            }
            if ((infoflags & ImageObserver.ERROR) == ImageObserver.ERROR) {
                // will never be able to draw the originalImage
            }
            if ((infoflags & ImageObserver.ABORT) == ImageObserver.ABORT) {
                // will not be able to draw the originalImage
            }
            if (ImageModel.this.originalWidth > 0 && ImageModel.this.originalHeight > 0) {
                return false;
            } else {
                return true;
            }
        }
        
    }
    
    private Image originalImage;
    
    private Image scaledImage;
    
    private int originalWidth;
    
    private int originalHeight;
    
    private int viewportWidth;
    
    private int viewportHeight;
    
    private boolean scaled;
    
    private final PropertyChangeSupport propertyChangeSupport;
    
    public ImageModel() {
        super();
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public Image getOriginalImage() {
        return originalImage;
    }
    
    public void setOriginalImage(final Image original) {
        final Image previousValue = this.originalImage;
        this.originalImage = original;
        final ImageObserver imageObserver = new OriginalImageObserver();
        setOriginalWidth(this.originalImage.getWidth(imageObserver));
        setOriginalHeight(this.originalImage.getHeight(imageObserver));
        this.propertyChangeSupport.firePropertyChange("originalImage", previousValue, this.originalImage);
    }
    
    public Image getScaledImage() {
        return scaledImage;
    }
    
    public void setScaledImage(final Image scaled) {
        final Image previousValue = this.scaledImage;
        this.scaledImage = scaled;
        this.propertyChangeSupport.firePropertyChange("scaledImage", previousValue, this.scaledImage);
    }
    
    public int getOriginalWidth() {
        return originalWidth;
    }
    
    private void setOriginalWidth(final int originalWidth) {
        final int previousValue = this.originalWidth;
        if (originalWidth < 0) {
            this.originalWidth = 0;
        } else {
            this.originalWidth = originalWidth;
        }
        this.propertyChangeSupport.firePropertyChange("originalWidth", previousValue, this.originalWidth);
    }
    
    public int getOriginalHeight() {
        return originalHeight;
    }
    
    private void setOriginalHeight(final int originalHeight) {
        final int previousValue = this.originalHeight;
        if (originalHeight < 0) {
            this.originalHeight = 0;
        } else {
            this.originalHeight = originalHeight;
        }
        this.propertyChangeSupport.firePropertyChange("originalHeight", previousValue, this.originalHeight);
    }
    
    public Dimension getOriginalDimension() {
        return new Dimension(originalWidth, originalHeight);
    }
    
    public Dimension getViewportDimension() {
        return new Dimension(this.viewportWidth, this.viewportHeight);
    }
    
    public void setViewportDimension(final Dimension dimension) {
        setViewportDimension(dimension.width, dimension.height);
    }

    public void setViewportDimension(final int width, final int height) {
        final Dimension previousValue = new Dimension(this.viewportWidth, this.viewportHeight);
        this.viewportWidth = width;
        this.viewportHeight = height;
        this.propertyChangeSupport.firePropertyChange("viewportDimension", previousValue, new Dimension(this.viewportWidth, this.viewportHeight));
    }

    public boolean isScaled() {
        return scaled;
    }

    public void setScaled(final boolean scaled) {
        final boolean previousValue = this.scaled;
        this.scaled = scaled;
        this.propertyChangeSupport.firePropertyChange("scaled", previousValue, this.scaled);
    }
    
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }
    
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
    }
    
}
