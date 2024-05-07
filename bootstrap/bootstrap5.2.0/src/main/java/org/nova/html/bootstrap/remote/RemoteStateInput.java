package org.nova.html.bootstrap.remote;

import org.nova.html.bootstrap.Col;
import org.nova.html.bootstrap.InputComponent;
import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Label;
import org.nova.html.bootstrap.Span;
import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.bootstrap.classes.BreakPoint;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Justify;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.elements.Element;
import org.nova.html.elements.InputElement;
import org.nova.html.ext.Content;
import org.nova.html.remote.RemoteResponse;

public abstract class RemoteStateInput<INPUT extends InputComponent<?>> extends StyleComponent<RemoteStateInput<INPUT>>
{
    public RemoteStateInput(BreakPoint breakPoint,Integer columns)
    {
        super("div",breakPoint!=null||columns!=null?"col":null);
        if (breakPoint!=null&&columns!=null)
        {
            addClass("col",breakPoint,columns);
        }
        else if (columns!=null)
        {
            addClass("col",columns);
        }
        if (breakPoint!=null)
        {
            addClass("col",breakPoint);
        }
    }

    abstract public void clearValidationMessage();
}
