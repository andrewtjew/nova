package org.sample;

import org.nova.http.server.BrotliContentEncoder;
import org.nova.http.server.Context;
import org.nova.http.server.DeflaterContentEncoder;
import org.nova.http.server.GzipContentDecoder;
import org.nova.http.server.GzipContentEncoder;
import org.nova.http.server.JSONContentReader;
import org.nova.http.server.JSONContentWriter;
import org.nova.html.remoting.HtmlRemotingWriter;
import org.nova.http.server.JSONPatchContentReader;
import org.nova.http.server.Response;

import org.nova.html.bootstrap.ext.RedirectToMessage;
import org.nova.html.elements.Element;
import org.nova.html.elements.HtmlElementWriter;
import org.nova.html.remote.RemoteResponseWriter;
import org.nova.http.server.annotations.ContentDecoders;
import org.nova.http.server.annotations.ContentEncoders;
import org.nova.http.server.annotations.ContentReaders;
import org.nova.http.server.annotations.ContentWriters;
import org.nova.http.server.annotations.Filters;
import org.nova.services.AbnormalSessionRequestHandling;
import org.nova.services.DeviceSessionFilter;
import org.nova.services.Session;
import org.nova.services.SessionFilter;
import org.nova.tracing.Trace;

@ContentDecoders(GzipContentDecoder.class)
@ContentWriters({HtmlElementWriter.class,RemoteResponseWriter.class,JSONContentWriter.class})
@ContentReaders({JSONContentReader.class})
@ContentEncoders({DeflaterContentEncoder.class,GzipContentEncoder.class,BrotliContentEncoder.class})
@Filters({DeviceSessionFilter.class})
public class PageController
{
    final protected Service service;
    public PageController(Service service) throws Throwable
    {
        this.service=service;
        
    }    
}