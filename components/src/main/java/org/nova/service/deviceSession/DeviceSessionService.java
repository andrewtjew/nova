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
package org.nova.service.deviceSession;


import org.nova.frameworks.CoreEnvironment;
import org.nova.frameworks.ServerApplication;
import org.nova.html.elements.TagElement;
import org.nova.http.server.HttpServer;
import org.nova.http.server.HttpTransport;
import org.nova.services.TokenGenerator;
import org.nova.tracing.Trace;

public abstract class DeviceSessionService<STATE> extends ServerApplication
{
    final private DeviceSessionManager deviceSessionManager;
//    private SessionFilter sessionFilter;


    public DeviceSessionService(String name,CoreEnvironment coreEnvironment,HttpTransport operatorTransport) throws Throwable
    {
        super(name,coreEnvironment,operatorTransport);
        
        long lockTimeoutMs=this.getConfiguration().getLongValue("SessionServerApplication.session.lockTimeout", 10*1000);
        long timeoutMs=this.getConfiguration().getLongValue("SessionServerApplication.session.timeout", 30*60*1000);
        int generations=this.getConfiguration().getIntegerValue("SessionServerApplication.session.timeoutGenerations", 10);
        this.deviceSessionManager=new DeviceSessionManager(this.getTraceManager(),this.getLogger("SessionService"),this.getTimerScheduler(), lockTimeoutMs,timeoutMs, generations);

        this.getMenuBar().add("/operator/sessions","Sessions","View All");
        
        SessionOperatorPages sessionOperatorPages=new SessionOperatorPages(this.deviceSessionManager,this);
        this.getOperatorServer().registerHandlers(sessionOperatorPages);
        
        if (isTest()==false)
        {
            if (TagElement.INCLUDE_STACK_TRACE_LEVELS>0)
            {
                String message="Build configuration: TagElement.INCLUDE_STACK_TRACE_LEVELS="+TagElement.INCLUDE_STACK_TRACE_LEVELS;
                System.err.println(message);
                throw new Exception(message);
            }
        }
    }
    public void addDeviceSessionControllerFilter(HttpServer server,String path,DeviceSessionControllerFilter controllerFilter) throws Throwable
    {
        server.registerHandlers(path, controllerFilter);
        server.addBottomFilters(controllerFilter);
    }
    public DeviceSessionManager getDeviceSessionManager()
    {
        return this.deviceSessionManager;
    }
}
