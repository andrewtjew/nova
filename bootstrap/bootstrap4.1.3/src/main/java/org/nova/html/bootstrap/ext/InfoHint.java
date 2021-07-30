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
package org.nova.html.bootstrap.ext;

import org.nova.html.bootstrap.BootStrapUtils;
import org.nova.html.bootstrap.ClassBuilder;
import org.nova.html.bootstrap.classes.Font;
import org.nova.html.bootstrap.classes.Rounded;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.ext.Head;

public class InfoHint extends Span
{
    public InfoHint(Head head,String key,String hintText,String text)
    {
        tooltip(hintText);
        addInner(text);
        if (head!=null)
        {
            head.add(key, BootStrapUtils.readyToolTipScript());
        }
    }
    public InfoHint(Head head,String key,String hintText)
    {
        this(head,key,hintText,"i");
        color(StyleColor.light).border(StyleColor.info);
        px(2).mx(1);
        rounded(Rounded.circle);
        border();
        addClass(new ClassBuilder().font(Font.weight_bold));
    }
    public InfoHint(String hintText)
    {
        this(null,null,hintText);
    }
}
