package org.nova.http.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.nova.collections.ContentCache;
import org.nova.collections.FileCacheConfiguration;
import org.nova.collections.ContentCache.ValueSize;
import org.nova.html.ExtensionToContentTypeMappings;
import org.nova.html.elements.StringComposer;
import org.nova.html.ext.Redirect;
import org.nova.tracing.Trace;
import org.nova.utils.FileUtils;
import org.nova.utils.TypeUtils;
import org.nova.utils.Utils;

import com.google.common.io.Files;
import com.nixxcode.jvmbrotli.enc.BrotliOutputStream;
import com.nixxcode.jvmbrotli.enc.Encoder;

public class RedirectHandler extends ServletHandler
{
    
    final private byte[] content;
    public RedirectHandler(String location) throws Throwable 
    {
    	Redirect redirect=new Redirect(location);
    	StringComposer composer=new StringComposer();
    	redirect.compose(composer);
    	String content=composer.getStringBuilder().toString();
    	this.content=content.getBytes(StandardCharsets.UTF_8);
    }
    @Override
    public boolean handle(Trace parent, HttpServletRequest request, HttpServletResponse response) throws Throwable
    {
        response.setContentLength(this.content.length);
        response.setContentType("text/html");
        response.setStatus(HttpStatus.OK_200);
        
        response.getOutputStream().write(this.content);
    	return true;
    }

}
