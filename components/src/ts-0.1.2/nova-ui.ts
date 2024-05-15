namespace nova.ui.media
{
    export class Camera
    {
        videoElement:HTMLMediaElement;
        canvasElement:HTMLCanvasElement;
        constructor(cameraElementId:string,canvasElementId:string)
        {
            this.videoElement=document.getElementById(cameraElementId) as HTMLMediaElement;
            this.canvasElement=document.getElementById(canvasElementId) as HTMLCanvasElement;
        }
        async videoOn()
        {
            this.videoElement.srcObject=await navigator.mediaDevices.getUserMedia({video:true});
            this.videoElement.onloadedmetadata = () => 
            {
                console.log("playing4...");
                this.videoElement.play();
            };
        }
    
        postSnapshot(url:string)
        {
            this.canvasElement.getContext('2d').drawImage(this.videoElement as HTMLElement as HTMLImageElement,0,0,this.canvasElement.width,this.canvasElement.height);
            var dataURL=this.canvasElement.toDataURL();
            nova.remote.postStatic(url,JSON.stringify({dataURL:dataURL}));
        }


        interval:number;

        startPostSnapshot(url:string,interval:number)
        {
            this.interval=setInterval(()=>
            {
                this.canvasElement.getContext('2d').drawImage(this.videoElement as HTMLElement as HTMLImageElement,0,0,this.canvasElement.width,this.canvasElement.height);
                var dataURL=this.canvasElement.toDataURL();
                nova.remote.postStatic(url,JSON.stringify({dataURL:dataURL}));
            }
            ,interval);

        }
        stopPostSnapshot()
        {
            this.videoElement.pause();
            clearInterval(this.interval);
        }

    
    }

    
}



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

namespace nova.ui.modal
{
    export function proceed(modalId:string,headerId:string,header:string,bodyId:string,body:string,buttonId:string,code:string)
    {
        var button=document.getElementById(buttonId) as HTMLButtonElement;
    
        if (headerId!=null)
        {
            document.getElementById(headerId).innerHTML=header;
        }
        if (bodyId!=null)
        {
            document.getElementById(bodyId).innerHTML=body;
        }
   
        var modal=bootstrap.Modal.getOrCreateInstance(document.getElementById(modalId));
        button.onclick=(event:MouseEvent) =>
        {
            modal.hide();
            if (code!=null)
            {
                eval(code);
            }
        };
        modal.show();
    }

    export function show(modalId:string)
    {
        var modal=bootstrap.Modal.getOrCreateInstance(document.getElementById(modalId));
        modal.show();
    }
}

namespace nova.ui.search
{
    //To show results as you type and allows item to be selected.
    export  class Picker
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
            console.log("keyCode:"+event.keyCode);
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

namespace nova.ui.remote
{
    export  class RemoteSearchSelect
    {
        private inputElement:HTMLInputElement;
        private action:string;
        private remoteStateBinding:string;
        private id:string;
        private optionIndex:number;
        private selectedOptionElement:HTMLElement;
        private selectedColor="bg-secondary";
        private blur:boolean=false;

        constructor(remoteStateBinding:string,action:string,id:string)
        {
            this.remoteStateBinding=remoteStateBinding;
            this.action=action;
            this.id=id;
            this.inputElement=document.getElementById(id) as HTMLInputElement;
            this.optionIndex=-1;
        }
    
        private timer:number=null;

        public processBlur(event:Event)
        {
            if (this.timer!=null)
            {
                clearTimeout(this.timer);
            }
            setTimeout(()=>{this.clearOptions();},200);
        }

        clearOptions()
        {
            var element=document.getElementById(this.id+"-options");
            element.innerHTML="";
        }

        public processKeyup(event:KeyboardEvent)
        {
            if (this.keydownCaptured==false)
            {
                console.log("keyup:"+event.keyCode);
                if (this.timer!=null)
                {
                    clearTimeout(this.timer);
                }
                this.timer=setTimeout(()=>{this.post();},400);
            }
            this.keydownCaptured=false;
        }
        public post()
        {
            if ((this.inputElement.value==null)||(this.inputElement.value.length==0))
            {
                return;
            }
            var pathAndQuery=this.action+"/options?"+this.remoteStateBinding+"&search="+encodeURIComponent(this.inputElement.value);
            console.log("path:"+pathAndQuery);
            this.optionIndex=-1;
            if (this.selectedOptionElement!=null)
            {
                this.selectedOptionElement.classList.remove(this.selectedColor);
                this.selectedOptionElement.classList.remove("bg-opacity-25");
            }
            nova.remote.postStatic(pathAndQuery);

        }

        keydownCaptured:boolean;
        public processKeydown(event:KeyboardEvent)
        {
            console.log("keydown:"+event.keyCode);
            var newOptionIndex=null;
            if (event.keyCode==40) //down
            {
                newOptionIndex=this.optionIndex+1;
            }
            else if (event.keyCode==38) //up
            {
                newOptionIndex=this.optionIndex-1;
            }
            else if (event.keyCode==13)
            {
                if (this.selectedOptionElement!=null)
                {
                    console.log("click:"+newOptionIndex);

                    this.selectedOptionElement.click();
                    return;
                    // var pathAndQuery=this.action+"/select?_id="+this.id+"&index="+this.optionIndex;
                    // console.log("path:"+pathAndQuery);
                    // nova.remote.postStatic(pathAndQuery);
                }
            }
            console.log("index:"+newOptionIndex);
            if (newOptionIndex!=null)
            {
                this.keydownCaptured=true;
                var optionElement=document.getElementById(this.id+"-option-"+newOptionIndex);
                if (optionElement!=null)
                {
                    if (this.selectedOptionElement!=null)
                    {
                        this.selectedOptionElement.classList.remove(this.selectedColor);
                        this.selectedOptionElement.classList.remove("bg-opacity-25");
                    }
                    this.optionIndex=newOptionIndex;
                    this.selectedOptionElement=optionElement;
                    this.selectedOptionElement.classList.add(this.selectedColor);
                    this.selectedOptionElement.classList.add("bg-opacity-25");
                }
           }
        }
    }
}

