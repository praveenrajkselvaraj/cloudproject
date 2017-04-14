package startup;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AESFileEncryption {
	
	public boolean encryptFile(String fileName, String encryptedFileName, String saltFileName, String ivFileName){
		
		try{
			// file to be encrypted
			FileInputStream inFile = new FileInputStream(fileName);

			// encrypted file
			FileOutputStream outFile = new FileOutputStream(encryptedFileName);

			// password to encrypt the file
			String password = "twincloud";

			byte[] salt = new byte[8];
			SecureRandom secureRandom = new SecureRandom();
			secureRandom.nextBytes(salt);
			FileOutputStream saltOutFile = new FileOutputStream(saltFileName);
			saltOutFile.write(salt);
			saltOutFile.close();

			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
			SecretKey secretKey = factory.generateSecret(keySpec);
			SecretKey secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secret);
			AlgorithmParameters params = cipher.getParameters();

			FileOutputStream ivOutFile = new FileOutputStream(ivFileName);
			byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
			ivOutFile.write(iv);
			ivOutFile.close();

			//file encryption
			byte[] input = new byte[64];
			int bytesRead;

			while ((bytesRead = inFile.read(input)) != -1) {
				byte[] output = cipher.update(input, 0, bytesRead);
				if (output != null)
					outFile.write(output);
			}

			byte[] output = cipher.doFinal();
			if (output != null)
				outFile.write(output);

			inFile.close();
			outFile.flush();
			outFile.close();

			System.out.println("File Encrypted.");
			return true;
		
		} catch (Exception ex){
			ex.printStackTrace();
			return false;
		}
	}

}
