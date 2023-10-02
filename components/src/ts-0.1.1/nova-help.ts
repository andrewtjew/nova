namespace nova.help
{
    class Link
    {
        public topicId:string;
        public targetId:string;
    }

    function isChildOf(child:Node, parent:Node) 
    {
        if (child.parentNode === parent) {
          return true;
        } else if (child.parentNode === null) {
          return false;
        } else {
          return isChildOf(child.parentNode, parent);
        }
    }

    function show()
    {
        instance.show(instance.index,false);
    }

    function closeIfTargetIsClicked(ev:Event)
    {
        if (ev.target==instance.targetElement)
        {
            instance.close();
        }
    }

    class Document
    {
        public links:Link[];
        index:number;
        helperElement:HTMLElement;
        topicElement:HTMLElement;
        currentTopic:HTMLElement;
        targetMargin:number;
        overlayTopElement:HTMLElement;
        overlayLeftElement:HTMLElement;
        overlayRightElement:HTMLElement;
        overlayBottomElement:HTMLElement;
        currentRect:DOMRect;
        targetRect:DOMRect;
        targetElement:HTMLElement;
        closeButton:HTMLButtonElement;
        nextButton:HTMLButtonElement;
        backButton:HTMLButtonElement;
        statusElement:HTMLElement;

        public constructor(targetMargin:number,zIndex:number,links:Link[])
        {
            window.addEventListener("resize",show);
            window.addEventListener("scroll",show);
            window.addEventListener("click",closeIfTargetIsClicked);

            this.links=links;
            this.targetMargin=targetMargin;
            this.helperElement=document.getElementById("nova-help-helper") as HTMLElement;
            this.topicElement=document.getElementById("nova-help-topic");
            this.statusElement=document.getElementById("nova-help-navigation-status");

            this.overlayTopElement=document.getElementById("nova-help-overlay-top");
            this.overlayLeftElement=document.getElementById("nova-help-overlay-left");
            this.overlayRightElement=document.getElementById("nova-help-overlay-right");
            this.overlayBottomElement=document.getElementById("nova-help-overlay-bottom");
            this.closeButton=document.getElementById("nova-help-navigation-close") as HTMLButtonElement;
            this.nextButton=document.getElementById("nova-help-navigation-next") as HTMLButtonElement;
            this.backButton=document.getElementById("nova-help-navigation-back") as HTMLButtonElement;


            var z=zIndex.toString();
            this.overlayTopElement.style.zIndex=z;
            this.overlayLeftElement.style.zIndex=z;
            this.overlayRightElement.style.zIndex=z;
            this.overlayBottomElement.style.zIndex=z;
            this.helperElement.style.zIndex=z;

            this.overlayTopElement.style.display="block";
            this.overlayLeftElement.style.display="block";
            this.overlayRightElement.style.display="block";
            this.overlayBottomElement.style.display="block";

            this.closeButton.onclick=function()
            {
                instance.close();
            };
            this.nextButton.onclick=function()
            {
                instance.next();
            };
            this.backButton.onclick=function()
            {
                instance.back();
            }

            for (var link in links)
            {
                var element=document.getElementById(links[link].topicId);
                element.style.display="none";
                this.topicElement.appendChild(element);
            }

            this.index=0;
            this.currentTopic=null;
        }

        getTargetRect(targetElement:HTMLElement):DOMRect
        {
            var unAdjustedTargetRect=targetElement.getBoundingClientRect();
            var targetRect=new DOMRect(unAdjustedTargetRect.x-this.targetMargin,unAdjustedTargetRect.y-this.targetMargin,unAdjustedTargetRect.width+2*this.targetMargin,unAdjustedTargetRect.height+2*this.targetMargin);
            return targetRect;
        }


        showOverlayAroundTarget(targetRect:DOMRect)
        {
            var innerWidth=window.innerWidth;
            var innerHeight=window.innerHeight;
            var targetTop=targetRect.top-this.targetMargin;
            var targetLeft=targetRect.left-this.targetMargin;
            var targetBottom=targetRect.bottom+this.targetMargin;
            var targetRight=targetRect.right+this.targetMargin;
            var targetHeight=targetRect.bottom-targetRect.top+2*this.targetMargin;
    
            this.overlayTopElement.style.left="0px";
            this.overlayTopElement.style.top="0px";
            this.overlayTopElement.style.width=innerWidth+"px";
            this.overlayTopElement.style.height=targetTop+"px";
            this.overlayLeftElement.style.left="0px";
            this.overlayLeftElement.style.top=targetTop+"px";
            this.overlayLeftElement.style.width=targetLeft+"px";
            this.overlayLeftElement.style.height=targetHeight+"px";
            this.overlayRightElement.style.left=targetRight+"px";
            this.overlayRightElement.style.top=targetTop+"px";
            this.overlayRightElement.style.width=innerWidth+"px";
            this.overlayRightElement.style.height=targetHeight+"px";
            this.overlayBottomElement.style.left="0px";
            this.overlayBottomElement.style.top=targetBottom+"px";
            this.overlayBottomElement.style.width=innerWidth+"px";
            this.overlayBottomElement.style.height=(innerHeight-targetBottom)+"px";
    
        }
        showOverlay()
        {
            var innerWidth=window.innerWidth;
            var innerHeight=window.innerHeight;
            var targetTop=innerHeight/2;
            var targetLeft=innerWidth/2;
            var targetBottom=targetTop;
            var targetRight=targetLeft;
            var targetHeight=0;
    
            this.overlayTopElement.style.left="0px";
            this.overlayTopElement.style.top="0px";
            this.overlayTopElement.style.width=innerWidth+"px";
            this.overlayTopElement.style.height=targetTop+"px";
            this.overlayLeftElement.style.left="0px";
            this.overlayLeftElement.style.top=targetTop+"px";
            this.overlayLeftElement.style.width=targetLeft+"px";
            this.overlayLeftElement.style.height=targetHeight+"px";
            this.overlayRightElement.style.left=targetRight+"px";
            this.overlayRightElement.style.top=targetTop+"px";
            this.overlayRightElement.style.width=innerWidth+"px";
            this.overlayRightElement.style.height=targetHeight+"px";
            this.overlayBottomElement.style.left="0px";
            this.overlayBottomElement.style.top=targetBottom+"px";
            this.overlayBottomElement.style.width=innerWidth+"px";
            this.overlayBottomElement.style.height=(innerHeight-targetBottom)+"px";
        }

        show(index:number,scroll:boolean=true)
        {
            this.index=index;
            var link=this.links[this.index];
            if (this.targetElement!=null)
            {
                if (this.targetElement.classList.contains("dropdown-menu"))
                {
                    this.targetElement.classList.remove("show");
                }
            }
            if (link.targetId!=null)
            {
                this.targetElement=document.getElementById(link.targetId);
                if (this.targetElement.classList.contains("dropdown-menu"))
                {
                    this.targetElement.classList.add("show");
                }
                if (scroll)
                {
                    this.targetElement.scrollIntoView();
                }
                this.targetRect=this.getTargetRect(this.targetElement);            
                this.showOverlayAroundTarget(this.targetRect);
                this.showHelperToTarget(document.getElementById(link.topicId));
            }
            else
            {
                this.showOverlay();
                this.showHelper(document.getElementById(link.topicId));
            }
            if (this.index==0)
            {
                this.backButton.style.display="none";
            }
            else
            {
                this.backButton.style.display="block";
            }
            if (this.index>=this.links.length-1)
            {
                this.nextButton.style.display="none";
            }
            else
            {
                this.nextButton.style.display="block";
            }
            this.statusElement.innerText=(index+1)+"/"+this.links.length;

        }

        showHelperToTarget(topic:HTMLElement)
        {
            this.helperElement.style.display="block";
            if (this.currentTopic!=null)
            {
                this.currentTopic.style.display="none";
            }
            topic.style.display="block";
            this.currentTopic=topic;

            var targetCenter=this.targetRect.left+this.targetRect.width/2;
            var helperX=targetCenter-this.helperElement.offsetWidth/2;
            var scrollX=window.scrollX;
            var scrollY=window.scrollY;
            if (helperX<scrollX)
            {
                helperX=scrollX;
            }
            this.helperElement.style.left=helperX+"px";
            var pointerX=targetCenter-helperX+scrollX;
            this.helperElement.style.setProperty("--nova-help-helper-left",pointerX+"px");
            var topLength=this.targetRect.top-scrollY;
            var bottomLength=scrollY+window.innerHeight-this.targetRect.bottom;
            if (topLength<bottomLength)
            {
                //show helper bottom of target
                this.helperElement.style.setProperty("--nova-help-helper-border-width","0 0.5em 1em");
                this.helperElement.style.setProperty("--nova-help-helper-top","-1em");
                this.helperElement.style.setProperty("--nova-help-helper-bottom","100%");
                this.helperElement.style.top=(scrollY+this.targetRect.bottom)+"px";
            }
            else
            {
                this.helperElement.style.setProperty("--nova-help-helper-border-width","1em 0.5em 0");
                this.helperElement.style.setProperty("--nova-help-helper-top","100%");
                this.helperElement.style.setProperty("--nova-help-helper-bottom","-1em");
                console.log("offset="+this.helperElement.offsetHeight);
                console.log("client="+this.helperElement.clientHeight);
                console.log("bounding="+this.helperElement.getBoundingClientRect().height);

                var bottom = window.getComputedStyle(
                    this.helperElement, ':after'
                ).getPropertyValue('bottom');
                var heightAdjust=-2*parseInt(bottom.substring(0,bottom.indexOf("px")));
                console.log("h="+heightAdjust);
                this.helperElement.style.top=(scrollY+this.targetRect.top-this.helperElement.offsetHeight-this.targetMargin-heightAdjust)+"px";
            }
            this.helperElement.style.marginLeft="0";
            this.helperElement.style.marginTop="1em";
        }
        showHelper(topic:HTMLElement)
        {

            this.helperElement.style.display="block";
            if (this.currentTopic!=null)
            {
                this.currentTopic.style.display="none";
            }
            topic.style.display="block";
            this.currentTopic=topic;

            this.helperElement.style.left="50%";
            this.helperElement.style.top="50%";
            var rect=this.helperElement.getBoundingClientRect();
            console.log("rect:width="+rect.width+",height="+rect.height);
            this.helperElement.style.marginLeft=(-rect.width/2)+"px";
            this.helperElement.style.marginTop=(-rect.height/2)+"px";
            this.helperElement.style.setProperty("--nova-help-helper-border-width","0");
        }
        alertTest()
        {
            alert("hello");
        }
       
        next()
        {
            if (this.index<this.links.length-1)
            {
                this.show(this.index+1);
                this.helperElement.style.display="none";
                setTimeout(()=>{ this.helperElement.style.display="block";},200);

            }
        }
        back()
        {
            if (this.index>0)
            {
                this.show(this.index-1);
                this.helperElement.style.display="none";
                setTimeout(()=>{ this.helperElement.style.display="block";},200);
            }
        }
        close()
        {
            if (this.targetElement!=null)
            {
                if (this.targetElement.classList.contains("dropdown-menu"))
                {
                    this.targetElement.classList.remove("show");
                }
            }
            window.removeEventListener("resize",show);
            window.removeEventListener("scroll",show);
            window.removeEventListener("click",closeIfTargetIsClicked);

            this.helperElement.style.display="none";
            this.overlayTopElement.style.display="none";
            this.overlayLeftElement.style.display="none";
            this.overlayRightElement.style.display="none";
            this.overlayBottomElement.style.display="none";
            instance=null;
        }
    }
    var instance:Document;

    export function activate(startIndex:number,targetMargin:number,zIndex:number,links:Link[])
    {
        instance=new Document(targetMargin,zIndex,links);
        instance.show(startIndex);
    }

}

