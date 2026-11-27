
class TestAlertTypeRequest
{
    public application:string;
    public type:string;
    public message:string;
    public url:string;
    public level:string;
    
    public constructor(application:string,type:string)
    {
        this.application=application;
        this.type=type;
        this.url=(document.getElementById("url") as HTMLInputElement).value;
        this.message=(document.getElementById("message") as HTMLInputElement).value;
        this.level=(document.getElementById("level") as HTMLInputElement).value;
    }

    public send()
    {
        var thisRequest=this;
        var request=$.ajax(
        {
            type:"POST"
            ,url:"/alert"
            ,data:JSON.stringify(this)
            ,dataType:'text'
            ,contentType:"application/json"
            ,async:false
            ,success:function(data)
            {
                showDialog(true,"Test alert successfully sent.");
            }
            ,error:function()
            {
                showDialog(false,"Error sending test alert.");
            }
        }
        )
        ;
    }
}

function sendTestAlertTypeRequest(application:string,type:string)
{
    new TestAlertTypeRequest(application,type).send();
}

