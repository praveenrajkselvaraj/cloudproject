package startup;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AESFileDecryption {
	
	public void decryptFile(String fileName){
		
		try{
			String password = "twincloud";
			String fileName1 = fileName.substring(fileName.lastIndexOf("/"),fileName.lastIndexOf("."));

			FileInputStream saltFis = new FileInputStream(fileName1+"_salt.enc");
			byte[] salt = new byte[8];
			saltFis.read(salt);
			saltFis.close();

			// reading the iv
			FileInputStream ivFis = new FileInputStream(fileName1+"_iv.enc");
			byte[] iv = new byte[16];
			ivFis.read(iv);
			ivFis.close();

			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
			SecretKey tmp = factory.generateSecret(keySpec);
			SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

			// file decryption
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
			FileInputStream fis = new FileInputStream("encryptedfile.des");
			FileOutputStream fos = new FileOutputStream("plainfile_decrypted.txt");
			byte[] in = new byte[64];
			int read;
			while ((read = fis.read(in)) != -1) {
				byte[] output = cipher.update(in, 0, read);
				if (output != null)
					fos.write(output);
			}

			byte[] output = cipher.doFinal();
			if (output != null)
				fos.write(output);
			fis.close();
			fos.flush();
			fos.close();
			System.out.println("File Decrypted.");
		
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
}
