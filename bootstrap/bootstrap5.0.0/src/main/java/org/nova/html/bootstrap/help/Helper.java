package org.nova.html.bootstrap.help;

import org.nova.html.bootstrap.Button;
import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Edge;
import org.nova.html.bootstrap.classes.Flex;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.elements.Composer;
import org.nova.html.tags.div;

public class Helper extends HelperElement
{
//    private div topic;
//    
//    private div bottomOverlay;
//    private div rightOverlay;
//    private div leftOverlay;
//    private div topOverlay;

    private Button back;
    private Button next;
    private Button close;
    
    public Helper()
    {
        this.close=new Button("Close");
        this.close.color(StyleColor.muted);
        this.close.id("nova-help-navigation-close");
        
        this.back=new Button("Back");
        this.back.color(StyleColor.secondary).ms(1);
        this.back.id("nova-help-navigation-back");
        
        this.next=new Button("Next");
        this.next.color(StyleColor.primary).ms(1);
        this.next.id("nova-help-navigation-next");
        

        Item helpBar=returnAddInner(new Item());
        helpBar.mt(1).pt(1).border(Edge.top).d(Display.flex).flex(Flex.row_reverse);      
        helpBar.addInner(this.next);
        helpBar.addInner(this.back);
        helpBar.addInner(this.close);
    }
    
}
