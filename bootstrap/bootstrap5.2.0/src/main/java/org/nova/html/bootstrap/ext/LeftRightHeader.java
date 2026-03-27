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


public class LeftRightHeader extends Item
{
    final private ArrayList<Element> left;
    final private ArrayList<Element> right;
    final private int leftSpacing;
    final private int rightSpacing;
    public LeftRightHeader(int leftSpacing,int rightSpacing)
    {
        w(100);
        this.leftSpacing=leftSpacing;
        this.rightSpacing=rightSpacing;
        d(Display.flex).flex(Flex.wrap).py(1).justify_content(Justify.between);
        this.left=new ArrayList<Element>();
        this.right=new ArrayList<Element>();
    }
    public LeftRightHeader()
    {
        this(1,1);
    }

    public LeftRightHeader addToLeft(Element element)
    {
        this.left.add(element);
        return this;
    }
    public LeftRightHeader addToRight(Element element)
    {
        this.right.add(element);
        return this;
    }
    public LeftRightHeader clearLeft()
    {
        this.left.clear();
        return this;
    }
    public LeftRightHeader clearRight()
    {
        this.right.clear();
        return this;
    }
    public <RETURN extends Element> RETURN returnAddToLeft(RETURN element)
    {
        this.left.add(element);
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
        Item right=returnAddInner(new Item()).d(Display.flex).flex(Flex.wrap).align_items(AlignItems.center);
        for (var element:this.right)
        {
            right.addInner(element);
            if (this.rightSpacing>0)
            {
                right.addInner(new Spacer(this.rightSpacing));
            }
        }
        super.compose(composer);
        
    }
    
}
