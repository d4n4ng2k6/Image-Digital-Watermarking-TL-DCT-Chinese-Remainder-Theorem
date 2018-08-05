/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Thesis;

/**
 *
 * @author ASUS A46CB
 */
import java.io.Serializable;
public class MiscFile implements Serializable{
   private double[] Maximum = new double[3];
   private double[] Minimum = new double[3];
   
   private long Key;
   private int BitSize;
   private double prePSNR;
   
   public void setMax(double Max, int index)
   {
       this.Maximum[index] = Max;
   }
   public void setMin(double Min, int index)
   {
       this.Minimum[index] = Min;
   }
   
   public void setKey(long K)
   {
       this.Key = K;
   }
   public void setBit (int Bit)
   {
       this.BitSize = Bit;
   }
   public void setPSNR(double PSNR)
   {
       this.prePSNR = PSNR;
   }
   public double getMax(int index)
   {
       return this.Maximum[index];
   }
   public double getMin(int index)
   {
       return this.Minimum[index];
   }
   public long getKey()
   {
       return this.Key;
   }
   public int getBit()
   {
       return this.BitSize;
   }
   public double getPSNR()
   {
       return this.prePSNR;
   }

    String arrangePath(String Path) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        String Result;
        int temp = Path.indexOf((int)'.');
        Result = Path.substring(0,temp);
        Result = Result.concat(".wmr");
        return Result;
    }
    
    
}
