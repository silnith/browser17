package org.silnith.browser.browser17.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.ImageObserver;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.silnith.browser.browser17.ui.model.ImageModel;


public class ImageThingy {
    
    private final ExecutorService service;

    private final class ImageScaleJob implements Runnable {
        
        @Override
        public void run() {
//            assert !SwingUtilities.isEventDispatchThread();
            
            final long startTime = System.currentTimeMillis();
            
            final Dimension viewportDimension = ImageThingy.this.model.getViewportDimension();
            final Image originalImage = ImageThingy.this.model.getOriginalImage();
            final Image scaledImage = originalImage.getScaledInstance(viewportDimension.width, viewportDimension.height, Image.SCALE_SMOOTH);
            ImageThingy.this.model.setScaledImage(scaledImage);
            
            final long endTime = System.currentTimeMillis();
            System.out.print("Image scale job took ");
            System.out.print(endTime - startTime);
            System.out.println("ms");
        }
        
    }
    
    private static final class ImageScaleTask implements Callable<Image> {
        
        private final Image source;
        private final Dimension size;

        public ImageScaleTask(Image source, Dimension size) {
            super();
            this.source = source;
            this.size = size;
        }

        @Override
        public Image call() throws Exception {
            return source.getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH);
        }

    }

    private final class CheckboxListener implements ItemListener {
        
        @Override
        public void itemStateChanged(ItemEvent e) {
            assert SwingUtilities.isEventDispatchThread();
            
            switch (e.getStateChange()) {
            case ItemEvent.SELECTED: {
                ImageThingy.this.model.setScaled(true);
            } break;
            case ItemEvent.DESELECTED: {
                ImageThingy.this.model.setScaled(false);
            } break;
            default: {} break;
            }
        }
        
    }

    private final class ViewportSizeListener implements ChangeListener {
        
        @Override
        public void stateChanged(ChangeEvent e) {
            final Object source = e.getSource();
            if (source instanceof JViewport) {
                final JViewport viewport = (JViewport) source;
                final Dimension extentSize = viewport.getExtentSize();
                ImageThingy.this.model.setViewportDimension(extentSize);
            }
        }
        
    }

    private final class DisplayPanel extends JPanel {
        
        private static final long serialVersionUID = 1L;

        @Override
        protected void paintComponent(Graphics g) {
            assert SwingUtilities.isEventDispatchThread();
            
            final long startTime = System.currentTimeMillis();
            
            if (ImageThingy.this.model.isScaled()) {
                g.drawImage(ImageThingy.this.model.getScaledImage(), 0, 0, ImageThingy.this.scaledImageObserver);
            } else {
                g.drawImage(ImageThingy.this.model.getOriginalImage(), 0, 0, null);
            }
            
            final long endTime = System.currentTimeMillis();
            System.out.print("paint component took ");
            System.out.print(endTime - startTime);
            System.out.println("ms");
        }
        
    }

    private final class ScaledImageObserver implements ImageObserver {
        
        @Override
        public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
            System.out.println("Image update is in event dispatch thread: " + SwingUtilities.isEventDispatchThread());
            
            System.out.print("image update: ");
            System.out.print(img);
            System.out.print(", info flags: ");
            System.out.print(infoflags);
            System.out.print(", (");
            System.out.print(x);
            System.out.print(", ");
            System.out.print(y);
            System.out.print("), (");
            System.out.print(width);
            System.out.print(", ");
            System.out.print(height);
            System.out.println(")");
            
            final long startTime = System.currentTimeMillis();
            
            if (img != ImageThingy.this.model.getScaledImage()) {
                System.out.println("Wrong scaled image.");
                return true;
            }
            if ((infoflags & ImageObserver.WIDTH) == ImageObserver.WIDTH) {
                System.out.println("New width: " + width);
            }
            if ((infoflags & ImageObserver.HEIGHT) == ImageObserver.HEIGHT) {
                System.out.println("New height: " + height);
            }
            if ((infoflags & ImageObserver.PROPERTIES) == ImageObserver.PROPERTIES) {
                System.out.println("New properties.");
            }
            if ((infoflags & ImageObserver.SOMEBITS) == ImageObserver.SOMEBITS) {
                final Rectangle newPixelsBoundingBox = new Rectangle(x, y, width, height);
                System.out.println("New bits: " + newPixelsBoundingBox);
                ImageThingy.this.displayPanel.repaint(newPixelsBoundingBox);
            }
            if ((infoflags & ImageObserver.FRAMEBITS) == ImageObserver.FRAMEBITS) {
            }
            if ((infoflags & ImageObserver.ALLBITS) == ImageObserver.ALLBITS) {
                System.out.println("All bits.");
                // redraw the whole originalImage
                ImageThingy.this.displayPanel.repaint();
            }
            if ((infoflags & ImageObserver.ERROR) == ImageObserver.ERROR) {
                // will never be able to draw the originalImage
            }
            if ((infoflags & ImageObserver.ABORT) == ImageObserver.ABORT) {
                // will not be able to draw the originalImage
            }
            
            final long endTime = System.currentTimeMillis();
            System.out.print("image update took ");
            System.out.print(endTime - startTime);
            System.out.println("ms");
            
            return false;
        }
        
    }

    private final ImageObserver scaledImageObserver;
    private final ImageModel model;
    private final JPanel displayPanel;
    private final JPanel configurationPanel;
    private final ViewportSizeListener viewportSizeListener;
    
    private Future<Image> rescaleJob;

    public ImageThingy(final Image image, final ExecutorService service) {
        super();
        if (image == null) {
            throw new IllegalArgumentException("Image may not be null.");
        }
        this.service = service;
        this.scaledImageObserver = new ScaledImageObserver();
        this.model = new ImageModel();
        this.model.setOriginalImage(image);
        this.model.setScaledImage(null);
//        final Object comment = image.getProperty("comment", this.imageObserver);
        
        this.displayPanel = new DisplayPanel();
        this.displayPanel.setOpaque(false);
        this.displayPanel.setPreferredSize(this.model.getOriginalDimension());
        
        final JCheckBox jCheckBox = new JCheckBox("Scale");
        jCheckBox.addItemListener(new CheckboxListener());
        this.configurationPanel = new JPanel();
        this.configurationPanel.add(jCheckBox);
        this.viewportSizeListener = new ViewportSizeListener();
        
        this.model.addPropertyChangeListener(new PropertyChangeListener() {
            
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                System.out.println("Property change is in event dispatch thread: " + SwingUtilities.isEventDispatchThread());
                
                System.out.println("Property change: " + evt);
                
                switch (evt.getPropertyName()) {
                case "originalImage": {
                    final Image newOriginalImage = (Image) evt.getNewValue();
                    ImageThingy.this.displayPanel.repaint();
                } break;
                case "scaledImage": {
                    if (ImageThingy.this.model.isScaled()) {
                        // do something
                        System.out.println("New scaled image.");
                        ImageThingy.this.displayPanel.repaint();
                    } else {
                        // do nothing
                        System.out.println("Ignored.");
                    }
                } break;
                case "originalWidth": // fall through
                case "originalHeight": {
                    ImageThingy.this.displayPanel.setPreferredSize(ImageThingy.this.model.getOriginalDimension());
                } break;
                case "viewportDimension": {
                    if (ImageThingy.this.model.isScaled()) {
                        final Dimension viewportDimension = (Dimension) evt.getNewValue();
                        ImageThingy.this.displayPanel.setPreferredSize(viewportDimension);
                        
                        startImageRescale();
                    }
                } break;
                case "scaled": {
                    final boolean scaled = (Boolean) evt.getNewValue();
                    if (scaled) {
                        final Dimension viewportDimension = ImageThingy.this.model.getViewportDimension();
                        ImageThingy.this.displayPanel.setPreferredSize(viewportDimension);
                        
                        startImageRescale();
                    } else {
                        ImageThingy.this.model.setScaledImage(null);
                        ImageThingy.this.displayPanel.setPreferredSize(ImageThingy.this.model.getOriginalDimension());
                    }
                    ImageThingy.this.displayPanel.revalidate();
                    ImageThingy.this.displayPanel.repaint();
                } break;
                }
            }

            private void startImageRescale() {
                if (ImageThingy.this.rescaleJob != null) {
                    ImageThingy.this.rescaleJob.cancel(true);
                }
                final Callable<Image> imageScaleJob = new ImageScaleTask(ImageThingy.this.model.getOriginalImage(), ImageThingy.this.model.getViewportDimension());
                ImageThingy.this.rescaleJob = ImageThingy.this.service.submit(imageScaleJob);
                try {
                    ImageThingy.this.model.setScaledImage(ImageThingy.this.rescaleJob.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
//                imageScaleJob.run();
            }
            
        });
    }

    public JComponent getPanel() {
        return displayPanel;
    }
    
    public JComponent getConfigurationPanel() {
        return configurationPanel;
    }
    
    public ChangeListener getChangeListener() {
        return viewportSizeListener;
    }
    
}
