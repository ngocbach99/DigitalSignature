package com.bach.sender;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class Message {
	private List<byte[]> list;
	
	//The constructor of Message class builds the list that will be written to the file. 
	//The list consists of the message and the signature.
	public Message(String data, String keyFile) throws InvalidKeyException, Exception {
		list = new ArrayList<byte[]>();
		list.add(data.getBytes());
		list.add(sign(data, keyFile));
	}
	
	//Phương thức ký dữ liệu bằng khóa cá nhân được lưu trữ trong đường dẫn keyFile.
	public byte[] sign(String data, String keyFile) throws InvalidKeyException, Exception{
		/*
		 * 
		 * Lớp Signature được sử dụng để cung cấp cho các ứng dụng chức năng của một thuật toán chữ ký số. 
		 * Chữ ký điện tử được sử dụng để xác thực và đảm bảo tính toàn vẹn của dữ liệu kỹ thuật số.
		 * 
		 */
		Signature rsa = Signature.getInstance("SHA1withRSA"); 
		
		//Khởi tảo đối tượng để kí
		rsa.initSign(getPrivate(keyFile));
		rsa.update(data.getBytes());
		return rsa.sign();
	}
	
	//Phương pháp lấy Khóa cá nhân từ một tệp
	public PrivateKey getPrivate(String filename) throws Exception {
		byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(spec);
	}
	
	//Phương thức ghi Danh sách byte [] vào một tệp
	private void writeToFile(String filename) throws FileNotFoundException, IOException {
		File f = new File(filename);
		f.getParentFile().mkdirs();
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
	    out.writeObject(list);
		out.close();
		System.out.println("File của bạn đã sẵn sàng.");
	}
	
	public static void main(String[] args) throws InvalidKeyException, IOException, Exception{
		String data = JOptionPane.showInputDialog("Nhập tin nhắn :");
		
		// Truyền vào dữ liệu và khóa cá nhân dùng để kí và lưu xuống file
		new Message(data, "Keys/privateKey").writeToFile("Message/SignedData.txt");
	}
}
	

