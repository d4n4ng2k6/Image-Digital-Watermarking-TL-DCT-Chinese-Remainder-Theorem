package Thesis;


/*
 * Utils.java
 *
 * Created on February 5, 2009, 4:49 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
import java.io.File;
/**
 *
 * @author Danny
 */
public class Utils {
    
     public final static String bitmap = "bmp";
     public final static String watermarked = "wmr";
   
    /*
     * Get the extension of a file.
     */  
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

    /** Creates a new instance of Utils */
    public Utils() {
    }
    
}
