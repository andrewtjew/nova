package org.nova.html.bootstrap.remote;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Justify;
import org.nova.html.elements.Element;
import org.nova.html.elements.FormElement;
import org.nova.html.elements.HtmlElementWriter;
import org.nova.html.enums.method;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.InputHidden;
import org.nova.html.ext.JsObject;
import org.nova.html.remote.RemoteForm;
import org.nova.html.remote.RemoteFormElement;
import org.nova.html.remote.RemoteResponse;
import org.nova.html.remote.RemoteResponseWriter;
import org.nova.http.server.BrotliContentEncoder;
import org.nova.http.server.Context;
import org.nova.http.server.DeflaterContentEncoder;
import org.nova.http.server.FilterChain;
import org.nova.http.server.GzipContentEncoder;
import org.nova.http.server.JSONContentReader;
import org.nova.http.server.JSONContentWriter;
import org.nova.http.server.StateHandling;
import org.nova.http.server.annotations.ContentEncoders;
import org.nova.http.server.annotations.ContentReaders;
import org.nova.http.server.annotations.ContentWriters;
import org.nova.http.server.annotations.Filters;
import org.nova.http.server.annotations.QueryParam;
import org.nova.http.server.annotations.StateParam;
import org.nova.tracing.Trace;

import org.nova.html.enums.enctype;

@ContentWriters(RemoteResponseWriter.class)
public class RemoteStateForm extends RemoteForm
{
    private RemoteResponse response;
    public RemoteStateForm(StateHandling stateHandling, String action) throws Throwable
    {
        stateHandling.setHandlerElement(this);
        action(action);
    }

    public RemoteStateForm(StateHandling stateHandling) throws Throwable
    {
        this(stateHandling, null);
    }

    public RemoteResponse beginResponse()
    {
        this.response=new RemoteResponse();
        for (Element element : this.inners)
        {
            if (element instanceof RemoteStateInput<?>)
            {
                ((RemoteStateInput<?>) element).clearValidationMessage();
            }
            else if (element instanceof SubmitRemoteStateFormButton)
            {
                ((SubmitRemoteStateFormButton) element).reset(response);
            }
        }
        return response;
    }

    public RemoteResponse location(String pathAndQuery) throws Throwable
    {
        this.response.location(pathAndQuery);
        return this.response;
    }
    
    public RemoteResponse endResponse()
    {
        this.response.outerHtml(this);
        return this.response;
    }


}