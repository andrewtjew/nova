package org.nova.localization;

import org.nova.html.ext.LocalText;

public abstract class LocalTextResolver
{
    final static char DELIMITER ='~';
    
    protected abstract String translate(LocalText localText);
    
    public String resolve(LocalText localText)
    {
        String text=translate(localText);
        Object[] arguments=localText.getArguments();
        StringBuilder sb=new StringBuilder();
        for (int index=0;index<text.length();)
        {
            int start=text.indexOf(DELIMITER,index);
            if (start<index)
            {
                sb.append(text.substring(index));
                break;
            }
            sb.append(text.substring(index,start));
            int end=text.indexOf(DELIMITER,start+1);
            if (end<0)
            {
                break;
            }
            if (end==start+1)
            {
                sb.append(DELIMITER);
                index=end+1;
                continue;
            }
            int argumentIndex=-1;
            for (int i=start+1;i<end;i++)
            {
                int c=text.charAt(i);
                if ((c>='0')&&(c<='9'))
                {
                    if (argumentIndex==-1)
                    {
                        argumentIndex=0;
                    }
                    argumentIndex=argumentIndex*10+(c-'0');
                }
                else
                {
                    argumentIndex=-1;
                    break;
                }
            }
            if ((argumentIndex>=0)&&(argumentIndex<arguments.length))
            {
                sb.append(arguments[argumentIndex]);
            }
            index=end+1;
        }
        return sb.toString();
    }
}
