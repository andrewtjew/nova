namespace nova.remote
{
    class Input 
    {
        name:string;
        inputType:string;
    }

    class Instruction
    {
        trace:boolean;
        command:string;
        parameters:string[];
    }

    function getInputElement(parent:Document|Element,input:Input):HTMLInputElement
    {
        var element=parent.querySelector("[name='"+input.name+"']") as HTMLInputElement;
        if (element==null)
        {
            console.log("Element not in scope: name="+input.name);
        }
        return element;
    }

    function getSelectElement(parent:Document|Element,input:Input):HTMLSelectElement
    {
        var element=parent.querySelector("[name='"+input.name+"']") as HTMLSelectElement;
        if (element==null)
        {
            console.log("Element not in scope: name="+input.name);
        }
        return element;
    }

    function toData(formID:string,text:string,trace:boolean):object
    {
        if (text==null)
        {
            return;
        }
        var parent=formID==null?document:document.getElementById(formID);
        if (parent==null)
        {
            throw new Error("No element with id="+formID);
        }

        var data=new Object();
        var inputs=JSON.parse(text) as Input[];
        for (var i=0,len=inputs.length;i<len;i++)
        {
            var input=inputs[i];
            if (trace==true)
            {
                console.log("remote:name="+input.name+",type="+input.inputType);
            }
            switch (input.inputType)
            {
                case "value":
                    {
                        var element=getInputElement(parent,input);
                        if (element!=null)
                        {
                            data[input.name]=element.value;
                        }
                    }
                    break;
                
                case "checked":
                    {
                        var element=getInputElement(parent,input);
                        if (element!=null)
                        {
                            if (element.checked)
                            {
                                data[input.name]="on";
                            }
                        }
                    }
                    break;
                
                case "select":
                {
                    var select=getSelectElement(parent,input);
                    if (select!=null)
                    {
                        data[input.name]=select.options[select.selectedIndex].value;
                    }
                }
                break;

                case "radio":
                var radio=getInputElement(parent,input);
                if ((radio!=null)&&(radio.checked))
                {
                    data[input.name]=radio.value;
                }
                break;

            }
        }
        return data;
    }


    export function post(formID:string,action:string,text:string,trace:boolean)
    {
        var data=toData(formID,text,trace);
        call("POST",action,data);
    }
    export function get(formID:string,action:string,text:string,trace:boolean)
    {
        var parent=formID==null?document:document.getElementById(formID)
        var inputs=JSON.parse(text) as Input[];
        var seperator="?";
        for (var i=0,len=inputs.length;i<len;i++)
        {
            var input=inputs[i];
            if (trace==true)
            {
                console.log("input:name="+input.name+",type="+input.inputType);
            }
            switch (input.inputType)
            {
                case "value":
                    {
                        var element=getInputElement(parent,input);
                        if (element!=null)
                        {
                            action+=seperator+input.name+"="+encodeURIComponent(element.value);
                        }
                    }
                break;
                
                case "checked":
                    {
                        var element=getInputElement(parent,input);
                        if (element!=null)
                        {
                            if (element.checked)
                            {
                                action+=seperator+input.name+"=on";
                            }
                        }
                    }
                break;
                
                case "select":
                    {
                        var select=getSelectElement(parent,input);
                        if (select!=null)
                        {
                            action+=seperator+input.name+"="+encodeURIComponent(select.options[select.selectedIndex].value);
                        }
                    }
                break;

                case "radio":
                    {
                        var element=getInputElement(parent,input);
                        if (element!=null)
                        {
                            action+=seperator+input.name+"="+encodeURIComponent(element.value);
                        }
                    }
                break;

            }
            seperator="&";
        }
        call("GET",action,null);
    }

    export function call(type:string,pathAndQuery:string,data:object)
    {
        $.ajax(
            {url:pathAndQuery,
            type:type,
            async:true,
            dataType:"json",
            cache: false,
            data:data,
            success:function(instructions:Instruction[],status,xhr)
            {
                run(instructions);
            },
            error:function(xhr)
            {
                alert("Error: "+xhr.status+" "+xhr.statusText);
            }
        }); 
    }
    
    export async function getStatic(pathAndQuery:string,error:Function=null)
    {
        return await fetch(pathAndQuery,
            {
                method:"GET",
                headers: {'Content-Type': 'application/json'},
            }).then(response=>
            {
                if (response.ok)
                {
                    return response.json();
                }
                else if (error!=null)
                {
                    error();
                }
            })
            .then((instructions:Instruction[])=>
            {
                run(instructions);
            });

    }

    export async function postStatic(pathAndQuery:string,data:string=null,error:Function=null)
    {
        return await fetch(pathAndQuery,
            {
                method:"POST",
                headers: {'Content-Type': 'application/json'},
                body: data
            }).then(response=>
            {
                if (response.ok)
                {
                    return response.json();
                }
                else if (error!=null)
                {
                    error();
                }
            })
            .then((instructions:Instruction[])=>
            {
                run(instructions);
            });

    }

    export function postForNative(pathAndQuery:string,data:string):string
    {
        var returnResult:string;
        var returnSuccess=false;
        $.ajax(
            {url:pathAndQuery,
            type:"POST",
            contentType:"application/json",
            async:false,
            dataType:"json",
            cache: false,
            data:data,
            success:function(result:string,status,xhr)
            {
                returnResult=result;
                returnSuccess=true;
            },
            error:function(xhr)
            {
            }
        }); 
        return JSON.stringify({success:returnSuccess,result:returnResult});
    }

    export async function postFile(event:Event)
    {
        event.preventDefault();
        let form=event.currentTarget as HTMLFormElement;
        return await fetch(form.action,
            {
                method: form.method,
                body: new FormData(form),
            }
            ).then(response=>
            {
                if (response.ok)
                {
                    return response.json();
                }
            })
            .then((instructions:Instruction[])=>
            {
                run(instructions);
            });

    }

    function run(instructions:Instruction[])
    {
        if (instructions!=null)
        {
            for (var i=0,len=instructions.length;i<len;i++)
            {
                var instruction=instructions[i];
                var parameters=instruction.parameters;
                if (instruction.trace)
                {
                    console.log("command:"+instruction.command);
                    console.log("parameters:"+parameters);
                }
                try
                {
                    switch (instruction.command)
                    {
                        case "value":
                            (document.getElementById(parameters[0]) as HTMLInputElement).value=parameters[1];
                            break;
                            
                            case "innerHTML":
                                document.getElementById(parameters[0]).innerHTML=parameters[1];
                                break;
                                
                            case "outerHTML":
                            document.getElementById(parameters[0]).outerHTML=parameters[1];
                            break;

                        case "innerText":
                        document.getElementById(parameters[0]).innerText=parameters[1];
                        break;
                                
                        case "alert":
                        alert(parameters[0]);
                        break;
                                
                        case "log":
                        console.log(parameters[0]);
                        break;
                                
                        case "documentObject":
                        document[parameters[0]]=parameters[1];
                        break;
                                
                        case "script":
                            eval(parameters[0]);
                            break;

                    }
                }
                catch (ex)
                {
                    alert("remote exception:"+ex);
                }
            }
        }
    }


}
