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
package org.nova.html;

import java.util.HashMap;

public class ExtionsionToContentTypeMappings
{
    public static ExtionsionToContentTypeMappings fromDefault()
    {
        ExtionsionToContentTypeMappings mappings=new ExtionsionToContentTypeMappings();
        mappings.addMappings("audio/aac", "aac");
        mappings.addMappings("audio/mpeg", "mp3","mpeg");
        mappings.addMappings("audio/mp4", "mp4");
        mappings.addMappings("text/plain", "txt","text");
        mappings.addMappings("text/csv", "csv");
        mappings.addMappings("text/css", "css");
        mappings.addMappings("text/javascript", "js");
        mappings.addMappings("text/css", "css");
        mappings.addMappings("text/html", "htm","html");
        mappings.addMappings("image/gif", "gif");
        mappings.addMappings("image/x-icon", "ico");
        mappings.addMappings("image/jpeg", "jpeg","jpg","jpe");
        mappings.addMappings("image/png", "png");
        mappings.addMappings("image/bmp", "bmp","bm");
        mappings.addMappings("application/json", "json");
        mappings.addMappings("application/pdf", "pdf");
        mappings.addMappings("application/vnd.ms-excel", "xls");
        mappings.addMappings("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx");
        mappings.addMappings("application/xml", "xml");
        return mappings;
    }
    
    final private HashMap<String,String> typeMappings;

	public ExtionsionToContentTypeMappings()
	{
		this.typeMappings=new HashMap<>();
	}
	
	public void addMappings(String contentType,String...extensions)
	{
		for (String extension:extensions)
		{
			this.typeMappings.put(extension, contentType);
		}
	}
	
	public String getContentType(String file)
	{
		int index=file.lastIndexOf('.');
		if (index<0)
		{
			return null;
		}
		return this.typeMappings.get(file.substring(index+1));
		
	}

}
