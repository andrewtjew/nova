package org.nova.html.ext;

import org.nova.html.StyleBuilder;
import org.nova.html.attributes.BorderStyleRect;
import org.nova.html.attributes.Color;
import org.nova.html.attributes.ColorRect;
import org.nova.html.attributes.Size;
import org.nova.html.attributes.Style;
import org.nova.html.attributes.border_style;
import org.nova.html.attributes.display;
import org.nova.html.attributes.overflow;
import org.nova.html.attributes.position;
import org.nova.html.attributes.unit;
import org.nova.html.elements.GlobalEventTagElement;
import org.nova.html.elements.QuotationMark;
import org.nova.html.tags.div;
import org.nova.html.tags.script;
import org.nova.html.tags.style;

public class BusyServerIndicator extends Content
{
	public BusyServerIndicator(int delay) 
	{
	    ModalSpinner spinner=this.returnAddInner(new ModalSpinner());
	    this.addInner(spinner.style(new Size(5,unit.em)));
        //window.visibilitychange is used to display spinner after delay,onpageshow is used to hide spinner if page is shown using browser back button. 
	    String script="window.visibilitychange=function(){"+spinner.js_hide()+";};window.onbeforeunload=function(){setTimeout(function(){"+spinner.js_show()+";},"+delay+");};window.addEventListener('pageshow',function(event){var traversal=event.persisted||(typeof window.performance!='undefined'&&window.performance.getEntriesByType('navigation')[0].entryType==='back_forward');if(traversal){window.location.reload();}});";
        this.returnAddInner(new script().addInner(script));
	}
	
}
