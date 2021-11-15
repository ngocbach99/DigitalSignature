package com.bach.digitalsignature.generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Collections;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.bach.digitalsignature.service.CryptoService;

public class XmlDigitalSignatureGenerator {

    /**
     * 
     * Phương thức được sử dụng để lấy tài liệu XML bằng cách phân tích cú pháp
     *
     */
    private Document getXmlDocument(String xmlFilePath) {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        //Chỉ định rằng trình phân tích cú pháp do mã này tạo ra sẽ cung cấp hỗ trợ cho XML.
        dbf.setNamespaceAware(true);
        try {
            doc = dbf.newDocumentBuilder().parse(new FileInputStream(xmlFilePath));
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return doc;
    }

    /**
     * 
     * Phương thức được sử dụng để lấy KeyInfo
     *  KeyInfo chứa danh sách các Cấu trúc XML, 
     *  	mỗi cấu trúc chứa thông tin cho phép (các) người nhận lấy khóa cần thiết để xác thực chữ ký XML.
     *
     *	KeyValue chứa một khóa công khai duy nhất có thể hữu ích trong việc xác thực chữ ký
     */
    private KeyInfo getKeyInfo(XMLSignatureFactory xmlSigFactory, String publicKeyPath) {
        KeyInfo keyInfo = null;
        KeyValue keyValue = null;
        PublicKey publicKey = new CryptoService().getStoredPublicKey(publicKeyPath);
        KeyInfoFactory keyInfoFact = xmlSigFactory.getKeyInfoFactory();

        try {
            keyValue = keyInfoFact.newKeyValue(publicKey);
        } catch (KeyException ex) {
            ex.printStackTrace();
        }
        keyInfo = keyInfoFact.newKeyInfo(Collections.singletonList(keyValue));
        return keyInfo;
    }

    /*
     * Phương thức được sử dụng để lưu trữ tài liệu XMl đã ký
     * 
     */
    private void storeSignedDoc(Document doc, String destnSignedXmlFilePath) {
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer trans = null;
        try {
            trans = transFactory.newTransformer();
        } catch (TransformerConfigurationException ex) {
            ex.printStackTrace();
        }
        try {
            StreamResult streamRes = new StreamResult(new File(destnSignedXmlFilePath));
            trans.transform(new DOMSource(doc), streamRes);
        } catch (TransformerException ex) {
            ex.printStackTrace();
        }
        System.out.println("Tệp XML với chữ ký điện tử đính kèm được tạo thành công ...");
    }

    /**
     * 
     * Phương pháp được sử dụng để đính kèm chữ ký điện tử đã tạo vào tài liệu hiện có
     *
     */
    public void generateXMLDigitalSignature(String originalXmlFilePath,
            String destnSignedXmlFilePath, String privateKeyFilePath, String publicKeyFilePath) {
        //Lấy ra đối tượng XML Document
        Document doc = getXmlDocument(originalXmlFilePath);
        //Trả về một XMLSignatureFactory hỗ trợ cơ chế xử lý XML cụ thể và kiểu biểu diễn cụ thể ở đây là DOM.
        XMLSignatureFactory xmlSigFactory = XMLSignatureFactory.getInstance("DOM");
        PrivateKey privateKey = new CryptoService().getStoredPrivateKey(privateKeyFilePath);
        
        /*
         * Một XMLSignContext dành riêng cho DOM. Lớp này chứa phương thức bổ sung để chỉ định vị trí trong cây DOM 
         * nơi đối tượng XMLSignature Object sẽ được sắp xếp khi tạo chữ ký.
         * 
         */
        DOMSignContext domSignCtx = new DOMSignContext(privateKey, doc.getDocumentElement());
        Reference ref = null;
        SignedInfo signedInfo = null;
        try {
            ref = xmlSigFactory.newReference("", xmlSigFactory.newDigestMethod(DigestMethod.SHA1, null),
                    Collections.singletonList(xmlSigFactory.newTransform(Transform.ENVELOPED,
                    (TransformParameterSpec) null)), null, null);
            signedInfo = xmlSigFactory.newSignedInfo(
                    xmlSigFactory.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE,
                    (C14NMethodParameterSpec) null),
                    xmlSigFactory.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
                    Collections.singletonList(ref));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } catch (InvalidAlgorithmParameterException ex) {
            ex.printStackTrace();
        }
        //Chuyển đường dẫn tệp khóa công khai.
        KeyInfo keyInfo = getKeyInfo(xmlSigFactory, publicKeyFilePath);
        //Tạo mới một đối tượng XML Signature -chuyển đổi các khóa khóa mã hóa thành các thông số kỹ thuật.
        XMLSignature xmlSignature = xmlSigFactory.newXMLSignature(signedInfo, keyInfo);
        try {
            //Kí vào tài liệu
            xmlSignature.sign(domSignCtx);
        } catch (MarshalException ex) {
            ex.printStackTrace();
        } catch (XMLSignatureException ex) {
            ex.printStackTrace();
        }
        //Lưu trữ tài liệu
        storeSignedDoc(doc, destnSignedXmlFilePath);
    }
}
