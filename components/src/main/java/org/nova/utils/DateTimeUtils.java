/*******************************************************************************
 * Copyright (C) 2017-2019 Kat Fung Tjew
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.nova.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import org.nova.concurrent.TimeBase;

public class DateTimeUtils
{
    public static ZoneId UTC_ZONE_ID=ZoneId.of("UTC");
    public static TimeZone UTC_TIME_ZONE=TimeZone.getTimeZone("UTC");
    public static DateTimeFormatter FILE_LOG_FORMATTER=DateTimeFormatter.ofPattern("YYYY_MM_dd_hh_mm_ss");
    public static DateTimeFormatter SYSTEM_FORMATTER=DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");

    //ZoneOffset at a zone is not always constant due to DST changes.
    static public ZoneOffset getOffset(ZoneId zoneId)
    {
        Instant now=Instant.now();
        LocalDateTime local=LocalDateTime.ofInstant(now,zoneId);
        ZoneOffset offset=zoneId.getRules().getOffset(local);
        return offset;
    }
    static public ZoneOffset getOffset(TimeZone timeZone)
    {
        return getOffset(timeZone.toZoneId());
    }
    static public long getOffsetMs(TimeZone timeZone)
    {
        return timeZone.getOffset(System.currentTimeMillis());
    }
    static public long getOffsetMin(TimeZone timeZone)
    {
        return timeZone.getOffset(System.currentTimeMillis())/1000L/60L;
    }
    
    public static String toSystemDateTimeString(long millis)
    {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), UTC_ZONE_ID).format(SYSTEM_FORMATTER);
    }
    
    public static String toDateTimeSeconds(long millis)
    {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), UTC_ZONE_ID).format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
    }

    public static long getEpochDayStart(ZoneId zoneId,long startOffsetS)
    {
        OffsetDateTime odt=OffsetDateTime.now(zoneId);
        long start=(odt.toEpochSecond()-odt.getHour()*3600L-odt.getMinute()*60L-odt.getSecond()+startOffsetS)*1000L;
        return start;
    }
    
    
    public static long getNextDailyOffset(ZoneId zoneId,long dayOffsetS)
    {
        ZonedDateTime zonedNow=ZonedDateTime.now(zoneId);
        ZonedDateTime due=ZonedDateTime.of(zonedNow.getYear(), zonedNow.getMonthValue(),zonedNow.getDayOfMonth(),0,0,0,0,zoneId).plusSeconds(dayOffsetS);
        if (due.isBefore(zonedNow))
        {
            due=due.plusDays(1);
        }
        return due.toEpochSecond()*1000-System.currentTimeMillis();
    }
    
    public static String formatEpochMillis(long epochMillis,TimeZone timeZone,DateTimeFormatter formatter)
    {
        ZonedDateTime local=ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), timeZone.toZoneId());
        return formatter.format(local);
    }
    public static String formatEpochMillis(long epochMillis,DateTimeFormatter formatter)
    {
        ZonedDateTime local=ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMillis),UTC_ZONE_ID);
        return formatter.format(local);
    }

    public static String nowFileLogFormat()
    {
        return formatEpochMillis(System.currentTimeMillis(),UTC_TIME_ZONE,FILE_LOG_FORMATTER);
    }

    /*
    static enum TimeBase
    {
        YEAR,
        MONTH,
        WEEK,
        DAY,
        HOUR,
        MINUTE
    }
    */

    static public ZonedDateTime align(TimeBase timeBase,ZonedDateTime zdt)
    {
        switch (timeBase)
        {
            case YEAR:
            return ZonedDateTime.of(zdt.getYear(), 1,1,0,0,0,0,zdt.getZone());
            
            case MONTH:
                return ZonedDateTime.of(zdt.getYear(), zdt.getMonthValue(),1,0,0,0,0,zdt.getZone());
        
            case WEEK:
            {
               int day=zdt.getDayOfWeek().getValue();
               return ZonedDateTime.of(zdt.getYear(), zdt.getMonthValue(),zdt.getDayOfMonth(),0,0,0,0,zdt.getZone()).minusDays(day);
            }
        
            case DAY:
                return ZonedDateTime.of(zdt.getYear(), zdt.getMonthValue(),zdt.getDayOfMonth(),0,0,0,0,zdt.getZone());
               
            case HOUR:
                return ZonedDateTime.of(zdt.getYear(), zdt.getMonthValue(),zdt.getDayOfMonth(),zdt.getHour(),0,0,0,zdt.getZone());
               
            case MINUTE:
                return ZonedDateTime.of(zdt.getYear(), zdt.getMonthValue(),zdt.getDayOfMonth(),zdt.getHour(),zdt.getMinute(),0,0,zdt.getZone());
                
                default:
                    return null;
        }
       
    }
    
    static public long getDayInstantBeforeNow(long days)
    {
    	return System.currentTimeMillis()-24L*3600L*1000L;
    }
    
}
