package org.nova.services;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TimeZone;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.nova.core.NameObject;
import org.nova.html.elements.FormElement;
import org.nova.html.elements.TagElement;
import org.nova.html.ext.InputHidden;
import org.nova.html.tags.sub;
import org.nova.html.tags.time;
import org.nova.http.server.Context;
import org.nova.http.server.RemoteStateBinding;
import org.nova.json.ObjectMapper;
import org.nova.localization.CountryCode;
import org.nova.localization.CurrencyCode;
import org.nova.security.QuerySecurity;
import org.nova.security.SecurityUtils;
import org.nova.testing.Debugging;
import org.nova.tracing.Trace;


public abstract class QuerySecurityDeviceSession<ROLE extends Enum> extends DeviceSession<ROLE> implements QuerySecurity
{
    public QuerySecurityDeviceSession(long deviceSessionId, String token, ZoneId zoneId, Class<ROLE> roleType) throws Throwable
    {
        super(deviceSessionId, token, zoneId, roleType);
    }
}
