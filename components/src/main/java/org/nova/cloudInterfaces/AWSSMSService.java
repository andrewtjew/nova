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

import java.util.HashMap;
import java.util.Map;

import org.nova.logging.Item;
import org.nova.logging.Logger;
import org.nova.tracing.Trace;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

public class AWSSMSService extends SMSService
{
    final private AmazonSNS sns;
    final private Logger logger;
    
    public AWSSMSService(Logger logger,String accessKey, String secretKey, Regions regions)
    {
        this.logger=logger;
        this.sns=AmazonSNSClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .withRegion(regions)
                .build();
    }

    public String send(Trace parent,String phoneNumber, String message)
    {
        try (Trace trace=new Trace(parent,"AWSSMSService.send"))
        {
            try
            {
                Map<String, MessageAttributeValue> messageAttributes = new HashMap<String, MessageAttributeValue>();
                MessageAttributeValue value=new MessageAttributeValue();
                value.setDataType("String");
                value.setStringValue("+18885988137");
                messageAttributes.put("AWS.MM.SMS.OriginationNumber", value);
                              
                
                PublishResult result = this.sns.publish(new PublishRequest().withMessageAttributes(messageAttributes).withMessage(message).withPhoneNumber(phoneNumber));
                
                String id=result.getMessageId();
                this.logger.log(trace,new Item("phoneNumber",phoneNumber),new Item("message",message),new Item("id",id));
                System.out.println("sent to "+phoneNumber+", result="+result.toString()+",message="+message);
                return id;
            }
            catch (Throwable t)
            {
                trace.close(t);
                this.logger.log(trace,new Item("phoneNumber",phoneNumber),new Item("message",message));
                throw t;
            }
        }
        
    }
}
