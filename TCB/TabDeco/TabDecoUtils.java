package TCB.TabDeco;

import java.io.DataOutputStream;
import java.io.IOException;

public class TabDecoUtils {
	 public static void writeString(String par0Str, DataOutputStream par1DataOutputStream) throws IOException
	  {
	      if (par0Str.length() > 32767)
	      {
	          throw new IOException("String too big");
	      }
	      else
	      {
	          par1DataOutputStream.writeShort(par0Str.length());
	          par1DataOutputStream.writeChars(par0Str);
	      }
	  }
}
