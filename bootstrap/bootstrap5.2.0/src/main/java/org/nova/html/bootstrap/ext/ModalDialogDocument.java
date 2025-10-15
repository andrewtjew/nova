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

import org.nova.html.bootstrap.ModalBody;
import org.nova.html.bootstrap.ModalContent;
import org.nova.html.bootstrap.ModalDialog;
import org.nova.html.bootstrap.ModalFooter;
import org.nova.html.bootstrap.ModalHeader;
import org.nova.html.elements.Composer;

public class ModalDialogDocument extends ModalDialog
{
    final private ModalContent content;
    ModalHeader header;
    ModalFooter footer;
    ModalBody body;
    
    public ModalDialogDocument()
    {
        this.content=this.returnAddInner(new ModalContent());
    }
    
    public ModalHeader header()
    {
        if (this.header==null)
        {
            this.header=new ModalHeader();
        }
        return this.header;
    }
    public ModalFooter footer()
    {
        if (this.footer==null)
        {
            this.footer=new ModalFooter();
        }
        return this.footer;
    }
    public ModalBody body()
    {
        if (this.body==null)
        {
            this.body=new ModalBody();
        }
        return this.body;
    }
    public ModalContent modalContent()
    {
        return this.content;
    }
    @Override
    public void compose(Composer composer) throws Throwable
    {
        if (this.header!=null)
        {
            this.content.addInner(this.header);
        }
        if (this.body!=null)
        {
            this.content.addInner(this.body);
        }
        if (this.footer!=null)
        {
            this.content.addInner(this.footer);
        }
        
        super.compose(composer);
    }
}
