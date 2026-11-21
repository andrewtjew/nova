package org.nova.service.deviceSession;

import org.nova.html.enums.character_set;
import org.nova.html.enums.http_equiv;
import org.nova.html.enums.name;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.Page;
import org.nova.html.ext.Script;
import org.nova.html.tags.div;
import org.nova.html.tags.meta;

public class InitializationPage extends Page 
{
    protected div content;
    public InitializationPage(String path,boolean includeCurrentPosition,String redirect)
    {
        head().addInner(new meta().charset(character_set.UTF_8).http_equiv(http_equiv.content_type).name(name.viewport).content("width=device-width, initial-scale=1, shrink-to-fit=no"));
        this.content=body().returnAddInner(new div());
        if (includeCurrentPosition)
        {
            String script=
                """
            function sendDeviceLocation(path,redirect) {
                var timeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;
                var parameters = new URLSearchParams({ redirect: redirect });
                var pathAndQuery = path+"?" + parameters.toString();
                try {
                    var location = navigator.geolocation.getCurrentPosition(function (position) {
                        window.location.href = pathAndQuery + encodeURI("&timeZone=" + timeZone + "&latitude=" + position.coords.latitude + "&longitude=" + position.coords.longitude);
                    }, function () {
                        window.location.href = pathAndQuery + encodeURI("&timeZone=" + timeZone);
                    });
                }
                catch (_a) {
                    window.location.href = pathAndQuery + encodeURI("&timeZone=" + timeZone);
                }
            }
                """;
            body().returnAddInner(new Script(script));
        }
        else
        {
            String script=
                """
            function sendDeviceLocation(path,redirect) {
                var timeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;
                var parameters = new URLSearchParams({ redirect: redirect });
                var pathAndQuery = path+"?" + parameters.toString();
                window.location.href = pathAndQuery + encodeURI("&timeZone=" + timeZone);
            }
                """;
            body().returnAddInner(new Script(script));
        }
        
//        System.out.println(script);
        body().onload(HtmlUtils.js_call("sendDeviceLocation",path+"/createDeviceSession", redirect));
    }
    
    public div content()
    {
        return this.content;
    }
}