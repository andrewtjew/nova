/*******************************************************************************
 * Copyright (C) 2017-2019 Kat Fung Tjew
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.nova.operator;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import org.nova.frameworks.OperatorPage;
import org.nova.frameworks.ServerApplication;
import org.nova.frameworks.ServerOperatorPages.WideTable;
import org.nova.html.elements.Element;
import org.nova.html.elements.HtmlElementWriter;
import org.nova.html.operator.MenuBar;
import org.nova.html.operator.Panel3;
import org.nova.html.operator.TableRow;
import org.nova.html.tags.p;
import org.nova.http.server.annotations.ContentWriters;
import org.nova.http.server.annotations.GET;
import org.nova.http.server.annotations.Path;
import org.nova.operations.OperatorVariable;
import org.nova.operations.VariableInstance;

@ContentWriters(HtmlElementWriter.class)
public class OperatorPages
{
	final private ServerApplication serverApplication;
	static public DateTimeFormatter DATETIME_FORMAT=DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"); 

	public static String formatDateTime(long dateTime)
	{
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTime), TimeZone.getDefault().toZoneId()).format(DATETIME_FORMAT);
	}
	
	public OperatorPages(ServerApplication serverApplication) throws Exception
	{
		this.serverApplication=serverApplication;
		MenuBar menuBar=serverApplication.getMenuBar();
		menuBar.add("/operator/variables/view","Variables","View");
		menuBar.add("/operator/variables/modify","Variables","Modify");
	}
	
	@GET
	@Path("/operator/variables/view")
	public Element list() throws Throwable
	{
        OperatorPage page=this.serverApplication.buildOperatorPage("View Operator Variables"); 

		List<String> list=Arrays.asList(serverApplication.getOperatorVariableManager().getCategories());
		Collections.sort(list);

		for (String category:list)
		{
			List<VariableInstance> instances=Arrays.asList(this.serverApplication.getOperatorVariableManager().getInstances(category));
//			Collections.sort(variables);

			Panel3 panel=page.content().returnAddInner(new Panel3(page.head(),category));
			page.content().addInner(new p());
			WideTable table=panel.content().returnAddInner(new WideTable(page.head()));
			
            table.setHeader("Name","Type","Default","Value","Modified","Description");

			for (VariableInstance instance:instances)
			{
                Field field=instance.getField();
                OperatorVariable variable=instance.getOperatorVariable();
                TableRow row=new TableRow();
			    row.add(instance.getName());
			    row.add(field.getType().getSimpleName());
			    row.add(variable.defaultValue());
			    row.add(instance.getValue());
			    row.add(instance.getModified()==0?"":formatDateTime(instance.getModified()));
			    row.add(variable.description());
			    table.addRow(row);

			}
		}
		
		return page;
	}

	
}
