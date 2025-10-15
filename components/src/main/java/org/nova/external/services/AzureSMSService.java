package org.nova.external.services;

import org.nova.concurrent.MultiTaskScheduler;
import org.nova.tracing.Trace;
import org.nova.tracing.TraceRunnable;

import com.azure.communication.sms.SmsClient;
import com.azure.communication.sms.SmsClientBuilder;


public class AzureSMSService extends SMSService
{
    final private SmsClient client;
    final private String senderPhoneNumber;
    final private MultiTaskScheduler scheduler;

    public AzureSMSService(MultiTaskScheduler scheduler,String connectionString,String senderPhoneNumber)
    {

        this.client= new SmsClientBuilder().connectionString(connectionString).buildClient();        
        this.scheduler=scheduler;
        this.senderPhoneNumber=senderPhoneNumber;
    }
    @Override
    public String send(Trace parent, String phoneNumber, String message)
    {
        this.scheduler.schedule(parent, "AzureSMSService", new TraceRunnable()
        {
            @Override
            public void run(Trace parent) throws Throwable
            {
                client.send(senderPhoneNumber,phoneNumber,message);
            }
        });
        return null;
    }
}
