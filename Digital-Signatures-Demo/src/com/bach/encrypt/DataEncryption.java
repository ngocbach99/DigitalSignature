package com.bach.encrypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;

	public class DataEncryption {
		
		//Lớp cipher cung cấp chức năng của một bộ mật mã để mã hóa và giải mã.
		private Cipher cipher;

		public DataEncryption() throws NoSuchAlgorithmException, NoSuchPaddingException {
			this.cipher = Cipher.getInstance("RSA");
		}

		// https://docs.oracle.com/javase/8/docs/api/java/security/spec/PKCS8EncodedKeySpec.html
		public PrivateKey getPrivate(String filename) throws Exception {
			byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
			
			//Lớp này đại diện cho mã hóa ASN.1 của khóa cá nhân, được mã hóa theo kiểu ASN.1 
			PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			
			//Trả về một đối tượng khóa riêng tư từ đặc điểm kĩ thuật khóa được cung cấp.
			return kf.generatePrivate(spec);
		}

		// https://docs.oracle.com/javase/8/docs/api/java/security/spec/X509EncodedKeySpec.html
		public PublicKey getPublic(String filename) throws Exception {
			byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
			X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			return kf.generatePublic(spec);
		}

		public void encryptFile(byte[] input, File output, PrivateKey key) throws IOException, GeneralSecurityException {
			this.cipher.init(Cipher.ENCRYPT_MODE, key);
			writeToFile(output, this.cipher.doFinal(input));
		}

		public void decryptFile(byte[] input, File output, PublicKey key) 
			throws IOException, GeneralSecurityException {
			this.cipher.init(Cipher.DECRYPT_MODE, key);
			writeToFile(output, this.cipher.doFinal(input));
		}

		private void writeToFile(File output, byte[] toWrite)
				throws IllegalBlockSizeException, BadPaddingException, IOException {
			FileOutputStream fos = new FileOutputStream(output);
			fos.write(toWrite);
			fos.flush();
			fos.close();
		}

		public String encryptText(String msg, PrivateKey key) 
				throws NoSuchAlgorithmException, NoSuchPaddingException,
				UnsupportedEncodingException, IllegalBlockSizeException, 
				BadPaddingException, InvalidKeyException {
			this.cipher.init(Cipher.ENCRYPT_MODE, key);
			return Base64.encodeBase64String(cipher.doFinal(msg.getBytes("UTF-8")));
		}

		public String decryptText(String msg, PublicKey key)
				throws InvalidKeyException, UnsupportedEncodingException, 
				IllegalBlockSizeException, BadPaddingException {
			this.cipher.init(Cipher.DECRYPT_MODE, key);
			return new String(cipher.doFinal(Base64.decodeBase64(msg)), "UTF-8");
		}

		public byte[] getFileInBytes(File f) throws IOException {
			FileInputStream fis = new FileInputStream(f);
			byte[] fbytes = new byte[(int) f.length()];
			fis.read(fbytes);
			fis.close();
			return fbytes;
		}

		public static void main(String[] args) throws Exception {
			DataEncryption ac = new DataEncryption();
			PrivateKey privateKey = ac.getPrivate("Keys/privateKey");
			PublicKey publicKey = ac.getPublic("Keys/publicKey");

			String msg = "This is my message!";
			String encrypted_msg = ac.encryptText(msg, privateKey);
			String decrypted_msg = ac.decryptText(encrypted_msg, publicKey);
			System.out.println("Original Message: " + msg + 
				"\nEncrypted Message: " + encrypted_msg
				+ "\nDecrypted Message: " + decrypted_msg);

			if (new File("Keys/text.txt").exists()) {
				ac.encryptFile(ac.getFileInBytes(new File("Keys/text.txt")), 
					new File("Keys/text_encrypted.txt"),privateKey);
				ac.decryptFile(ac.getFileInBytes(new File("Keys/text_encrypted.txt")),
					new File("Keys/text_decrypted.txt"), publicKey);
			} else {
				System.out.println("Vui lòng tạo một file text.txt bên dưới folder Keys");
			}
		}
	
}
