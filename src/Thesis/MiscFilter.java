/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Thesis;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author ASUS A46CB
 */
public class MiscFilter extends FileFilter{
    
    /** Creates a new instance of TextFilter */
    public MiscFilter() {
    }
    @Override
     public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = Utils.getExtension(f);
	if (extension != null) 
        {
            if (extension.equals(Utils.watermarked))
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
        return "*.wmr";

    }
}
