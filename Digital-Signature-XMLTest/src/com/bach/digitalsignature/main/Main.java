package com.bach.digitalsignature.main;

import com.bach.digitalsignature.generator.XmlDigitalSignatureGenerator;
import com.bach.digitalsignature.service.CryptoService;
import com.bach.digitalsignature.verifier.XmlDigitalSignatureVerifier;
import java.io.File;

/**
 * 
 * 1. Tạo khóa riêng và khóa chung.
 * 2. Sử dụng Khóa riêng tư ký vào tài liệu.
 * 3. Xác minh tài liệu XML đã ký sử dụng khóa công khai
 *
 */
public class Main {

    public static void main(String[] args) {
        //Tạo khóa 
        String keysDirPath = "keys";
        CryptoService util = new CryptoService();
        util.storeKeyPairs(keysDirPath);
        System.out.println("Khóa công khai và khóa bí mật được tao thành công ...");
        //Kí vào tài liệu XML
        String xmlFilePath = "xml" + File.separator + "employeesalary.xml";
        String signedXmlFilePath = "xml" + File.separator + "digitallysignedEmpSal.xml";
        String privateKeyFilePath = "keys" + File.separator + "privatekey.key";
        String publicKeyFilePath = "keys" + File.separator + "publickey.key";
        XmlDigitalSignatureGenerator xmlSig = new XmlDigitalSignatureGenerator();
        xmlSig.generateXMLDigitalSignature(xmlFilePath, signedXmlFilePath, privateKeyFilePath, publicKeyFilePath);
        //Xác thực chữ kí
        try {
            boolean validFlag = XmlDigitalSignatureVerifier.
                    isXmlDigitalSignatureValid(signedXmlFilePath, publicKeyFilePath);
            System.out.println("Xác thực : " + validFlag);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}