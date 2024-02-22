
class AlertType
{
    public application:string;
    public type:string;
    constructor(application:string,type:string)
    {
        this.application=application;
        this.type=type;
    }
    remove(alertType)
    {
        post("/alertType/remove","json",alertType);
    }
}


function removeAlertType(application:string,type:string,message:string)
{
    var alertType=new AlertType(application,type);
    confirmDialog(message,alertType,alertType.remove);
}

