package org.nova.html.help;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.nova.core.NameValue;
import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.elements.NodeElement;
import org.nova.html.elements.TagElement;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.tags.div;
import org.nova.json.ObjectMapper;

public class Document extends div
{
    static class Link
    {
        final public String topicId;
        final public String targetId;
        public Link(String topicID,String targetId)
        {
            this.topicId=topicID;
            this.targetId=targetId;
        }
    }
    final private ArrayList<Link> links;
    
    public Document()
    {
        addClass("nova-help-document");
        this.links=new ArrayList<Link>();
    }
    public void add(TagElement<?> topic,TagElement<?> target)
    {
        this.links.add(new Link(topic.id(),target.id()));
        addInner(topic);
    }
    public void add(TagElement<?> topic)
    {
        this.links.add(new Link(topic.id(),null));
        addInner(topic);
    }
    
    public String js_activate(int startIndex,int targetMargin,int zIndex) throws Throwable
    {
        Link[] links=this.links.toArray(new Link[this.links.size()]);
        String code=HtmlUtils.js_call("nova.help.activate", startIndex,targetMargin,zIndex,links);
        return code;
    }
    public String js_activate() throws Throwable
    {
        return js_activate(0,2,10000);
    }
}
