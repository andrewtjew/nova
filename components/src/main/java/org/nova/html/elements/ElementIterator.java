package org.nova.html.elements;

public class ElementIterator
{
    public static interface Runnable
    {
        public void run(Element element);
        
    }
    static public void iterate(Element element,Runnable runnable) throws Throwable
    {
        if (element==null)
        {
            return;
        }
        runnable.run(element);
        if (element instanceof NodeElement<?>)
        {
            for (Element child:((NodeElement<?>)element).getInners())
            {
                iterate(child,runnable);
            }
        }
    }
}
