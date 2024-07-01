package org.nova.html.bootstrap.help;

import org.nova.html.bootstrap.CardBody;
import org.nova.html.bootstrap.CardHeader;
import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.bootstrap.classes.FontWeight;
import org.nova.html.bootstrap.classes.StyleColor;

public class Topic extends StyleComponent<Topic> 
{
    private CardBody body;
    private CardHeader header;

    public Topic(String heading,String message)
    {
        super("div",null);
        style("max-width:40em;");
        this.header=new CardHeader();
        this.header.bg(StyleColor.warning);
        this.header.px(1).fw(FontWeight.bold).mb(1);
        this.body=new CardBody();
        addInner(this.header);
        addInner(this.body);
        this.header.addInner(heading);
        this.body.addInner(message);
    }
    public Topic(String heading)
    {
        this(heading,null);
    }
    public CardHeader header()
    {
        return this.header;
    }
    public CardBody body()
    {
        return this.body;
    }
}
