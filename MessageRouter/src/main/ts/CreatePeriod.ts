


class CreatePeriodRequest extends RequestSender
{
    public contactId:string;
    public level:string;
    public timeZoneOffset:number;
    public start:string;
    public duration:number;
    public recurrence:string;
    
    public constructor(contactId:string,level:string)
    {
        super();
        this.contactId=contactId;
        this.level=level;
        this.timeZoneOffset=new Date().getTimezoneOffset();
        this.start=(document.getElementById("start") as HTMLInputElement).value;
        this.duration=Number((document.getElementById("duration") as HTMLInputElement).value);
        this.recurrence=(document.getElementById("recurrence") as HTMLInputElement).value;
    }

}

function sendCreatePeriodRequest(contactId:string,level:string)
{
    new CreatePeriodRequest(contactId,level).send("/schedule/period/create","json");
}

