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
package org.nova.html.bootstrap.ext;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class DateTimeRange
{
    final private DateTimeFormatter formatter;
    final private String start;
    final private String end;
    final private TimeZone timeZone;
    static final String divider=" - ";
    
    public DateTimeRange(String range,TimeZone timeZone,DateTimeFormatter formatter) throws Throwable
    {
        this.formatter=formatter;
        this.timeZone=timeZone;
        int index=range.indexOf(divider);
        if (index<0)
        {
            throw new Exception("Invalid format: "+range);
        }
        this.start=range.substring(0,index);
        this.end=range.substring(index+divider.length());
    }
    
    public String end()
    {
        return this.end;
    }
    public String start()
    {
        return this.start;
    }

    public long endEpochMillis()
    {
        return toEpochMillis(this.end);
    }
    public long startEpochMillis()
    {
        return toEpochMillis(this.start);
    }
    private long toEpochMillis(String dateTime)
    {
        LocalDateTime ldt=LocalDateTime.parse(dateTime, this.formatter);
        if (this.timeZone!=null)
        {
            ZoneOffset zo=ZoneOffset.ofTotalSeconds(this.timeZone.getOffset(System.currentTimeMillis())/1000);
            OffsetDateTime odt=OffsetDateTime.of(ldt, zo);
            return odt.toInstant().toEpochMilli();
        }
        return ldt.toInstant(ZoneOffset.UTC).toEpochMilli();
    }
    public String range()
    {
        return this.start+divider+this.end;
        
    }
}
