package org.nova.frameworks;

import org.nova.html.elements.HtmlElementWriter;
import org.nova.html.remote.RemoteResponseWriter;
import org.nova.http.server.BrotliContentEncoder;
import org.nova.http.server.DeflaterContentDecoder;
import org.nova.http.server.DeflaterContentEncoder;
import org.nova.http.server.GzipContentDecoder;
import org.nova.http.server.GzipContentEncoder;
import org.nova.http.server.JSONContentReader;
import org.nova.http.server.JSONContentWriter;
import org.nova.http.server.JSONPatchContentReader;
import org.nova.http.server.annotations.ContentDecoders;
import org.nova.http.server.annotations.ContentEncoders;
import org.nova.http.server.annotations.ContentReaders;
import org.nova.http.server.annotations.ContentWriters;

@ContentDecoders({DeflaterContentDecoder.class,GzipContentDecoder.class})
@ContentEncoders({DeflaterContentEncoder.class,GzipContentEncoder.class})
@ContentReaders({JSONContentReader.class, JSONPatchContentReader.class})
@ContentWriters({JSONContentWriter.class, HtmlElementWriter.class, RemoteResponseWriter.class})
public class BasicController
{

}
