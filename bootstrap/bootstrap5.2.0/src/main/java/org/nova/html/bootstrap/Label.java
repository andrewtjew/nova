package org.nova.html.bootstrap;

import org.nova.html.elements.TagElement;

public class Label extends StyleComponent<Label>
{
    public Label()
    {
        super("label",null);
    }
    public Label(String label)
    {
        super("label",null);
        addInner(label);
    }
    public Label for_(String element_id)
    {
        return attr("for",element_id);
    }
    public Label for_(TagElement<?> element)
    {
        return attr("for",element.id());
    }
    public Label form_label()
    {
        addClass("form-label");
        return this;
    }
    public Label visually_hidden()
    {
        addClass("form_label");
        return this;
    }
}
