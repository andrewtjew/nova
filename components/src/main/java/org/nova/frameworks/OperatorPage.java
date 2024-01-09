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
package org.nova.frameworks;

import org.nova.html.enums.link_rel;
import org.nova.html.ext.Content;
import org.nova.html.ext.DocType;
import org.nova.html.ext.Head;
import org.nova.html.operator.MenuBar;
import org.nova.html.tags.body;
import org.nova.html.tags.div;
import org.nova.html.tags.head;
import org.nova.html.tags.html;
import org.nova.html.tags.link;
import org.nova.html.tags.p;
import org.nova.html.tags.script;
import org.nova.html.tags.style;
import org.nova.html.templating.Instance;
import org.nova.html.templating.ReplaceMarker;
import org.nova.html.templating.Template;

public class OperatorPage extends Instance
{
    final private Head head;
    final private Content content;
    
    public OperatorPage(Template template)
    {
        super(template);
        this.head=this.returnReplace("head", new Head());
        this.content=this.returnReplace("content", new Content());
    }
    
    public Head head()
    {
        return head;
    }
    public Content content()
    {
        return content;
    }
    
    static Template buildTemplate(MenuBar menuBar,String name,String hostName) throws Throwable
    {
        Content content=new Content();
        content.addInner(new DocType("html"));
        html html=content.returnAddInner(new html());
        head head=html.returnAddInner(new head());
        body body=html.returnAddInner(new body());

        int bannerHeight=48;
        int zIndex=10000;
        int height=bannerHeight+34;
        head.addInner(new script().src("/resources/html/jquery/jquery-1.9.1.min.js"));
//        head.addInner(new script().src("/resources/html/js/nova/nova-remote.js"));
        
        head.addInner(new style().addInner(".heading {z-index:"+zIndex+";position:fixed;top:0px;left:0px;width:100%;margin:0 auto;font-family: Trebuchet MS, Lucida Sans Unicode, Arial, sans-serif;height:"+height+"px;}"
        +" .heading-banner{display:flex;width:100%;height:"+bannerHeight+"px;padding:0;margin:0;border:0;background-color:#ddd;}"
        +" .heading-banner-left{height:"+bannerHeight+"px;background-color:#eee;}"
        +" .heading-banner-center{width:100%;height:"+bannerHeight+"px;}"
        +" .heading-banner-right{background-color:#eee;height:"+bannerHeight+"px;}"
        +" .heading-p{margin:2px;white-space:nowrap;padding-left:8px;padding-right:8px;}"
        +" .body-content{padding:8px;}"));
        
        
        
        head.addInner(new link().rel(link_rel.stylesheet).type("text/css").href("/resources/html/ServerApplication/style.css"));
        head.addInner(new link().rel(link_rel.stylesheet).type("text/css").href("/resources/html/widgets/MenuBar/style.css"));
        head.addInner(new link().rel(link_rel.stylesheet).type("text/css").href("/resources/html/w3c/Accordion/style.css"));
        head.addInner(new ReplaceMarker("head"));
        
        div heading=body.returnAddInner(new div()).addClass("heading");
        div banner=heading.returnAddInner(new div()).addClass("heading-banner");
        div bannerLeft=banner.returnAddInner(new div()).addClass("heading-banner-left");
        bannerLeft.addInner(new div().style("text-align:left;font-size:15px;font-weight:bold;margin:16px;white-space:nowrap;").addInner(name));
        div bannerCenter=banner.returnAddInner(new div()).addClass("heading-banner-center");
        bannerCenter.addInner(new div().style("text-align:center;font-size:24px;font-weight:bold;margin:10px;").addInner(new ReplaceMarker("title")));
        div bannerRight=banner.returnAddInner(new div()).addClass("heading-banner-right");
        bannerRight.addInner(new div().style("text-align:center;").addInner(new p().addClass("heading-p").addInner(hostName).addInner(new p().addClass("heading-p").addInner(new ReplaceMarker("now")))));
        
        heading.addInner(new div().addInner(menuBar));
        
        body.returnAddInner(new div()).style("height:"+height+"px;");
        body.returnAddInner(new div()).addClass("body-content").addInner(new ReplaceMarker("content"));
        return new Template(content);
    }
    

}
