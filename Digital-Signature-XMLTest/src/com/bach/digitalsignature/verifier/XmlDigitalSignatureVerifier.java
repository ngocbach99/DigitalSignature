package com.bach.digitalsignature.verifier;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.PublicKey;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.bach.digitalsignature.service.CryptoService;

/**
 * 
 * Xác minh tài liệu XML
 * 
 */
public class XmlDigitalSignatureVerifier {
    /**
     * 
     * Phương thức được sử dụng để lấy đối tượng tài liệu XML bằng cách phân tích cú pháp tệp xml
     *  
     */
    private static Document getXmlDocument(String xmlFilePath) {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
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
     * Phương thức xác thực chữ kí
     * 
     */
    public static boolean isXmlDigitalSignatureValid(String signedXmlFilePath, 
            String pubicKeyFilePath) throws Exception {
        boolean validFlag = false;
        Document doc = getXmlDocument(signedXmlFilePath);
        
        //Xác định vị trí các nút
        NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
        if (nl.getLength() == 0) {
            throw new Exception("Không tìm thấy chữ ký số XML ...");
        }
        //Lấy Public Key
        PublicKey publicKey = new CryptoService().getStoredPublicKey(pubicKeyFilePath);
        
        //Lớp này chứa các phương thức bổ sung để chỉ định vị trí trong cây DOM, 
        //nơi XMLSignatureis sẽ không được quản lý và xác thực từ đó.
        DOMValidateContext valContext = new DOMValidateContext(publicKey, nl.item(0));
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
        XMLSignature signature = fac.unmarshalXMLSignature(valContext);
        validFlag = signature.validate(valContext);
        return validFlag;
    }
}

