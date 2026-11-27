
class CreateAlertTypeRequest extends RequestSender
{
    public application:string;
    public type:string;
    public description:string;
    public reminderInterval:number;
    public groupingInterval:number;
    
    public constructor()
    {
        super();
        this.application=(document.getElementById("application") as HTMLInputElement).value;
        this.type=(document.getElementById("type") as HTMLInputElement).value;
        this.description=(document.getElementById("description") as HTMLInputElement).value;
        this.reminderInterval=Number((document.getElementById("reminderInterval") as HTMLSelectElement).value);
        this.groupingInterval=Number((document.getElementById("groupingInterval") as HTMLSelectElement).value);
    }
}



function sendCreateAlertTypeRequest()
{
    new CreateAlertTypeRequest().send("/alertType/create","json");
}

