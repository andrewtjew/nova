namespace nova.ui.validation
{
    export function provideFeedback(event:Event)
    {
        var form=event.target as HTMLFormElement;
        if (!form.checkValidity())
        {
            event.preventDefault();
            event.stopPropagation();
        }
        form.classList.add("was-validated");
    }

}

namespace nova.ui
{
    //To show results as you type and allows item to be selected.
    export  class SearchPicker
    {
        private readonly itemIdPrefix:string;
        private readonly focusClass:string;
        private currentId:number;
        private itemCount:number;
    
        constructor(itemIdPrefix:string,focusClass:string)
        {
            this.focusClass=focusClass;
            this.itemIdPrefix=itemIdPrefix;
            this.currentId=-1;
            this.itemCount=0;
        }
    
        public reset(itemCount:number) 
        {
            this.itemCount=itemCount;
            this.currentId=-1;    
        }
    
        public process(event:KeyboardEvent)
        {
            console.log("process"+event.keyCode);
            if (event.keyCode==40) //down
            {
                var lastId=this.currentId;
                this.currentId++;
                if (this.currentId>=this.itemCount)
                {
                    this.currentId=0;
                }
                var item=document.getElementById(this.itemIdPrefix+this.currentId);
                item.classList.add(this.focusClass);
                if (lastId>=0)
                {
                    document.getElementById(this.itemIdPrefix+lastId).classList.remove(this.focusClass);
                }
            }
            else if (event.keyCode==38) //up
            {
                var lastId=this.currentId;
                this.currentId--;
                if (this.currentId<0)
                {
                    this.currentId=this.itemCount-1;
                }
                var item=document.getElementById(this.itemIdPrefix+this.currentId);
                item.classList.add(this.focusClass);
                if (lastId>=0)
                {
                    document.getElementById(this.itemIdPrefix+lastId).classList.remove(this.focusClass);
                }
            }
            else if (event.keyCode==13)
            {
                var item=document.getElementById(this.itemIdPrefix+this.currentId);
                item.click();
            }
    
        }
    
    }
    
    // export function toggleInput(checkboxId:string,inputId:string):void
    // {
    //     var checkbox=document.getElementById(checkboxId) as HTMLInputElement;
    //     var input=document.getElementById(inputId) as HTMLInputElement;
    //     input.disabled=!checkbox.checked;
    // }

    // export function showModalMessage(root:string,header:string,body:string)
    // {
    //     document.getElementById(root+".header").innerHTML=header;
    //     document.getElementById(root+".body").innerHTML=body;
    //     $('#'+root).modal("show");

    // }

    // export function validate()
    // {
    //         var forms = document.getElementsByClassName('needs-validation');
    //         var validation = Array.prototype.filter.call(forms, function(form)
    //         {
    //             form.addEventListener('submit', function(event)
    //             {
    //                 if (form.checkValidity()===false)
    //                 {
    //                     event.preventDefault();
    //                     event.stopPropagation();
    //                 }
    //                 form.classList.add('was-validated');
    //             }
    //             ,false);
    //         }
    //         );
    // }

    // export function validateOnLoad()
    // {
    //     window.addEventListener('load', function()
    //     {
    //         var forms = document.getElementsByClassName('needs-validation');
    //         var validation = Array.prototype.filter.call(forms, function(form)
    //         {
    //             form.addEventListener('submit', function(event)
    //             {
    //                 if (form.checkValidity()===false)
    //                 {
    //                     event.preventDefault();
    //                     event.stopPropagation();
    //                 }
    //                 form.classList.add('was-validated');
    //             }
    //             ,false);
    //         }
    //         );
    //     }
    //     , false);
    // }

}
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

    export function updateNewPasswordProgress(messages:string[],minLength:number,inputId1:string,inputId2:string,buttonId1:string,buttonId2:string,feedbackId:string,progressId:string,submitId:string)
    {
        var inputElement1 = document.getElementById(inputId1) as HTMLInputElement;
        var password1=inputElement1.value;
        var inputElement2 = document.getElementById(inputId2) as HTMLInputElement;
        var password2=inputElement2.value;
        var button=document.getElementById(submitId) as HTMLButtonElement;
    
        var score=getPasswordScore(password1,minLength);
    
        if (submitId!=null)
        {
            if ((password1==password2)&&(score>=3))
            {
                $("#"+submitId).removeClass("disabled");
                $("#"+submitId).prop("disabled",false);
            }
            else
            {
                $("#"+submitId).addClass("disabled");
                $("#"+submitId).prop("disabled",true);
            }
        }
        if (password2.length==0)
        {
            $("#"+buttonId2).addClass("disabled");
            $("#"+buttonId2).prop("disabled",true);
        }
        else
        {
            $("#"+buttonId2).removeClass("disabled");
            $("#"+buttonId2).prop("disabled",false);
        }
    
        if (password1.length==0)
        {
            $("#"+buttonId1).addClass("disabled");
            $("#"+buttonId1).prop("disabled",true);
        }
        else
        {
            $("#"+buttonId1).removeClass("disabled");
            $("#"+buttonId1).prop("disabled",false);
        }
    
        if (progressId!=null)
        {
    
            var barElement = document.getElementById(progressId);
            var feedbackElement = document.getElementById(feedbackId);
            if (password1.length==0)
            {
                $("#"+progressId).attr("style","width:0%");
            }
            else if (score>=5)
            {
                $("#"+progressId).attr("style","width:100%");
                $("#"+progressId).addClass("bg-success");
                $("#"+progressId).removeClass("bg-warning");
                $("#"+progressId).removeClass("bg-danger");
                $("#"+progressId).addClass("text-light");
                $("#"+progressId).removeClass("text-dark");
                
                feedbackElement.innerHTML=messages[2];
            }
            else if (score>=3)
            {
                $("#"+progressId).attr("style","width:66%");
                $("#"+progressId).removeClass("bg-success");
                $("#"+progressId).addClass("bg-warning");
                $("#"+progressId).removeClass("bg-danger");
                $("#"+progressId).removeClass("text-light");
                $("#"+progressId).addClass("text-dark");
                feedbackElement.innerHTML=messages[1];
            }
            else 
            {
                $("#"+progressId).attr("style","width:33%");
                $("#"+progressId).removeClass("bg-success");
                $("#"+progressId).removeClass("bg-warning");
                $("#"+progressId).addClass("bg-danger");
                $("#"+progressId).addClass("text-light");
                $("#"+progressId).removeClass("text-dark");
                feedbackElement.innerHTML=messages[0];
            }
                
        }
    
    }
    
    
}

