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
package org.nova.http.server;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class ValueQ 
{
	static class ValueQComparator implements Comparator<ValueQ>
	{
		@Override
		public int compare(ValueQ arg0, ValueQ arg1)
		{
			return arg0.q<=arg1.q?1:-1;
		}
	}
	
	final String value;
	final double q;
	ValueQ(String value,double q)
	{
		this.value=value;
		this.q=q;
	}
	final static ValueQ.ValueQComparator COMPARATOR=new ValueQComparator();
	
	static List<ValueQ> sort(String accept)
	{
		String[] parts=org.nova.utils.Utils.split(accept.trim(), ',');
		ArrayList<ValueQ> list=new ArrayList<>();
		for (String item:parts)
		{
			double q=1;
			String[] mediaTypeParameter=org.nova.utils.Utils.split(item.trim(), ';');
			if (mediaTypeParameter.length>1)
			{
				String[] qvalue=org.nova.utils.Utils.split(mediaTypeParameter[1].trim(), '=');
				if ((qvalue.length==2)&&("q".equals(qvalue[0])))
				{
					try
					{
						q=Double.parseDouble(qvalue[1]);
					}
					catch (NumberFormatException ex)
					{ 
						//Be tolerant...
					}
				}
                list.add(new ValueQ(mediaTypeParameter[0],q));
			}
			else
			{
				list.add(new ValueQ(mediaTypeParameter[0],q));
			}
		}
		list.sort(COMPARATOR);
		return list;
	}
	
}
