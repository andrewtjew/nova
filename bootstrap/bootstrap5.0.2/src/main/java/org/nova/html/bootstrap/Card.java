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

import org.nova.html.elements.Composer;

public class Card extends StyleComponent<Card>
{
    private CardHeader header;
    private CardFooter footer;
    private CardBody body;
    
    public Card()
    {
        super("div","card");
    }
    
    public CardHeader header()
    {
        if (this.header==null)
        {
            this.header=new CardHeader();
        }
        return this.header;
    }
    public CardFooter footer()
    {
        if (this.footer==null)
        {
            this.footer=new CardFooter();
        }
        return this.footer;
    }
    public CardBody body()
    {
        if (this.body==null)
        {
            this.body=new CardBody();
        }
        return this.body;
    }
    
    @Override
    public void compose(Composer composer) throws Throwable
    {
        if (this.header!=null)
        {
            this.addInner(this.header);
        }
        if (this.body!=null)
        {
            this.addInner(this.body);
        }
        if (this.footer!=null)
        {
            this.addInner(this.footer);
        }
        super.compose(composer);
    }
}