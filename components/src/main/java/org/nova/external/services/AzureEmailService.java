package org.nova.external.services;

import org.nova.concurrent.MultiTaskScheduler;
import org.nova.html.elements.Element;
import org.nova.tracing.Trace;
import org.nova.tracing.TraceRunnable;

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
    final private EmailClient client;
    final private String senderAddress;
    final private MultiTaskScheduler scheduler;

    public AzureEmailService(MultiTaskScheduler scheduler,String connectionString,String senderAddress)
    {
        this.client = new EmailClientBuilder().connectionString(connectionString).buildClient();
        this.senderAddress=senderAddress;
        this.scheduler=scheduler;
        
        
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
        send(parent,emailMessage);
    }
    
    void send(Trace parent,EmailMessage emailMessage)
    {
        this.scheduler.schedule(parent, "AzureEmailService", new TraceRunnable()
        {

            @Override
            public void run(Trace parent) throws Throwable
            {
                SyncPoller<EmailSendResult, EmailSendResult> poller = client.beginSend(emailMessage, null);
                PollResponse<EmailSendResult> result = poller.waitForCompletion();
            }
        });
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
        SyncPoller<EmailSendResult, EmailSendResult> poller = client.beginSend(emailMessage, null);
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
        send(parent,emailMessage);
    }

}
