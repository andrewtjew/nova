package org.nova.logsearch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.nova.parsing.Enclosure;
import org.nova.parsing.ExpressionNode;
import org.nova.parsing.ExpressionParser;
import org.nova.parsing.TextSource;
import org.nova.parsing.Token;
import org.nova.parsing.Tokenizer;
import org.nova.parsing.Tokenizer.Configuration;

public class SearchExpressionParser extends ExpressionParser
{
    public SearchExpressionParser()
    {
        super(",",precedenceLevels(),new String[] {"+","-","not"},null,new Enclosure[] {Enclosure.PARENTHESIS,Enclosure.BRACKETS});
    }

    static Map<String,Integer> precedenceLevels()
    {
        Map<String,Integer> precedenceLevels=new java.util.LinkedHashMap<>();
        
        int level=0;
        //comma operators
        precedenceLevels.put(",",level);
        level++;

        //assignment operators
        level++;
        
        //ternary operators
        level++;

        //logical or
        precedenceLevels.put("or",level);
        level++;
        
        //logical and
        precedenceLevels.put("and",level);
        level++;
        
        //bitwise or
        level++;

        //bitwise xor
        level++;
        
        //bitwise and
        level++;

        //equality operators
        precedenceLevels.put("!=",level);
        precedenceLevels.put("==",level);
        level++;
        
        //relational operators
        precedenceLevels.put("=>",level);
        precedenceLevels.put("<=",level);
        precedenceLevels.put(">",level);
        precedenceLevels.put("<",level);
        level++;

        //shift operators
        level++;
        
        //additive operators
        precedenceLevels.put("+",level);
        precedenceLevels.put("-",level);
        level++;

        //multiplicative operators
        precedenceLevels.put("/",level);
        precedenceLevels.put("*",level);
        level++;

        //special operators
        precedenceLevels.put("contains",level);
        level++;

        //prefix operators
        precedenceLevels.put("not",level);
        level++;

        //postfix operators
        level++;
        
        
        return precedenceLevels;
    }
    
    public ExpressionNode parse(String text) throws Throwable
    {
        Configuration configuration=new Configuration();
        configuration.caseSensitive=false;
        configuration.includeEndOfLine=true;
        configuration.includeWhiteSpaceTokens=false;
        configuration.allowinUnsignedIntegers=false;
        configuration.useSingleQuoteStrings=false;
        configuration.useDoubleQuoteStrings=true;

        configuration.commentEnclosures=null;
        configuration.punctuators=new String[]{"(",")","[","]"};
        configuration.operators=new String[]{"==","!=","<",">","<=",">=","and","or","contains","+","-","*","/","not",","};
        configuration.keywords=new String[]{"category","created","message","exception","trace","duration","wait","ancestors","fromLink","toLink","exceptionMessage","item"};                 
        
        TextSource textSource=new TextSource(text);
        Tokenizer tokenizer=new Tokenizer(textSource,configuration);
        var tokens=tokenizer.tokenize();
        for (Token token:tokens)
        {
            System.out.print(token.toString()+"|");
        }
        System.out.println();
        return parse(tokens);
        
    }
}
