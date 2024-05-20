package org.nova.html.remoting;

import org.nova.html.elements.InputElement;

@Deprecated
public class Input
{
    final public String name;
    final public InputType inputType;
    

    public Input(InputElement<?> inputElement)
    {
        this.name=inputElement.name();
        switch (inputElement.getInputType())
        {
            case button:
            case color:
            case date:
            case datetime_local:
            case month:
            case number:
            case password:
            case search:
            case email:
            case tel:
            case text:
            case textarea:
            case time:
            case url:
            case hidden:
            case submit:
            case range:
            case week:
            inputType=InputType.value;
            break;
            
            case checkbox:
            inputType=InputType.checked;
            break;

            case select:
            inputType=InputType.select;
            break;

            case radio:
            inputType=InputType.radio;
             break;
             
            case image:
            case reset:
            case file:
            default:
                inputType=null;
                break;
        }
    }
}
