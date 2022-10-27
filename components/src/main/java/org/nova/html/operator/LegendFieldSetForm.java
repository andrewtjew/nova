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

import org.nova.html.elements.NodeElement;
import org.nova.html.elements.Element;
import org.nova.html.elements.GlobalEventTagElement;
import org.nova.html.tags.fieldset;
import org.nova.html.tags.form_get;
import org.nova.html.tags.form_post;
import org.nova.html.tags.legend;

public class LegendFieldSetForm extends NodeElement<LegendFieldSetForm>
{
    final private GlobalEventTagElement<?> form;
    final private fieldset fieldset;
    final private legend legend;
    
    public LegendFieldSetForm(String heading,String actionURL,boolean post)
    {
        if (post)
        {
            this.form=new form_post().action(actionURL);
        }
        else
        {
            this.form=new form_get().action(actionURL);
        }
        super.addInner(this.form);
        this.fieldset=this.form.returnAddInner(new fieldset());
        this.legend=this.fieldset.returnAddInner(new legend()).addInner(heading);
    }
    public LegendFieldSetForm(String heading,String actionURL)
    {
        this(heading,actionURL,true);
    }
    
    @Override
    public LegendFieldSetForm addInner(Element element)
    {
        this.fieldset.addInner(element);
        return this;
    }
//    @Override
//    public LegendFieldSetForm setInner(Element element)
//    {
//        this.fieldset.setInner(element);
//        return this;
//    }
    @Override
    public LegendFieldSetForm addInners(Element... elements)
    {
        this.fieldset.addInners(elements);
        return this;
    }
    @Override
    public LegendFieldSetForm addInner(Object object)
    {
        this.fieldset.addInner(object);
        return this;
    }
    
    
    
}
