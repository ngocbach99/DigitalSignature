package com.bach.docx;

import java.nio.file.Files;

import com.aspose.words.CertificateHolder;
import com.aspose.words.DigitalSignatureUtil;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.SignOptions;
import com.aspose.words.SignatureLine;
import com.aspose.words.SignatureLineOptions;

public class AddDigitalSignatureForWord {

	public static void main(String[] args) throws Exception {
		
	Document doc = new Document();
	DocumentBuilder builder = new DocumentBuilder(doc);
	//Cung cấp quyền truy cập vào thuộc tính dòng chữ ký.
	SignatureLine signatureLine = builder.insertSignatureLine(new SignatureLineOptions()).getSignatureLine();
	doc.save("Document.NewSignatureLine.doc");
	
	//SignOptions cho phép chỉ định các tùy chọn để ký tài liệu.
	SignOptions signOptions = new SignOptions();
	signOptions.setSignatureLineId(signatureLine.getId());
	java.nio.file.Path path = java.nio.file.Paths.get("logo.png");
	signOptions.setSignatureLineImage(Files.readAllBytes(path));
	
	//Chỉ định chứng chỉ số
	CertificateHolder certHolder = CertificateHolder.create("certificate.pfx", "bach");
	DigitalSignatureUtil.sign("Document.NewSignatureLine.doc", "Document.NewSignatureLine.doc_out.doc", certHolder, signOptions);  
	
	}
}


