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

import org.nova.html.bootstrap.CustomCheckBox;
import org.nova.html.bootstrap.CustomControl;
import org.nova.html.bootstrap.CustomLabel;
import org.nova.html.bootstrap.StyleComponent;

public class CustomCheckBoxControl extends StyleComponent<CustomControl>
{
    private CustomCheckBox customCheckBox;
    private CustomLabel customLabel;

    public CustomCheckBoxControl(String label,String name,Object value)
    {
        super("div", "custom-control");
        addClass("custom-checkbox");
        this.customCheckBox = returnAddInner(new CustomCheckBox()).name(name);
        if (value!=null)
        {
            this.customCheckBox.value(value);
        }
        this.customCheckBox.id();
        this.customLabel=returnAddInner(new CustomLabel()).for_(customCheckBox);
        if (label!=null)
        {
            this.customLabel.addInner(label);
        }
        else
        {
            this.customLabel.addInner("&#x200B;");
        }
    }

    public CustomCheckBoxControl(String label,String name,String id,Object value) throws Exception
    {
        super("div", "custom-control");
        addClass("custom-checkbox");
        this.customCheckBox = returnAddInner(new CustomCheckBox()).name(name);
        if (value!=null)
        {
            this.customCheckBox.value(value);
        }
        if (id!=null)
        {
            this.customCheckBox.id(id);
        }
        else
        {
            this.customCheckBox.id();
        }
        this.customLabel=returnAddInner(new CustomLabel()).for_(customCheckBox);
        if (label!=null)
        {
            this.customLabel.addInner(label);
        }
        else
        {
            this.customLabel.addInner("&#x200B;");
        }
    }
    
    public CustomCheckBoxControl(String label,String name)
    {
        this(label,name,null);
    }
    
    public CustomCheckBox customCheckBox()
    {
        return this.customCheckBox;
    }
    public CustomLabel label()
    {
        return this.customLabel;
    }
}
