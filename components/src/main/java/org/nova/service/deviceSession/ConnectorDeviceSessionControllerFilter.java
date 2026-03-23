package org.nova.service.deviceSession;

import java.time.ZoneId;

import org.nova.geo.LatitudeLongitude;
import org.nova.http.server.Context;
import org.nova.localization.CountryCode;
import org.nova.services.SessionManager;
import org.nova.sqldb.Connector;
import org.nova.sqldb.Insert;
import org.nova.sqldb.Select;
import org.nova.sqldb.SqlUtils;
import org.nova.tracing.Trace;

abstract public class ConnectorDeviceSessionControllerFilter<ROLE extends Enum<?>> extends DeviceSessionControllerFilter<ROLE>
{
    final private String deviceType;
    final private Connector connector;
    final private Class<ROLE> roleType;
    final private long deviceSessionMaxAgeSeconds;
    public ConnectorDeviceSessionControllerFilter(SessionManager<DeviceSession<ROLE>> sessionManager,String deviceSessionControllerPath,String cookieName,Integer cookieAge,String deviceType,Connector connector,Class<ROLE> roleType,long deviceSessionMaxAgeSeconds)
    {
        super(sessionManager, deviceSessionControllerPath,cookieName,cookieAge);
        this.deviceType=deviceType;
        this.connector=connector;
        this.roleType=roleType;
        this.deviceSessionMaxAgeSeconds=deviceSessionMaxAgeSeconds;
    }
    @Override
    protected DeviceSession<ROLE> getDeviceSession(Trace parent, Context context, String token) throws Throwable
    {
        try (var accessor=this.connector.openAccessor(parent))
        {
            var row=Select.source("devicesession JOIN device ON devicesession.deviceId=device.id").columns("*").where("identifier=?", token).orderBy("devicesession.created DESC").limit(1).executeOne(parent, accessor);
            if (row==null)
            {
                return null;
            }
            long created=row.getTIMESTAMP("created").getTime();
            long age=System.currentTimeMillis()-created;
            if (age>this.deviceSessionMaxAgeSeconds*1000)
            {
                return null;
            }
            
            long deviceSessionId=row.getBIGINT("id");
            String language=row.getVARCHAR("language");
            Double latitude=row.getNullableFLOAT("latitude");
            Double longitude=row.getNullableFLOAT("longitude");
            
            
            LatitudeLongitude position=null;
            if ((latitude!=null)&&(longitude!=null))
            {
                position=new LatitudeLongitude(latitude,longitude);
            }
            var countryCode=CountryCode.fromAlpha2Code(row.getVARCHAR("countryCode"));
            var zoneId=ZoneId.of(row.getVARCHAR("zoneId"));

            DeviceSession<ROLE> deviceSession=new DeviceSession<>(deviceSessionId, token,language,position,countryCode,zoneId,this.roleType);
            return deviceSession;
        }
    }
        
    @Override
    protected DeviceSession<ROLE> createDeviceSession(Trace parent, Context context, String token, String language,LatitudeLongitude position,CountryCode countryCode,ZoneId zoneId) throws Throwable
    {
        var request=context.getHttpServletRequest();
        try (var accessor=this.connector.openAccessor(parent))
        {
            try (var transaction=accessor.beginTransaction("createDeviceSession"))
            {
                Long deviceId=null;
                var row=Select.source("device").columns("id").where("identifier=?", token).executeOne(parent, accessor);
                if (row==null)
                {
                    deviceId=Insert.table("device")
                    .value("created", SqlUtils.now())
                    .value("type",this.deviceType)
                    .value("identifier", token)
                    .executeAndReturnLongKey(parent, accessor);
                }    
                else
                {
                    deviceId=row.getBIGINT(0);
                }
                
                var insert=Insert.table("devicesession");
                insert
                .value("deviceId",deviceId)
                .value("created",SqlUtils.now())
                .value("userAgent",request.getHeader("User-Agent"))
                .value("remote",request.getRemoteAddr())
                .value("language",language);
                
                if (countryCode!=null)
                {
                    insert               
                    .value("countryCode", countryCode.getValue().alpha2Code);
                }

                if (position!=null)
                {
                    insert
                    .value("latitude",position.latitude)
                    .value("longitude",position.longitude);
                }
                if (zoneId!=null)
                {
                    insert
                    .value("zoneId", zoneId.getId());
                }
                var deviceSessionId=insert.executeAndReturnLongKey(parent, accessor);
                transaction.commit();

                DeviceSession<ROLE> deviceSession=new DeviceSession<>(deviceSessionId, token,language,position,countryCode,zoneId,this.roleType);
                return deviceSession;
            }
        }

    }

}