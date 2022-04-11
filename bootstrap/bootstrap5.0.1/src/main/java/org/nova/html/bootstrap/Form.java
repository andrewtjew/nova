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
package org.nova.html.bootstrap;

import org.nova.html.elements.FormElement;
import org.nova.html.enums.method;
import org.nova.html.tags.script;

public class Form extends FormElement<Form>
{
    public Form()
    {
        super();
    }
    
    public Form(method method)
    {
        super(method);
    }
    public Form inline()
    {
        addClass("form-inline");
        return this;
    }

    public static script js_needsValidation()
    {
        script script=new script();
//        script.addInner("(function(){'use strict';window.addEventListener('load', function(){var forms = document.getElementsByClassName('needs-validation');var validation = Array.prototype.filter.call(forms, function(form){form.addEventListener('submit', function(event){if (form.checkValidity()===false){event.preventDefault();event.stopPropagation();}form.classList.add('was-validated');},false);});}, false);})();");
        script.addInner("ui.validateOnLoad();");
        return script;
    }
    
}
