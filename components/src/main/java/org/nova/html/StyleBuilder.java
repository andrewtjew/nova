package org.nova.html;

import org.nova.html.attributes.Style;

public class StyleBuilder 
{
	final private StringBuilder sb;
	public StyleBuilder()
	{
		this.sb=new StringBuilder();
	}
	
	public StyleBuilder begin(String name)
	{
		this.sb.append(name);
		this.sb.append('{');
		return this;
	}
	public StyleBuilder end()
	{
		this.sb.append('}');
		return this;
	}
	public StyleBuilder add(Style style)
	{
		this.sb.append(style.toString());
		return this;
	}
	public StyleBuilder add(String text)
	{
		this.sb.append(text);
		return this;
	}

	@Override
	public String toString()
	{
		return this.sb.toString();
	}

}
