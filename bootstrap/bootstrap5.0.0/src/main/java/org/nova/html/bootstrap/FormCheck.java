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

import org.nova.html.tags.label;
import org.nova.html.tags.small;

public class FormCheck extends StyleComponent<FormCheck>
{
    public FormCheck()
    {
        super("div","form-check");
    }
    
    public FormCheck inline()
    {
        this.addClass("form-check-inline");
        return this;
    }

//    public FormCheck add(FormCheckbox element,String labelText)
//    {
//        returnAddInner(element);
//        FormCheckLabel label=returnAddInner(new FormCheckLabel()).addInner(labelText).form_control();
//        label.for_(element);
//        return this;
//    }

    public InputCheckbox addInputCheckbox(String labelText)
    {
        InputCheckbox element=new InputCheckbox().form_check_input();
        returnAddInner(element);
        FormCheckLabel label=returnAddInner(new FormCheckLabel()).addInner(labelText);
        label.for_(element);
        return element;
    }
    public InputRadio addInputRadio(String labelText)
    {
        InputRadio element=new InputRadio().form_check_input();
        returnAddInner(element);
        FormCheckLabel label=returnAddInner(new FormCheckLabel()).addInner(labelText);
        label.for_(element);
        return element;
    }
    public InputRadio addInputRadio(String labelText,String id)
    {
        InputRadio element=new InputRadio().form_check_input();
        element.id(id);
        returnAddInner(element);
        FormCheckLabel label=returnAddInner(new FormCheckLabel()).addInner(labelText);
        label.for_(element);
        return element;
    }
    
}
