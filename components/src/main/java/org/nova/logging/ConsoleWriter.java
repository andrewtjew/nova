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
package org.nova.logging;

import java.io.OutputStream;

import org.nova.logging.Formatter;
import org.nova.utils.Utils;

public class ConsoleWriter extends OutputStreamWriter
{
    final private boolean outputSegments; 
    public ConsoleWriter(Formatter formatter,boolean outputSegments) throws Throwable
    {
        super(formatter);
        this.outputSegments=outputSegments;
    }

    @Override
    public OutputStream openOutputStream(long marker) throws Throwable
    {
        if (this.outputSegments)
        {
            System.out.println("--- Begin Segment: Marker="+marker+", Time="+Utils.millisToLocalDateTimeString(marker));
        }
        return System.out;
    }
    @Override
    public void closeOutputStream(OutputStream outputStream)
    {
        if (this.outputSegments)
        {
            System.out.println("--- End Segment ---");
        }
    }

}
