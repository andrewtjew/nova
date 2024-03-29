package org.nova.html.templating;

import java.util.HashMap;
import java.util.Map;

public class TemplateCache
{
    
    final private HashMap<String,Template> templates;
    final private HashMap<String,TemplateGenerator> generators;

    public TemplateCache()
    {
        this.templates=new HashMap<String, Template>();
        this.generators=new HashMap<String, TemplateGenerator>();
    }
    public void register(String key,TemplateGenerator generator)
    {
        synchronized (this)
        {
            this.generators.put(key, generator);
        }
    }
    public Instance get(String key, String locale) throws Throwable
    {
        String k=locale+":"+key;
        synchronized (this)
        {
            Template template=this.templates.get(k);
            if (template==null)
            {
                TemplateGenerator generator=this.generators.get(key);
                if (generator==null)
                {
                    throw new Exception("Not found:"+key);
                }
                template=generator.generate(locale);
                this.templates.put(k, template);
            }
            return new Instance(template);
        }
        
    }

    public void clear()
    {
        synchronized (this)
        {
            this.templates.clear();
        }        
    }
    public Map<String,TemplateGenerator> getTemplateGenerators()
    {
        return this.generators;
    }
}
