import java.io.*;

public class Various
{
   public int getInt()
   {
      return 4;
   } // end getInt();
   
   public float getFloat()
   {
      return 1.2345f;
   } // end getFloat();

   public double getDouble()
   {
      return 1.2345d;
   } // end getDouble();
   
   public char getChar()
   {
      return 'a';
   } // end getChar();

   public boolean getBool()
   {
      return false;
   } // end getBool();
   
   public long getLong()
   {
      return 1234567890L;
   } // end getLong();

   public static void main(String argv[])
   {
      Various x = new Various();
      x.getInt();
      x.getFloat();
      x.getDouble();
      x.getChar();
      x.getBool();
      x.getLong();
   } // end main();
} // end Various;
