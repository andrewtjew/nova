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

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;

import org.nova.configuration.Configuration;
import org.nova.configuration.ConfigurationItem;
import org.nova.configuration.ConfigurationSource;
import org.nova.operations.OperatorVariableStore;
import org.nova.operations.VariableInstance;
import org.nova.tracing.Trace;
import org.nova.utils.FileUtils;
import org.nova.utils.TypeUtils;
import org.nova.utils.Utils;

public class FileOperatorVariableStore extends OperatorVariableStore
{
    final private String fileName;
    final private HashMap<String,String> variables;
    public FileOperatorVariableStore(String fileName) throws Exception
    {
        this.variables=new HashMap<>();
        if (fileName!=null)
        {
            fileName=FileUtils.toNativePath(fileName);
            String text=FileUtils.readTextFile(fileName);
            var lines=Utils.splitLn(text);
            for (String line:lines)
            {
                int index=line.indexOf('=');
                if (index>0)
                {
                    String name=line.substring(0,index);
                    String value=line.substring(index+1);
                    this.variables.put(name, value);
                }
            }
        }
        this.fileName=fileName;
    }

    public void save(Trace parent,String category,VariableInstance instance, String value) throws Throwable
    {
        if (fileName!=null)
        {
            Files.move(Paths.get(fileName), Paths.get(fileName+".previous"), StandardCopyOption.REPLACE_EXISTING);
            synchronized(this)
            {
                String name=category+"."+instance.getName();
                variables.put(name, value);
                StringBuilder sb=new StringBuilder();
                for (var entry:this.variables.entrySet())
                {
                    sb.append(entry.getKey()+"="+entry.getValue()+"\r\n");
                }
                FileUtils.writeTextFile(this.fileName, sb.toString());
            }
        }
    }
    @Override
    public OperatorVariableStoreValue load(Trace parent, String category,VariableInstance instance)
    {
        String name=category+"."+instance.getName();
        synchronized(this)
        {
            if (this.variables.containsKey(name)==false)
            {
                return null;
            }
            return new OperatorVariableStoreValue(this.variables.get(name));
        }
    }
}
