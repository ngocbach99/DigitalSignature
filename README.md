
<h3> Trước khi bắt đầu ta cần tạo chứng chỉ số cho riêng mình. </h3>

<h4> 1. Tạo CA's private key và Chứng chỉ tự kí </h4>
openssl req -x509 -newkey rsa:4096 -days 365 -nodes -keyout ca-key.pem -out ca-cert.pem -subj "/C=VN/ST=Hanoi/L=Hanoi/O=PTIT/OU=IT/CN=*.myca.com/emailAddress=ngocbachnguyen99@gmail.com"

echo "CA's self-signed certificate"
openssl x509 -in ca-cert.pem -noout -text

<h4> 2. Tạo web server's private key and Yêu cầu kí chứng chỉ (CSR) </h4>
openssl req -newkey rsa:4096 -nodes -keyout server-key.pem -out server-req.pem -subj "/C=VN/ST=BacGiang/L=BacGiang/O=MyAppPTIT/OU=Computer/CN=*.myapp.com/emailAddress=ngocbachnguyen@gmail.com"

<h4> 3. Sử dụng CA's private key để kí web server's CSR và trả về chứng chỉ đã kí.</h4>
openssl x509 -req -in server-req.pem -days 60 -CA ca-cert.pem -CAkey ca-key.pem -CAcreateserial -out server-cert.pem

<h4> 4. Xác thực chứng chỉ <h4>
openssl verify -CAfile ca-cert.pem server-cert.pem

------------------------------------------------------------------------------

<h4>Chuyển đổi định dạng PEM -> PFX <h4>
openssl pkcs12 -export -out certificate.pfx -inkey server-key.key -in server-cert.pem
(Lưu ý hãy chuyển định dạng file key từ .pem và .key đơn giản bằng cách đổi tên file trước khi chuyển đổi )
