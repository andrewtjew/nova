package org.nova.html.templating;

public interface TemplateGenerator
{
    public Template generate(String locale) throws Throwable;
}
