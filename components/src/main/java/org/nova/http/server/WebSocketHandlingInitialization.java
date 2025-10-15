package org.nova.http.server;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.eclipse.jetty.websocket.api.Session;
import org.nova.html.ext.HtmlUtils;
import org.nova.http.server.RequestMethodMap.DistanceContentWriter;
import org.nova.http.server.annotations.Attributes;
import org.nova.http.server.annotations.ContentDecoders;
import org.nova.http.server.annotations.ContentEncoders;
import org.nova.http.server.annotations.ContentParam;
import org.nova.http.server.annotations.ContentReaders;
import org.nova.http.server.annotations.ContentWriters;
import org.nova.http.server.annotations.CookieParam;
import org.nova.http.server.annotations.CookieStateParam;
import org.nova.http.server.annotations.DELETE;
import org.nova.http.server.annotations.DefaultValue;
import org.nova.http.server.annotations.Filters;
import org.nova.http.server.annotations.GET;
import org.nova.http.server.annotations.HEAD;
import org.nova.http.server.annotations.Log;
import org.nova.http.server.annotations.OPTIONS;
import org.nova.http.server.annotations.PATCH;
import org.nova.http.server.annotations.POST;
import org.nova.http.server.annotations.PUT;
import org.nova.http.server.annotations.ParamName;
import org.nova.http.server.annotations.Path;
import org.nova.http.server.annotations.PathParam;
import org.nova.http.server.annotations.QueryParam;
import org.nova.http.server.annotations.Required;
import org.nova.http.server.annotations.StateParam;
import org.nova.http.server.annotations.TRACE;
import org.nova.http.server.annotations.Test;
import org.nova.services.ForbiddenRoles;
import org.nova.services.RequiredRoles;
import org.nova.tracing.Trace;
import org.nova.utils.TypeUtils;

public interface WebSocketHandlingInitialization<HANDLER extends WebSocketHandling>
{
    public abstract HANDLER createWebSocketHandler();
}
