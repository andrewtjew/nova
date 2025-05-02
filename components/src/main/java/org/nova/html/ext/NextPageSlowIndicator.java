package org.nova.html.ext;

import org.nova.html.properties.Size;
import org.nova.html.properties.unit;
import org.nova.html.tags.script;

public class NextPageSlowIndicator extends Content
{
	public NextPageSlowIndicator(int delay) 
	{
	    ModalSpinner spinner=this.returnAddInner(new ModalSpinner());
	    this.addInner(spinner.style(new Size(5,unit.em)));
        //window.visibilitychange is used to display spinner after delay,onpageshow is used to hide spinner if page is shown using browser back button. 
	    String script="window.visibilitychange=function(){"+spinner.js_hide()+";};window.onbeforeunload=function(){setTimeout(function(){"+spinner.js_show()+";},"+delay+");};window.addEventListener('pageshow',function(event){var traversal=event.persisted||(typeof window.performance!='undefined'&&window.performance.getEntriesByType('navigation')[0].entryType==='back_forward');if(traversal){window.location.reload();}});";
        this.returnAddInner(new script().addInner(new LiteralHtml(script)));
	}
	
}
