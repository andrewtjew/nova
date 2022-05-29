package org.nova.html.bootstrap.ext;

import org.nova.html.bootstrap.InputComponent;
import org.nova.html.bootstrap.InputEmail;
import org.nova.html.bootstrap.InputNumber;
import org.nova.html.bootstrap.InputPassword;
import org.nova.html.bootstrap.InputText;
import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Label;
import org.nova.html.bootstrap.Select;
import org.nova.html.elements.Element;
import org.nova.html.elements.InputElement;

public class Floating extends Item 
{
	public Floating(String label,InputComponent<?> input)
	{
		this.form_floating();
		returnAddInner(input).attr("placeholder","placeholder").form_control();
		returnAddInner(new Label()).for_(input).addInner(label);
	}
}
