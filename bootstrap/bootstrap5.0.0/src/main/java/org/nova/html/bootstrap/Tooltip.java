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

import org.nova.annotations.Description;
import org.nova.html.bootstrap.classes.Boundary;
import org.nova.html.bootstrap.classes.Placement;
import org.nova.html.bootstrap.classes.Trigger;
import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.elements.NodeElement;
import org.nova.html.elements.QuotationMark;
import org.nova.html.elements.StringComposer;
import org.nova.html.elements.TagElement;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.tags.script;
import org.nova.json.ObjectMapper;
import org.nova.utils.Utils;

public class Tooltip extends TipComponent<Tooltip>
{
    public Tooltip(TagElement<?> toggler)
    {
    	super(toggler);
        this.toggler.attr("data-toggle","tooltip");
    }
    
    public String js_tooltip(TipOption option,QuotationMark mark)
    {
    	return "$("+mark+"#"+this.toggler.id()+mark+").tooltip("+mark+option+mark+")";
    }
    public String js_tooltip(TipOption option)
    {
    	return js_tooltip(option, QuotationMark.APOS);
    }
    
    public static script script_ready()
    {
        return new script().addInner("$(document).ready(function(){$('[data-toggle=\"tooltip\"]').tooltip();});");
    }
}
