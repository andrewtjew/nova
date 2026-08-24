namespace nova.handle
{
    export function onTextAreaChange(handle:string)
    {
        var textareaElement=document.getElementById("textarea-"+handle) as HTMLInputElement;
        var saveButton=document.getElementById("button-save-"+handle) as HTMLButtonElement;
        var disabled=textareaElement.value.length==0;
        saveButton.disabled=disabled;

    }

    export function disableSaveButton(handle:string,undefined:boolean)
    {
        var textareaElement=document.getElementById("textarea-"+handle) as HTMLInputElement;
        if (undefined)
        {
            textareaElement.value="";
        }
        var saveButton=document.getElementById("button-save-"+handle) as HTMLButtonElement;
        var disabled=textareaElement.value.length==0;
        saveButton.disabled=true;
    }

  
}