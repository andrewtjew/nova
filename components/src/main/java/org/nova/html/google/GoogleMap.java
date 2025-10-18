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
package org.nova.html.google;

import java.util.ArrayList;

import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.properties.Length_;
import org.nova.html.properties.Style;
import org.nova.html.tags.div;
import org.nova.html.tags.script;

public class GoogleMap extends Element
{
    final div div;
    final double lattitude;
    final double longtitude;
    final double zoom;
    final ArrayList<GoogleMapCircle> circles;
    
    public static script api_script(String key)
    {
    	return new script().async().src("https://maps.googleapis.com/maps/api/js?callback=initMap&key="+key);
    }
    
    public GoogleMap(Length_ width,Length_ height,double lattitude,double longtitude,double zoom)
    {
        this.div=new div();
        this.div.id();
        div.style(new Style().width(width).height(height));
        this.lattitude=lattitude;
        this.longtitude=longtitude;
        this.zoom=zoom;
        this.circles=new ArrayList<>();
    }
    
    public void add(GoogleMapCircle circle)
    {
        this.circles.add(circle);
    }

    @Override
    public void compose(Composer composer) throws Throwable
    {
        composer.compose(this.div);
    }
    

}
