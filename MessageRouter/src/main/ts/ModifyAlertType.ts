
class ModifyAlertTypeRequest extends RequestSender
{
    public application:string;
    public type:string;
    public reminderInterval:number;
    public groupingInterval:number;
    
    public constructor(application:string,type:string)
    {
        super();
        this.application=application;
        this.type=type;
        this.reminderInterval=Number((document.getElementById("reminderInterval") as HTMLSelectElement).value);
        this.groupingInterval=Number((document.getElementById("groupingInterval") as HTMLSelectElement).value);
    }
}

function sendModifyAlertTypeRequest(application:string,type:string)
{
    var request=new ModifyAlertTypeRequest(application,type);
    request.send("/alertType/modify","json");
}

