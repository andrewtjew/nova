package org.nova.html.NumberFlow;

import org.nova.html.elements.TagElement;
import org.nova.html.ext.Content;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.tags.script;
import org.nova.json.ObjectMapper;

public class number_flow extends TagElement<number_flow>
{
    public static final String SRC="https://cdn.jsdelivr.net/npm/number-flow@0.6.0/+esm";

    public static String js_set(String id,String value,Properties properties) throws Throwable
    {
        if (properties==null)
        {
            return HtmlUtils.js_call("barvian.number_flow.set", id, value);
        }
        else
        {
            return HtmlUtils.js_call("barvian.number_flow.set", id, value, ObjectMapper.writeObjectToString(properties));
        }
    }    
    public static String js_set(String id,String value) throws Throwable
    {
        return js_set(id,value,null);
    }    
    
    public number_flow()
    {
        super("number-flow");
    }
    
    public static class ScriptContent extends Content
    {
        public ScriptContent(String rootPath)
        {
            addInner(new script().type("module").src(SRC));
            addInner(new script().src(rootPath+"/barvian/number-flow.js"));
        }
    }
    
    public static enum Style
    {
        decimal("decimal"),
        percent("percent"),
        currency("currency"),
        unit("unit"),
        ;
        
        final private String value;

        private Style(String value) 
        {
            this.value = value;
        }

        @Override
        public String toString() 
        {
            return this.value;
        }        
    }
    public static class NumberFormatOptions
    {
        public Style style;
        public String currency;
    }
    
    public static class Properties
    {
        public NumberFormatOptions format;
        public String locales;
        public String numberPrefix;
        public Integer opacityTiming;
        public Integer transformTiming;
        public Integer spinTiming;
        public Integer trend;
        public Boolean animated;
        public Boolean respectMotionPreference;
    }
}