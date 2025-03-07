/*******************************************************************************
 * Copyright (C) 2016-2019 Kat Fung Tjew
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
package org.nova.operations;

public class ApplicationResult
{
    final Object result;
    final Status status;
    final String message;
    public ApplicationResult(Status status,Object result,String message)
    {
        this.status=status;
        this.result=result;
        this.message=message;
    }
    public ApplicationResult()
    {
        this(Status.SUCCESS,null,null);
    }
    public ApplicationResult(Object result)
    {
        this(Status.SUCCESS,result,null);
    }
    public ApplicationResult(String message)
    {
        this(Status.VALIDATION_FAILED,null,message);
    }
    public Object getResult()
    {
        return result;
    }
    public Status getStatus()
    {
        return status;
    }
    public String getMessage()
    {
        return this.message;
    }
}
