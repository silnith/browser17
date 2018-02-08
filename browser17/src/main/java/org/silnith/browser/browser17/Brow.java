package org.silnith.browser.browser17;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.WindowConstants;

import org.silnith.browser.browser17.ui.ImageThingy;

public class Brow {
    
    public static void main(String[] args) throws IOException {
        JFrame.setDefaultLookAndFeelDecorated(false);
        
        final JFileChooser jFileChooser = new JFileChooser();
        
        final int showOpenDialog = jFileChooser.showOpenDialog(null);
        
        switch (showOpenDialog) {
        case JFileChooser.APPROVE_OPTION: {
            final File selectedFile = jFileChooser.getSelectedFile();
            final BufferedImage image = ImageIO.read(selectedFile);
            
            final ImageThingy imageThingy = new ImageThingy(image, Executors.newSingleThreadExecutor());
            
            final JScrollPane displayScrollPane = new JScrollPane(imageThingy.getPanel(), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            displayScrollPane.setPreferredSize(new Dimension(800, 600));
            final JViewport viewport = displayScrollPane.getViewport();
            viewport.addChangeListener(imageThingy.getChangeListener());
            
            final JScrollPane configScrollPane = new JScrollPane(imageThingy.getConfigurationPanel(), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            
            final JSplitPane jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, displayScrollPane, configScrollPane);
            jSplitPane.setOneTouchExpandable(true);
            jSplitPane.setResizeWeight(1.0);
            
            final JTabbedPane jTabbedPane = new JTabbedPane();
            jTabbedPane.addTab("Tab", jSplitPane);
            
            final JFrame jFrame = new JFrame("Browser 17");
            jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            
            jFrame.setContentPane(jTabbedPane);
            
            jFrame.pack();
            jFrame.setVisible(true);
        } break;
        case JFileChooser.CANCEL_OPTION: {} break;
        case JFileChooser.ERROR_OPTION: {} break;
        }
    }
    
}
