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
package org.nova.html.elements;

public class InputElement<ELEMENT extends InputElement<ELEMENT>> extends GlobalEventTagElement<ELEMENT>
{
    private String name;
    final InputType inputType;
    
    protected InputElement(String tag,InputType inputType)
    {
        super(tag);
        this.inputType=inputType;
    }

    protected InputElement(InputType inputType)
    {
        this("input",inputType);
        attr("type",inputType);
    }


    public InputType getInputType()
    {
        return this.inputType;
    }
    
    public ELEMENT autofocus()
    {
        return attr("autofocus");
    }
    public ELEMENT autofocus(boolean autofocus)
    {
        if (autofocus)
        {
            return attr("autofocus");
        }
        return (ELEMENT)this;
    }
    public ELEMENT disabled()
    {
        return attr("disabled");
    }
    public ELEMENT disabled(boolean disabled)
    {
        if (disabled)
        {
            return attr("disabled");
        }
        else
        {
            this.getAttributes().remove("disabled");
        }
        return (ELEMENT)this;
    }
    public ELEMENT form(String form_id)
    {
        return attr("form",form_id);
    }
    public ELEMENT form(TagElement<?> element)
    {
        return attr("form",element.id());
    }
    public ELEMENT readonly()
    {
        return attr("readonly");
    }
    public ELEMENT readonly(boolean readonly)
    {
        if (readonly)
        {
            attr("readonly");
        }
        return (ELEMENT)this;
    }
    public ELEMENT name(String name)
    {
        this.name=name;
        attr("name",this.name);
        return (ELEMENT)this;
    }
    public ELEMENT list(TagElement<?> element)
    {
        attr("list",element.id());
        return (ELEMENT)this;
    }
    public String name()
    {
        return this.name;
    }
    

    /*
    public input accept(String value) //file
    {
        return attr("accept",value);
    }
    public input alt(String text) //image
    {
        return attr("alt",text);
    }
    public input autocomplete(autocomplete autocomplete) //text, search, url, tel, email, password, datepickers, range, and color.
    {
        return attr("autocomplete",autocomplete);
    }
    public input autocomplete(boolean autocomplete)
    {
        if (autocomplete)
        {
            attr("autocomplete");
        }
        return this;
    }
    public input checked() //checkbox or radio
    {
        return attr("checked");
    }
    public input checked(boolean checked)
    {
        if (checked)
        {
            attr("checked");
        }
        return this;
    }
    
    
    public input dirname(String value) //text
    {
        return attr("dirname",value);
    }
    public input formaction(String URL) //submit, image
    {
        return attr("formaction",URL);
    }
    public input formenctype(enctype enctype) //submit, image
    {
        return attr("formenctype",enctype.toString());
    }
    public input formmethod(String value) //submit, image
    {
        return attr("formmethod",value);
    }
    public input formnovalidate()  //submit
    {
        return attr("formnovalidate");
    }
    public input formnovalidate(boolean formnovalidate) //submit
    {
        if (formnovalidate)
        {
            attr("formnovalidate");
        }
        return this;
    }
    public input formtarget(target target) //submit,image
    {
        return attr("target",target.toString());
    }
    public input formtarget(String framename)  //submit,image
    {
        return attr("target",framename);
    }
    public input height(String pixels) //image
    {
        return attr("height",pixels);
    }
    public input max(String date) //number, range, date, datetime, datetime-local, month, time and week.
    {
        return attr("max",date);
    }
    public input max(long number)
    {
        return attr("max",number);
    }
    public input maxlength(long number) //text
    {
        return attr("maxlength",number);
    }
    public input min(String date) //number, range, date, datetime, datetime-local, month, time and week.
    {
        return attr("min",date);
    }
    public input min(long number)
    {
        return attr("min",number);
    }
    public input multiple() //file
    {
        return attr("multiple");
    }
    public input multiple(boolean multiple) //file, email
    {
        if (multiple)
        {
            attr("multiple");
        }
        return this;
    }
    public input pattern(String regex) text, date, search, url, tel, email, and password.
    {
        return attr("pattern",regex);
    }
    public ELEMENT placeholder(String text) //text, search, url, tel, email, and password.
    {
        return attr("placeholder",text);
    }
    
    public input size(int number) //text, search, tel, url, email, and password.
    {
        return attr("size",number);
    }
    public input src(String URL) //image
    {
        return attr("src",URL);
    }
    public input step(int number) //number, range, date, datetime, datetime-local, month, time and week.
    {
        return attr("step",number);
    }
    public input width(String pixels) //image
    {
        return attr("width",pixels);
    }
    public input required()  //text, search, url, tel, email, password, date pickers, number, checkbox, radio, and file.
    {
        return attr("required");
    }
    public input required(boolean required)
    {
        if (required)
        {
            attr("required");
        }
        return this;
    }
    public input value(String text) //button, reset, submit, text, password, hidden, checkbox, radio, image
    {
        return attr("value",text);
    }
    */
//-------------
    

    /*
    public input autocomplete(autocomplete autocomplete) //text, search, url, tel, email, password, datepickers, range, and color.
    {
        return attr("autocomplete",autocomplete);
    }
    public input autocomplete(boolean autocomplete)
    {
        if (autocomplete)
        {
            attr("autocomplete");
        }
        return this;
    }
    
    public input max(String date) //number, range, date, datetime, datetime-local, month, time and week.
    {
        return attr("max",date);
    }
    public input max(long number)
    {
        return attr("max",number);
    }
    public input min(String date) //number, range, date, datetime, datetime-local, month, time and week.
    {
        return attr("min",date);
    }
    public input min(long number)
    {
        return attr("min",number);
    }
    public input multiple() //file
    {
        return attr("multiple");
    }
    public input pattern(String regex) text, date, search, url, tel, email, and password.
    {
        return attr("pattern",regex);
    }
    public ELEMENT placeholder(String text) //text, search, url, tel, email, and password.
    {
        return attr("placeholder",text);
    }
    
    public input size(int number) //text, search, tel, url, email, and password.
    {
        return attr("size",number);
    }
    public input step(int number) //number, range, date, datetime, datetime-local, month, time and week.
    {
        return attr("step",number);
    }
    public input required()  //text, search, url, tel, email, password, date pickers, number, checkbox, radio, and file.
    {
        return attr("required");
    }
    public input required(boolean required)
    {
        if (required)
        {
            attr("required");
        }
        return this;
    }
    public input value(String text) //button, reset, submit, text, password, hidden, checkbox, radio, image
    {
        return attr("value",text);
    }
    */

    
    
}
