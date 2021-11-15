package com.bach.consumer;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;


public class VerifySignature {

	private List<byte[]> list;

	@SuppressWarnings("unchecked")
	//Phương thức khởi tạo của lớp VerifyMessage lấy các mảng byte từ Tệp
	//in thông báo chỉ khi chữ ký được xác minh.
	public VerifySignature(String filename, String keyFile) throws Exception {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
	    this.list = (List<byte[]>) in.readObject();
	    in.close();
	    
	    System.out.println(verifySignature(list.get(0), list.get(1), keyFile) ? "VERIFIED MESSAGE" + 
	      "\n----------------\n" + new String(list.get(0)) : "Không thể xác thực chữ kí vui lòng kiểm tra lại.");	    
	}
	
	
	private boolean verifySignature(byte[] data, byte[] signature, String keyFile) throws Exception {
		//Cung cấp thuật toán chữ kí số
		Signature sig = Signature.getInstance("SHA1withRSA");
		sig.initVerify(getPublic(keyFile));
		sig.update(data);
		
		return sig.verify(signature);
	}
	
	//Phương pháp lấy Khóa công khai từ tệp
	public PublicKey getPublic(String filename) throws Exception {
		byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
		//Lớp này đại diện cho mã hóa ASN.1 của khóa công khai, được mã hóa theo kiểu ASN.1 
		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePublic(spec);
	}
	
	public static void main(String[] args) throws Exception{
		new VerifySignature("Message/SignedData.txt", "Keys/publicKey");
	}
	
}
