package org.nova.service.deviceSession;

import java.util.HashMap;
import org.nova.html.elements.FormElement;
import org.nova.html.elements.TagElement;
import org.nova.html.ext.InputHidden;
import org.nova.html.operator.NameValueList;
import org.nova.html.remote.RemoteStateBinding;
import org.nova.http.client.PathAndQuery;
import org.nova.http.server.Context;
import org.nova.utils.Utils;


public abstract class PageStateSession<ROLE extends Enum<?>> extends RoleSession<ROLE> implements RemoteStateBinding,AdditionalSessionInformation
{
    final static boolean DEBUG=false;
    final static boolean DEBUG_PAGESTATE=false;
    final static String LOG_DEBUG_CATEGORY=PageStateSession.class.getSimpleName();

    public HashMap<String,Object> states;
    public HashMap<String,Object> newStates;
    
    public PageStateSession(Class<ROLE> roleType) throws Throwable
    {
        super(roleType);
    }
    
    
    public <T> T getState(String key)
    {
        Object state=null;
        if (this.states!=null)
        {
            state=this.states.get(key);
        }
        if (state!=null)
        {
            //As long as the handler refers to the state, we keep it alive.
            if (this.newStates==null)
            {
                this.newStates=new HashMap<String, Object>();
            }
            this.newStates.put(key, state);
        }
        else
        {
            state=this.newStates.get(key);
        }
        return (T)state;
    }
    @Override
    public void setState(String key,Object state) throws Throwable
    {
        if (state!=null)
        {
            if (this.newStates==null)
            {
                this.newStates=new HashMap<>();
            }
            this.newStates.put(key, state);
        }
    }
    public <T> T removeState(String key)
    {
        Object removed=null;
        if (this.newStates!=null)
        {
            removed=this.newStates.remove(key);
        }
        if (this.states!=null)
        {
            if (removed!=null)
            {
                this.states.remove(key);
            }
            else
            {
                removed=this.states.remove(key);
            }
        }
        return (T)removed;
    }
    public void expireStates()
    {
        if (this.newStates!=null)
        {
            this.states=this.newStates;
            this.newStates=null;
        }
    }

    public void setState(long key,Object state) throws Throwable
    {
        setState(Long.toString(key),state);
    }
    
    
    @Override
    final public String getQueryBinding(TagElement<?> element)
    {
        return STATE_KEY+"="+element.id();
    }
    
    
    @Override
    final public <T> T getState(Context context) throws Throwable
    {
        String stateKey=context.getHttpServletRequest().getParameter(STATE_KEY);
        return (T)getState(stateKey);
    }
    final static public String STATE_KEY="@state";

    @Override
    final public <PATHANDQUERY extends PathAndQuery> PATHANDQUERY bind(String id,Object state,PATHANDQUERY pathAndQuery) throws Throwable
    {
        setState(id,state);
        pathAndQuery.addQuery(STATE_KEY, id);
        return pathAndQuery;
    }
  
    @Override
    final public void bind(FormElement<?> element) throws Throwable
    {
        setState(element.id(),element);
        element.addInner(new InputHidden(STATE_KEY,element.id()));
    }

    
//    @Override
//    public AbnormalResult beginRequest(Trace parent,Context context) throws Throwable
//    {
//       AbnormalResult result=super.beginRequest(parent, context);
//       if (result!=null)
//       {
//           return result;
//       }
//       return null;
//    }
//    @Override
//    public void endRequest(Trace parent,Context context,Response<?> response) throws Throwable
//    {
//        super.endRequest(parent, context, response);
//    }
    
    @Override
    public void fill(NameValueList list)
    {
        var roles=Utils.combine(this.getRoles(), ",");
        list.add("Roles", roles);
        list.add("States", "--------------------");
        if (this.states!=null)
        {
            for (var entry:this.states.entrySet())
            {
                var value=entry.getValue();
                if (value!=null)
                {
//                    list.add(entry.getKey()+":"+value.getClass().getName(), entry.getValue());
                    list.add(entry.getKey(), value.getClass().getName());
                }
                else
                {
                    list.add(entry.getKey()+":null", "");
                }
            }
        }
        if (this.newStates!=null)
        {
            for (var entry:this.newStates.entrySet())
            {
                if ((this.states==null)||(this.states.containsKey(entry.getKey())==false))
                {
                    var value=entry.getValue();
                    if (value!=null)
                    {
//                        list.add(entry.getKey(), value.getClass().getName());
                      list.add(entry.getKey()+":"+value.getClass().getName(), entry.getValue());
                    }
                    else
                    {
                        list.add(entry.getKey()+":null", "");
                    }
                }
            }
        }
    }
}
