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

import org.nova.html.bootstrap.InputComponent;
import org.nova.html.bootstrap.InputGroup;
import org.nova.html.bootstrap.Modal;
import org.nova.html.bootstrap.ModalBody;
import org.nova.html.bootstrap.ModalCancelButton;
import org.nova.html.bootstrap.ModalContent;
import org.nova.html.bootstrap.ModalDialog;
import org.nova.html.bootstrap.ModalFooter;
import org.nova.html.bootstrap.ModalHeader;
import org.nova.html.bootstrap.SubmitButton;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.elements.Composer;
import org.nova.html.tags.form_post;

public class InputDialog extends ModalDocument
{
    public InputDialog(boolean centered,String title,String prepend,InputComponent<?> input,String action,String buttonLabel)
    {
        super(centered);
        this.header().addInner(title).addInner(new ModalCancelButton("&#10060;"));
        form_post form=this.body().returnAddInner(new form_post()).action(action);
        InputGroup group=form.returnAddInner(new InputGroup());
        if (prepend!=null)
        {
            group.prepend(prepend);
        }
        input.autofocus();
        group.addInner(input);
        group.append(new SubmitButton(buttonLabel).mr(2).color(StyleColor.success).form(form.id()));
    }
    public InputDialog(boolean centered,String title,String prepend,InputComponent<?> input,String action)
    {
        this(centered,title,prepend,input,action,"Save");
    }
    public InputDialog(String title,String prepend,InputComponent<?> input,String action)
    {
        this(false,title,prepend,input,action);
    }
    public InputDialog(String title,InputComponent<?> input,String action)
    {
        this(false,title,null,input,action);
    }
    public InputDialog(boolean centered,String title,InputComponent<?> input,String action)
    {
        this(centered,title,null,input,action);
    }
}
