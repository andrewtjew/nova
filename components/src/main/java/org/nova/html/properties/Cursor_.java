/*******************************************************************************
 * Copyright (C) 2017-2019 Kat Fung Tjew
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.nova.html.properties;

public enum Cursor_
{
    alias("alias"),
    all_scroll("all-scroll"),
    auto("auto"),
    cell("cell"),
    col_resize("col-resize"),
    context_menu("context-menu"),
    copy("copy"),
    crosshair("crosshair"),
    default_("default"),
    e_resize("e-resize"),
    ew_resize("ew-resize"),
    grab("grab"),
    grabbing("grabbing"),
    help("help"),
    move("move"),
    n_resize("n-resize"),
    ne_resize("ne-resize"),
    nesw_resize("nesw-resize"),
    ns_resize("ns-resize"),
    nw_resize("nw-resize"),
    nwse_resize("nwse-resize"),
    no_drop("no-drop"),
    none("none"),
    not_allowed("not-allowed"),
    pointer("pointer"),
    progress("progress"),
    row_resize("row-resize"),
    s_resize("s-resize"),
    se_resize("se-resize"),
    sw_resize("sw-resize"),
    text("text"),
    url("url(myBall.cur),auto"),
    w_resize("w-resize"),
    wait("wait"),
    zoom_in("zoom-in"),
    zoom_out("zoom-out")    
    
    ;
    final String value;
    Cursor_(String value)
    {
        this.value=value;
    }
    @Override
    public String toString()
    {
        return this.value;
    }
}
