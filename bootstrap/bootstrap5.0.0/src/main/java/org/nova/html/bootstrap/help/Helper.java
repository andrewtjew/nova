package org.nova.html.bootstrap.help;

import org.nova.html.bootstrap.Button;
import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Edge;
import org.nova.html.bootstrap.classes.Flex;
import org.nova.html.bootstrap.classes.Justify;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.elements.Composer;
import org.nova.html.tags.div;

public class Helper extends HelperElement
{
    private Button back;
    private Button next;
    private Button close;
    
    public Helper(String closeText,String backText,String nextText)
    {
        this.close=new Button("Close").sm();
        this.close.color(StyleColor.muted);
        this.close.id("nova-help-navigation-close");
        
        this.back=new Button("Back").sm();
        this.back.color(StyleColor.secondary).ms(1);
        this.back.id("nova-help-navigation-back");
        
        this.next=new Button("Next").sm();
        this.next.color(StyleColor.primary).ms(1);
        this.next.id("nova-help-navigation-next");
        
        Item statusBar=returnAddInner(new Item()).d(Display.flex).justify_content(Justify.between);
        statusBar.mt(1).pt(1).border(Edge.top);      
        
        statusBar.returnAddInner(new Item()).id("nova-help-navigation-status");

        Item helpItem=statusBar.returnAddInner(new Item());
        helpItem.d(Display.flex).flex(Flex.row_reverse);      
        helpItem.addInner(this.next);
        helpItem.addInner(this.back);
        helpItem.addInner(this.close);
    }
    public Helper()
    {
        this("Close","Back","Next");
    }
}
