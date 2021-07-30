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

import org.nova.html.tags.img;

public class NavbarBrand extends StyleComponent<NavbarBrand>
{
    public NavbarBrand(String label,String href,String imageURL,int imageWidth,int imageHeight)
    {
        super("a","navbar-brand");
        addInner(label);
        attr("href",href);
        if (imageURL!=null)
        {
            addInner(new img().src(imageURL).width(imageWidth).height(imageHeight));
        }
        
    }
    public NavbarBrand(String href,String imageURL,int imageWidth,int imageHeight)
    {
        this(null,href,imageURL,imageWidth,imageHeight);
    }
    
    public NavbarBrand(String label,String href)
    {
        this(label,href,null,0,0);
    }
    
    public NavbarBrand(String label)
    {
        this(label,null,null,0,0);
    }
    
    
}
