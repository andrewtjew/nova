package org.nova.cloudInterfaces;

import org.nova.html.elements.Element;
import org.nova.tracing.Trace;

import com.azure.communication.email.EmailClient;
import com.azure.communication.email.EmailClientBuilder;
import com.azure.communication.email.models.EmailAddress;
import com.azure.communication.email.models.EmailAttachment;
import com.azure.communication.email.models.EmailMessage;
import com.azure.communication.email.models.EmailSendResult;
import com.azure.core.util.BinaryData;
import com.azure.core.util.polling.PollResponse;
import com.azure.core.util.polling.SyncPoller;

public class AzureEmailService extends EmailService
{
    final private EmailClient emailClient;
    final private String senderAddress;

    public AzureEmailService(String connectionString,String senderAddress)
    {
        this.emailClient = new EmailClientBuilder().connectionString(connectionString).buildClient();
        this.senderAddress=senderAddress;
        
    }
    
    @Override
    public void send(Trace parent, String to, String subject, String content, String mediaType) throws Throwable
    {
        EmailAddress toAddress = new EmailAddress(to);
        EmailMessage emailMessage = new EmailMessage()
            .setSenderAddress(this.senderAddress)
            .setToRecipients(toAddress)
            .setSubject(subject)
            .setBodyPlainText(content)
            .setBodyHtml(content);
        SyncPoller<EmailSendResult, EmailSendResult> poller = emailClient.beginSend(emailMessage, null);
        PollResponse<EmailSendResult> result = poller.waitForCompletion();
    }

    @Override
    public void send(Trace parent, String to, String subject, String content, String mediaType, String attachementMediaType, String filename, byte[] attachment) throws Throwable
    {
        EmailAddress toAddress = new EmailAddress(to);
        EmailMessage emailMessage = new EmailMessage()
            .setSenderAddress(this.senderAddress)
            .setToRecipients(toAddress)
            .setSubject(subject)
            .setBodyPlainText(content)
            .setBodyHtml(content);
        EmailAttachment emailAttachement=new EmailAttachment(filename,attachementMediaType,BinaryData.fromBytes(attachment));
        emailMessage.setAttachments(emailAttachement);
        SyncPoller<EmailSendResult, EmailSendResult> poller = emailClient.beginSend(emailMessage, null);
        PollResponse<EmailSendResult> result = poller.waitForCompletion();
    }

    @Override
    public void send(Trace parent, String to, String subject, String plainText, Element html) throws Throwable
    {
        EmailAddress toAddress = new EmailAddress(to);

        EmailMessage emailMessage = new EmailMessage()
            .setSenderAddress(this.senderAddress)
            .setToRecipients(toAddress)
            .setSubject(subject);
        if (plainText!=null)
        {
            emailMessage.setBodyPlainText(plainText);
        }
        if (html!=null)
        {
            String htmlText=""+html.toString()+"";
            emailMessage.setBodyHtml(htmlText);
        }
        SyncPoller<EmailSendResult, EmailSendResult> poller = emailClient.beginSend(emailMessage, null);
        PollResponse<EmailSendResult> result = poller.waitForCompletion();
    }

}
