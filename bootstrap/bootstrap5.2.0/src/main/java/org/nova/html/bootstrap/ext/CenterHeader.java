package org.nova.html.bootstrap.ext;

import java.util.ArrayList;

import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.classes.AlignItems;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Flex;
import org.nova.html.bootstrap.classes.Justify;
import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;


public class CenterHeader extends Item
{
    final private ArrayList<Element> elements;
    final private int spacing;
    public CenterHeader(int spacing)
    {
        w(100);
        this.spacing=spacing;
        d(Display.flex).px(2).py(1).justify_content(Justify.center);
        this.elements=new ArrayList<Element>();
    }
    public CenterHeader()
    {
        this(1);
    }

    public CenterHeader add(Element element)
    {
        this.elements.add(element);
        return this;
    }
    public CenterHeader clear()
    {
        this.elements.clear();
        return this;
    }
    public <RETURN extends Element> RETURN returnAdd(RETURN element)
    {
        this.elements.add(element);
        return element;
    }

    
    
    @Override
    public void compose(Composer composer) throws Throwable
    {
        clearInners();

        Item item=returnAddInner(new Item()).d(Display.flex).flex(Flex.wrap).align_items(AlignItems.center);
        
        for (var element:this.elements)
        {
            item.addInner(element);
            if (this.spacing>0)
            {
                item.addInner(new Spacer(this.spacing));
            }
        }
        super.compose(composer);
        
    }
    
}
