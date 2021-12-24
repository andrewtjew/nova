package org.nova.html.bootstrap.help;

import java.util.ArrayList;

import org.nova.core.NameValue;
import org.nova.html.elements.Element;
import org.nova.html.elements.TagElement;

public class TopicGuide
{
    private ArrayList<NameValue<String>> items;
    public TopicGuide()
    {
        this.items=new ArrayList<NameValue<String>>();
    }
    public void bind(HelpElement help,TagElement<?> target)
    {
        this.items.add(new NameValue<String>(help.id(),target.id()));
    }
}
