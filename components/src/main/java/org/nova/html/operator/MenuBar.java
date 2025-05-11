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

import java.util.ArrayList;
import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.enums.link_rel;
import org.nova.html.ext.Head;
import org.nova.html.ext.InlineHtml;
import org.nova.html.tags.ul;
import org.nova.html.tags.a;
import org.nova.html.tags.hr;
import org.nova.html.tags.label;
import org.nova.html.tags.li;
import org.nova.html.tags.link;
import org.nova.html.tags.span;

public class MenuBar extends Element
{
    static class Item
    {
        ArrayList<Item> subItems;
        String href;
        boolean enabled;
        final public String name;
        public Item(String name,boolean enabled)
        {
            this.name=name;
            this.enabled=enabled;
        }
    }
    
    private final ArrayList<Item> rootItems=new ArrayList<>();
    
    public MenuBar(Head head,String id,String sourcePath,String cssFile)
    {
        head.add(MenuBar.class.getCanonicalName(), new link().rel(link_rel.stylesheet).type("text/css").href(sourcePath+cssFile));
    }
    public MenuBar(Head head,String id)
    {
        this(head,id, "/resources/html","/widgets/MenuBar/style.css");
    }

    public MenuBar()
    {
    }
    
    Item find(ArrayList<Item> items,String name)
    {
        for (Item item:items)
        {
            if ((name!=null)&&(name.equals(item.name)))
            {
                return item;
            }
        }
        return null;
    }
    
    public MenuBar add(String href,String...names)
    {
        add(true,href,names,0,rootItems);
        return this;
    }
    public MenuBar add(boolean enabled,String href,String...names)
    {
        add(enabled,href,names,0,rootItems);
        return this;
    }
    public MenuBar addDivider(String...names)
    {
        String[] namesWithDivider=new String[names.length+1];
        System.arraycopy(names, 0, namesWithDivider, 0, names.length);
        add(true,null,namesWithDivider,0,rootItems);
        return this;
    }

    public MenuBar setEnabled(boolean enabled,String...names)
    {
        setEnabled(enabled,names,0,this.rootItems);
        return this;
    }
    
    private void setEnabled(boolean enabled,String[] names,int level,ArrayList<Item> items)
    {
        String name=names[level];
        Item item=find(items,name);
        if (item==null)
        {
            return;
        }
        if (level==names.length-1)
        {
            item.enabled=enabled;
        }
        else
        {
            if (item.subItems==null)
            {
                return;
            }
            setEnabled(enabled,names,level+1,item.subItems);
        }
    }

    
    private void add(boolean enabled,String href,String[] names,int level,ArrayList<Item> items)
    {
        String name=names[level];
        Item item=find(items,name);
        if (item==null)
        {
            item=new Item(name,enabled);
            items.add(item);
        }
        if (level==names.length-1)
        {
            item.href=href;
        }
        else
        {
            if (item.subItems==null)
            {
                item.subItems=new ArrayList<>();
            }
            add(enabled,href,names,level+1,item.subItems);
        }
    }

    @Override
    public void compose(Composer builder) throws Throwable
    {
        ul menu=new ul();
        menu.addClass("menu-level-0");
        write(menu,0,this.rootItems);
        menu.compose(builder);
    }
    
    private void write(ul ul,int level,ArrayList<Item> items)
    {
        double longestSubMenu=0;
        if (level>0)
        {
            for (Item item:items)
            {
                ArrayList<Item> subItems=item.subItems;
                if (item.name!=null)
                {
                    int length=item.name.length();
                    if (length>longestSubMenu)
                    {
                        longestSubMenu=length;
                    }
                }
            }
        }
        if (longestSubMenu>20)
        {
            longestSubMenu=longestSubMenu*0.7+1;
        }
        if (longestSubMenu>15)
        {
            longestSubMenu=longestSubMenu*0.7+2;
        }
        else
        {
            longestSubMenu=longestSubMenu*0.9+2;
        }
            
        for (Item item:items)
        {
            String href=item.href;
            ArrayList<Item> subItems=item.subItems;
            if ((subItems==null)&&(href==null))
            {
                li li=ul.returnAddInner(new li());
                //Seperator
                li.addClass("menu-separator-item");
                li.returnAddInner(new hr());
            }
            else
            {
                li li=ul.returnAddInner(new li());
                if (item.enabled)
                {
                    a a=li.returnAddInner(new a());
                    if (href==null)
                    {
                        a.href("#");
                    }
                    else
                    {
                        a.href(href);
                    }
                    if ((subItems!=null)&&(level>0))
                    {
                        a.addInner(new span().addClass("menu-expand").addInner(new InlineHtml("&#x27a4;")));
                        a.addInner(item.name);
                    //    a.addInner(new span().class_("menu-expand").addInner("&#9658;"));
                        ;            
                    }
                    else
                    {
                        a.addInner(item.name);
                    }
                }
                else
                {
                    li.returnAddInner(new label().addClass("menu-item-disabled").addInner(item.name));
                }
                if (subItems!=null)
                {
                    if (level>0)
                    {
                        li.style("width:"+longestSubMenu+"em;");
                    }
                    ul subMenu=li.returnAddInner(new ul());
                    subMenu.addClass("menu-level-"+(level+1));
                    write(subMenu,level+1,subItems);
                }
            }
        }
    }
}
