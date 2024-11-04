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

import org.nova.html.bootstrap.classes.Placement;

public class Button extends ButtonComponent<Button>
{
    public Button()
    { 
        super("button");
        attr("type","button");
    }
    public Button(String label)
    { 
        super("button");
        attr("type","button");
        addInner(label);
    }
    
    public Button popover(String title,String content)
    {
        this.attr("data-bs-toggle","popover");        
        this.attr("data-bs-title",title);
        this.attr("data-bs-content",content);
        return this;
    }
    
    public Button tooltip(String title,Placement placement)
    {
        this.attr("data-bs-toggle","tooltip");        
        this.attr("data-bs-title",title);
        this.attr("data-bs-placement",placement.toString());
        return this;
    }

}
