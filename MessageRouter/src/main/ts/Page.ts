class IdleWatcher
{
    timer:number;
    secondsLeft:number;
    mousemoveSeconds:number;
    mousedownSeconds:number;
    keyboardSeconds:number;


    constructor(initialSeconds:number,mousemoveSeconds:number,mousedownSeconds:number,keyboardSeconds:number)
    {
        this.mousemoveSeconds=mousemoveSeconds;
        this.mousedownSeconds=mousedownSeconds;
        this.keyboardSeconds=keyboardSeconds;
        this.secondsLeft=initialSeconds;
        this.timer=setInterval(this.check,1000);
        window.onmousemove=this.resetMousemove;
        window.onmousedown=this.resetMousedown;
        window.onkeydown=this.resetKeyboard;


    }
    
    resetMousemove()
    {
        if (thisIdleWatcher.secondsLeft<thisIdleWatcher.mousemoveSeconds)
        {
            thisIdleWatcher.secondsLeft=thisIdleWatcher.mousemoveSeconds;
        }
    }

    resetMousedown()
    {
        if (thisIdleWatcher.secondsLeft<thisIdleWatcher.mousedownSeconds)
        {
            thisIdleWatcher.secondsLeft=thisIdleWatcher.mousedownSeconds;
        }
    }

    resetKeyboard()
    {
        thisIdleWatcher.secondsLeft=thisIdleWatcher.keyboardSeconds;
    }

    check()
    {
        if (thisIdleWatcher.secondsLeft<=0)
        {
            window.location.href="/alert/status";
        }
        else
        {
            thisIdleWatcher.secondsLeft--;
        }
    }
}

var thisIdleWatcher:IdleWatcher;

window.onload=()=>
{
    thisIdleWatcher=new IdleWatcher(600,30,120,300);
}

