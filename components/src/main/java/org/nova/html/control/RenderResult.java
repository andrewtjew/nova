package org.nova.html.control;

import org.nova.html.elements.TagElement;
import org.nova.utils.TypeUtils;

public class RenderResult
{
    final TagElement<?> tagElement;
    final String script;
    public RenderResult(TagElement<?> tagElement,String script)
    {
        this.tagElement=tagElement;
        this.script=TypeUtils.isNullOrEmpty(script)?null:script;
    }
    public RenderResult(TagElement<?> tagElement)
    {
        this(tagElement,null);
    }
}