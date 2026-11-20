
class Alert
{
    public id:number;

    constructor(id:number)
    {
        this.id=id;
    }
    remove(alert:Alert)
    {
        post("/alert/remove","json",alert);
    }
}


function removeAlert(message:string,id:number)
{
    var alert=new Alert(id);
    confirmDialog(message,alert,alert.remove);
}

