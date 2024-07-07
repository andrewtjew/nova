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
package org.nova.html.ext;
import org.nova.html.tags.html;
import org.nova.html.tags.body;

public class Page extends Content
{
    final private html html;
    final private Head head;
    final private body body;
    final private boolean stateless;
    
    public Page(String docType,boolean stateless)
    {
        addInner(new DocType(docType));
        this.html=returnAddInner(new html());
        this.head=this.html.returnAddInner(new Head());
        this.body=this.html.returnAddInner(new body());
        this.stateless=stateless;
    }
    
    public Page(boolean continuationPage)
    {
        this("html",continuationPage);
    }
    public Page()
    {
        this(false);
    }
    
    public Head head()
    {
        return this.head;
    }
    public body body()
    {
        return this.body;
    }
    public html html()
    {
        return this.html;
    }
    public boolean isStateless()
    {
        return this.stateless;
    }
    
}
