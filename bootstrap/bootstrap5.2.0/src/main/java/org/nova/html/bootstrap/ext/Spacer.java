package org.nova.html.bootstrap.ext;

import org.nova.html.bootstrap.Item;

public class Spacer extends Item 
{
	public Spacer(int spacing)
	{
	    if (spacing>0)
	    {
	        ps(spacing);pt(spacing);
	    }
	}
	
	
	
}
