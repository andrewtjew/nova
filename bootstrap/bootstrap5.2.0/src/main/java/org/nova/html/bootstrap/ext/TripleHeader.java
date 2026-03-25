package org.nova.html.bootstrap.ext;

import java.util.ArrayList;

import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.classes.AlignContent;
import org.nova.html.bootstrap.classes.AlignItems;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Edge;
import org.nova.html.bootstrap.classes.Flex;
import org.nova.html.bootstrap.classes.FontWeight;
import org.nova.html.bootstrap.classes.Justify;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.bootstrap.classes.Text;
import org.nova.html.bootstrap.remote.RemoteItem;
import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.ext.LiteralHtml;
import org.nova.html.tags.script;


public class TripleHeader extends Item
{
    final private ArrayList<Element> left;
    final private ArrayList<Element> center;
    final private ArrayList<Element> right;
    final private int leftSpacing;
    final private int centerSpacing;
    final private int rightSpacing;
    public TripleHeader(int leftSpacing,int centerSpacing,int rightSpacing)
    {
        w(100);
        this.leftSpacing=leftSpacing;
        this.centerSpacing=centerSpacing;
        this.rightSpacing=rightSpacing;
        d(Display.flex).flex(Flex.wrap).px(2).py(1).justify_content(Justify.between);
        this.left=new ArrayList<Element>();
        this.center=new ArrayList<Element>();
        this.right=new ArrayList<Element>();
    }
    public TripleHeader()
    {
        this(1,1,1);
    }

    public TripleHeader addToLeft(Element element)
    {
        this.left.add(element);
        return this;
    }
    public TripleHeader addToCenter(Element element)
    {
        this.center.add(element);
        return this;
    }
    public TripleHeader addToRight(Element element)
    {
        this.right.add(element);
        return this;
    }
    public TripleHeader clearLeft()
    {
        this.left.clear();
        return this;
    }
    public TripleHeader clearCenter()
    {
        this.center.clear();
        return this;
    }
    public TripleHeader clearRight()
    {
        this.right.clear();
        return this;
    }
    public <RETURN extends Element> RETURN returnAddToLeft(RETURN element)
    {
        this.left.add(element);
        return element;
    }
    public <RETURN extends Element> RETURN returnAddToCenter(RETURN element)
    {
        this.center.add(element);
        return element;
    }
    public <RETURN extends Element> RETURN returnAddToRight(RETURN element)
    {
        this.right.add(element);
        return element;
    }

    
    
    @Override
    public void compose(Composer composer) throws Throwable
    {
        clearInners();
        Item left=returnAddInner(new Item()).d(Display.flex).flex(Flex.wrap).align_items(AlignItems.center).me_auto();
        for (var element:this.left)
        {
            left.addInner(element);
            if (this.leftSpacing>0)
            {
                left.addInner(new Spacer(this.leftSpacing));
            }
        }
        Item center=returnAddInner(new Item()).d(Display.flex).flex(Flex.wrap).align_items(AlignItems.center);
        for (var element:this.center)
        {
            if (this.centerSpacing>0)
            {
                center.addInner(new Spacer(this.centerSpacing));
            }
            center.addInner(element);
        }
        Item right=returnAddInner(new Item()).d(Display.flex).flex(Flex.wrap).align_items(AlignItems.center);
        for (var element:this.right)
        {
            if (this.rightSpacing>0)
            {
                right.addInner(new Spacer(this.rightSpacing));
            }
            right.addInner(element);
        }
        super.compose(composer);
        
    }
    
}
