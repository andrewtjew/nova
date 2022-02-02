/*******************************************************************************
 * Talisman Kore © 2017 Evolve Softworks
 *******************************************************************************/
package org.nova.html.bootstrap.localization;

import org.nova.html.bootstrap.BootStrapPage;
import org.nova.html.bootstrap.Button;
import org.nova.html.bootstrap.Collapse;
import org.nova.html.bootstrap.Styler;
import org.nova.html.bootstrap.NavbarTogglerButton;
import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.bootstrap.Container;
import org.nova.html.bootstrap.DropdownItem;
import org.nova.html.bootstrap.DropdownMenu;
import org.nova.html.bootstrap.Footer;
import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.LinkButton;
import org.nova.html.bootstrap.NavItem;
import org.nova.html.bootstrap.NavItemLink;
import org.nova.html.bootstrap.NavLink;
import org.nova.html.bootstrap.Navbar;
import org.nova.html.bootstrap.NavbarBrand;
import org.nova.html.bootstrap.NavbarCollapse;
import org.nova.html.bootstrap.NavbarNav;
import org.nova.html.bootstrap.classes.BreakPoint;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.FW;
import org.nova.html.bootstrap.classes.Justify;
import org.nova.html.bootstrap.classes.NavbarPlacement;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.bootstrap.classes.Text;
import org.nova.html.bootstrap.ext.Span;
import org.nova.html.enums.link_rel;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.tags.a;
import org.nova.html.tags.div;
import org.nova.html.tags.i;
import org.nova.html.tags.li;
import org.nova.html.tags.link;
import org.nova.html.tags.p;
import org.nova.html.tags.script;
import org.nova.html.tags.title;


public class Page extends BootStrapPage
{
    private Navbar navbar;
    private Item content;

    
    public Page() throws Throwable
    {
        String title="StringHandleEditor";
        head().title(title);

        head().addInner(new link().rel(link_rel.stylesheet).href("/resources/html/css/style.css"));
        head().addInner(new script().src("/resources/html/js/remote.js"));
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
        nav.mx_auto().mx(0).d(Display.flex).w(100);
        {
            this.content=new Item();
//            this.content.mx_auto().d(Display.flex).justify_content(Justify.center);
            body().addInner(content);

            nav.returnAddInner(new NavItemLink()).addInner(new i().addClass("bi-list-ul")+" Language").href("/StringHandleEditor/viewByLanguages").text(StyleColor.light).title("View by language");
            nav.returnAddInner(new NavItemLink()).addInner(new i().addClass("bi-list-ul")+" Handles").href("/StringHandleEditor/name").text(StyleColor.light).title("View by handles");
            nav.returnAddInner(new NavItemLink()).addInner(new i().addClass("bi-plus-circle-fill")).addInner(" Handle").href("/StringHandleEditor/addHandle").text(StyleColor.light).title("Add handle");
            nav.returnAddInner(new NavItemLink()).addInner(new i().addClass("bi-plus-circle-fill")).addInner(" Enum").href("/StringHandleEditor/addEnum").text(StyleColor.light).title("Add all enum constants");
            nav.returnAddInner(new NavItemLink()).addInner(new i().addClass("bi-question-diamond-fill")+" Undefines").href("/StringHandleEditor/checkHandles").text(StyleColor.warning).title("Check for undefined handles");
        }
    }
  
    public StyleComponent<?> content()
    {
        return this.content;
    }

    
    
}
