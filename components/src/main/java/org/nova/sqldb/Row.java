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
package org.nova.sqldb;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

public class Row
{
    final protected Map<String, Integer> mappings;
    final protected Object[] data;

    public Row(Map<String, Integer> mappings, Object[] data)
    {
        this.mappings = mappings;
        this.data = data;
    }

    public Integer getColumnIndex(String columnName) throws Exception
    {
        try
        {
            return this.mappings.get(columnName);
        }
        catch (Throwable ex)
        {
            throw new Exception("columnName=" + columnName, ex);
        }
    }

    // get using SQL types
    public String getCHAR(int columnIndex)
    {
        return (String) this.data[columnIndex];
    }

    public String getCHAR(String columnName) throws Exception
    {
        try
        {
            return getCHAR(this.mappings.get(columnName));
        }
        catch (Throwable ex)
        {
            throw new Exception("columnName=" + columnName, ex);
        }
    }

    public String getVARCHAR(int columnIndex) throws Exception
    {
        try
        {
            return (String) this.data[columnIndex];
        }
        catch (Throwable ex)
        {
            throw new Exception("columnIndex=" + columnIndex, ex);
        }
    }

    public String getVARCHAR(String columnName) throws Exception
    {
        try
        {
            return getVARCHAR(this.mappings.get(columnName));
        }
        catch (Throwable ex)
        {
            throw new Exception("columnName=" + columnName, ex);
        }
    }

    public String getLONGVARCHAR(int columnIndex) throws Exception
    {
        try
        {
            return (String) this.data[columnIndex];
        }
        catch (Throwable ex)
        {
            throw new Exception("columnIndex=" + columnIndex, ex);
        }
    }

    public String getLONGVARCHAR(String columnName) throws Exception
    {
        try
        {
            return getLONGVARCHAR(this.mappings.get(columnName));
        }
        catch (Throwable ex)
        {
            throw new Exception("columnName=" + columnName, ex);
        }
    }

    public BigDecimal getNUMERIC(int columnIndex) throws Exception
    {
        try
        {
            return (BigDecimal) this.data[columnIndex];
        }
        catch (Throwable ex)
        {
            throw new Exception("columnIndex=" + columnIndex, ex);
        }
    }

    public BigDecimal getNUMERIC(String columnName) throws Exception
    {
        try
        {
            return getNUMERIC(this.mappings.get(columnName));
        }
        catch (Throwable ex)
        {
            throw new Exception("columnName=" + columnName, ex);
        }
    }

    public BigDecimal getDECIMAL(int columnIndex) throws Exception
    {
        try
        {
            return (BigDecimal) this.data[columnIndex];
        }
        catch (Throwable ex)
        {
            throw new Exception("columnIndex=" + columnIndex, ex);
        }
    }

    public BigDecimal getDECIMAL(String columnName) throws Exception
    {
        try
        {
            return getDECIMAL(this.mappings.get(columnName));
        }
        catch (Throwable ex)
        {
            throw new Exception("columnName=" + columnName, ex);
        }
    }

    public BigInteger getBigInteger(int columnIndex) throws Exception
    {
        try
        {
            return (BigInteger) this.data[columnIndex];
        }
        catch (Throwable ex)
        {
            throw new Exception("columnIndex=" + columnIndex, ex);
        }
    }

    public BigInteger getBigInteger(String columnName) throws Exception
    {
        try
        {
            return getBigInteger(this.mappings.get(columnName));
        }
        catch (Throwable ex)
        {
            throw new Exception("columnName=" + columnName, ex);
        }
    }

    public short getTINYINT(int columnIndex) throws Exception
    {
        try
        {
            return (short) this.data[columnIndex];
        }
        catch (Throwable ex)
        {
            throw new Exception("columnIndex=" + columnIndex, ex);
        }
    }

    public short getTINYINT(String columnName) throws Exception
    {
        try
        {
            return getTINYINT(this.mappings.get(columnName));
        }
        catch (Throwable ex)
        {
            throw new Exception("columnName=" + columnName, ex);
        }
    }

    public Short getNullableTINYINT(int columnIndex) throws Exception
    {
        try
        {
            return (Short) this.data[columnIndex];
        }
        catch (Throwable ex)
        {
            throw new Exception("columnIndex=" + columnIndex, ex);
        }
    }

    public Short getNullableTINYINT(String columnName) throws Exception
    {
        try
        {
            return getNullableTINYINT(this.mappings.get(columnName));
        }
        catch (Throwable ex)
        {
            throw new Exception("columnName=" + columnName, ex);
        }
    }

    public short getSMALLINT(int columnIndex)
    {
        Object object = this.data[columnIndex];
        if (object instanceof Short)
        {
            return (short) object;
        }
        else if (object instanceof Integer)
        {
            Integer value = (Integer) object;
            return value.shortValue();
        }
        throw new RuntimeException();
    }

    public short getSMALLINT(String columnName) throws Exception
    {
        try
        {
            return getSMALLINT(this.mappings.get(columnName));
        }
        catch (Throwable ex)
        {
            throw new Exception("columnName=" + columnName, ex);
        }
    }

    public Short getNullableSMALLINT(int columnIndex)
    {
        Object object = this.data[columnIndex];
        if (object == null)
        {
            return null;
        }
        if (object instanceof Short)
        {
            return (Short) object;
        }
        else if (object instanceof Integer)
        {
            Integer value = (Integer) object;
            return value.shortValue();
        }
        throw new RuntimeException();

        // return (Short)this.data[columnIndex];
    }

    public Short getNullableSMALLINT(String columnName) throws Exception
    {
        try
        {
            return getNullableSMALLINT(this.mappings.get(columnName));
        }
        catch (Throwable ex)
        {
            throw new Exception("columnName=" + columnName, ex);
        }
    }

    public int getINTEGER(int columnIndex) throws Exception
    {
        try
        {
            return (int) this.data[columnIndex];
        }
        catch (Throwable ex)
        {
            throw new Exception("columnIndex=" + columnIndex, ex);
        }
    }

    public int getINTEGER(String columnName) throws Exception
    {
        try
        {
            return getINTEGER(this.mappings.get(columnName));
        }
        catch (Throwable ex)
        {
            throw new Exception("columnName=" + columnName, ex);
        }
    }

    public Integer getNullableINTEGER(int columnIndex) throws Exception
    {
        try
        {
            return (Integer) this.data[columnIndex];
        }
        catch (Throwable ex)
        {
            throw new Exception("columnIndex=" + columnIndex, ex);
        }
    }

    public Integer getNullableINTEGER(String columnName) throws Exception
    {
        try
        {
            return getNullableINTEGER(this.mappings.get(columnName));
        }
        catch (Throwable ex)
        {
            throw new Exception("columnName=" + columnName, ex);
        }
    }

    public long getBIGINT(int columnIndex) throws Exception
    {
        try
        {
            return (long) this.data[columnIndex];
        }
        catch (Throwable ex)
        {
            throw new Exception("columnIndex=" + columnIndex, ex);
        }
    }

    public long getBIGINT(String columnName) throws Exception
    {
        try
        {
            return getBIGINT(this.mappings.get(columnName));
        }
        catch (Throwable ex)
        {
            throw new Exception("columnName=" + columnName, ex);
        }
    }

    public Long getNullableBIGINT(int columnIndex) throws Exception
    {
        try
        {
            return (Long) this.data[columnIndex];
        }
        catch (Throwable ex)
        {
            throw new Exception("columnIndex=" + columnIndex, ex);
        }
    }

    public Long getNullableBIGINT(String columnName) throws Exception
    {
        try
        {
            return getNullableBIGINT(this.mappings.get(columnName));
        }
        catch (Throwable ex)
        {
            throw new Exception("columnName=" + columnName, ex);
        }
    }

    public float getREAL(int columnIndex) throws Exception
    {
        try
        {
            return (float) this.data[columnIndex];
        }
        catch (Throwable ex)
        {
            throw new Exception("columnIndex=" + columnIndex, ex);
        }
    }

    public float getREAL(String columnName) throws Exception
    {
        try
        {
            return getREAL(this.mappings.get(columnName));
        }
        catch (Throwable ex)
        {
            throw new Exception("columnName=" + columnName, ex);
        }
    }

    public Float getNullableREAL(int columnIndex) throws Exception
    {
        try
        {
            return (Float) this.data[columnIndex];
        }
        catch (Throwable ex)
        {
            throw new Exception("columnIndex=" + columnIndex, ex);
        }
    }

    public Float getNullableREAL(String columnName) throws Exception
    {
        try
        {
            return getNullableREAL(this.mappings.get(columnName));
        }
        catch (Throwable ex)
        {
            throw new Exception("columnName=" + columnName, ex);
        }
    }

    public double getFLOAT(int columnIndex) throws Exception
    {
        try
        {
            return (double) this.data[columnIndex];
        }
        catch (Throwable ex)
        {
            throw new Exception("columnIndex=" + columnIndex, ex);
        }
    }

    public double getFLOAT(String columnName) throws Exception
    {
        try
        {
            return getFLOAT(this.mappings.get(columnName));
        }
        catch (Throwable ex)
        {
            throw new Exception("columnName=" + columnName, ex);
        }
    }

    public Double getNullableFLOAT(int columnIndex)
    {
        Object object = this.data[columnIndex];
        if (object == null)
        {
            return null;
        }
        if (object instanceof Double)
        {
            return (Double) object;
        }
        else if (object instanceof Float)
        {
            Float value = (Float) object;
            return value.doubleValue();
        }
        throw new RuntimeException();
    }

    public Double getNullableFLOAT(String columnName) throws Exception
    {
        try
        {
            return getNullableFLOAT(this.mappings.get(columnName));
        }
        catch (Throwable ex)
        {
            throw new Exception("columnName=" + columnName, ex);
        }
    }

    public double getDOUBLE(int columnIndex) throws Exception
    {
        try
        {
            return (double) this.data[columnIndex];
        }
        catch (Throwable ex)
        {
            throw new Exception("columnIndex=" + columnIndex, ex);
        }
    }

    public double getDOUBLE(String columnName) throws Exception
    {
        try
        {
            return getDOUBLE(this.mappings.get(columnName));
        }
        catch (Throwable ex)
        {
            throw new Exception("columnName=" + columnName, ex);
        }
    }

    public Double getNullableDOUBLE(int columnIndex) throws Exception
    {
        try
        {
            return (Double) this.data[columnIndex];
        }
        catch (Throwable ex)
        {
            throw new Exception("columnIndex=" + columnIndex, ex);
        }
    }

    public Double getNullableDOUBLE(String columnName) throws Exception
    {
        try
        {
            return getNullableDOUBLE(this.mappings.get(columnName));
        }
        catch (Throwable ex)
        {
            throw new Exception("columnName=" + columnName, ex);
        }
    }

    public byte[] getBINARY(int columnIndex) throws Exception
    {
        try
        {
            return (byte[]) this.data[columnIndex];
        }
        catch (Throwable ex)
        {
            throw new Exception("columnIndex=" + columnIndex, ex);
        }
    }

    public byte[] getBINARY(String columnName) throws Exception
    {
        try
        {
            return getBINARY(this.mappings.get(columnName));
        }
        catch (Throwable ex)
        {
            throw new Exception("columnName=" + columnName, ex);
        }
    }

    public byte[] getVARBINARY(int columnIndex) throws Exception
    {
        try
        {
            return (byte[]) this.data[columnIndex];
        }
        catch (Throwable ex)
        {
            throw new Exception("columnIndex=" + columnIndex, ex);
        }
    }

    public byte[] getVARBINARY(String columnName) throws Exception
    {
        try
        {
            return getVARBINARY(this.mappings.get(columnName));
        }
        catch (Throwable ex)
        {
            throw new Exception("columnName=" + columnName, ex);
        }
    }

    public byte[] getLONGVARBINARY(int columnIndex) throws Exception
    {
        try
        {
            return (byte[]) this.data[columnIndex];
        }
        catch (Throwable ex)
        {
            throw new Exception("columnIndex=" + columnIndex, ex);
        }
    }

    public byte[] getLONGVARBINARY(String columnName) throws Exception
    {
        try
        {
            return getLONGVARBINARY(this.mappings.get(columnName));
        }
        catch (Throwable ex)
        {
            throw new Exception("columnName=" + columnName, ex);
        }
    }

    public Date getDATE(int columnIndex) throws Exception
    {
        try
        {
            return (Date) this.data[columnIndex];
        }
        catch (Throwable ex)
        {
            throw new Exception("columnIndex=" + columnIndex, ex);
        }
    }

    public Date getDATE(String columnName) throws Exception
    {
        try
        {
            return getDATE(this.mappings.get(columnName));
        }
        catch (Throwable ex)
        {
            throw new Exception("columnName=" + columnName, ex);
        }
    }
    public LocalDate getLocalDate(int columnIndex) throws Exception
    {
        Date date=getDATE(columnIndex);
        if (date!=null)
        {
            return date.toLocalDate();
        }
        return null;
    }
    public LocalDate getLocalDate(String columnName) throws Exception
    {
        Date date=getDATE(this.mappings.get(columnName));
        if (date!=null)
        {
            return date.toLocalDate();
        }
        return null;
    }

    public Time getTIME(int columnIndex) throws Exception
    {
        try
        {
            return (Time) this.data[columnIndex];
        }
        catch (Throwable ex)
        {
            throw new Exception("columnIndex=" + columnIndex, ex);
        }
    }

    public Time getTIME(String columnName) throws Exception
    {
        try
        {
            return getTIME(this.mappings.get(columnName));
        }
        catch (Throwable ex)
        {
            throw new Exception("columnName=" + columnName, ex);
        }
    }

    public Timestamp getTIMESTAMP(int columnIndex)
    {
        Object object = this.data[columnIndex];
        if (object == null)
        {
            return null;
        }
        if (object instanceof Timestamp)
        {
            return (Timestamp) object;
        }
        else if (object instanceof LocalDateTime)
        {
            return Timestamp.valueOf((LocalDateTime) object);
        }
        throw new RuntimeException();
    }

    public Timestamp getTIMESTAMP(String columnName) throws Exception
    {
        try
        {
            return getTIMESTAMP(this.mappings.get(columnName));
        }
        catch (Throwable ex)
        {
            throw new Exception("columnName=" + columnName, ex);
        }
    }

    public LocalDateTime getLocalDateTime(int columnIndex)
    {
        Timestamp timestamp=getTIMESTAMP(columnIndex);
        if (timestamp == null)
        {
            return null;
        }
        return timestamp.toLocalDateTime();
    }

    public LocalTime getLocalTime(String columnName) throws Exception
    {
        try
        {
            return getLocalTime(this.mappings.get(columnName));
        }
        catch (Throwable ex)
        {
            throw new Exception("columnName=" + columnName, ex);
        }

    }

    public LocalTime getLocalTime(int columnIndex)
    {
        Object object = this.data[columnIndex];
        if (object == null)
        {
            return null;
        }
        if (object instanceof Time)
        {
            return ((Time) object).toLocalTime();
        }
        else if (object instanceof LocalTime)
        {
            return (LocalTime) object;
        }
        throw new RuntimeException();
    }

    public LocalDateTime getLocalDateTime(String columnName) throws Exception
    {
        try
        {
            return getLocalDateTime(this.mappings.get(columnName));
        }
        catch (Throwable ex)
        {
            throw new Exception("columnName=" + columnName, ex);
        }

    }

    public boolean getBIT(int columnIndex) throws Exception
    {
        try
        {
            return (boolean) this.data[columnIndex];
        }
        catch (Throwable ex)
        {
            throw new Exception("columnIndex=" + columnIndex, ex);
        }
    }

    public boolean getBIT(String columnName) throws Exception
    {
        try
        {
            return getBIT(this.mappings.get(columnName));
        }
        catch (Throwable ex)
        {
            throw new Exception("columnName=" + columnName, ex);
        }
    }

    public Boolean getNullableBIT(int columnIndex)
    {
        Object object = this.data[columnIndex];
        if (object == null)
        {
            return null;
        }
        if (object instanceof Boolean)
        {
            return (Boolean) object;
        }
        else if (object instanceof Integer)
        {
            Integer value = (Integer) object;
            return value == 1;
        }
        throw new RuntimeException();
    }

    public Boolean getNullableBIT(String columnName) throws Exception
    {
        try
        {
            return getNullableBIT(this.mappings.get(columnName));
        }
        catch (Throwable ex)
        {
            throw new Exception("columnName=" + columnName, ex);
        }
    }

    @SuppressWarnings("unchecked")
    public <TYPE> TYPE get(int columnIndex) throws Exception
    {
        try
        {
            return (TYPE) this.data[columnIndex];
        }
        catch (Throwable ex)
        {
            throw new Exception("columnIndex=" + columnIndex, ex);
        }
    }

    public <TYPE> TYPE get(String columnName) throws Exception
    {
        try
        {
            return get(this.mappings.get(columnName));
        }
        catch (Throwable ex)
        {
            throw new Exception("columnName=" + columnName, ex);
        }
    }

    public Object[] getObjects()
    {
        return this.data;
    }

    public int getColumns()
    {
        if (this.data == null)
        {
            return 0;
        }
        return this.data.length;
    }

}
