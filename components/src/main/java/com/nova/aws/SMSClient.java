package com.nova.aws;

import java.util.HashMap;
import java.util.Map;

import org.nova.logging.Item;
import org.nova.logging.Logger;
import org.nova.tracing.Trace;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

public class SMSClient
{
    final private Map<String, MessageAttributeValue> attributes; 
    final private AmazonSNSClient client;
    final private Logger logger;
    
    public SMSClient(Logger logger,String accessKeyId,String secretKey,long maximumPrice,boolean transactional)
    {
        this.logger=logger;
        System.setProperty("aws.accessKeyId", accessKeyId);
        System.setProperty("aws.secretKey", secretKey);
        this.attributes =new HashMap<String, MessageAttributeValue>();
        String price=String.format("%.2f", maximumPrice*0.01);
        this.attributes.put("AWS.SNS.SMS.MaxPrice", new MessageAttributeValue().withStringValue(price).withDataType("Number"));
        String type=transactional?"Transactional":"Promotional";
        this.attributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue().withStringValue(type).withDataType("String"));        
        this.client= new AmazonSNSClient();
    }    
    
    public String send(Trace parent,String phoneNumber,String message)
    {
        try (Trace trace=new Trace(parent,"SMSClient.publish"))
        {
            try
            {
                PublishResult result = this.client.publish(new PublishRequest()
                        .withMessage(message)
                        .withPhoneNumber(phoneNumber)
                        .withMessageAttributes(this.attributes));
                logger.log("SMSClient.publish", new Item("phoneMumber",phoneNumber),new Item("message",message),new Item("result",result));
                return result.getMessageId();
            }
            catch (Throwable t)
            {
                logger.log(t,"SMSClient.publish", new Item("phoneMumber",phoneNumber),new Item("message",message));
                trace.close(t);
                throw t;
            }
        }        
    }
    
    public static String cleanZone1(String phone)
    {
        StringBuilder sb=new StringBuilder();
        for (char c:phone.toCharArray())
        {
            if (Character.isDigit(c))
            {
                sb.append(c);
            }
        }
        if (sb.length()==10)
        {
            return "+1"+sb.toString();
        }
        else if (sb.length()==11)
        {
            if (sb.charAt(0)=='1')
            {
                return "+"+sb.toString();
            }
        }
        return null;
    }
    
}
