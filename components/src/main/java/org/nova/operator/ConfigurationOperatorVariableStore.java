/*******************************************************************************
 * Copyright (C) 2016-2019 Kat Fung Tjew
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

import java.util.ArrayList;

import org.nova.configuration.Configuration;
import org.nova.configuration.ConfigurationItem;
import org.nova.configuration.ConfigurationSource;
import org.nova.operations.OperatorVariableStore;
import org.nova.operations.VariableInstance;
import org.nova.tracing.Trace;
import org.nova.utils.FileUtils;
import org.nova.utils.TypeUtils;
import org.nova.utils.Utils;

public class ConfigurationOperatorVariableStore extends OperatorVariableStore
{
    final private String fileName;

    final ArrayList<String> lines;
    final Configuration configuration;
    public ConfigurationOperatorVariableStore(String fileName,Configuration configuration) throws Exception
    {
        this.lines=new ArrayList<>();
        if (fileName!=null)
        {
            String text=FileUtils.readTextFile(fileName);
            var lines=Utils.splitLn(text);
            for (String line:lines)
            {
                this.lines.add(line);
            }
        }
        this.configuration=configuration;
        if (fileName!=null)
        {
            fileName=FileUtils.toNativePath(fileName);
        }
        this.fileName=fileName;
    }

    public void save(Trace parent,String category,VariableInstance instance, String value) throws Throwable
    {
        if (fileName!=null)
        {
            boolean found=false;
            String name=category+"."+instance.getName();
            var item=this.configuration.getConfigurationItem(name);
            if (item!=null)
            {
                var source=item.getSource();
                if ((source==ConfigurationSource.FILE)||(source==ConfigurationSource.OPERATOR))
                {
                    String context=item.getSourceContext();
                    if (context!=null)
                    {
                        int start=context.indexOf('(');
                        if (start>0)
                        {
                            int end=context.indexOf(')',start);
                            if (end>start)
                            {
                                String sourceFile=FileUtils.toNativePath(context.substring(0,start));
                                if (sourceFile.contains(this.fileName)||(this.fileName.contains(sourceFile)))
                                {
                                    String lineString=context.substring(start+1,end);
                                    Integer lineIndex=TypeUtils.tryParseInt(lineString);
                                    if (lineIndex!=null)
                                    {
                                        lineIndex--;
                                        String line=this.lines.get(lineIndex);
                                        {
                                            String newLine=name+"="+value;
                                            this.lines.set(lineIndex, newLine);
                                            var newItem=new ConfigurationItem(name, value, ConfigurationSource.OPERATOR, item.getSourceContext(), item.getDescription());
                                            this.configuration.add(newItem);
                                            found=true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            if (found==false)
            {
                if (value!=null)
                {
                    var newLine=name+"="+value;
                    this.lines.add(newLine);
                    var newItem=new ConfigurationItem(name, value, ConfigurationSource.OPERATOR, fileName+"("+this.lines.size()+")",instance.getOperatorVariable().description());
                    this.configuration.add(newItem);
                }
            }
            String text=Utils.combine(this.lines, "\r\n");
            FileUtils.writeTextFile(this.fileName, text);
        }
    }
    @Override
    public String load(Trace parent, String category,VariableInstance instance)
    {
        if (fileName!=null)
        {
            String name=category+"."+instance.getName();
            var item=this.configuration.getConfigurationItem(name);
            if (item!=null)
            {
                return item.getValue();
            }
        }
        return null;
    }
}
