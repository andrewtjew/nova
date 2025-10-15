namespace nova.remote
{
    class Instruction
    {
        trace:boolean;
        command:string;
        parameters:string[];
    }
 
    class PathCommand
    {
        constructor(pathAndQuery:string,content:any)
        {
            this.pathAndQuery=pathAndQuery;
            if (content!=null)
            {
                this.content=JSON.stringify(content);
            }
        }
        pathAndQuery:string;
        content:string;
    }

    export async function postWebSocket(webSocket:WebSocket,pathAndQuery:string,content:any=null,error:Function=null)
    {
        webSocket.send(JSON.stringify(new PathCommand(pathAndQuery,content)));
    }

    export async function bindToRemote(webSocket:WebSocket)
    {
        webSocket.onmessage=(ev:MessageEvent)=>
        {
            let text=ev.data as string;
            let instructions=JSON.parse(text) as Instruction[];
            console.log(instructions);
            run(instructions);
        }
    }

    //Deprecate 
    export async function onCheckboxChange(evt:Event,pathAndQuery:string)
    {
        var checkbox=evt.target as HTMLInputElement;
        return await nova.remote.postStatic(pathAndQuery+"&checked="+checkbox.checked);
    }

    export async function getRemote(id:string,pathAndQuery:string)
    {
        return await fetch(pathAndQuery,
            {
                method:"GET",
                headers: {'Content-Type': 'text/html'},
            }).then(response=>
            {
                if (response.ok)
                {
                    return response.text();
                }
            })
            .then((text:string)=>
            {
                if (text!=undefined)
                {
                    document.getElementById(id).innerHTML=text;
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
                    if (error!=null)
                    {
                        error(error);
                    }
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



    export async function postUrlEncoded(action:string,obj:any)
    {
        let params=new URLSearchParams();
        
        for (const key in obj)
        {
            params.append(key,obj[key]);
        }

        return await fetch(action,
            {
                method:"POST",
                body: params
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

    export async function preventDefault(event:Event)
    {
        event.preventDefault();
    }

    export async function submit(event:Event,formId:string=null)
    {
        event.preventDefault();
        let formEvent=event as SubmitEvent;
        let form=(formId==null?event.currentTarget:document.getElementById(formId)) as HTMLFormElement;
        let data=new FormData(form);
        let params=new URLSearchParams();
        for (const pair of data)
        {
            params.append(pair[0],pair[1].toString());
        }

        return await fetch(form.action,
            {
                method:"POST",
                body: params
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

    //deprecated use postForm;
    export async function postFormUrlEncoded(id:string,action:string=null)
    {
        let form=document.getElementById(id) as HTMLFormElement;
        let data=new FormData(form);
        let params=new URLSearchParams();
        for (const pair of data)
        {
            params.append(pair[0],pair[1].toString());
        }

        return await fetch(action!=null?action:form.action,
            {
                method:"POST",
                body: params
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

    export async function postForm(id:string,action:string=null)
    {
        let form=document.getElementById(id) as HTMLFormElement;
        if (form.enctype=="data")
        {
            let data=new FormData(form);
            return await fetch(action!=null?action:form.action,
                {
                    method:"POST",
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
        else
        {
            let data=new FormData(form);
            let params=new URLSearchParams();
            for (const pair of data)
            {
                params.append(pair[0],pair[1].toString());
            }
            return await fetch(action!=null?action:form.action,
                {
                    method:"POST",
                    body: params
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
    }

    //deprecated. Use postForm
    export async function postFormData(id:string,action:string=null)
    {
        let form=document.getElementById(id) as HTMLFormElement;
        return await fetch(action!=null?action:form.action,
            {
                method:"POST",
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

    export function parseHTML(html) 
    {
        var t = document.createElement('template');
        t.innerHTML = html;
        return t.content;
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
                        case "innerHTML":
                            document.getElementById(parameters[0]).innerHTML=parameters[1];
                            break;
                            
                        case "outerHTML":
                        document.getElementById(parameters[0]).outerHTML=parameters[1];
                        break;

                        case "innerText":
                        document.getElementById(parameters[0]).innerText=parameters[1];
                        break;
                                
                        case "remove":
                        {
                            let element=document.getElementById(parameters[0]);
                            if (element!=null)
                            {
                                element.remove();
                            }
                        }
                        break;
                        
                        case "removeChilderen":
                        document.getElementById(parameters[0]).replaceChildren();
                        break;
                                
                        case "prepend":
                        document.getElementById(parameters[0]).prepend(parseHTML(parameters[1]));
                        break;
                                
                        case "append":
                        document.getElementById(parameters[0]).append(parseHTML(parameters[1]));
                        break;

                        case "appendChild":
                        document.getElementById(parameters[0]).appendChild(parseHTML(parameters[1]));
                        break;
                                
                                
                        case "before":
                        document.getElementById(parameters[0]).before(parseHTML(parameters[1]));
                        break;
                                
                        case "after":
                        document.getElementById(parameters[0]).after(parseHTML(parameters[1]));
                        break;
                                
                        case "value":
                            (document.getElementById(parameters[0]) as HTMLInputElement).value=parameters[1];
                            break;
                            
                            case "checked":
                            (document.getElementById(parameters[0]) as HTMLInputElement).checked=parameters[1].toLowerCase()=="true";
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

                        default:
                        if (instruction.trace)
                        {
                            console.log("nova-remote:invalid command="+instruction.command);
                        }
            
                    }
                }
                catch (ex)
                {
                    if (instruction.trace)
                    {
                        console.log("nova-remote:exception="+ex);
                    }
                }
            }
        }
    }


}
