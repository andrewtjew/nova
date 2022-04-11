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
import org.nova.html.bootstrap.CustomRadio;
import org.nova.html.bootstrap.CustomSwitch;
import org.nova.html.bootstrap.StyleComponent;

public class CustomSwitchControl extends StyleComponent<CustomSwitchControl>
{
    private CustomSwitch customSwitch;
    private CustomLabel customLabel;

    public CustomSwitchControl(String label,String name)
    {
        super("div", "custom-control");
        addClass("custom-switch");
        this.customSwitch = returnAddInner(new CustomSwitch()).name(name);
        this.customSwitch.id();
        this.customLabel=returnAddInner(new CustomLabel()).for_(customSwitch);
        if (label!=null)
        {
            this.customLabel.addInner(label);
        }
        else
        {
            this.customLabel.addInner("&#x200B;");
        }
    }
    public CustomSwitchControl(String name)
    {
        this(null,name);
    }

    public CustomSwitchControl(String label,String name,String id) throws Exception
    {
        super("div", "custom-control");
        addClass("custom-checkbox");
        this.customSwitch = returnAddInner(new CustomSwitch()).name(name);
        if (id!=null)
        {
            this.customSwitch.id(id);
        }
        else
        {
            this.customSwitch.id();
        }
        this.customLabel=returnAddInner(new CustomLabel()).for_(customSwitch);
        if (label!=null)
        {
            this.customLabel.addInner(label);
        }
        else
        {
            this.customLabel.addInner("&#x200B;");
        }
    }
    
    
    public CustomSwitch customSwitch()
    {
        return this.customSwitch;
    }
    public CustomLabel customLabel()
    {
        return this.customLabel;
    }
    
}
