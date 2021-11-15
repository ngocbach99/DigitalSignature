package com.bach.pdf;

import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;

import com.aspose.pdf.DocMDPAccessPermissions;
import com.aspose.pdf.DocMDPSignature;
import com.aspose.pdf.Document;
import com.aspose.pdf.PKCS7;

import com.aspose.pdf.facades.PdfFileSignature;

public class AddDigitalSignatureForPDF {

    public static void main(String[] args) {
    	
    	System.out.println("Tên file cần kí :");
    	Scanner scanner0 = new Scanner(System.in);
    	String fileName = scanner0.nextLine();

    	// Tạo một đối tượng Document
    	Document doc = new Document(fileName);
    	PdfFileSignature signature = new PdfFileSignature(doc);
    	// Chuẩn PKCS - mô tả cú pháp chung cho dữ liệu có thể áp dụng mật mã
    	PKCS7 pkcs = new PKCS7("certificate.pfx", "bach"); 
    	DocMDPSignature docMdpSignature = new DocMDPSignature(pkcs, DocMDPAccessPermissions.FillingInForms);
    	//Vị trí đặt chữ kí
    	Rectangle rect = new Rectangle(100, 500, 400, 100);                                          
    	// Set giao diện chữ kí
    	signature.setSignatureAppearance("logo.png"); 
    	
    	// Khởi tạo và set thông tin cho chữ ký
    	System.out.println("Lí do ký :");
    	Scanner scanner1 = new Scanner(System.in);
    	String signatureReason = scanner1.nextLine();
    	
    	System.out.println("Thông tin liên hệ :");
    	Scanner scanner2 = new Scanner(System.in);
    	String contact = scanner2.nextLine();
    	
    	System.out.println("Địa chỉ nơi ký :");
    	Scanner scanner3 = new Scanner(System.in);
    	String location = scanner3.nextLine();
    	
    	signature.certify(1, signatureReason, contact, location, true, rect, docMdpSignature);
    	
    	// Lưu tệp chữ kí
    	String[] fileName1 = fileName.split("\\.");
    	String fileNameSigned = fileName1[0] + "-signed.pdf";	
    	signature.save(fileNameSigned);
    	if(signature != null) {
    		System.out.println("... Thành công ...");
    	}
    }
}

