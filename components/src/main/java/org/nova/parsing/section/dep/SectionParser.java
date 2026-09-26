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
package org.nova.parsing.section.dep;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.nova.lexing.dep.Lexeme;
import org.nova.lexing.dep.Token;

public class SectionParser
{
    final private HashSet<String> startKeywords;
    
    public SectionParser(String[] startKeywords)
    {
        this.startKeywords=new HashSet<>();
        for (String startKeyword:startKeywords)
        {
            this.startKeywords.add(startKeyword);
        }
    }
    public List<Section> parse(int start,int end,List<Lexeme> lexemes)
    {
        ArrayList<Section> sections=new ArrayList<>();
        Lexeme lexeme=lexemes.get(start);
        if (lexeme.getToken()!=Token.KEYWORD)
        {
            return null;
        }        
        int sectionStart=start;
        for (int i=start+1;i<end;i++)
        {
            lexeme=lexemes.get(i);
            if ((lexeme.getToken()==Token.KEYWORD)&&(this.startKeywords.contains((String)lexeme.getValue())))
            {
                sections.add(new Section(sectionStart,i));
                sectionStart=i;
            }
        }
        if (sectionStart<end-1)
        {
            sections.add(new Section(sectionStart,end));
        }
        return sections;
    }
}
