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
import java.util.Random;
import static org.opencv.core.Core.idct;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import static org.opencv.core.Mat.zeros;
import org.opencv.core.Rect;

public class CRTExtraction {
    private long Key;
    private int BitSize;
    private int WBLok[][][];
    private Mat [] MatRec; 
    private int H;
    private int W;
    public void setKey(long K)
    {
        this.Key = K;
    }
    public void setBitSize(int B)
    {
        this.BitSize = B;
    }

   public  void extraction(Mat get) {
     Mat Result = new Mat();
     int Height = get.rows()/8;
     this.H = Height;
     int Width = get.cols()/8;
     this.W = Width;
     byte Temp [][] = new byte[Height*Width][64];
     int Block[][] = new int [Height*Width][64];
     int Watermark[][] = new int[Height*Width][16];
     this.WBLok = new int[Height*Width][4][4];
     int h =0;
     int w =0;
     int count = 0;
     int rndPixPst [] = new int[64];
     long seed = this.Key;
     Random rndX = new Random (seed);
     for(int i=0; i<get.rows(); i+=8 ) // Separating Block to byte[]
       {
           for(int j=0;j<get.cols();j+=8)
           {
               Mat blok = new Mat(get,new Rect(j,i,8,8));
               //blok.create(8,8,CvType.CV_8UC1);
               blok.get(0, 0, Temp[count]);
               Block[count] = getUnsigned(Temp[count]); //Create Blok Unsigned Integer
               w++;
               count++;
           }
           h++;
           w=0;
       }
     for(int i=0;i<64;i++) // Random Pixel Position
        {
            int random = rndX.nextInt(65);           
           while(check(rndPixPst,random))
           {
               random = rndX.nextInt(65);
           }
            rndPixPst[i] = random;           
        }
     
     for(int i=0;i<Block.length;i++)
     {
         int[]blk = Block[i];
         int bitcount =0;
         int watcount =0;
         int Water= 0;
         for(int j=0;j<BitSize*16;j++)
         {
           
             int ZL = blk[rndPixPst[j]-1]&3;// Get 2 LSB
             int W = GetBit(ZL);            //Get Watermark bit from LSB
             
             if (bitcount < BitSize-1)
             {               
                 Water = Water | W;             //Merge Watermark Bit
                 //System.out.println(" Watermark "+Water);
                 Water = Water<<1;
                 bitcount++;
             }
             else
             {                
                 Water = Water|W;                 
                 //System.out.println("Watermark ke "+watcount+" = "+Water);
                 Watermark[i][watcount] = Water; //Save Watermark 
                 Water = 0;
                 
                 bitcount = 0;
                 watcount++;
             }
         }
     }
     for(int i=0;i<Watermark.length;i++)
     {
         this.WBLok[i] = ChangeShape(Watermark[i]);
     }    
 }
   
   public Mat reconstruction (double Max, double Min)
   {
       Mat Result ;
       int [][][]ABlok = new int[H*W][4][4];
       double [][][]Dequan = new double[H*W][4][4];
       double[][][] Dnorm = new double[H*W][4][4];
       MatRec = new Mat[H*W];
       int rndBlockPst [] = new int [H*W];
       int range = (int) Math.pow(2, BitSize);
       long seed = this.Key;
       Random rndX = new Random (seed);
        for(int i=0;i<H*W;i++) // Random Blok position
        {
            int random = rndX.nextInt((H*W)+1);           
           while(check(rndBlockPst,random))
           {
               random = rndX.nextInt((H*W)+1);
           }
            rndBlockPst[i] = random;   
            ABlok[i] = WBLok[rndBlockPst[i]-1];
            /*for(int j=0;j<4;j++){
                for(int k=0;k<4;k++)
                    System.out.println("Watermark Reconstruct "+j+","+k+" = "+ABlok[i][j][k]);
            }*/
        }
        for(int i=0;i<H*W;i++)
        {
            Dequan[i] = Dequantization(ABlok[i],range); // Dequantization process
            Dnorm[i] = Denormalization(Dequan[i],Max,Min); // Denormalization process
            MatRec[i] = InverseTLDCT(Dnorm[i]);  // Inverse Two Level DCT            
        }
        Result = new Mat(H*8,W*8,CvType.CV_32FC1);
        int count =0;
        for(int i=0;i<H*8;i+=8)
        {
            for(int j=0;j<W*8;j+=8)
            {
                Mat submat = Result.submat(new Rect(j,i,8,8));
                MatRec[count].copyTo(submat);
                count++;
            }
        }
        return Result;
   }
   
   public boolean check(int[] arr, int val)// Cheak Random number
    {     
        for(int i=0;i<arr.length;i++)
        {
            if(arr[i]==val)
                return true;
        }
        return false;
    }
   
   
    private int[] getUnsigned(byte[] Block) {
        int [] Result = new int[Block.length];
        for(int i=0;i<Block.length;i++)
        {
           int temp = (int) Block[i];
           Result[i] = temp&0xFF;
           
        }
        return Result;
    }

    private int GetBit(int ZL) {
       int S1 = 1;
       int S2 = 4;
       int P1 = ZL%S1;
       int P2 = ZL%S2;
       if(P1>=P2)
       {
           return 1;
       }
       else
           return 0;
    }

    private int[][] ChangeShape(int[] Watermark) {
        int[][] Result = new int[4][4];
        int x =0;
        int y =0;
        //System.out.println("Ukuran Blok ="+Watermark.length);
        for(int i=0;i<Watermark.length;i++)
        {           
            if(i%4==0&&i>0)
            {
                x++;
                y=0;
                //System.out.println("X' ="+x);
                //System.out.println("Y' ="+y);
                Result[x][y] = Watermark[i];
                y++;
            }
            else
            {
                //System.out.println("X ="+x);
                //System.out.println("Y ="+y);
                 Result[x][y] = Watermark[i];
                 y++;
            }            
        }
        return Result;
    }

    public double[][] Dequantization(int[][] ABlok, int range) {
        double [][]result = new double[4][4];
        for (int i=0;i<4;i++)
        {
            for(int j=0;j<4;j++)
            {
                double temp = (double)ABlok[i][j];
                result[i][j]=((temp+(temp+1))/2)/range;
            }
            
        }
        return result;
    }

    public double[][] Denormalization(double[][] Dequan, double Max, double Min) {
       double [][] result = new double[4][4];
       for(int i=0;i<4;i++)
       {
           for(int j=0;j<4;j++)
           {
              
               result[i][j]= Min+(Dequan[i][j]*(Max-Min));
               //System.out.println("["+i+"]["+j+"] = "+result[i][j]);
           }
       }
       return result;
    }

    public Mat InverseTLDCT(double[][] Dnorm) {
        Mat Temp = new Mat(4,4,CvType.CV_32FC1);
        Mat First = new Mat(4,4,CvType.CV_32FC1);
        Mat Result = new Mat(8,8,CvType.CV_32FC1);
        //System.out.println("Before Inverted =");
        for(int i=0;i<4;i++)
        {
            for(int j=0;j<4;j++)
            {
               
                Temp.put(i, j, Dnorm[i][j]);
            }
        }
        idct(Temp,First); // First Level inverse DCT
        Mat Temp2 = zeros(8,8,CvType.CV_32FC1);  // create padded block matrix
        Mat Submat = Temp2.submat(new Rect(0,0,4,4));
        First.copyTo(Submat);     // Copy first level inverse to padded matrix
        idct(Temp2,Result);  // second level inverse DCT
        return Result;
        
    }

    public Mat decompress(double max, double min) {
        Mat Result;
        double [][][]Dequan = new double[H*W][4][4];
        double[][][] Dnorm = new double[H*W][4][4];
        int range = (int) Math.pow(2, BitSize);
        MatRec = new Mat[H*W];
        for(int i=0;i<H*W;i++)
        {
            Dequan[i] = Dequantization(WBLok[i],range); // Dequantization process
            Dnorm[i] = Denormalization(Dequan[i],max,min); // Denormalization process
            MatRec[i] = InverseTLDCT(Dnorm[i]);  // Inverse Two Level DCT            
        }
        Result = new Mat(H*8,W*8,CvType.CV_32FC1);
        int count =0;
        for(int i=0;i<H*8;i+=8)
        {
            for(int j=0;j<W*8;j+=8)
            {
                Mat submat = Result.submat(new Rect(j,i,8,8));
                MatRec[count].copyTo(submat);
                count++;
            }
        }
        return Result;
    }
}
