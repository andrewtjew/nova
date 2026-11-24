package org.nova.html.remote;

import org.nova.html.elements.Composer;
import org.nova.html.enums.enctype;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.JsObject;
import org.nova.http.client.PathAndQuery;

//Use to implement complex, stateful and re-usable form. Allows sub class forms to have method handlers returning RemoteResponse results. Subclass form lifetime is page lifetime and it is ended if the next page does not use it and is extended when next page uses it by calling getPageState(). 
//Register using register(PATH,SubClassOfRemoteObjectForm.class), then new subclass in method handler.  
//All request handlers in the subclass of RemoteObjectBindingForm must have a @StatePram SubclassOfRemoteObjectBinding binding parameter.
public class RemoteObjectForm extends RemoteForm 
{
    RemoteObjectBinding binding;

    public RemoteObjectForm(String id,RemoteObjectBinding binding,String action) throws Throwable
    {
        super(id);
        this.binding=binding;
        binding.bind(this,action);
    }
    public RemoteObjectForm(String id,RemoteObjectBinding binding) throws Throwable
    {
        this(id,binding,null);
    }
    public RemoteObjectForm(RemoteObjectBinding binding) throws Throwable
    {
        this(null,binding);
    }
    public RemoteObjectForm(RemoteObjectBinding binding,String action) throws Throwable
    {
        this(null,binding,action);
    }
    public String js_postStatic(PathAndQuery pathAndQuery) throws Throwable
    {
        return Remote.js_postStatic(this.binding.bind(this,pathAndQuery).toString());
        
    }

    
    
    
    
}