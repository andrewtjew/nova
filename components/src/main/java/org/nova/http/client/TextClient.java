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
//package org.nova.http.client;
//
//import java.util.concurrent.TimeUnit;
//
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpDelete;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPatch;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.client.methods.HttpPut;
//import org.apache.http.entity.StringEntity;
//import org.nova.http.Header;
//import org.nova.logging.Logger;
//import org.nova.tracing.Trace;
//import org.nova.tracing.TraceManager;
//import org.nova.utils.FileUtils;
//
//public class TextClient
//{
//	final private TraceManager trace;
//	final private Logger logger;
//	final private HttpClient client;
//	final private String endPoint;
//	final private Header[] headers;
//	
//	public TextClient(TraceManager trace,Logger logger,String endPoint,HttpClient client,Header...headers)
//	{
//		this.trace=trace;
//		this.logger=logger;
//		this.endPoint=endPoint;
//		this.client=client;
//		this.headers=headers;
//	}
//
//	public TextClient(TraceManager traceManager,Logger logger,String endPoint)
//	{
//		this(traceManager,logger,endPoint,HttpClientFactory.createClient());
//	}
//
//	public TextResponse get(Trace parent,String traceCategoryOverride,String pathAndQuery,Header...headers) throws Exception
//	{
//		try (Trace trace=new Trace(this.trace, parent, traceCategoryOverride!=null?traceCategoryOverride:pathAndQuery))
//		{
//			HttpGet get=new HttpGet(this.endPoint+pathAndQuery);
//			if (this.headers!=null)
//			{
//				for (Header header:this.headers)
//				{
//					get.setHeader(header.getName(),header.getValue());
//				}
//			}
//			for (Header header:headers)
//			{
//                get.setHeader(header.getName(),header.getValue());
//			}
//			HttpResponse response=this.client.execute(get);
//			try
//			{
//				int statusCode=response.getStatusLine().getStatusCode();
//				org.apache.http.Header contentType=response.getEntity().getContentType();
//				String text=FileUtils.readString(response.getEntity().getContent());
//				org.apache.http.Header[] responseHeaders=response.getAllHeaders();
//				Header[] textResponseHeaders=new Header[responseHeaders.length];
//				for (int i=0;i<responseHeaders.length;i++)
//				{
//				    textResponseHeaders[i]=new Header(responseHeaders[i].getName(),responseHeaders[i].getValue());
//				}
//				return new TextResponse(statusCode,text,textResponseHeaders);
//			}
//			finally
//			{
//				response.getEntity().getContent().close();
//			}
//		}		
//	}
//    
//	public TextResponse post(Trace parent,String traceCategoryOverride,String pathAndQuery,String content,Header...headers) throws Exception
//    {
//        try (Trace trace=new Trace(this.trace, parent, traceCategoryOverride!=null?traceCategoryOverride:pathAndQuery))
//        {
//            HttpPost post=new HttpPost(this.endPoint+pathAndQuery);
//            if (content!=null)
//            {
//                StringEntity entity=new StringEntity(content);
//                post.setEntity(entity);
//            }
//            if (this.headers!=null)
//            {
//                for (Header header:this.headers)
//                {
//                    post.setHeader(header.getName(),header.getValue());
//                }
//            }
//            for (Header header:headers)
//            {
//                post.setHeader(header.getName(),header.getValue());
//            }
//            HttpResponse response=this.client.execute(post);
//            try
//            {
//                int statusCode=response.getStatusLine().getStatusCode();
//                org.apache.http.Header[] responseHeaders=response.getAllHeaders();
//                Header[] textResponseHeaders=new Header[responseHeaders.length];
//                for (int i=0;i<responseHeaders.length;i++)
//                {
//                    textResponseHeaders[i]=new Header(responseHeaders[i].getName(),responseHeaders[i].getValue());
//                }
//                return new TextResponse(statusCode,FileUtils.readString(response.getEntity().getContent()),textResponseHeaders);
//            }
//            finally
//            {
//                response.getEntity().getContent().close();
//            }
//        }       
//    }
//
//	public int put(Trace parent,String traceCategoryOverride,String pathAndQuery,String content,Header...headers) throws Exception
//    {
//        try (Trace trace=new Trace(this.trace, parent, traceCategoryOverride!=null?traceCategoryOverride:pathAndQuery))
//        {
//            HttpPut put=new HttpPut(this.endPoint+pathAndQuery);
//            if (content!=null)
//            {
//                StringEntity entity=new StringEntity(content);
//                put.setEntity(entity);
//            }
//            if (this.headers!=null)
//            {
//                for (Header header:this.headers)
//                {
//                    put.setHeader(header.getName(),header.getValue());
//                }
//            }
//            for (Header header:headers)
//            {
//                put.setHeader(header.getName(),header.getValue());
//            }
//            HttpResponse response=this.client.execute(put);
//            try
//            {
//                return response.getStatusLine().getStatusCode();
//            }
//            finally
//            {
//                response.getEntity().getContent().close();
//            }
//        }       
//    }
//
//	public int patch(Trace parent,String traceCategoryOverride,String pathAndQuery,String content,Header...headers) throws Exception
//    {
//        try (Trace trace=new Trace(this.trace, parent, traceCategoryOverride!=null?traceCategoryOverride:pathAndQuery))
//        {
//            HttpPatch patch=new HttpPatch(this.endPoint+pathAndQuery);
//            if (content!=null)
//            {
//                StringEntity entity=new StringEntity(content);
//                patch.setEntity(entity);
//            }
//            if (this.headers!=null)
//            {
//                for (Header header:this.headers)
//                {
//                    patch.setHeader(header.getName(),header.getValue());
//                }
//            }
//            for (Header header:headers)
//            {
//                patch.setHeader(header.getName(),header.getValue());
//            }
//            HttpResponse response=this.client.execute(patch);
//            try
//            {
//                return response.getStatusLine().getStatusCode();
//            }
//            finally
//            {
//                response.getEntity().getContent().close();
//            }
//        }       
//    }
//    public int delete(Trace parent,String traceCategoryOverride,String pathAndQuery,Header...headers) throws Exception
//    {
//        try (Trace trace=new Trace(this.trace, parent, traceCategoryOverride!=null?traceCategoryOverride:pathAndQuery))
//        {
//            HttpDelete delete=new HttpDelete(this.endPoint+pathAndQuery);
//            if (this.headers!=null)
//            {
//                for (Header header:this.headers)
//                {
//                    delete.setHeader(header.getName(),header.getValue());
//                }
//            }
//            for (Header header:headers)
//            {
//                delete.setHeader(header.getName(),header.getValue());
//            }
//            HttpResponse response=this.client.execute(delete);
//            try
//            {
//                return response.getStatusLine().getStatusCode();
//            }
//            finally
//            {
//                response.getEntity().getContent().close();
//            }
//        }       
//    }
//
//			
//}
