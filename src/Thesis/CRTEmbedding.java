/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Thesis;

import java.util.Random;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

/**
 *
 * @author ASUS A46CB
 */
public class CRTEmbedding {
   private long Key;
   private int BitSize;
   private int S1=1;
   private int S2=4;
  
   public void setKey(long K)
   {
       this.Key = K;
   }
   public void setBit(int B)
   {
       this.BitSize = B;
   }
    public Mat embedding(Mat part, byte[][][] watermark) {
        int Height = part.rows()/8;
        int Width = part.cols()/8;
        byte Temp [][] = new byte[Height*Width][64];
        int Block[][] = new int [Height*Width][64];
        Mat Result = new Mat(part.rows(),part.cols(),CvType.CV_8UC1);
        int h =0;
        int w =0;
        long seed = this.Key;
        int count = 0;
        int rndBlockPst [] = new int [Height*Width]; 
        int rndPixPst [] = new int[64];
       Random rndX = new Random (seed);
       for(int i=0; i<part.rows(); i+=8 ) // Separating Block to byte[]
       {
           for(int j=0;j<part.cols();j+=8)
           {
               Mat blok = new Mat(part,new Rect(j,i,8,8));
               //blok.create(8,8,CvType.CV_8UC1);
               blok.get(0, 0, Temp[count]);
               Block[count] = getUnsigned(Temp[count]); //Create Blok Unsigned Integer
               w++;
               count++;
           }
           h++;
           w=0;
       }
       for(int i=0;i<Height*Width;i++) // Random Blok position
        {
            int random = rndX.nextInt((Height*Width)+1);           
           while(check(rndBlockPst,random))
           {
               random = rndX.nextInt((Height*Width)+1);
           }
            rndBlockPst[i] = random;          
        }
       rndX = new Random(seed);
       for(int i=0;i<64;i++) // Random Pixel Position
        {
            int random = rndX.nextInt(65);           
           while(check(rndPixPst,random))
           {
               random = rndX.nextInt(65);
           }
            rndPixPst[i] = random;           
        }
       byte[][] Water = flattened(watermark);
      /* for(int i=0;i<Water.length;i++)
       {
           for(int j=0;j<Water[i].length;j++)
           {
               System.out.println("Watermark ke "+i+","+j+"="+Water[i][j]);
           }
       }*/
       for(int i=0;i<Block.length;i++)  //Embedding Loop
       {
           int[] blk = Block[rndBlockPst[i]-1];
           byte[] wtr = Water[i];
           //System.out.println(" Array Watermark "+wtr.length);
           int WatCounter = 0;
           int BitCounter = 0;
           for(int j=0;j<wtr.length*BitSize;j++)
           {
               //System.out.println("Nilai Blok = "+blk[rndPixPst[j]-1]);
               int ZL =  blk[rndPixPst[j]-1] & 3;
               //System.out.println("Nilai ZL = "+ZL);
               int W = wtr[WatCounter];
             //  System.out.println("Nilai W ["+WatCounter+"] = "+W);
               if(BitCounter<BitSize-1)
               {
                   W =  W<<BitCounter;
                   // System.out.println("Nilai W shifted = "+W);
                   //System.out.println("Bit Ke "+BitCounter);
                   int Bit =  W & (int)Math.pow(2, BitSize-1);
                   //
                   blk[rndPixPst[j]-1] = ChangeCRT(blk[rndPixPst[j]-1],ZL,Bit);
                   BitCounter++;
                  // System.out.println("Nilai Bit = "+Bit);                 
                  
               }
               else
               {
                   
                   
                   //W = wtr[WatCounter];
                   //System.out.println("Nilai W = "+W);
                   //System.out.println("Bit Ke "+BitCounter);
                   W = W<<BitCounter;
                   int Bit =  W & (int)Math.pow(2, BitSize-1);
                   blk[rndPixPst[j]-1] = ChangeCRT(blk[rndPixPst[j]-1],ZL,Bit);
                  BitCounter = 0;
                   WatCounter++;
                   //System.out.println("Nilai Bit = "+Bit);                 
                   //System.out.println("Nilai W shifted = "+W);
                   
               }
           }
           Block[rndBlockPst[i]-1] = blk;
               
       }
       //System.out.println("Finish Embedding watermark !!");
       // Put every block togeher to Mat Result
       count = 0;
       //System.out.println("Lebar = "+part.cols());
       //System.out.println("Tinggi = "+part.rows());
       for(int i=0;i<part.rows();i+=8)
       {
           for(int j=0;j<part.cols();j+=8)
           {
               //System.out.println("Iterasi i :"+i);
               //System.out.println("Iterasi j :"+j);
               byte[] temp = getSigned(Block[count]);
               Mat m = new Mat(8,8,CvType.CV_8UC1);
               m.put(0, 0, temp);
               Mat submat = Result.submat(new Rect(j,i,8,8));
               m.copyTo(submat);
               count++;
           }
       }
           
       return Result;
    }
    public byte[][] flattened (byte[][][] Water)
    {
        byte[][] hasil = new byte[(Water.length*Water[1].length)][16];
        int count =0;
        for(int i=0;i<Water.length;i++)
        {
            for(int j=0;j<Water[i].length;j++)
            {
                hasil[count] = Water[i][j];
                //System.out.println("Watermark Ke "+count+" = "+hasil[count]);
                count++;
            }
        }
        return hasil;
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

    private int[] getUnsigned(byte[] Block) {
        int [] Result = new int[Block.length];
        for(int i=0;i<Block.length;i++)
        {
           int temp = (int) Block[i];
           Result[i] = temp&0xFF;
           
        }
        return Result;
    }

    

    private int ChangeCRT(int blk, int ZL, int Bit) {
       int result=0;
       int p1 = ZL%S1;
       int p2 = ZL%S2;
      if(ZL==0) // Problem
      {
          if(Bit==0)
          {  
              result = blk+1;          
          }          
          else
          {
              result = blk;
          }
      }
      else
      {
          if(Bit==8)
          {  
              result = blk&0xFC;
              
          }
          else
          {
              result = blk;
          }
              
      }
      //System.out.println("Nilai Pixel Awal = "+blk);
      //System.out.println("Nilai Bit ="+Bit);
      //System.out.println("Nilai ZL ="+ZL);
      //System.out.println("After Embedding = "+result);
       return result;
    }

    private byte[] getSigned(int[] Block) {
       byte [] result = new byte[Block.length];
       for(int i=0;i<Block.length;i++)
       {
           result[i] = (byte)Block[i];
       }
       return result;
    }
}
