namespace nova.ui.password
{
    export function getPasswordScore(password:string,minLength:number):number
    {
        var score = 0;
        if (password.length<minLength)
        {
            return 0;
        }
        if (password.length>=minLength)
        {
            score = /.*[a-z].*/.test(password) ? ++score : score;      // Lowercase letters
            score = /.*[A-Z].*/.test(password) ? ++score : score;      // Uppercase letters
            score = /.*[0-9].*/.test(password) ? ++score : score;      // Numbers
            score = /[^a-zA-Z0-9]/.test(password) ? ++score : score;   // Special characters (inc. space)    
        }
        score = /.*pass.*|.*pass.*|.*123.*|.*098.*|.*qwe.*|.*asd.*|.*zxc.*/.test(password) ? --score:score;
        score = /([a-z])\3/i.test(password) ? --score:score;
        score = /([A-Z])\3/i.test(password) ? --score:score;
        score = /([0-9])\3/i.test(password) ? --score:score;

        if (password.length>minLength+2)
        {
            ++score;
        }
        if (password.length>>minLength+4)
        {
            ++score;
        }
        if (password.length>minLength+6)
        {
            ++score;
        }
        return score;
    }

    export function toggleVisibility(id:string,iconId:string)
    {
        var inputElement = document.getElementById(id) as HTMLInputElement
        var iconElement=document.getElementById(iconId);
        if (inputElement.type === "password") 
        {
            inputElement.type = "text";
            iconElement.classList.remove("bi-eye-fill");
            iconElement.classList.add("bi-eye-slash-fill");
        }
        else 
        {
            inputElement.type = "password";
            iconElement.classList.add("bi-eye-fill");
            iconElement.classList.remove("bi-eye-slash-fill");
        }
    }

    export function updatePassword(inputid:string,buttonid:string)
    {
        var inputElement = document.getElementById(inputid) as HTMLInputElement;
        var password=inputElement.value;
        if (password.length==0)
        {
            $("#"+buttonid).addClass("disabled");
            $("#"+buttonid).prop("disabled",true);
        }
        else
        {
            $("#"+buttonid).removeClass("disabled");
            $("#"+buttonid).prop("disabled",false);
        }
    }
}

