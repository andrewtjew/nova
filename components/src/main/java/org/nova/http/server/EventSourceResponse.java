package org.nova.http.server;

import java.io.PrintWriter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class EventSourceResponse 
{
    final private HttpServletResponse response;
    final private HttpServletRequest request;
    public EventSourceResponse(HttpServletRequest request,HttpServletResponse response,long timeout) throws Throwable
    {   
        this.request=request;
        request.startAsync().setTimeout(timeout);

        this.response=response;
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");
    }
    public EventSourceResponse(Context context,long timeout) throws Throwable
    {
        this(context.getHttpServletRequest(),context.getHttpServletResponse(),timeout);
    }
    public EventSourceResponse(HttpServletRequest request,HttpServletResponse response) throws Throwable
    {
        this(request,response,0);
    }
    public EventSourceResponse(Context context) throws Throwable
    {
        this(context.getHttpServletRequest(),context.getHttpServletResponse());
    }
    public void sendData(String data) throws Throwable
    {
        PrintWriter writer=new PrintWriter(this.response.getOutputStream());
        writer.write("data: "+data+"\n\n");
        writer.flush();
    }
     public void sendEvent(String event,String data) throws Throwable
    {
        PrintWriter writer=new PrintWriter(this.response.getOutputStream());
        writer.write("event: "+event+"\n");
        writer.write("data: "+data+"\n\n");
        writer.flush();
    }
     public void sendEvent(String event,String id,String data) throws Throwable
    {
        PrintWriter writer=new PrintWriter(this.response.getOutputStream());
        writer.write("id: "+id+"\n");
        writer.write("event: "+event+"\n");
        writer.write("data: "+data+"\n\n");
        writer.flush();
    }
     public void sendId(String id) throws Throwable
    {
        PrintWriter writer=new PrintWriter(this.response.getOutputStream());
        writer.write("id: "+id+"\n\n");
        writer.flush();
    }
     public void close() throws Throwable
     {
         this.request.getAsyncContext().complete();
     }
}
