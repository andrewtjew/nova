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
package org.nova.html.Chartjs;

import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.ext.Head;
import org.nova.html.tags.canvas;
import org.nova.html.tags.script;
import org.nova.json.ObjectMapper;

public class ScatterChart extends Element
{
    final private canvas canvas;
    final private String id;
    private ScatterType type;
    
    public ScatterChart(String id,int width,int height)
    {
        this.canvas=new canvas().id(id).width(width).height(height);
        this.id=id;
        this.type=new ScatterType();
    }
    
    public void setData(Data data)
    {
        this.type.data=data;
    }
    
    public String script() throws Throwable
    {
        String data=ObjectMapper.writeObjectToString(this.type);
        
        StringBuilder sb=new StringBuilder();
        String ctx=this.id+"_idx";
        sb.append("var "+ctx+"=document.getElementById('"+this.id+"');");
        sb.append("var "+this.id+"=new Chart("+ctx+",");
        sb.append(data);
        sb.append(");");
        return sb.toString();
    }
    
    @Override
    public void compose(Composer composer) throws Throwable
    {
        composer.compose(this.canvas);
        script script=new script();
        script.addInner(script());
        composer.compose(script);
    }
}
