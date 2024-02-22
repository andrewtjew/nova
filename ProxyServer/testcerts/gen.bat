del server.*
del client.*

rem ----- server -------------
rem A server certficate allows the client to trust the server.
rem To complete trust, the client *must* validate check that the server certficate presented to the client has the right host name.

rem create some random text
openssl rand -out server.txt 4096

rem Create a private key of server
openssl genrsa -aes128 -out server.key 4096 -rand server.txt

rem Create server certificate. 
rem No password is needed to use this certificate, so it should not be distributed. It is also needed to generate a corresponding client certficate.
openssl req -new -x509 -days 10000 -newkey rsa:2048 -sha256 -key server.key -out server.crt -passin pass:password1

rem Server certificate in pfx format for others to use as it is password protected. Contains both the certificate and private key.
rem Can be used by Jetty as server certificate. C# HttpClient and java HttpClient can use this certificate directly.
rem C# HttpListener can use this certficate in an indirect way:
rem Open mmc and import it into the personal store. Then associate with program using netsh. Example:
rem netsh http add urlacl url=https://+:10051/ user=azuread\andrewtjew
rem netsh http add sslcert ipport=0.0.0.0:10051 certhash=E448BED6BF7B0F0C17F1F9B334F93674F9BC93925C820BEE35E5A5C859A96BA9 appid={b5c62c12-b5ea-4806-b8d5-bb7a7c023d0f}
rem combine key and cert into pkcs12 before we can import into jks. Export password must be entered. 
openssl pkcs12 -inkey server.key -in server.crt -export -out server.pfx -passin pass:password1 -passout pass:password1
rem put pfx in jks for jetty to use
keytool -importkeystore -srckeystore server.pfx -srcstoretype PKCS12 -destkeystore server.jks -storepass password1

rem The browser cannot use either server.crt or server.pfx. The browser will initially display that it cannot validate the server and offers the option to continue or stop.
rem If the user chooses to continue, the browser will display the web page as not secure.
rem For the browser to trust the server, create a server certificate signing request. 
rem The csr is to be signed by a well-known certificate authority, producing a certificate that can be used by the browser to validate the server.
rem We don't currently generate the csr
rem openssl req -new -key server.key -out server.csr


rem ---- client side --------------
rem A client certificate is almost the same as the server certficate, but must be signed by the server.key.
rem This allows the server to trust the client.

rem create some random text
openssl rand -out client.txt 4096

rem Create a private key of server
openssl genrsa -aes128 -out client.key 4096 -rand client.txt

rem create client certificate signing request
openssl req -new -key client.key -out client.csr -passin pass:password1 

rem create self-signed client certicate using server.key.
rem The serial number should be changed if the same server.key is used to sign another client certficate.
rem The signing associates the client certificate with the server certficate, hence we need to pass in server.crt
openssl x509 -req -days 10000 -in client.csr -CA server.crt -CAkey server.key -set_serial 01 -out client.crt -passin pass:password1

rem Password protect the certicate. Jetty uses it from the client.jks. C# HttpListener can just load it as X509Certificate2.
rem The browser can use it from the certficate store. Place it in the personal store.
openssl pkcs12 -export -clcerts -in client.crt -inkey client.key -out client.pfx -passin pass:password1 -passout pass:password1
rem For jetty to to use
keytool -importkeystore -srckeystore client.pfx -srcstoretype PKCS12 -destkeystore client.jks -storepass password1
rem for openssl
rem openssl pkcs12 -in client.pfx -out client.pem -clcerts

