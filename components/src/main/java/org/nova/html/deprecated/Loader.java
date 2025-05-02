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
package org.nova.html.deprecated;

import org.nova.html.ext.Head;
import org.nova.html.properties.Color;
import org.nova.html.properties.Size;
import org.nova.html.properties.Unit;
import org.nova.html.tags.div;
import org.nova.html.tags.style;

public class Loader extends div
{
    public Loader(Head head,Size size,Color topColor,Color bottomColor)
    {
        
        String key=Loader.class.getCanonicalName();
        String style=".loader {  border: 16px solid "+bottomColor.toString()+";  border-radius: 50%;  border-top: 16px solid "+topColor.toString()+";  width: "+size.toString()+";  height: "+size.toString()+";  -webkit-animation: spin 2s linear infinite;animation: spin 1s linear infinite;} @-webkit-keyframes spin {  0% { -webkit-transform: rotate(0deg); }  100% { -webkit-transform: rotate(360deg); }}@keyframes spin {  0% { transform: rotate(0deg); }  100% { transform: rotate(360deg); }}head.add(key, style);}}";
        head.add(key, new style().addInner(style));
        this.addClass("loader");
    }
    public Loader(Head head)
    {
        this(head,new Size(80,Unit.px),Color.rgb(0, 0, 0),Color.rgb(220, 220, 220));
    }
}
