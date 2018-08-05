/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Thesis;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author ASUS A46CB
 */
public class ImageViewer {
    private JLabel imageView;
    
    public void show(Mat image) {
        show(image,"");
    }
     public void show(Mat image, String windowName) {
         setSystemLookAndFeel();
         
         JFrame frame = createJFrame(windowName);
            
            Image loadedImage = toBufferedImage (image);
            loadedImage = loadedImage.getScaledInstance(635, 475, Image.SCALE_DEFAULT);
            imageView.setIcon(new ImageIcon(loadedImage));
            
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
         
     }

    private void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        } catch (InstantiationException e){
            e.printStackTrace();
        } catch (IllegalAccessException e){
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e){
            e.printStackTrace();
        }
        
    }

    private JFrame createJFrame(String windowName) {
        JFrame frame = new JFrame(windowName);
        imageView = new JLabel();
        final JScrollPane imageScrollPane = new JScrollPane(imageView);
            imageScrollPane.setPreferredSize(new Dimension(640,480));
            frame.add(imageScrollPane, BorderLayout.CENTER);
           // frame.setDefaultCloseOperation(WindowConstants.);
            
        return frame;
    }

    private Image toBufferedImage(Mat matrix) {
        
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if(matrix.channels()>1)
        {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int buffersize = matrix.channels()*matrix.cols()*matrix.rows();
        byte[] buffer = new byte[buffersize];
        matrix.get(0,0, buffer);
        BufferedImage image =   new BufferedImage(matrix.cols(),matrix.rows(),type);
        final byte[] targetPixels = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
        System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
        return image;
    }
    
}
