//<reference path="path/to/jqueryui-1.9.d.ts"/>

class ElementResult
{
    public id:string;
    public text:string;
}

 class FormResult
{
    public elementResults:ElementResult[];
    public error:string;
    public success:string;
    public location:string;

}

var oldFormResult:FormResult;

function process(formResult:FormResult):boolean
{
    var success:boolean=false;
    /*
    if ((oldFormResult!=null)&&(oldFormResult!=undefined))
    {
        if ((oldFormResult.elementResults!=undefined)&&(oldFormResult.elementResults!=null))
        {
            for (let result of oldFormResult.elementResults)
            {
                var errorElement=document.getElementById(result.id).nextElementSibling as HTMLDivElement;
                errorElement.style.display="none";
            }
        }
    }
    oldFormResult=formResult;
    */
    if ((formResult.success!=null)&&(formResult.success!=undefined))
    {
        success=true;
        var dialog=document.getElementById("dialog").innerHTML=formResult.success;
        $("#dialog").dialog({
            autoOpen : true,
            title: "Success",
            draggable:false,
            modal : true,
            width:600,
            height:320,
            resizable:false,
            buttons:[{
            text:"Close",
            icons: {
                primary: "ui-icon-closethick"
            },
            click: function() {
                $( this ).dialog( "close" );
                processForm(formResult);
            }  
            }]
        }).prev(".ui-dialog-titlebar").css("background","#040");
    }
    else if ((formResult.error!=null)&&(formResult.error!=undefined))
    {
        var dialog=document.getElementById("dialog").innerHTML=formResult.error;
        $("#dialog").dialog({
            autoOpen : true,
            title: "Error",
            draggable:false,
            modal : true,
            width:600,
            height:320,
            resizable:false,
            buttons:[{
            text:"Close",
            icons: {
                primary: "ui-icon-closethick"
            },
            click: function() {
                $( this ).dialog( "close" );
                processForm(formResult);
            }  
            }]
        }).prev(".ui-dialog-titlebar").css("background","#400");
    }
    else
    {
        processForm(formResult);
    }
    return success;
}

function processForm(formResult:FormResult)
{
    if ((formResult.location!=undefined)&&(formResult.location!=null))
    {
        window.location.href=formResult.location;
    }
    if ((formResult.elementResults!=undefined)&&(formResult.elementResults!=null))
    {
        for (let result of formResult.elementResults)
        {
            var errorElement=document.getElementById(result.id).nextElementSibling as HTMLDivElement;
            errorElement.style.display="block";
            errorElement.innerHTML=result.text;
        }
    }

}

function confirmDialog(text:string,obj:any,action)
{
    var dialog=document.getElementById("dialog").innerHTML=text;
    $("#dialog").dialog({
        autoOpen : true,
        title: "Confirm",
        draggable:false,
        modal : true,
        width:600,
        height:320,
        resizable:false,
        buttons:[{
            text:"OK",
            className: 'save',
            icons: {
                primary: "ui-icon-check"
            },
            click: function() {
                action(obj);
            }  
        },{
            text:"Cancel",
            className: 'cancel',
            icons: {
                primary: "ui-icon-cancel"
            },
            click: function() {
                $( this ).dialog( "close" );
            }  
        }]
    }).prev(".ui-dialog-titlebar").css("background","#444");
}

function showDialog(success:boolean,text:string)
{
    var dialog=document.getElementById("dialog").innerHTML=text;
    if (success)
    {
        $("#dialog").dialog({
            autoOpen : true,
            title: "Success",
            draggable:false,
            modal : true,
            width:600,
            height:320,
            resizable:false,
            buttons:[{
            text:"Close",
            icons: {
                primary: "ui-icon-closethick"
            },
            click: function() {
                $( this ).dialog( "close" );
            }  
            }]
        }).prev(".ui-dialog-titlebar").css("background","#040");
    }
    else
    {
        $("#dialog").dialog({
            autoOpen : true,
            title: "Error",
            draggable:false,
            modal : true,
            width:600,
            height:320,
            resizable:false,
            buttons:[{
            text:"Close",
            icons: {
                primary: "ui-icon-closethick"
            },
            click: function() {
                $( this ).dialog( "close" );
            }  
            }]
        }).prev(".ui-dialog-titlebar").css("background","#400");
    }    
}

class RequestSender
{
    public send(url:string,dataType:string)
    {
        var thisRequest=this;
        var request=$.ajax(
        {
            type:"POST"
            ,url:url
            ,data:JSON.stringify(this)
            ,dataType:dataType
            ,contentType:"application/json"
            ,async:false
            ,success:function(formResult:FormResult)
            {
                process(formResult);
            }
            ,error:function(response:JQueryXHR)
            {
                alert("Error: "+response.status);
            }
        }
        )
        ;
    }
}

function post(url:string,dataType:string,object:any)
{
    $.ajax(
        {
            type:"POST"
            ,url:url
            ,data:JSON.stringify(object)
            ,dataType:dataType
            ,contentType:"application/json"
            ,async:false
            ,success:function(formResult:FormResult)
            {
                process(formResult);
            }
            ,error:function(response:JQueryXHR)
            {
                alert("Error: "+response.status);
            }
        }
        )
        ;
}