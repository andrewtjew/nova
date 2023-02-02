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

public class SqlServerConfiguration
{
    String default_host="localhost";
    static int default_port=1433;
    static int default_poolSize=10;
    static long default_connectionKeepAlive=3600*1000;
    static long default_maximumRecentlyUsedCount=1000;

	public SqlServerConfiguration(String host,int port,String database,int poolSize,long connectionKeepAliveMs,long maximumLeastRecentlyUsedCount)
	{
		this.host=host;
		this.port=port;
		this.database=database;
		this.poolSize=poolSize;
		this.connectionKeepAlive=connectionKeepAliveMs;
		this.maximumLeastRecentlyUsedCount=maximumLeastRecentlyUsedCount;
	}
	public SqlServerConfiguration(String host,String database)
	{
		this(host,1433,database,10,10000,1000000);
	}

	String database;
	boolean connectImmediately=true;
//	public String host;
//	public int port=1433;
//	public int poolSize=10;
//	public long connectionKeepAliveMs=10000;
//	public long maximumLeastRecentlyUsedCount=1000000;
	
    String host=default_host;
    int port=default_port;
    int poolSize=default_poolSize;
    long connectionKeepAlive=default_connectionKeepAlive;
    long maximumLeastRecentlyUsedCount=default_maximumRecentlyUsedCount;	
	
}
