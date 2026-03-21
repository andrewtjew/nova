package org.nova.service.deviceSession;

import org.nova.html.enums.character_set;
import org.nova.html.enums.http_equiv;
import org.nova.html.enums.link_rel;
import org.nova.html.enums.name;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.Page;
import org.nova.html.ext.Script;
import org.nova.html.tags.h3;
import org.nova.html.tags.link;
import org.nova.html.tags.meta;
import org.nova.html.tags.script;
import org.nova.html.tags.title;

public class DeviceSessionInitializationPage extends Page 
{
    public DeviceSessionInitializationPage(String path,String redirect)
    {
        head().addInner(new meta().charset(character_set.UTF_8).http_equiv(http_equiv.content_type).name(name.viewport).content("width=device-width, initial-scale=1, shrink-to-fit=no"));
        body().returnAddInner(new h3()).addInner("Initializing...");
        String script=
            """
        function createDeviceSession(path,redirect) {
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
        
        System.out.println(script);
        body().returnAddInner(new Script(script));
        body().onload(HtmlUtils.js_call("createDeviceSession",path+"/createDeviceSession", redirect));
    }
}