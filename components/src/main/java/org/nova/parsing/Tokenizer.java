package org.nova.parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/*
 * Extension checklist:
 * When adding optional token types, use addOptionalToken() to add tokens to the list. 
 * This method checks if the token should be included based on the configuration and also includes error tokens.
*/
public class Tokenizer
{
    final private Source source;
    final private HashSet<String> punctuators;
    final private HashSet<String> operators;
    final private HashSet<String> keywords;
    final private HashMap<String,Enclosure> commentEnclosures;
    final private Configuration configuration;
    final private int maximumOpenCommentLength;
    final private HashSet<Character> escapeCharacters;

    /**
     * single quoted strings not tested.
     */
    
    public static class Configuration
    {
        public boolean caseSensitive=true;
        public boolean includeEndOfLine=false;
        public boolean includeWhiteSpaceTokens=false;
        public boolean includeCommentTokens=false;
        public boolean allowUnsignedIntegers=false;
        public boolean allowLongIntegers=true;
        public boolean allowFloatAndDoubles=true;
        public boolean useSingleQuoteStrings=false; //if true includeCharacterTypes is ignored and characters are treated as strings
        public boolean useDoubleQuoteStrings=true;
        
        public String[] punctuators;
        public String[] operators;
        public String[] keywords;
        public Enclosure[] commentEnclosures;
        
        static public Configuration javaConfiguration()
        {
            Configuration configuration=new Configuration();
            configuration.caseSensitive=true;
            configuration.includeEndOfLine=false;
            configuration.includeWhiteSpaceTokens=false;
            configuration.includeCommentTokens=false;
            configuration.allowUnsignedIntegers=false;
            configuration.allowLongIntegers=true;
            configuration.allowFloatAndDoubles=true;
            configuration.useSingleQuoteStrings=false;
            configuration.useDoubleQuoteStrings=true;

            configuration.commentEnclosures=new Enclosure[]{new Enclosure("//","\n",false),new Enclosure("/*","*/",true)};
            configuration.punctuators=new String[]{"(",")","{","}","[","]",";",",",".","...","@",":","::","->"};
            configuration.operators=new String[]{"+","-","*","/","%","+","-","++","--","==","!=","<",">","<=",">=","&&","||","!","&","|","^","~","<<",">>","<<<",">>>"};
            configuration.keywords=new String[]{"abstract","assert","boolean","break","byte","case","catch","char","class","const","continue","default","do","double","else","enum","extends","final","finally","float","for","goto","if","implements","import","instanceof","int","interface","long","native","new","package","private","protected","public","return","short","static","strictfp","super","switch","synchronized","this","throw","throws","transient","try","void","volatile"};
            return configuration;
        }

    }
    
    public Tokenizer(Source source, Configuration configuration)
    {
        this.source = source;
        this.configuration = configuration;
        this.punctuators = new HashSet<>();
        if (configuration.punctuators != null)
        {
            for (String punctuator : configuration.punctuators)
            {
                this.punctuators.add(punctuator);
            }
        }
        this.operators = new HashSet<>();
        if (configuration.operators != null)
        {
            for (String operator : configuration.operators)
            {
                this.operators.add(operator);
            }
        }
        this.keywords = new HashSet<>();
        if (configuration.keywords != null)
        {
            for (String keyword : configuration.keywords)
            {
                if (this.configuration.caseSensitive)
                {
                    this.keywords.add(keyword);
                }
                else
                {
                    this.keywords.add(keyword.toLowerCase());
                }
            }
        }
        int maximumOpenCommentLength=0;
        this.commentEnclosures=new HashMap<>();
        if (configuration.commentEnclosures!=null)
        {
            for (var commentEncloser:this.configuration.commentEnclosures)
            {
                this.commentEnclosures.put(commentEncloser.open(),commentEncloser);
                if (commentEncloser.open().length()>maximumOpenCommentLength)
                {
                    maximumOpenCommentLength=commentEncloser.open().length();
                }
            }
        }
        
        this.escapeCharacters=new HashSet<>();
        this.escapeCharacters.add('t');
        this.escapeCharacters.add('b');
        this.escapeCharacters.add('n');
        this.escapeCharacters.add('r');
        this.escapeCharacters.add('f');
        this.escapeCharacters.add('\'');
        this.escapeCharacters.add('"');
        this.escapeCharacters.add('\\');
        

        
        this.maximumOpenCommentLength=maximumOpenCommentLength;
    }

    static void addOptionalToken(List<Token> tokens, boolean add, Token token)
    {
        if (add || token.getType() == TokenType.ERROR)
        {
            tokens.add(token);
        }
    }
    
    public List<Token> tokenize() throws Throwable
    {
        ArrayList<Token> tokens = new ArrayList<>();
        for (;;)
        {
            this.source.begin(0);
            char c = this.source.next();
            if (c == 0)
            {
                break;
            }
            if (c == '\n')
            {
                var snippet=this.source.endAndGetSnippet(0);
                if (this.configuration.includeEndOfLine)
                {
                    Token token = new Token(TokenType.END_OF_LINE, snippet);
                    tokens.add(token);
                }
                continue;
            }
            if (c == '\r')
            {
                c = this.source.next();
                if (c != '\n')
                {
                    this.source.back(1);
                }
                var snippet=this.source.endAndGetSnippet(0);
                if (this.configuration.includeEndOfLine)
                {
                    Token token = new Token(TokenType.END_OF_LINE, snippet);
                    tokens.add(token);
                }
                continue;
            }
            if (Character.isWhitespace(c))
            {
                Token token = this.produceWhiteSpace();
                addOptionalToken(tokens, this.configuration.includeWhiteSpaceTokens, token);
                continue;
            }
            if (configuration.useSingleQuoteStrings)
            {
                if (c=='\'')
                {
                    var token=produceString('\'');
                    tokens.add(token);
                    continue;
                }
            }
            else
            {
                if (c=='\'')
                {
                    var token=produceCharacter();
                    tokens.add(token);
                    continue;
                }
            }
            if (configuration.useDoubleQuoteStrings)
            {
                if (c=='"')
                {
                    var token=produceString('"');
                    tokens.add(token);
                    continue;
                }
            }
            if (isDigit(c))
            {
                char c2=source.next();
                Token token = this.produceNumber(c,c2);
                tokens.add(token);
                continue;
            }
            if (c=='.')
            {
                char c2=source.next();
                if (isDigit(c2))
                {
                    Token token = this.produceNumber(c,c2);
                    tokens.add(token);
                    continue;
                }
                source.back(1);
            }
            if (this.punctuators.contains(c))
            {
                tokens.add(new Token(TokenType.PUNCTUATOR,this.source.endAndGetSnippet(0)));
                continue;
            }
            if (Character.isJavaIdentifierStart(c))
            {
                Token token=produceWordToken();
                tokens.add(token);
                continue;
            }
            {
                var token=detectComment(c);
                if (token!=null)
                {
                    addOptionalToken(tokens, this.configuration.includeCommentTokens, token);
                    continue;
                }
            }
            
            Token token = this.produceOperatorOrPunctuator(c);
            tokens.add(token);
        }
        return tokens;
    }
    private Token produceOperatorOrPunctuator(char c) throws Throwable
    {
        StringBuilder sb=new StringBuilder();
        sb.append(c);
        for (c=this.source.next();(c!=0)&&(!Character.isLetterOrDigit(c)&&(!Character.isWhitespace(c)));c=this.source.next())
        {
            sb.append(c);
        }
        for (String target=sb.toString();target.length()>0;target=target.substring(0,target.length()-1))
        {
            if (this.punctuators.contains(target))
            {
                return new Token(TokenType.PUNCTUATOR,this.source.endAndGetSnippet(sb.length()-target.length()+1));
            }
            if (this.operators.contains(target))
            {
                return new Token(TokenType.OPERATOR,this.source.endAndGetSnippet(sb.length()-target.length()+1));
            }
        }
        Snippet snippet=this.source.endAndGetSnippet(1);
        return new Token(TokenType.ERROR, snippet,"Unrecognized symbols.",this.source.getIndex());
    }
    
    private Token detectComment(char c) throws Throwable
    {
        if (this.maximumOpenCommentLength>0)
        {
            StringBuilder sb=new StringBuilder();
            sb.append(c);
            for (;;)
            {
                String target=sb.toString();
                var commentEncloser=this.commentEnclosures.get(target);
                if (commentEncloser!=null)
                {
                    return produceComment(commentEncloser);
                }
                if (sb.length()==this.maximumOpenCommentLength)
                {
                    this.source.revert();
                    return null;
                }
                c=this.source.next();
                sb.append(c);
            }
        }
        return null;
    }    
    private Token produceWordToken() throws Throwable
    {
        for (char c=this.source.next();Character.isJavaIdentifierPart(c)&&(c!=0);c=this.source.next())
        {
        }
        Snippet snippet=this.source.endAndGetSnippet(1);
        String word=snippet.getTarget();
        if (this.configuration.caseSensitive==false)
        {
            word=word.toLowerCase();
        }
        if ("true".equals(word)||"false".equals(word))
        {
            return new Token(TokenType.NUMBER,snippet,NumericType.BOOLEAN,null);
        }
        if (this.operators.contains(word))
        {
            return new Token(TokenType.OPERATOR,snippet);
        }
        if (this.keywords.contains(word))
        {
            return new Token(TokenType.KEYWORD,snippet);
        }
        return new Token(TokenType.IDENTIFIER,snippet);
    }
    
    private Token produceWhiteSpace() throws Throwable
    {
        for (char c=this.source.next();c!=0;c=this.source.next())
        {
            if (Character.isWhitespace(c)==false)
            {
                if (c!=65279) //BOM or zero width space
                {
                    break;
                }
            }
        }
        Snippet snippet=this.source.endAndGetSnippet(1);
        return new Token(TokenType.WHITESPACE,snippet);
    }
    static private boolean isDigit(char c)
    {
        return c>='0'&&c<='9';
    }
    static private boolean isHexadecimalDigit(char c)
    {
        if ((c>='0')&&(c<='9'))
        {
            return true;
        }
        if ((c>='a')&&(c<='f'))
        {
            return true;
        }
        if ((c>='A')&&(c<='F'))
        {
            return true;
        }
        return false;
    }
    static private boolean isOctalDigit(char c)
    {
        return c>='0'&&c<='7';
    }
    static private boolean isBinaryDigit(char c)
    {
        return c>='0'&&c<='1';
    }
    
    Token produceNumberErrorToken(String message) throws Throwable
    {
        int index=this.source.getIndex();
        for (char c=this.source.next();Character.isLetterOrDigit(c);c=this.source.next())
        {
        }
        return new Token(TokenType.ERROR, this.source.endAndGetSnippet(1),message,index);
        
    }
    Token produceNumber(char c,char second) throws Throwable
    {
        if (c=='0')
        {
            if (second=='.')
            {
                return produceFloatingPointNumber(c);
            }
            if ((second=='x')||(second=='X'))
            {
                return produceHexadecimalNumber();
            }
            if ((second=='b')||(second=='B'))
            {
                return produceBinaryNumber();
            }
            if (isOctalDigit(second))
            {
                return produceOctalNumber();
            }
            if (isDigit(second))
            {
                return produceNumberErrorToken("Invalid number.");
            }
        }
        boolean underscore=false;
        for (c=second;c!=0;c=this.source.next())
        {
            if (isDigit(c))
            {
                underscore=false;
                continue;
            }
            if (c=='.')
            {
                if (underscore==true)
                {
                    return produceNumberErrorToken("Invalid number.");
                }
                return produceFloatingPointNumber(c);
            }
            if ((c=='f')||(c=='F')||(c=='d')||(c=='D'))
            {
                if (underscore==true)
                {
                    return produceNumberErrorToken("Invalid number.");
                }
                return produceFloatingPointNumberSuffix(c);
            }
            if (c=='e'||c=='E')
            {
                if (underscore==true)
                {
                    return produceNumberErrorToken("Invalid number.");
                }
                return produceFloatingPointNumberWithExponent();
            }
            if (c=='_')
            {
                underscore=true;
                continue;
            }
            break;
        }
        if (underscore==true)
        {
            source.back(1);
            return produceNumberErrorToken("Invalid number.");
        }
        return produceIntegerSuffix(c,NumericType.INTEGER);
    }
    Token produceOctalNumber() throws Throwable
    {
        boolean underscore=false;
        char c=this.source.next();
        for (;c!=0;c=this.source.next())
        {
            if (isOctalDigit(c))
            {
                underscore=false;
                continue;
            }
            if (c=='_')
            {
                underscore=true;
                continue;
            }
            break;
        }
        if (underscore==true)
        {
            return produceNumberErrorToken("Invalid octal number.");
        }
        if (isDigit(c))
        {
            return produceNumberErrorToken("Invalid octal number.");
        }
        return produceIntegerSuffix(c,NumericType.OCTAL_INTEGER);
    }
    Token produceHexadecimalNumber() throws Throwable
    {
        boolean underscore=false;
        char c=this.source.next();
        for (;c!=0;c=this.source.next())
        {
            if (isHexadecimalDigit(c))
            {
                underscore=false;
                continue;
            }
            if (c=='_')
            {
                underscore=true;
                continue;
            }
            break;
        }
        if (underscore==true)
        {
            return produceNumberErrorToken("Invalid hexadecimal number.");
        }
        return produceIntegerSuffix(c,NumericType.HEXADECIMAL_INTEGER);
    }
    Token produceBinaryNumber() throws Throwable
    {
        boolean underscore=false;
        char c=this.source.next();
        for (;c!=0;c=this.source.next())
        {
            if (isBinaryDigit(c))
            {
                underscore=false;
                continue;
            }
            if (c=='_')
            {
                underscore=true;
                continue;
            }
            break;
        }
        if (underscore==true)
        {
            return produceNumberErrorToken("Invalid binary number.");
        }
        if (isDigit(c))
        {
            return produceNumberErrorToken("Invalid binary number.");
        }
        return produceIntegerSuffix(c,NumericType.BINARY_INTEGER);
    }
    Token produceIntegerSuffix(char c,NumericType numericType) throws Throwable
    {
        IntegerSize integerSize=IntegerSize.INTEGER;
        if (this.configuration.allowLongIntegers)
        {
            if (c=='l'||c=='L')
            {
                c=this.source.next();
                integerSize=IntegerSize.LONG;
                if (this.configuration.allowUnsignedIntegers)
                {
                    if (c=='u'||c=='U')
                    {
                        integerSize=IntegerSize.UNSIGNED_LONG;
                        c=this.source.next();
                    }
                }
            }
        }
        if (this.configuration.allowUnsignedIntegers)
        {
            if (c=='u'||c=='U')
            {
                integerSize=IntegerSize.UNSIGNED_INTEGER;
                c=this.source.next();
                if (this.configuration.allowLongIntegers)
                {
                    if (c=='l'||c=='L')
                    {
                        integerSize=IntegerSize.UNSIGNED_LONG;
                        c=this.source.next();
                    }
                }
            }
        }
        if (Character.isLetter(c))
        {
            return produceNumberErrorToken("Invalid number suffix.");
        }
        return new Token(TokenType.NUMBER,this.source.endAndGetSnippet(1),numericType,integerSize);
    }
    Token produceFloatingPointNumberSuffix(char c) throws Throwable
    {
        NumericType numericType=NumericType.DOUBLE;
        if (configuration.allowFloatAndDoubles)
        {
            if (c=='f'||c=='F')
            {
                c=this.source.next();
                numericType=NumericType.FLOAT;
            }
            if (c=='d'||c=='D')
            {
                c=this.source.next();
            }
        }
        if (Character.isLetter(c))
        {
            return produceNumberErrorToken("Invalid number suffix.");
        }
        return new Token(TokenType.NUMBER,this.source.endAndGetSnippet(1),numericType,null);
    }

    Token produceFloatingPointNumber(char c) throws Throwable
    {
        if (c=='.')
        {
            c=this.source.next();
            if (c=='f'||c=='F'||c=='d'||c=='D')
            {
                return produceFloatingPointNumberSuffix(c);
            }
            source.back(1);
        }
        for (c=this.source.next();c!=0;c=this.source.next())
        {
            if ((c=='e')||(c=='E'))
            {
                return produceFloatingPointNumberWithExponent();
            }
            if (isDigit(c))
            {
                continue;
            }
            break;
        }
        return produceFloatingPointNumberSuffix(c);
    }
    Token produceFloatingPointNumberWithExponent() throws Throwable
    {
        char c=this.source.next();
        if ((c=='-')||(c=='+'))
        {
            c=this.source.next();
        }
        if (isDigit(c)==false)
        {
            return produceNumberErrorToken("Invalid floating point exponent.");
        }
        boolean underscore=false;
        for (c=this.source.next();c!=0;c=this.source.next())
        {
            if (isDigit(c))
            {
                underscore=false;
                continue;
            }
            if (c=='_')
            {
                underscore=true;
                continue;
            }
            break;
        }
        if (underscore==true)
        {
            return produceNumberErrorToken("Invalid number.");
        }
        if (isDigit(c))
        {
            return produceNumberErrorToken("Invalid number.");
        }
        return produceFloatingPointNumberSuffix(c);
    }
    Token produceStringErrorToken(String message) throws Throwable
    {
        int index=this.source.getIndex();
        for (char c=this.source.next();(c!=0)&&(c!='\n')&&(c!='"');c=this.source.next())
        {
        }
        return new Token(TokenType.ERROR, this.source.endAndGetSnippet(0),message,index);
    }

    public Token produceString(char delimiter) throws Throwable
    {
//        StringBuilder sb = new StringBuilder();
        for (char c=this.source.next();c!=0;c=this.source.next())
        {
            if (c != '\\')
            {
                if (c == delimiter)
                {
                    return new Token(TokenType.STRING, this.source.endAndGetSnippet(0));
                }
                else if (c == '\n')
                {
                    return produceStringErrorToken("Invalid new line character in string.");
                }
//                sb.append(c);
                continue;
            }
            //Escape cases
            c = this.source.next();
            if (c == 'u')
            {
                for (int i = 0; i < 4; i++)
                {
                    c = this.source.next();
                    if (c==0)
                    {
                        return produceStringErrorToken("Premature end of string.");
                    }
                    if (Character.isDigit(c) || ((c >= 'a') && (c <= 'f')) || ((c >= 'A') && (c <= 'F')))
                    {
                        continue;
                    }
                    return produceStringErrorToken("Invalid unicode escape character in string.");
                }
                continue;
            }
            else if (this.escapeCharacters.contains(c))
            {
                continue;
            }
            return produceStringErrorToken("Invalid escape character in string.");
        }
        return produceStringErrorToken("Premature end of string.");
    }
    Token produceCharacterErrorToken(String message) throws Throwable
    {
        int index=this.source.getIndex();
        for (char c=this.source.next();(c!=0)&&(c!='\n')&&(c!='\'');c=this.source.next())
        {
        }
        return new Token(TokenType.ERROR, this.source.endAndGetSnippet(0),message,index);
    }
    public Token produceCharacter() throws Throwable
    {
//        char c='\aadfasdsa char t; char t=5;
        char endCharacter='\'';
        char c=this.source.next();
        if (c != '\\')
        {
            c=this.source.next();
            if (c == endCharacter)
            {
                return new Token(TokenType.CHARACTER, this.source.endAndGetSnippet(0));
            }
            return produceCharacterErrorToken("Invalid character.");
        }
        //Escape cases
        c = this.source.next();
        if (c == 'u')
        {
            StringBuilder unicode=new StringBuilder();
            for (int i = 0; i < 4; i++)
            {
                c = this.source.next();
                if (c==0)
                {
                    return produceCharacterErrorToken("Premature end of chracter.");
                }
                if (Character.isDigit(c) || ((c >= 'a') && (c <= 'f')) || ((c >= 'A') && (c <= 'F')))
                {
                    unicode.append(c);
                    continue;
                }
                return produceCharacterErrorToken("Invalid unicode escape character.");
            }
        }
        else if ((c == 'r')||(c == 'n')||(c == '\\')||(c == 't')||(c == '\''))
        {
        }
        else
        {
            return produceCharacterErrorToken("Invalid escape character.");
        }
        c=this.source.next();
        if (c == endCharacter)
        {
            return new Token(TokenType.CHARACTER, this.source.endAndGetSnippet(0));
        }
        return produceCharacterErrorToken("Invalid character.");
    }
    private Token produceComment(Enclosure commentEnclosure) throws Throwable
    {
        if (commentEnclosure.nestable())
        {
            source.set(commentEnclosure.open().length());
        }
        int level=1; //From the first comment enclosure
        String start=commentEnclosure.open();
        String end=commentEnclosure.close();
        
        for (char c=this.source.next();c!=0;c=this.source.next())
        {
            if (commentEnclosure.nestable())
            {
                if (c==start.charAt(0))
                {
                    boolean match=true;
                    for (int i=1;i<start.length();i++)
                    {
                        c=this.source.next();
                        if (c!=start.charAt(i))
                        {
                            match=false;
                            break;
                        }
                    }
                    if (match)
                    {
                        level++;
                    }
                    continue;
                }
            }
            if (c==end.charAt(0))
            {
                boolean match=true;
                for (int i=1;i<end.length();i++)
                {
                    c=this.source.next();
                    if (c!=commentEnclosure.close().charAt(i))
                    {
                        match=false;
                        break;
                    }
                }
                if (match)
                {
                    level--;
                    if (level==0)
                    {
                        return new Token(TokenType.COMMENT, this.source.endAndGetSnippet(0));
                    }
                }
                continue;
            }
        }
        
        if (commentEnclosure.nestable())
        {
            return new Token(TokenType.ERROR, this.source.endAndGetSnippet(1),"Premature end of comment.",this.source.getIndex());
        }
        return new Token(TokenType.COMMENT, this.source.endAndGetSnippet(1));
    }
    
    public static void test() throws Throwable
    {
        for (;;)
        {
            String text="1f 1F 2d 2D 12f 12.f 12. 12.3f 12e4 12.3e-4f 12.3e+4f 12.3e4f .5 0.5e6 0.5f 0. 0.f .3e4f 0x1Fa2 0x1L 0b11 0b0L 012 012l 5 0 000 0ul 1ul 1lu 0_1 012_000 0_12u 12_33.e5f 12_4.3e5_6 77777";
            text="5ulg 12.g 0_ 1_ 1_f 12_.f 12_e5 12e+-5 0xFgFF 0X3g 099 0b2 12g 12_4.3ul"; //error cases 
            text="a+ b ==3 + (5+3) and (3) created";
//            text="a";
            //text="1_3.e+5";
            
            text="'a' '\\'' '\\u1234' '\\n' '\\r' '\\t' '\\\\'";
       //     text="'ab' '\\cde' '\\a' '\\u123' '\\'"; //error cases

            text="\"a'bc\" \"a\\\"bc\" \"x\\\"y\" \"a\\\\bc\" \"e\\nfg\" \"e\\rfg\" \"a\\tbc\" \"a\\u1234bc\" ";
//            text="\"a\\xbc\"  \"a\\u124xc\" "; //error cases
            

            text="/*//1/*2*/3/*\n*/*/ //ab\n//cd";
            text="/* /* */ ab"; //error cases
            
            text="((5))";
            
            System.out.println("text=["+text+"]");
            TextSource source=new TextSource(text);
            var configuration=new Tokenizer.Configuration();
            configuration.allowUnsignedIntegers=true;
            configuration.includeWhiteSpaceTokens=true;
            configuration.includeCommentTokens=true;
            configuration.operators=new String[]{"+","-","==","!=",">",">=","<","<=","and","or"};
            configuration.punctuators=new String[]{"(",")","[","]",",","."};
            configuration.keywords=new String[]{"number","logLevel","category","created","message","exception","trace","duration","wait","ancestors","fromLink","toLink","exceptionMessage","null","entry","key","value"};
            configuration.commentEnclosures=new Enclosure[]{Enclosure.SINGLE_LINE_COMMENT,Enclosure.MULTI_LINE_COMMENT};
            
            Tokenizer tokenizer=new Tokenizer(source,configuration);
            
            var tokens=tokenizer.tokenize();
            for (var token:tokens)
            {
  //              if (token.getType()==TokenType.ERROR)
                {
                    System.out.print(token.getType()+": ["+token.getSnippet().getTarget()+"]"+" nummeric="+token.getNumericType()+", message="+token.getMessage());
                    int errorIndex=token.getSourceIndex();
                    String errorPosition=text.substring(0,errorIndex)+"^"+text.substring(errorIndex);
                    System.out.println(" position: "+errorPosition+", target="+token.getSnippet().getTarget());
                    continue;
                }
//                System.out.println(token.getType()+": ["+token.getSnippet().getTarget()+"]"+" nummeric="+token.getNumericType()+", integerSize="+token.getIntegerSize());
            }
            System.out.println("------------------");
        }        
    }
    
}
