package org.nova.html.bootstrap.ext;

import org.nova.html.bootstrap.Button;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.bootstrap.classes.Text;

public class CancelButton extends Button 
{
	public CancelButton() 
	{
		super();
        addInner(new Icon("x").me(1));
        addInner("Cancel");
        text(Text.nowrap);
        color(StyleColor.secondary);
	}
	
}
