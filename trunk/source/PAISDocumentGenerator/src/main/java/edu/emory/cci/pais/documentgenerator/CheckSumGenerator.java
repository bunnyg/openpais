package edu.emory.cci.pais.documentgenerator;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * @author Fusheng Wang, Center for Comprehensive Informatics, Emory University
 * @version 1.0 
 * This class generates CheckSums. Not used right now.
 */

/**
 * @author Fusheng Wang, Center for Comprehensive Informatics, Emory University
 * @version 1.0 
 */
public class CheckSumGenerator {

	public CheckSumGenerator() {
	}

	public static long getChecksumValue(String fname) {	
		Checksum checksum = new CRC32();
		return getChecksumValue(checksum, fname);
	}


	public byte[] createChecksum(String filename) {
		try{
			InputStream fis =  new FileInputStream(filename);

			byte[] buffer = new byte[1024];
			MessageDigest complete = MessageDigest.getInstance("MD5");
			int numRead;
			do {
				numRead = fis.read(buffer);
				if (numRead > 0) {
					complete.update(buffer, 0, numRead);
				}
			} while (numRead != -1);
			fis.close();
			return complete.digest();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}  
	}


	public static long getChecksumValue(Checksum checksum, String fname) {
		try {
			BufferedInputStream is = new BufferedInputStream(
					new FileInputStream(fname));
			byte[] bytes = new byte[1024];
			int len = 0;

			while ((len = is.read(bytes)) >= 0) {
				checksum.update(bytes, 0, len);
			}
			is.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println( checksum.toString() );
		return checksum.getValue();
	}

	public static byte[] longToByteArray(long l) 
	{
		byte[] bArray = new byte[8];
		ByteBuffer bBuffer = ByteBuffer.wrap(bArray);
		LongBuffer lBuffer = bBuffer.asLongBuffer();
		lBuffer.put(0, l);
		return bArray;
	}	  

	//String MD5_ad1 = AeSimpleMD5.MD5("Java solutions class libraries");
	public static String MD5(String text) 
	throws NoSuchAlgorithmException, UnsupportedEncodingException  { 
		MessageDigest md;
		md = MessageDigest.getInstance("MD5");
		byte[] md5hash = new byte[32];
		md.update(text.getBytes("iso-8859-1"), 0, text.length());
		md5hash = md.digest();
		return convertToHex(md5hash);
	} 


	private static String convertToHex(byte[] data) { 
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) { 
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do { 
				if ((0 <= halfbyte) && (halfbyte <= 9)) 
					buf.append((char) ('0' + halfbyte));
				else 
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while(two_halfs++ < 1);
		} 
		return buf.toString();
	} 

	public static byte[] longToHex(final long l) {
		long v = l & 0xFFFFFFFFFFFFFFFFL;

		byte[] result = new byte[16];
		Arrays.fill(result, 0, result.length, (byte)0);

		for (int i = 0; i < result.length; i += 2) {
			byte b = (byte) ((v & 0xFF00000000000000L) >> 56);

			byte b2 = (byte) (b & 0x0F);
			byte b1 = (byte) ((b >> 4) & 0x0F);

			if (b1 > 9) b1 += 39;
			b1 += 48;

			if (b2 > 9) b2 += 39;
			b2 += 48;

			result[i] = (byte) (b1 & 0xFF);
			result[i + 1] = (byte) (b2 & 0xFF);

			v <<= 8;
		}

		return result;
	}




	public static void main(String[] args) {
		//String filename = "C:\\temp\\data\\TCGA-02-0001-01Z-00-DX1-tile\\TCGA-02-0001-01Z-00-DX1.svs-0000012288-0000024576.ppm.grid4.mat.xml.zip";
		String filename = "C:\\temp\\data\\TCGA-02-0001-01Z-00-DX1-tile\\TCGA-02-0001-01Z-00-DX1.svs-0000016384-0000024576.ppm.grid4.mat.xml.zip";
		CheckSumGenerator cks = new CheckSumGenerator();

		long cs = cks.getChecksumValue(new CRC32(), filename);
		byte [] csHex = cks.longToHex(cs);
		System.out.println("crc32: " + cs) ;
		System.out.println("cs hex: " +   new String(csHex)  );
		
		byte [] md5 = cks.createChecksum(filename);
		String md5str = null;
		try {
			md5str = new String(md5, "ASCII");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.out.println("md5 in bytes: " + md5);
		String md5Hex = cks.convertToHex(md5);
		System.out.println("md5 hex: " + md5Hex) ;

		//System.out.println("crc32: " + cks.longToByteArray(cs) ) ;
	}

}
