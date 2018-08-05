/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Thesis;

import java.util.Random;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

/**
 *
 * @author ASUS A46CB
 */
public class Recovery {
    private long Key;
    private double Threshold;
    private double[][]MSE ;
    private boolean[][]det;
    private int Height,Width;
    private double MinMSE;
    private double MaxMSE;
    private double AvgMSE;
    public void setKey(long K)
    {
        this.Key=K;
    }
    public void setThresh(double T)
    {
        this.Threshold=T;
    }
    public void setMSE(double[][] MSE)
    {
        this.MSE=MSE;
    }

    public void Detect() {
       int rndBlockPst [] = new int [Height*Width]; 
       det =new boolean[Height][Width];
       Random rndX = new Random (Key);
       
       for(int i=0;i<Height*Width;i++) // Random Blok position
        {
            int random = rndX.nextInt((Height*Width)+1);           
           while(check(rndBlockPst,random))
           {
               random = rndX.nextInt((Height*Width)+1);
           }
            rndBlockPst[i] = random;          
        }
       int count = 0;
       //System.out.println("Height ="+det.length);
       //System.out.println("Width ="+det[0].length);
       for(int i=0;i<Height;i++)
       {
           //System.out.println("i ="+i);
           for(int j=0;j<Width;j++)
           {
               //System.out.println("j ="+j);
               int pos = rndBlockPst[count]-1;
               //System.out.println("pos = "+pos);
               int x = pos%Width;
               int y = pos/Width;
               //System.out.println(" X ="+x);
               //System.out.println(" Y ="+y);
               det[i][j] = MSE[i][j]>Threshold && MSE[y][x]>Threshold;
               count++;
           }
       }
       
        System.out.println("Done Detection!!");
    }

    public Mat Recover(Mat Image, Mat Ext) {
        int counti=0;
        int countj=0;
        Mat Result = Image.clone();
        for(int i=0;i<Image.rows();i+=8)
        {
            for(int j=0;j<Image.cols();j+=8)
            {
                if(det[counti][countj]==true)
                {
                    Mat submat1 = Result.submat(new Rect(j,i,8,8));
                    Mat submat2 = Ext.submat(new Rect(j,i,8,8));
                    submat2.copyTo(submat1);
                }
                countj++;
            }
            counti++;
            countj=0;
        }
       return Result;
    }
    public double getAvgMSE()
    {
        return this.AvgMSE;
    }
    public double getMinMSE()
    {
        return this.MinMSE;
    }
    public double getMaxMSE()
    {
        return this.MaxMSE;
    }
    void setHeight(int Height) {
        this.Height=Height;
    }

    void setWidth(int Width) {
        this.Width=Width;
    }
    public boolean check(int[] arr, int val)// Check Random number
    {     
        for(int i=0;i<arr.length;i++)
        {
            if(arr[i]==val)
                return true;
        }
        return false;
    }

    void MSEStat() {
       int Height = MSE.length;
       int Width = MSE[0].length;
       double min = Double.MAX_VALUE;
       double max = 0;
       double acc=0;
       for(int i=0;i<Height;i++)
       {
           for(int j=0;j<Width;j++)
           {
               if(MSE[i][j]>max)
                   max=MSE[i][j];
               if(MSE[i][j]<min)
                   min=MSE[i][j];
               acc+=MSE[i][j];
           }
       }
       this.MaxMSE=max;
       this.MinMSE=min;
       this.AvgMSE=acc/(Height*Width);
    }

}
