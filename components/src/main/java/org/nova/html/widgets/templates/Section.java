package org.nova.html.widgets.templates;

import java.io.OutputStream;
import java.util.Map;

abstract class Section
{
	public abstract void write(OutputStream outputStream,ElementMap map) throws Throwable;
}