package com.bach.pdf;

import java.util.Scanner;

import com.aspose.pdf.DocMDPAccessPermissions;
import com.aspose.pdf.facades.PdfFileSignature;

public class VerifyDigitalSignatureForPDF {
	
	public static void main(String[] args) {
		
		System.out.println("Tên chữ kí :");
    	Scanner scanner1 = new Scanner(System.in);
    	String signatureName = scanner1.nextLine();
		
		// Tạo một đối tượng PdfFileSignature.
		PdfFileSignature pdfSign = new PdfFileSignature();
		// Gán file pdf đã kí của mình vào đối tượng.
		pdfSign.bindPdf("test-signed.pdf");
		// Xác thực với tên chữ kí.
		if (pdfSign.verifySigned(signatureName))
		{
			if (pdfSign.isCertified()) // Certified?
			{
				if (pdfSign.getAccessPermissions() == DocMDPAccessPermissions.FillingInForms) // Lấy quyền truy cập
				{
					System.out.println("*** Xác thực thành công " + signatureName +" ***");
				}
			} 
		}
	}
	
 }

