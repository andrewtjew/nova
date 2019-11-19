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
package org.nova.html;

import java.util.ArrayList;

import org.nova.core.Pair;
import org.nova.parsing.scan.Lexeme;
import org.nova.parsing.scan.Scanner;
import org.nova.parsing.scan.TextSource;

public class Accept
{
    static class QValue extends Pair<Float,String>
    {
        public QValue(Float key, String value)
        {
            super(key, value);
        }
        
    }
    public Accept(String value) throws Throwable
    {
        ArrayList<QValue> list=new ArrayList<>();
        Scanner scanner=new Scanner(new TextSource(value));
        for (;;)
        {
            char c=scanner.skipWhiteSpaceAndBegin();
            if (c==0)
            {
                break;
            }
            Lexeme lexeme=scanner.produceTerminatedText(',',';');
            String type=lexeme.getValue();
            c=scanner.skipWhiteSpaceAndBegin();
            float q=1;
            if (c==';')
            {
                c=scanner.skipWhiteSpaceAndBegin();
                if (c!='q')
                {
                    throw new Exception("q expected");
                }
                c=scanner.skipWhiteSpaceAndBegin();
                if (c!='=')
                {
                    throw new Exception("= expected");
                }
                lexeme=scanner.produceNumber(true);
            }
                
            
        }
            
    }
}
