/*******************************************************************************
 * Talisman Kore © 2017 Evolve Softworks
 *******************************************************************************/
package org.nova.html.bootstrap.localization;

import org.nova.html.bootstrap.BootStrapPage;
import org.nova.html.bootstrap.NavbarTogglerButton;
import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.NavItemLink;
import org.nova.html.bootstrap.Navbar;
import org.nova.html.bootstrap.NavbarBrand;
import org.nova.html.bootstrap.NavbarCollapse;
import org.nova.html.bootstrap.NavbarNav;
import org.nova.html.bootstrap.classes.BreakPoint;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.FW;
import org.nova.html.bootstrap.classes.Justify;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.bootstrap.ext.Icon;
import org.nova.html.enums.link_rel;
import org.nova.html.enums.target;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.tags.link;
import org.nova.html.tags.script;


public class Page extends BootStrapPage
{
    private Navbar navbar;
    private Item content;

    
    public Page() throws Throwable
    {
        String title="StringHandleEditor";
        head().title(title);

        head().addInner(new link().rel(link_rel.stylesheet).href("/resources/html/css/editor.css"));
        head().addInner(new script().src("/resources/html/js/nova/remote.js"));
        head().addInner(new script().src("/resources/html/js/nova/nova-handle.js"));
        head().addInner(new script().src("https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"));

        this.navbar=body().returnAddInner(new Navbar()).expand(BreakPoint.sm);       
        this.navbar.color(StyleColor.dark).bg(StyleColor.dark).px(2).py(0);
        
        NavbarBrand brand=navbar.returnAddInner(new NavbarBrand(title));
        brand.onclick(HtmlUtils.js_location(""));
        brand.style("cursor:pointer;");
        brand.py(0).my(0).fw(FW.bold);
        
        NavbarTogglerButton navButton=navbar.returnAddInner(new NavbarTogglerButton());
        NavbarCollapse navBarCollapse=navbar.returnAddInner(new NavbarCollapse(navButton));
        NavbarNav nav=navBarCollapse.returnAddInner(new NavbarNav());
        nav.mx(0).d(Display.flex).w(100).justify_content(Justify.end);
        {

            nav.returnAddInner(new NavItemLink()).addInner(new Icon(Icons.LANGUAGE)+" Language").href("/StringHandleEditor/viewByLanguages").text(StyleColor.light).title("View by language");
            nav.returnAddInner(new NavItemLink()).addInner(new Icon(Icons.HANDLES)+" Handles").href("/StringHandleEditor/viewByHandles").text(StyleColor.light).title("View by handles");
            nav.returnAddInner(new NavItemLink()).addInner(new Icon(Icons.ADD)).addInner(" Language").href("/StringHandleEditor/addLanguage").text(StyleColor.light).title("Add language");
            nav.returnAddInner(new NavItemLink()).addInner(new Icon(Icons.ADD)).addInner(" Handle").href("/StringHandleEditor/addHandle").text(StyleColor.light).title("Add handle");
            nav.returnAddInner(new NavItemLink()).addInner(new Icon(Icons.ADD)).addInner(" Enum").href("/StringHandleEditor/addEnum").text(StyleColor.light).title("Add all enum constants");
            nav.returnAddInner(new NavItemLink()).addInner(new Icon(Icons.UNDEFINED)+" Undefines").href("/StringHandleEditor/nextUndefined").text(StyleColor.warning).title("Check for undefined handles");

            nav.returnAddInner(new NavItemLink()).addInner(new Icon(Icons.DOCUMENTATION)).href("https://docs.oracle.com/javase/7/docs/api/java/util/Formatter.html").target(target._blank).text(StyleColor.light).title("Link to Format string documentation");
            nav.returnAddInner(new NavItemLink()).addInner(new Icon(Icons.SETTINGS)).href("/StringHandleEditor/settings").text(StyleColor.light).title("Language and enum settings").ms_auto();
        }
        this.content=new Item();
        body().addInner(content);
    }
  
    public StyleComponent<?> content()
    {
        return this.content;
    }

    
    
}
