/*******************************************************************************
 * Copyright (C) 2017-2019 Kat Fung Tjew
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.nova.cloudInterfaces;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.nova.html.elements.Element;
import org.nova.logging.Item;
import org.nova.logging.Logger;
import org.nova.tracing.Trace;

public class SMTPEmailService extends EmailService
{
    final private String username;
    final private String password;
    final private String host;
    final private String from;
    final int port;
    final Session session; 
    final Logger logger;
    public SMTPEmailService(String from,String username,String password,String host,int port,int timeout,Logger logger)
    {
        this.username=username;
        this.password=password;
        this.host=host;
        this.port=port;
        this.from=from;
        this.logger=logger;
        
        Properties props=System.getProperties();
        props.put("mail.transport.protocol", "smtps");
        props.put("mail.smtp.port", port); 
        props.put("mail.smtp.connectiontimeout", timeout);
        props.put("mail.smtp.timeout", timeout);
        
        // Set properties indicating that we want to use STARTTLS to encrypt the connection.
        // The SMTP session will begin on an unencrypted connection, and then the client
        // will issue a STARTTLS command to upgrade to an encrypted connection.
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
    
        // Create a Session object to represent a mail session with the specified properties. 
        this.session = Session.getDefaultInstance(props);
    }
    public void send(Trace parent,String to,String subject,String content,String mediaType) throws Throwable
    {
        try (Trace trace=new Trace(parent,"SMTPEmailService.send"))
        {
            trace.setDetails("from:"+from+",to:"+to+",subject:"+subject);
            MimeMessage msg = new MimeMessage(this.session);
            msg.setFrom(new InternetAddress(from));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            msg.setSubject(subject);
            msg.setContent(content,mediaType);
                
            // Create a transport.        
            Transport transport = this.session.getTransport();
            try
            {
                transport.connect(this.host, this.username, this.password);
                transport.sendMessage(msg, msg.getAllRecipients());
                this.logger.log("Emailer",new Item("from",from),new Item("to",to),new Item("subject",subject),new Item("mediaType",mediaType),new Item("content",content));
            }
            catch (Throwable t) 
            {
                this.logger.log(t);
                trace.close(t);
            }
            finally
            {
                transport.close();
            }
        }
    }
    public void send(Trace parent,String to,String subject,String content,String mediaType,String attachementMediaType,String filename,byte[] attachment) throws Throwable
    {
        try (Trace trace=new Trace(parent,"SMTPEmailService.send"))
        {
            trace.setDetails("from:"+from+",to:"+to+",subject:"+subject);
            MimeMessage msg = new MimeMessage(this.session);
            msg.setFrom(new InternetAddress(from));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            msg.setSubject(subject);
            msg.setContent(content,mediaType);
            MimeBodyPart contentBodyPart=new MimeBodyPart();
            contentBodyPart.setContent(content,mediaType);

            Multipart multipart=new MimeMultipart();
            multipart.addBodyPart(contentBodyPart);
            
            BodyPart attachmentBodyPart=new MimeBodyPart();
            DataSource source=new ByteArrayDataSource(attachment,attachementMediaType);
            attachmentBodyPart.setDataHandler(new DataHandler(source));
            attachmentBodyPart.setFileName(filename);
            multipart.addBodyPart(attachmentBodyPart);
            
            msg.setContent(multipart);
            
            // Create a transport.        
            Transport transport = this.session.getTransport();
            try
            {
                transport.connect(this.host, this.username, this.password);
                transport.sendMessage(msg, msg.getAllRecipients());
                this.logger.log(trace,"Emailer",new Item("from",from),new Item("to",to),new Item("subject",subject),new Item("mediaType",mediaType),new Item("content",content));
            }
            catch (Throwable t) 
            {
                trace.close(t);
                this.logger.log(trace,"Emailer",new Item("from",from),new Item("to",to),new Item("subject",subject),new Item("mediaType",mediaType),new Item("content",content));
            }
            finally
            {
                transport.close();
            }
        }        
    }
    @Override
    public void send(Trace parent, String to, String subject, String plainText, Element html) throws Throwable
    {
        // TODO Auto-generated method stub
        
    }
    
}
