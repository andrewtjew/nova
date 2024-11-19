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
package org.nova.external.services;

//import java.util.HashMap;
//import java.util.Map;
//
//import org.nova.logging.Item;
//import org.nova.logging.Logger;
//import org.nova.tracing.Trace;
//
//import com.amazonaws.auth.AWSCredentials;
//import com.amazonaws.auth.AWSStaticCredentialsProvider;
//import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.regions.Regions;
//import com.amazonaws.services.sns.AmazonSNS;
//import com.amazonaws.services.sns.AmazonSNSClientBuilder;
//import com.amazonaws.services.sns.model.PublishRequest;
//import com.amazonaws.services.sns.model.PublishResult;
//
//import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.pinpoint.PinpointClient;
//import software.amazon.awssdk.services.pinpoint.model.AddressConfiguration;
//import software.amazon.awssdk.services.pinpoint.model.ChannelType;
//import software.amazon.awssdk.services.pinpoint.model.DirectMessageConfiguration;
//import software.amazon.awssdk.services.pinpoint.model.MessageRequest;
//import software.amazon.awssdk.services.pinpoint.model.MessageResponse;
//import software.amazon.awssdk.services.pinpoint.model.PinpointException;
//import software.amazon.awssdk.services.pinpoint.model.SMSMessage;
//import software.amazon.awssdk.services.pinpoint.model.SendMessagesRequest;
//import software.amazon.awssdk.services.pinpoint.model.SendMessagesResponse;

//public class AWSPinPointSMSService extends SMSService
//{
//    final private Logger logger;
//    final private PinpointClient pinpoint;
//    
//    public AWSPinPointSMSService(Logger logger,String accessKey, String secretKey)
//    {
//        this.pinpoint = PinpointClient.builder()
//                .region(Region.US_EAST_2)
//                .credentialsProvider(ProfileCredentialsProvider.create())
//                .build();
//        
//        this.logger=logger;
//    }
//
//    public String send(Trace parent,String phoneNumber, String message)
//    {
//        String appId="b8027a964b8b444d80ffc5e0f323766e";
//        String originationNumber="+18885988137";
//        String messageType="TRANSACTIONAL";
//        
//        try {
//            Map<String, AddressConfiguration> addressMap = new HashMap<String, AddressConfiguration>();
//            AddressConfiguration addConfig = AddressConfiguration.builder()
//                .channelType(ChannelType.SMS)
//                .build();
//
//            addressMap.put(phoneNumber, addConfig);
//            SMSMessage smsMessage = SMSMessage.builder()
//                .body(message)
//                .messageType(messageType)
//                .originationNumber(originationNumber)
////                .senderId(senderId)
////                .keyword(registeredKeyword)
//                .build();
//
//            // Create a DirectMessageConfiguration object.
//            DirectMessageConfiguration direct = DirectMessageConfiguration.builder()
//                .smsMessage(smsMessage)
//                .build();
//
//            MessageRequest msgReq = MessageRequest.builder()
//                .addresses(addressMap)
//                .messageConfiguration(direct)
//                .build();
//
//            // create a  SendMessagesRequest object
//            SendMessagesRequest request = SendMessagesRequest.builder()
//                .applicationId(appId)
//                .messageRequest(msgReq)
//                .build();
//
//            SendMessagesResponse response= pinpoint.sendMessages(request);
//            MessageResponse msg1 = response.messageResponse();
//            Map map1 = msg1.result();
//
//            //Write out the result of sendMessage.
//            map1.forEach((k, v) -> System.out.println((k + ":" + v)));
//
//        } catch (PinpointException e) {
//            System.err.println(e.awsErrorDetails().errorMessage());
//            System.exit(1);
//        }
//        return null;
//        
//    }
//}
