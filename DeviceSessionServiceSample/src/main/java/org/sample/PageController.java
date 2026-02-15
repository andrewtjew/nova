package org.sample;

import org.nova.http.server.BrotliContentEncoder;
import org.nova.http.server.DeflaterContentEncoder;
import org.nova.http.server.GzipContentDecoder;
import org.nova.http.server.GzipContentEncoder;
import org.nova.http.server.JSONContentReader;
import org.nova.http.server.JSONContentWriter;
import org.nova.html.elements.HtmlElementWriter;
import org.nova.html.remote.RemoteResponseWriter;
import org.nova.http.server.annotations.ContentDecoders;
import org.nova.http.server.annotations.ContentEncoders;
import org.nova.http.server.annotations.ContentReaders;
import org.nova.http.server.annotations.ContentWriters;
import org.nova.http.server.annotations.Filters;
import org.nova.services.SimpleDeviceSessionFilter;

@ContentDecoders(GzipContentDecoder.class)
@ContentWriters({HtmlElementWriter.class,RemoteResponseWriter.class,JSONContentWriter.class})
@ContentReaders({JSONContentReader.class})
@ContentEncoders({DeflaterContentEncoder.class,GzipContentEncoder.class,BrotliContentEncoder.class})
@Filters({SimpleDeviceSessionFilter.class})
public class PageController
{
    final protected Service service;
    public PageController(Service service) throws Throwable
    {
        this.service=service;
        
    }    
}
