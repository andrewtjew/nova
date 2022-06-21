package org.nova.html.bootstrap.alerting;

public class Alert
{
    final private Priority priority;
    final private DisplayMethod displayMethod;
    final private String title;
    final private String message;
    
    public Alert(Priority priority,DisplayMethod displayMethod,String title,String message)
    {
        this.priority=priority;
        this.displayMethod=displayMethod;
        this.title=title;
        this.message=message;
    }

    public Priority getPriority()
    {
        return priority;
    }

    public DisplayMethod getDeliveryMethod()
    {
        return displayMethod;
    }

    public String getTitle()
    {
        return title;
    }

    public String getMessage()
    {
        return message;
    }
    
    
}
