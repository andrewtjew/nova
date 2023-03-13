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
package org.nova.html.bootstrap;

import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.enums.character_set;
import org.nova.html.enums.http_equiv;
import org.nova.html.enums.link_rel;
import org.nova.html.enums.name;
import org.nova.html.ext.Content;
import org.nova.html.ext.DocType;
import org.nova.html.ext.Head;
import org.nova.html.tags.body;
import org.nova.html.tags.html;
import org.nova.html.tags.link;
import org.nova.html.tags.meta;
import org.nova.html.tags.script;

public class BootStrapPage extends Element
{
    final private Head head;
    final private body body;
    final private Content content;
    public BootStrapPage(String title,String lang,String compatible)
	{
        this.content=new Content();
        this.content.addInner(new DocType("html"));
    	html html=this.content.returnAddInner(new html().lang(lang));
    	
    	this.head=html.returnAddInner(new Head());
    	if (title!=null)
    	{
    	    this.head.title(title);
    	}
    	if (compatible!=null)
    	{
    	    this.head().addInner(new meta().http_equiv_content(http_equiv.X_UA_compatible,compatible));
    	}
        this.head.addInner(new meta().charset(character_set.UTF_8).http_equiv(http_equiv.content_type).name(name.viewport).content("width=device-width, initial-scale=1, shrink-to-fit=no"));
//        this.head.addInner(new meta().http_equiv(http_equiv.content_type).content("text/html;charset=utf-8;"));
    	
    	
    	this.head.addInner(new link().rel(link_rel.stylesheet).href("https://cdn.jsdelivr.net/npm/bootstrap@5.0.1/dist/css/bootstrap.min.css"));
        this.head.addInner(new link().rel(link_rel.stylesheet).href("https://cdn.jsdelivr.net/npm/bootstrap-icons@1.3.0/font/bootstrap-icons.css"));
    	this.head.addInner(new script().src("https://cdn.jsdelivr.net/npm/bootstrap@5.0.1/dist/js/bootstrap.bundle.min.js"));
        

        this.body=html.returnAddInner(new body());
	}

    public BootStrapPage(String title,String lang)
    {
        this(title,lang,null);
    }  
    public BootStrapPage(String title)
    {
        this(title,"en");
    }
    public BootStrapPage()
    {
        this(null);
    }
    public Head head()
    {
    	return this.head;
    }
    public body body()
    {
    	return this.body;
    }

    @Override
    public void compose(Composer composer) throws Throwable
    {
        composer.compose(this.content);
        
    }
    
}
