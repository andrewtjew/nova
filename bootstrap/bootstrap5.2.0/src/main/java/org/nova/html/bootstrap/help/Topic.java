package org.nova.html.bootstrap.help;

import org.nova.html.bootstrap.CardBody;
import org.nova.html.bootstrap.CardHeader;
import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.bootstrap.classes.StyleColor;

public class Topic extends StyleComponent<Topic> 
{
    private CardBody body;
    private CardHeader header;

    public Topic(String heading)
    {
        super("div",null);
        bg(StyleColor.warning);
        this.header=new CardHeader();
        if (heading!=null)
        {
            this.header.addInner(heading);
        }
        this.body=new CardBody();
        addInner(this.header);
        addInner(this.body);
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
