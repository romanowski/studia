import java.io.*;

public class Various2
{
   public int[] getInt()
   {
      return new int[] {0, 1, 2, 3};
   } // end getInt();
   
   public float[] getFloat()
   {
      return new float[] {0.0f, 1.0f, 2.0f, 3.0f};
   } // end getFloat();

   public double[] getDouble()
   {
      return new double[] {0.0d, 1.0d, 2.0d, 3.0d};
   } // end getDouble();
   
   public char[] getChar()
   {
      return new char[] {'a', 'b', 'c', 'd'};
   } // end getChar();

   public boolean[] getBool()
   {
      return new boolean[] {true, false};
   } // end getBool();
   
   public long[] getLong()
   {
      return new long[] {1L, 2L, 3L, 4L};
   } // end getLong();
   
   public Object getObject()
   {
      return new Object();
   } // end getObject();
   
   public Object getSomethingNull()
   {
      return null;
   } // end getObject();
   
   public Various2 getSomeOtherObject()
   {
      return new Various2();
   } // end getObject();
   
   public void getVoid()
   {
      int i = 5;
   } // end getVoid();

   public static void main(String argv[])
   {
      Various2 x = new Various2();
      x.getInt();
      x.getFloat();
      x.getDouble();
      x.getChar();
      x.getBool();
      x.getLong();
      x.getObject();
      x.getSomethingNull();
      x.getSomeOtherObject();
      x.getVoid();
   } // end main();
   
   public String toString()
   {
      return "Hi, universe!";
   } // end toString();
} // end Various2;
