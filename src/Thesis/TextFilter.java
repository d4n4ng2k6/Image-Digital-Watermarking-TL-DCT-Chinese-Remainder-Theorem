package Thesis;




/*
 * TextFilter.java
 *
 * Created on February 5, 2009, 4:49 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
import java.io.File;
import javax.swing.filechooser.*;
/**
 *
 * @author Danny
 */
public class TextFilter extends FileFilter{
    
    /** Creates a new instance of TextFilter */
    public TextFilter() {
    }
    @Override
     public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = Utils.getExtension(f);
	if (extension != null) 
        {
            if (extension.equals(Utils.bitmap))
            {
                    return true;
            } 
            else
            {
                return false;
            }
    	}

        return false;

    }

    @Override
    public String getDescription() {
        return "*.bmp";

    }
}
