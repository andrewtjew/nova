
class AddServer extends RequestSender
{
    public url:string;
    public description:string;
    public interval:number;
    
    public constructor()
    {
        super();
        this.url=(document.getElementById("url") as HTMLInputElement).value;
        this.description=(document.getElementById("description") as HTMLInputElement).value;
        this.interval=Number((document.getElementById("interval") as HTMLSelectElement).value);
    }
}

function sendAddServerRequest()
{
    var addServer=new AddServer();
    var creator=(document.getElementById("creator") as HTMLInputElement).value;
    addServer.send("/server/add/post?creator="+creator,"json");
}

