
class Contact
{
    public id:string;
    constructor(id:string)
    {
        this.id=id;
    }
    remove(alertType)
    {
        post("/contact/remove","json",alertType);
    }
}


function removeContact(message:string,id:string)
{
    var contact=new Contact(id);
    confirmDialog(message,contact,contact.remove);
}

