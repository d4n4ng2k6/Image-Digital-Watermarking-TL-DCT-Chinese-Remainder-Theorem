/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Thesis;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.CvType;
import org.opencv.core.Scalar;
/**
 *
 * @author ASUS A46CB
 */
public class TwoLevDCTGenerate {
    private int BitSize;
    private Mat Image;
    private double Maximum;
    private double Minimum;
    private byte[][][] Watermark;
    public void setSize (int B)
    {
        this.BitSize = B;
    }
    public void setMat (Mat Im)
    {
        this.Image = Im;
    }
    public double getMax()
    {
        return this.Maximum;
    }
    public double getMin()
    {
        return this.Minimum;
    }
    public int getSize()
    {
        return this.BitSize;
    }
    public byte[][][] getWatermark()
    {
        return this.Watermark;
    }
    public void Generate()
    {
        int width = Image.cols();
        int height = Image.rows();
        //System.out.println("Width ="+width);
        //System.out.println("Height="+height);
        Mat [][] Coef = new Mat[height/8][width/8];
        int h =0;
        int w =0;
        for(int i=0;i<height;i+=8)
        {
            for(int j=0;j<width;j+=8)
            {
                Mat Blok = new Mat(Image,new Rect(j,i,8,8));
                Mat First = Blok.clone();
                Blok.convertTo(Blok,CvType.CV_32FC1);
                Core.dct(Blok,First);
                Mat Blok2 = new Mat(First,new Rect(0,0,4,4));
                Mat Second = Blok2.clone();
                Core.dct(Blok2,Second);
                Coef[h][w] = Second;
                //System.out.println("Done"+j);
                w++;
            }
            //System.out.println("Done Row"+i);
            h++;
            w=0;
        }
        scanMinMax(Coef);
        Coef = Normalize(Coef);
        this.Watermark = QuanBin(Coef,this.BitSize);
        
        
    }

    private void scanMinMax(Mat[][] Coef) {
        double max = Double.MIN_VALUE,min = Double.MAX_VALUE;
        Mat Temp = null;
        for(int i=0; i<Coef.length;i++)
        {
            for(int j=0;j<Coef[i].length;j++)
            {
                Temp = Coef[i][j];
               MinMaxLocResult Hasil =  Core.minMaxLoc(Temp);
              // System.out.println(" Value Max = "+Hasil.maxVal);
              // System.out.println(" Value Min = "+Hasil.minVal);
               if(Hasil.maxVal>max)
                   max = Hasil.maxVal;
               if(Hasil.minVal<min)
                   min = Hasil.minVal;
            }
        }
      this.Maximum = max;
      this.Minimum = min;
      
    }

    private Mat[][] Normalize(Mat[][] Coef) {
       //Mat[][] Result = Coef.clone();
       Mat Temp = null;
       double range = this.Maximum - this.Minimum;
       for(int i=0; i<Coef.length;i++)
       {
           for(int j=0;j<Coef[i].length;j++)
           {
               Temp = Coef[i][j];
               Core.subtract(Temp, new Scalar(this.Minimum), Temp);
               Core.divide(Temp, new Scalar(range), Temp);
               Coef[i][j] = Temp;
              // System.out.println(Temp.dump());
           }
       }
       return Coef;
    }

    private byte[][][] QuanBin(Mat[][] Coef, int BitSize) {
        int Range = (int) Math.pow(2, BitSize);
        byte[][][] Watermark = new byte[Coef.length][Coef[0].length][16];
        Mat Temp = null;
        for(int i=0; i<Coef.length;i++)
        {
            for(int j=0;j<Coef[i].length;j++)
            {
                Temp = Coef[i][j];
                //System.out.println("Ukuran Array :"+Temp.size());
                float[] Test = new float[(int)(Temp.size().width*Temp.size().height)];
                Temp.get(0, 0,Test);
                for(int x=0;x<Test.length;x++)
                {
                    if(Test[x]< 1)
                        Watermark[i][j][x] = (byte) (Test[x]*Range);
                    else
                        Watermark[i][j][x] = (byte) (Range-1);
                    //System.out.println("Watermark ke "+i+","+j+","+x+" ="+Watermark[i][j][x]);
                }
                //System.out.println(Test[15]);
                
            }
        }
        return Watermark;
    }
}
