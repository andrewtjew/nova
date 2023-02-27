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
package org.nova.services;


import org.nova.frameworks.CoreEnvironment;
import org.nova.frameworks.ServerApplication;
import org.nova.html.remote.RemoteResponseWriter;
import org.nova.http.server.HttpTransport;
import org.nova.utils.Utils;

public abstract class SessionServerApplication<SESSION extends Session> extends ServerApplication
{
    final private TokenGenerator tokenGenerator;
    final private SessionManager<SESSION> sessionManager;
    private SessionFilter sessionFilter;

    public SessionServerApplication(String name,CoreEnvironment coreEnvironment,HttpTransport operatorTransport) throws Throwable
    {
    	this(name,coreEnvironment,operatorTransport,new DefaultAbnormalSessionRequestHandler());
    }

    public SessionServerApplication(String name,CoreEnvironment coreEnvironment,HttpTransport operatorTransport,AbnormalSessionRequestHandling...sessionRejectResponders) throws Throwable
    {
        super(name,coreEnvironment,operatorTransport);
        long lockTimeoutMs=this.getConfiguration().getLongValue("SessionServerApplication.session.lockTimeout", 10*1000);
        long timeoutMs=this.getConfiguration().getLongValue("SessionServerApplication.session.timeout", 30*60*1000);
        int generations=this.getConfiguration().getIntegerValue("SessionServerApplication.session.timeoutGenerations", 10);
        this.sessionManager=new SessionManager<SESSION>(this.getTraceManager(),this.getLogger("SessionService"),this.getTimerScheduler(), lockTimeoutMs,timeoutMs, generations);
        this.tokenGenerator=new TokenGenerator();

        String directoryServiceEndPoint=this.getConfiguration().getValue("SessionServerApplication.directoryServiceEndPoint", "http://"+Utils.getLocalHostName()+":"+this.getPublicTransport().getPorts()[0]);
        String headerTokenKey=this.getConfiguration().getValue("SessionServerApplication.tokenKey.header", "X-Token");
        String queryTokenKey=this.getConfiguration().getValue("SessionServerApplication.tokenKey.query", "token");
        String cookieTokenKey=this.getConfiguration().getValue("SessionServerApplication.tokenKey.cookie", headerTokenKey);
        this.sessionFilter=new SessionFilter(this.sessionManager,headerTokenKey,queryTokenKey,cookieTokenKey,sessionRejectResponders);

        this.getMenuBar().add("/operator/sessions","Sessions","View All");
        this.getPublicServer().addFilters(this.sessionFilter);
        
        SessionOperatorPages<SESSION> sessionOperatorPages=new SessionOperatorPages<>(this.sessionManager,this);
        this.getOperatorServer().registerHandlers(sessionOperatorPages);
    }

    public SessionManager<SESSION> getSessionManager()
    {
        return this.sessionManager;
    }

    public void setSessionFilter(SessionFilter sessionFilter)
    {
        this.sessionFilter=sessionFilter;
    }
    
    public SessionFilter getSessionFilter()
    {
        return sessionFilter;
    }

    public String generateSessionToken()
    {
        String token=this.tokenGenerator.next();
        while (this.sessionManager.getSessionByToken(token)!=null)
        {
            token=this.tokenGenerator.next();
        }
        return token;
    }
    
    public TokenGenerator getTokenGenerator()
    {
        return this.tokenGenerator;
    }

    public String generateToken()
    {
        return this.tokenGenerator.next();
    }
    
}
