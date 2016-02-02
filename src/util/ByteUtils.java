package util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ByteUtils {
	public static byte[] serialize(Object obj) throws IOException {
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    ObjectOutputStream os = new ObjectOutputStream(out);
	    os.writeObject(obj);
	    return out.toByteArray();
	}
	public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
	    ByteArrayInputStream in = new ByteArrayInputStream(data);
	    ObjectInputStream is = new ObjectInputStream(in);
	    return is.readObject();
	}
	
	public static byte[] toByteArray(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		byte[] buffer = new byte[4096];
		 
		int byteRead;
		 
		while ((byteRead = is.read(buffer)) != -1) {
		    baos.write(buffer, 0, byteRead);
		}
		baos.close();
		
		return baos.toByteArray();
	}
}
