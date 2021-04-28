package tools;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public class BytePacker {
	
	public static void packIntToArray(byte[] array, int n, int beginIndex){
		for(int i = 0; i < 4; i++){
			array[beginIndex + i] = (byte)(n >> (8 * i));
		}
	}
	
	public static int unpackIntFromArray(byte[] array, int beginIndex){
		int r = 0;
		for(int i = 0; i < 4; i++){
			r = r | (((int)array[beginIndex + i] & 255) << (8 * i));
		}
		return r;
	}
	
	/////////////////////////////////////////////////////////////////////////////
	
	public static void packLongToArray(byte[] array, long n, int beginIndex){
		for(int i = 0; i < 8; i++){
			array[beginIndex + i] = (byte)(n >> (8 * i));
		}
	}
	
	public static long unpackLongFromArray(byte[] array, int beginIndex){
		long r = 0;
		for(int i = 0; i < 8; i++){
			r = r | (((long)array[beginIndex + i] & 255) << (8 * i));
		}
		return r;
	}
	
	/////////////////////////////////////////////////////////////////////////////
	
	public static void packFloatToArray(byte[] array, float n, int beginIndex){
		int bits = Float.floatToIntBits(n);
		for(int i = 0; i < 4; i++){
			array[beginIndex + i] = (byte)(bits >> (8 * i));
		}
	}
	
	public static float unpackFloatFromArray(byte[] array, int beginIndex){
		int r = unpackIntFromArray(array, beginIndex);
		return Float.intBitsToFloat(r);
	}
	
	/////////////////////////////////////////////////////////////////////////////
	
	public static void packDoubleToArray(byte[] array, double n, int beginIndex){
		long bits = Double.doubleToLongBits(n);
		for(int i = 0; i < 8; i++){
			array[beginIndex + i] = (byte)(bits >> (8 * i));
		}
	}
	
	public static double unpackDoubleFromArray(byte[] array, int beginIndex){
		long r = unpackLongFromArray(array, beginIndex);
		return Double.longBitsToDouble(r);
	}
	
	/////////////////////////////////////////////////////////////////////////////
	
	public static void packShortToArray(byte[] array, short n, int beginIndex){
		for(int i = 0; i < 2; i++){
			array[beginIndex + i] = (byte)((n >> (8 * i)) & 0xff);
		}
	}
	
	public static short unpackShortFromArray(byte[] array, int beginIndex){
		return ByteBuffer.wrap(new byte[]{array[beginIndex], array[beginIndex+1]}).order(ByteOrder.LITTLE_ENDIAN).getShort();
	}
	
	/////////////////////////////////////////////////////////////////////////////
	
	private static final Charset CHARSET = Charset.forName("UTF-8");
	
	public static int packStringToArray(byte[] array, String s, int beginIndex){
		byte[] bytes = s.getBytes(CHARSET);
		
		short len = Short.MIN_VALUE;
		for(int i = 0; i < bytes.length; i++){
			try{
				array[beginIndex + i + 2] = bytes[i];
			}catch(ArrayIndexOutOfBoundsException e){
				break;
			}
			len++;
		}
		
		packShortToArray(array, len, beginIndex);
		return len - Short.MIN_VALUE +2;
	}

	public static UnpackedString unpackStringFromArray(byte[] array, int beginIndex){
		int len = (int)unpackShortFromArray(array, beginIndex) - Short.MIN_VALUE;
		
		byte[] bytes = new byte[len];
		for(int i = 0; i < len; i++){
			try{
				bytes[i] = array[beginIndex + i + 2];
			}catch(ArrayIndexOutOfBoundsException e){
				break;
			}
		}

		return new UnpackedString(new String(bytes, CHARSET), len + 2);
	}
	
	public static class UnpackedString{
		public String string;
		public int length;
		
		private UnpackedString(String str, int len){
			length = len;
			string = str;
		}
	}
}
