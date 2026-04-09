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

import org.nova.concurrent.MultiTaskScheduler;
import org.nova.tracing.Trace;
import org.nova.tracing.TraceRunnable;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class TwilioSMSService extends SMSService
{
    final private MultiTaskScheduler scheduler;
    final private PhoneNumber fromNumber;
    
    public TwilioSMSService(MultiTaskScheduler scheduler,String fromNumber,String SID,String authToken)
    {
        this.scheduler=scheduler;
        Twilio.init(SID,authToken);
        this.fromNumber=new PhoneNumber(fromNumber);
    }
    void _send(Trace parent,String phoneNumber,String message)
    {
        this.scheduler.schedule(parent, "TwilioSMSService", new TraceRunnable()
        {
            @Override
            public void run(Trace parent) throws Throwable
            {
            }
        });
    }
    @Override
    public String send(Trace parent, String phoneNumber, String message)
    {
        if (phoneNumber.startsWith("+")==false)
        {
            phoneNumber="+"+phoneNumber;
        }
        try (Trace trace=new Trace(parent,"Twilio:send"))
        {
            Message twilioMessage = Message.creator(
                    new com.twilio.type.PhoneNumber(phoneNumber),
                    this.fromNumber,
                    message)
                  .create();
            
            var sid=twilioMessage.getSid();
//            System.out.println("Twilio sid:"+sid);
            trace.setDetails(sid);
        }
        return null;
     }
}
