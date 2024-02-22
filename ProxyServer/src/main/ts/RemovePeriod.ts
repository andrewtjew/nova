
class Period
{
    public contactId:string;
    public level:string;
    public start:number;
    public duration:number;
    public recurrence:string;

    constructor(contactId:string,level:string,start:number,duration:number,recurrence:string)
    {
        this.contactId=contactId;
        this.level=level;
        this.start=start;
        this.duration=duration;
        this.recurrence=recurrence;

    }
    remove(period)
    {
        post("/period/remove","json",period);
    }
}


function removeCoveragePeriod(message:string,contactId:string,level:string,start:number,duration:number,recurrence:string)
{
    var period=new Period(contactId,level,start,duration,recurrence);
    confirmDialog(message,period,period.remove);
}

