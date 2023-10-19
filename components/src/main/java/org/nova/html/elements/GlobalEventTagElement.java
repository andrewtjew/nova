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
package org.nova.html.elements;

public class GlobalEventTagElement<ELEMENT extends GlobalEventTagElement<ELEMENT>> extends GlobalTagElement<ELEMENT>
{
    protected GlobalEventTagElement(String tag)
    {
        super(tag);
    }
    protected GlobalEventTagElement(String tag,boolean noEndTag)
    {
        super(tag,noEndTag);
    }

    public ELEMENT onafterprint(String script)
    {
        return attr("onafterprint",script);
    }
    public ELEMENT onbeforeprint(String script)
    {
        return attr("onbeforeprint",script);
    }
    public ELEMENT onbeforeunload(String script)
    {
        return attr("onbeforeunload",script);
    }
    public ELEMENT onerror(String script)
    {
        return attr("onerror",script);
    }
    public ELEMENT onhashchange(String script)
    {
        return attr("onhashchange",script);
    }
    public ELEMENT onload(String script)
    {
        return attr("onload",script);
    }
    public ELEMENT onmessage(String script)
    {
        return attr("onmessage",script);
    }
    public ELEMENT onoffline(String script)
    {
        return attr("onoffline",script);
    }
    public ELEMENT ononline(String script)
    {
        return attr("ononline",script);
    }
    public ELEMENT onpagehide(String script)
    {
        return attr("onpagehide",script);
    }
    public ELEMENT onpageshow(String script)
    {
        return attr("onpageshow",script);
    }
    public ELEMENT onpopstate(String script)
    {
        return attr("onpopstate",script);
    }
//    public ELEMENT onresize(String script)
//    {
//        return attr("onresize",script);
//    }
    public ELEMENT onstorage(String script)
    {
        return attr("onstorage",script);
    }
    public ELEMENT onunload(String script)
    {
        return attr("onunload",script);
    }
    //form
    public ELEMENT onblur(String script)
    {
        return attr("onblur",script);
    }
    public ELEMENT onchange(String script)
    {
        return attr("onchange",script);
    }
    public ELEMENT oncontextmenu(String script)
    {
        return attr("oncontextmenu",script);
    }
    public ELEMENT onfocus(String script)
    {
        return attr("onfocus",script);
    }
    public ELEMENT oninput(String script)
    {
        return attr("oninput",script);
    }
    public ELEMENT oninvalid(String script)
    {
        return attr("oninvalid",script);
    }
    public ELEMENT onreset(String script)
    {
        return attr("onreset",script);
    }
    public ELEMENT onsearch(String script)
    {
        return attr("onsearch",script);
    }
    public ELEMENT onselect(String script)
    {
        return attr("onselect",script);
    }
    //keyboard
    public ELEMENT onkeydown(String script)
    {
        return attr("onkeydown",script);
    }
    public ELEMENT onkeypress(String script)
    {
        return attr("onkeypress",script);
    }
    public ELEMENT onkeyup(String script)
    {
        return attr("onkeyup",script);
    }
    //mouse
    public ELEMENT onclick(String script)
    {
        return attr("onclick",script);
    }
    public ELEMENT ondblclick(String script)
    {
        return attr("ondblclick",script);
    }
    public ELEMENT onmousedown(String script)
    {
        return attr("onmousedown",script);
    }
    public ELEMENT onmouseout(String script)
    {
        return attr("onmouseout",script);
    }
    public ELEMENT onmouseover(String script)
    {
        return attr("onmouseover",script);
    }
    public ELEMENT onmouseenter(String script)
    {
        return attr("onmouseenter",script);
    }
    public ELEMENT onmouseleave(String script)
    {
        return attr("onmouseleave",script);
    }
    public ELEMENT onmouseup(String script)
    {
        return attr("onmouseup",script);
    }
    public ELEMENT onmousemove(String script)
    {
        return attr("onmousemove",script);
    }
    public ELEMENT onwheel(String script)
    {
        return attr("onwheel",script);
    }
    public ELEMENT ondrag(String script)
    {
        return attr("ondrag",script);
    }
    public ELEMENT ondragend(String script)
    {
        return attr("ondragend",script);
    }
    public ELEMENT ondragenter(String script)
    {
        return attr("ondragenter",script);
    }
    public ELEMENT ondragleave(String script)
    {
        return attr("ondragleave",script);
    }
    public ELEMENT ondragover(String script)
    {
        return attr("ondragover",script);
    }
    public ELEMENT ondragstart(String script)
    {
        return attr("ondragstart",script);
    }
    public ELEMENT ondrop(String script)
    {
        return attr("ondrop",script);
    }
    public ELEMENT onscroll(String script)
    {
        return attr("onscroll",script);
    }
    //clipboard
    public ELEMENT oncopy(String script)
    {
        return attr("oncopy",script);
    }
    public ELEMENT oncut(String script)
    {
        return attr("oncut",script);
    }
    public ELEMENT onpaste(String script)
    {
        return attr("onpaste",script);
    }
    //media
    public ELEMENT onabort(String script)
    {
        return attr("onabort",script);
    }
    public ELEMENT oncanplay(String script)
    {
        return attr("oncanplay",script);
    }
    public ELEMENT oncanplaythrough(String script)
    {
        return attr("oncanplaythrough",script);
    }
    public ELEMENT oncuechange(String script)
    {
        return attr("oncuechange",script);
    }
    public ELEMENT ondurationchange(String script)
    {
        return attr("ondurationchange",script);
    }
    public ELEMENT onemptied(String script)
    {
        return attr("onemptied",script);
    }
    public ELEMENT onended(String script)
    {
        return attr("onended",script);
    }
    /*
    public ELEMENT onerror(String script)
    {
        return attr("onerror",script);
    }
    */
    public ELEMENT onloadeddata(String script)
    {
        return attr("onloadeddata",script);
    }
    public ELEMENT onloadedmetadata(String script)
    {
        return attr("onloadedmetadata",script);
    }
    public ELEMENT onloadstart(String script)
    {
        return attr("onloadstart",script);
    }
    public ELEMENT onpause(String script)
    {
        return attr("onpause",script);
    }
    public ELEMENT onplay(String script)
    {
        return attr("onplay",script);
    }
    public ELEMENT onplaying(String script)
    {
        return attr("onplaying",script);
    }
    public ELEMENT onprogress(String script)
    {
        return attr("onprogress",script);
    }
    public ELEMENT onratechange(String script)
    {
        return attr("onratechange",script);
    }
    public ELEMENT onseeked(String script)
    {
        return attr("onseeked",script);
    }
    public ELEMENT onseeking(String script)
    {
        return attr("onseeking",script);
    }
    public ELEMENT onstalled(String script)
    {
        return attr("onstalled",script);
    }
    public ELEMENT onsuspend(String script)
    {
        return attr("onsuspend",script);
    }
    public ELEMENT ontimeupdate(String script)
    {
        return attr("ontimeupdate",script);
    }
    public ELEMENT onvolumechange(String script)
    {
        return attr("onvolumechange",script);
    }
    public ELEMENT onwaiting(String script)
    {
        return attr("onwaiting",script);
    }
    //misc
    public ELEMENT onshow(String script)
    {
        return attr("onshow",script);
    }
    public ELEMENT ontoggle(String script)
    {
        return attr("ontoggle",script);
    }

}
