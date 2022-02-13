package org.nova.html.help;

import org.nova.html.elements.Composer;
import org.nova.html.tags.div;

public class HelperElement extends div
{
    private div topic;
    
    private div bottomOverlay;
    private div rightOverlay;
    private div leftOverlay;
    private div topOverlay;

    public HelperElement()
    {
        addClass("nova-help-helper");
        id("nova-help-helper");
        
        this.bottomOverlay=new div();
        this.bottomOverlay.addClass("nova-help-overlay").id("nova-help-overlay-bottom");

        this.rightOverlay=new div();
        this.rightOverlay.addClass("nova-help-overlay").id("nova-help-overlay-right");

        this.leftOverlay=new div();
        this.leftOverlay.addClass("nova-help-overlay").id("nova-help-overlay-left");
        
        this.topOverlay=new div();
        this.topOverlay.addClass("nova-help-overlay").id("nova-help-overlay-top");
        
        this.topic=returnAddInner(new div());
        this.topic.id("nova-help-topic");
    }
    
    @Override
    public void compose(Composer composer) throws Throwable
    {
        this.topOverlay.compose(composer);
        this.leftOverlay.compose(composer);
        this.bottomOverlay.compose(composer);
        this.rightOverlay.compose(composer);
        super.compose(composer);
    }
}
