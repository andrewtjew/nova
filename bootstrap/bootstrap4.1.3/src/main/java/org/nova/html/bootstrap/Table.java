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
import org.nova.html.elements.Element;
import org.nova.html.tags.div;
import org.nova.html.tags.tbody;

public class Table extends StyleComponent<Table>
{
    private final tbody tbody;
    private boolean responsive=false;
    public Table(TableHeading heading)
    {
       super("table","table");
       if (heading!=null)
       {
           addInner(heading);
       }
       this.tbody=returnAddInner(new tbody());
    }

    public Table addHeading(TableHeading heading)
    {
        addInner(heading);
        return this;
    }
    public Table()
    {
        this(null);
    }

    public Table w_auto()
    {
        addClass("w-auto");
        return this;
    }
    public Table hover()
    {
        addClass("table-hover");
        return this;
    }
    public Table striped()
    {
        addClass("table-striped");
        return this;
    }
    public Table bordered()
    {
        addClass("table-bordered");
        return this;
    }
    public Table borderless()
    {
        addClass("table-borderless");
        return this;
    }
    public Table responsive()
    {
        this.responsive=true;
        return this;
    }

    public Table addRow(TableRow row)
    {
        this.tbody.addInner(row);
        return this;
    }
    public Table addRowInline(Object...objects)
    {
        this.tbody.addInner(new TableRow().add(objects));
        return this;
    }
    
    public TableRow returnAddRow()
    {
        TableRow tableRow=new TableRow();
        addRow(tableRow);
        return tableRow;
    }
    public tbody tbody()
    {
        return this.tbody;
    }
    @Override
    public void compose(Composer composer) throws Throwable
    {
        
        if (this.responsive)
        {
            div group=new div().addClass("table-responsive");
            group.addInner(new Element()
            {
                @Override
                public void compose(Composer composer) throws Throwable
                {
                    composeThis(composer);
                }
            });
            group.compose(composer);
        }
        else
        {
            super.compose(composer);
        }
        
    }
    
}
