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

public class MySqlConfiguration
{
	public MySqlConfiguration(String host,int port,String schema,int poolSize,long connectionKeepAlive,long maximumRecentlyUsedCount)
	{
		this.host=host;
		this.port=port;
		this.schema=schema;
		this.poolSize=poolSize;
		this.connectionKeepAlive=connectionKeepAlive;
		this.maximumRecentlyUsedCount=maximumRecentlyUsedCount;
	}
    public MySqlConfiguration(String host,int port,String schema,int poolSize,long connectionKeepAlive)
    {
        this(host,port,schema,poolSize,10000,1000000);
    }
	public MySqlConfiguration(String host,String schema)
	{
		this(host,3306,schema,10,10000,1000000);
	}
    public MySqlConfiguration(String schema)
    {
        this("localhost",schema);
    }
	
	String host="localhost";
	int port=3306;
	String schema;
	int poolSize=2;
	long connectionKeepAlive=100000;
	long maximumRecentlyUsedCount=1000000;
}
