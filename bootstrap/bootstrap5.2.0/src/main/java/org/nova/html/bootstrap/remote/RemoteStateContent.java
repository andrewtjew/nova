package org.nova.html.bootstrap.remote;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.nova.html.elements.Element;
import org.nova.html.elements.FormElement;
import org.nova.html.elements.HtmlElementWriter;
import org.nova.html.enums.method;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.InputHidden;
import org.nova.html.ext.JsObject;
import org.nova.html.remote.RemoteResponse;
import org.nova.html.remote.RemoteResponseWriter;
import org.nova.html.tags.div;
import org.nova.http.server.BrotliContentEncoder;
import org.nova.http.server.Context;
import org.nova.http.server.DeflaterContentEncoder;
import org.nova.http.server.FilterChain;
import org.nova.http.server.GzipContentEncoder;
import org.nova.http.server.JSONContentReader;
import org.nova.http.server.JSONContentWriter;
import org.nova.http.server.RemoteStateBinding;
import org.nova.http.server.annotations.ContentEncoders;
import org.nova.http.server.annotations.ContentReaders;
import org.nova.http.server.annotations.ContentWriters;
import org.nova.http.server.annotations.Filters;
import org.nova.http.server.annotations.QueryParam;
import org.nova.http.server.annotations.StateParam;
import org.nova.tracing.Trace;

import org.nova.html.enums.enctype;

@ContentWriters(RemoteResponseWriter.class)
public class RemoteStateContent extends RemoteContent
{
    final private RemoteStateBinding binding;
    
    public RemoteStateContent(RemoteStateBinding binding) throws Throwable
    {
        super(null);
        binding.setState(this);
        this.binding=binding;
    }
    
    public RemoteStateBinding getRemoteStateBinding()
    {
        return this.binding;
    }
}