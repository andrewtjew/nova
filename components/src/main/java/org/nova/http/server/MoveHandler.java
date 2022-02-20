package org.nova.http.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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
import org.nova.tracing.Trace;
import org.nova.utils.FileUtils;
import org.nova.utils.TypeUtils;
import org.nova.utils.Utils;

import com.google.common.io.Files;
import com.nixxcode.jvmbrotli.enc.BrotliOutputStream;
import com.nixxcode.jvmbrotli.enc.Encoder;

public class MoveHandler extends ServletHandler
{
    
    final private String moveToLocation;
    public MoveHandler(String moveToLocation) 
    {
    	this.moveToLocation=moveToLocation;
    }
    @Override
    public boolean handle(Trace parent, HttpServletRequest request, HttpServletResponse response) throws Throwable
    {
        response.setHeader("Location", this.moveToLocation);
        response.setStatus(HttpStatus.MOVED_PERMANENTLY_301);
        response.setHeader("Cache-Control","no-cache, no-store, must-revalidate");
    	return true;
    }

}
