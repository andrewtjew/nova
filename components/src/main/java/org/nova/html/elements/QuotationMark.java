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
package org.nova.html.elements;

public enum QuotationMark
{
    DOUBLE("\""), SINGLE("\'"), APOS("&#39;"), QOUT("&#34;");

    private String value;

    QuotationMark(String value)
    {
        this.value = value;
    }

//    static public char asChar(QuotationMark mark)
//    {
//        switch (mark)
//        {
//            case SINGLE:
//            case APOS:
//            case ESC_SINGLE:
//                return '\'';
//
//            case QOUT:
//            case DOUBLE:
//            case ESC_DOUBLE:
//                return '"';
//
//            default:
//                return (char)0;
//
//        }
//
//    }

    public String toString()
    {
        return this.value;
    }
}
