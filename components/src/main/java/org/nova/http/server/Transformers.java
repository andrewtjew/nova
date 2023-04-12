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
package org.nova.http.server;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Transformers
{

    final ArrayList<ContentReader<?>> contentReaders;
    final ArrayList<ContentWriter> contentWriters;
    final ArrayList<ContentEncoder> contentEncoders;
    final ArrayList<ContentDecoder> contentDecoders;
    final ArrayList<Filter> bottomFilters;
    final ArrayList<Filter> topFilters;
    
    public Transformers()
    {
        this.contentDecoders=new ArrayList<>();
        this.contentEncoders=new ArrayList<>();
        this.contentReaders=new ArrayList<>();
        this.contentWriters=new ArrayList<>();
        this.bottomFilters=new ArrayList<>();
        this.topFilters=new ArrayList<>();
    }

    public void add(ContentReader<?>...contentReaders)
    {
        for (ContentReader<?> contentReader:contentReaders)
        {
            if (this.getContentReader(contentReader.getClass())==null)
            {
                this.contentReaders.add(contentReader);
            }
        }
    }
    public void add(ContentWriter...contentWriters)
    {
        for (ContentWriter contentWriter:contentWriters)
        {
            if (this.getContentWriter(contentWriter.getClass())==null)
            {
                this.contentWriters.add(contentWriter);
            }
        }
    }
    public void add(ContentEncoder...contentEncoders)
    {
        for (ContentEncoder contentEncoder:contentEncoders)
        {
            if (this.getContentEncoder(contentEncoder.getClass())==null)
            {
                this.contentEncoders.add(contentEncoder);
            }
        }
    }
    public void add(ContentDecoder...contentDecoders)
    {
        for (ContentDecoder contentDecoder:contentDecoders)
        {
            if (this.getContentDecoder(contentDecoder.getClass())==null)
            {
                this.contentDecoders.add(contentDecoder);
            }
        }
    }
    public void addBottomFilters(Filter...filters)
    {
        for (Filter filter:filters)
        {
            if (this.getBottomFilter(filter.getClass())==null)
            {
                this.bottomFilters.add(filter);
            }
        }
    }

    public void addTopFilters(Filter...filters)
    {
        for (Filter filter:filters)
        {
            if (this.getTopFilter(filter.getClass())==null)
            {
                this.topFilters.add(filter);
            }
        }
    }

    private Filter getFilter(ArrayList<Filter> filters,Class<? extends Filter> type)
    {
        for (Filter filter:filters)
        {
            for (Class<?> filterClass=filter.getClass();filterClass!=null;filterClass=filterClass.getSuperclass())
            {
                if (filterClass==type)
                {
                    return filter;
                }
            }
        }
        return null;
    }
    
    public Filter getBottomFilter(Class<? extends Filter> type)
    {
    	return getFilter(this.bottomFilters,type);
    }

    public Filter getTopFilter(Class<? extends Filter> type)
    {
    	return getFilter(this.topFilters,type);
    }

    public ContentDecoder getContentDecoder(Class<? extends ContentDecoder> type)
    {
        for (ContentDecoder item:this.contentDecoders)
        {
            if (item.getClass()==type)
            {
                return item;
            }
        }
        return null;
    }
    
    public ContentEncoder getContentEncoder(Type type)
    {
        for (ContentEncoder item:this.contentEncoders)
        {
            if (item.getClass()==type)
            {
                return item;
            }
        }
        return null;
    }
    
    public ContentReader<?> getContentReader(Type type)
    {
        for (ContentReader<?> item:this.contentReaders)
        {
            if (item.getClass()==type)
            {
                return item;
            }
        }
        return null;
    }

    public ContentWriter getContentWriter(Type type)
    {
        for (ContentWriter item:this.contentWriters)
        {
            if (item.getClass()==type)
            {
                return item;
            }
        }
        return null;
    }
//    public ContentReader<?> getContentReader(String mediaType)
//    {
//        for (ContentReader<?> item:this.contentReaders)
//        {
//            if (item.getMediaType().equalsIgnoreCase(mediaType))
//            {
//                return item;
//            }
//        }
//        return null;
//    }
//
//    public ContentWriter<?> getContentWriter(String mediaType)
//    {
//        for (ContentWriter<?> item:this.contentWriters)
//        {
//            if (item.getMediaType().equalsIgnoreCase(mediaType))
//            {
//                return item;
//            }
//        }
//        return null;
//    }
}
