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
package org.nova.html.ext;

import java.lang.reflect.Field;

import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.properties.Style;
import org.nova.html.tags.option;
import org.nova.html.tags.select;

public class SelectEnums<E extends Enum<E>> extends Element
{
    private final select select;
    public SelectEnums(String name,Class<E> enumType)
    {
        this.select=new select();
        this.select.name(name);
        for (Field field:enumType.getDeclaredFields())
        {
            if (field.isEnumConstant())
            {
                this.select.addInner(new option().addInner(field.getName()).value(field.getName()));
            }
        }
    }
    
    public SelectEnums<E> style(Style value)
    {
        this.select.style(value);
        return this;
    }
    public SelectEnums<E> style(String value)
    {
        this.select.style(value);
        return this;
    }
    public SelectEnums<E> size(int size)
    {
        this.select.size(size);
        return this;
    }
    
    public SelectEnums(String name,Class<E> enumType,Enum<E> selected)
    {
        this.select=new select();
        this.select.name(name);
        for (Field field:enumType.getDeclaredFields())
        {
            if (field.isEnumConstant())
            {
                boolean select=selected.toString().equals(field.getName());
                this.select.addInner(new option().addInner(field.getName()).value(field.getName()).selected(select));
            }
        }
    }
    @Override
    public void compose(Composer composer) throws Throwable
    {
        composer.compose(this.select);
        
    }
    
}
