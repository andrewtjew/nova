


class CreateContactRequest extends RequestSender
{
    public id:string;
    public email:string;
    public smsNumber:string;
    public info:string;
    
    public constructor()
    {
        super();
        this.id=(document.getElementById("id") as HTMLInputElement).value;
        this.email=(document.getElementById("email") as HTMLInputElement).value;
        this.smsNumber=(document.getElementById("smsNumber") as HTMLInputElement).value;
        this.info=(document.getElementById("info") as HTMLInputElement).value;
    }

}

function sendCreateContactRequest()
{
    new CreateContactRequest().send("/contact/create","json");
}

