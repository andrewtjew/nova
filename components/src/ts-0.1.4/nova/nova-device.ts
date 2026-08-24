namespace nova.device
{
    export function initialize(path:string)
    {
        var timeZone=Intl.DateTimeFormat().resolvedOptions().timeZone;
        window.location.href=encodeURI(path+"&timeZone="+timeZone);
    }
}

