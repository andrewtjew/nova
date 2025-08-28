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
package org.nova.html.operator;

import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.properties.Style;
import org.nova.html.tags.button_button;
import org.nova.html.tags.div;
import org.nova.html.tags.input_checkbox;

public class ConfirmButton extends Element
{
    final private div content;

    public ConfirmButton(String location,String label,String title)
    {
        input_checkbox check=new input_checkbox();
        check.id();
        button_button button=new button_button();
        button.addInner(label);
        if (title!=null)
        {
            button.title(title);
        }
        button.onclick("if (document.getElementById('"+check.id()+"').checked==true) {window.location='"+location+"';}");
        this.content=new div().addInner(check).addInner(button);

    }

    public ConfirmButton(String location,String label)
    {
        this(location,label,null);
    }
    public ConfirmButton style(String style)
    {
        this.content.style(style);
        return this;
    }

    public ConfirmButton style(Style style)
    {
        this.content.style(style);
        return this;
    }

    
    @Override
    public void compose(Composer composer) throws Throwable
    {
        composer.compose(this.content);
    }
}
