package org.nova.html.bootstrap.ext;

import org.nova.html.bootstrap.Button;

public class DismissModalButton extends Button 
{
	public DismissModalButton() 
	{
        addInner(new Icon("x-lg"));
        dismissModal();
	}
	
}
