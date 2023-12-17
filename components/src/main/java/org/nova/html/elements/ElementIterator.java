package org.nova.html.elements;

public class ElementIterator
{
    public static interface Runnable
    {
        public void run(Element element) throws Throwable;
        
    }
    static public void iterate(Element element,Runnable runnable) throws Throwable
    {
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
