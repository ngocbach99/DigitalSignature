package com.bach.key;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
/*
 * 
 *   KeyPairGenerator của Java Security để generate một cặp public key và private key
 *   
 */
public class GenKeys {

	private KeyPairGenerator keyGen;
	private KeyPair pair;
	private PrivateKey privateKey;
	private PublicKey publicKey;

	public GenKeys(int keylength) throws NoSuchAlgorithmException, NoSuchProviderException {
		this.keyGen = KeyPairGenerator.getInstance("RSA");
		this.keyGen.initialize(keylength);
	}

	public void createKeys() {
		this.pair = this.keyGen.generateKeyPair();
		this.privateKey = pair.getPrivate();
		this.publicKey = pair.getPublic();
	}

	public PrivateKey getPrivateKey() {
		return this.privateKey;
	}

	public PublicKey getPublicKey() {
		return this.publicKey;
	}
	
	//Lưu khóa vào file
	public void writeToFile(String path, byte[] key) throws IOException {
		File f = new File(path);
		f.getParentFile().mkdirs();

		FileOutputStream fos = new FileOutputStream(f);
		fos.write(key);
		fos.flush();
		fos.close();
	}

	public static void main(String[] args) {
		GenKeys gk;
		try {
			gk = new GenKeys(1024);
			gk.createKeys();
			gk.writeToFile("Keys/publicKey", gk.getPublicKey().getEncoded());
			gk.writeToFile("Keys/privateKey", gk.getPrivateKey().getEncoded());
			
			if(gk != null) {
				System.out.println(" *** Tạo cặp khóa thành công ***");
			}
			
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

	}

}
