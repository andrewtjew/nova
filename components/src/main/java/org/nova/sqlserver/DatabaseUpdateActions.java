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
package org.nova.sqlserver;

public class DatabaseUpdateActions
{
    public int deleteTable=0;
    public int deleteColumn=0;
    public int createTable=0;
    public int createNullableTableColumn=0;
    public int createNotNullableTableColumn=0;
    public int alterColumnToLargerSize=0;
    public int alterColumnToSmallerSize=0;
    public int alterColumnToNullable=0;
    public int alterColumnToNotNullable=0;
    public int alterIdentityColumns=0;
    public int alterIdentityStart=0;
    public int alterIdentityIncrement=0;
    public int deleteFunction=0;
    public int createFunction=0;
    public int replaceFunction=0;

    public int deleteProcedure=0; 
    public int createProcedure=0;
    public int replaceProcedure=0;
}
