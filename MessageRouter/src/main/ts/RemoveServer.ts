
class RemoveServerRequest
{
    public url:string;

    constructor(url:string)
    {
        this.url=url;

    }
    remove(period)
    {
        post("/server/remove","json",period);
    }
}


function removeServer(message:string,url:string)
{
    var request=new RemoveServerRequest(url);
    confirmDialog(message,request,request.remove);
}

