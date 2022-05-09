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
                            data[input.name]=element.checked
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


    export function post(formID:string,action:string,text:string,async:boolean,trace:boolean)
    {
        var data=toData(formID,text,trace);
        call("POST",action,data,async);
    }
    export function get(formID:string,action:string,text:string,async:boolean,trace:boolean)
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
                            action+=seperator+input.name+"="+encodeURIComponent(element.checked);
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
        call("GET",action,null,async);
    }

    export function call(type:string,pathAndQuery:string,data:object,async:boolean)
    {
        $.ajax(
            {url:pathAndQuery,
            type:type,
            async:async,
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
    export function postJSON(type:string,pathAndQuery:string,data:object,async:boolean)
    {
        fetch(pathAndQuery,
            {
                method:"POST",
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(data)
            }).then(response=>
            {
                if (response.ok)
                {
                    return response.json();
                }
                alert(response);
            })
            .then((instructions:Instruction[])=>
            {
                run(instructions);
            });

        

        // $.ajax(
        //     {url:pathAndQuery,
        //     type:type,
        //     headers: 
        //     {
        //         "Content-Type": 'application/json'
        //     },
        //     async:async,
        //     dataType:"json",
        //     cache: false,
        //     data:data!=null?JSON.stringify(data):null,
        //     success:function(instructions:Instruction[],status,xhr)
        //     {
        //         run(instructions);
        //     },
        //     error:function(xhr)
        //     {
        //         alert("Error: "+xhr.status+" "+xhr.statusText);
        //     }
        // }); 
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
