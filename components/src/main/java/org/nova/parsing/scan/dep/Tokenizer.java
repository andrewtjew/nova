//package org.nova.parsing.scan;
//
//import java.time.ZonedDateTime;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//
//
//abstract public class Tokenizer
//{
//    class OperatorTree
//    {
//        private HashMap<Character,OperatorTree> leaves;
//        private boolean terminator; 
//        
//        public OperatorTree()
//        {
//            this.leaves=null;
//        }
//
//        public boolean isTerminator()
//        {
//            return terminator;
//        }
//        public void setAsTerminator()
//        {
//            this.terminator=true;
//        }
//        public OperatorTree getLeave(char c)
//        {
//            if (this.leaves==null)
//            {
//                return null;
//            }
//            if (caseSensitive==false)
//            {
//                if ((c>='A')&&(c<='Z'))
//                {
//                    c=(char)(c+'a'-'A');
//                }
//            }
//            return this.leaves.get(c);
//        }
//
//        public void add(String s)
//        {
//            if (caseSensitive==false)
//            {
//                s=s.toLowerCase();
//            }
//            build(s.toCharArray(),0);
//        }
//        
//        private void build(char[] array,int index)
//        {
//            char c=array[index];
//            if (this.leaves==null)
//            {
//                this.leaves=new HashMap<>();
//            }
//            OperatorTree childNode=this.leaves.get(c);
//            if (childNode==null)
//            {
//                childNode=new OperatorTree();
//                this.leaves.put(c, childNode);
//            }
//            if (index<array.length-1)
//            {
//                childNode.build(array, index+1);;
//            }
//            else
//            {
//                childNode.setAsTerminator();
//            }
//        }
//
//        public Lexeme getOperator(char first) throws Throwable
//        {
//            OperatorTree child;
//            for (OperatorTree node=this.getLeave(first);node!=null;node=child)
//            {
//                char c=source.next();
//                child=node.getLeave(c);
//                if (child==null)
//                {
//                    if (node.isTerminator())
//                    {
//                        if (Character.isLetter(first)==false)
//                        {
//                            var snippet=source.endAndGetSnippet(0);
//                            return new Lexeme(Token.OPERATOR,snippet);
//                        }
//                        if (Character.isLetter(c)==false)
//                        {
//                            var snippet=source.endAndGetSnippet(0);
//                            return new Lexeme(Token.OPERATOR,snippet);
//                        }
//                    }
//                }
//            }
//            return null;
//        }
//        
//    }
//    
//    final private HashSet<Character> punctuators;
//    final private HashSet<String> keywords;
//    final private boolean caseSensitive;
//    final private Source source;
//    
//    private int position;
//    private int mark;
//    private boolean ignoreEndOfLine;
//    
//    private ArrayList<Lexeme> lexemes;
//    private ArrayList<Lexeme> comments;
//    private ArrayList<Lexeme> extras;
//    private ArrayList<Lexeme> stream;
//
//    private final OperatorTree operatorTree;
//
//    static record CommentMarkers(String start,String end,boolean nestable)
//    {
//        public CommentMarkers(String start,String end)
//        {
//            this(start,end,false);
//        }
//    }
//    
//    final private Scanner scanner;
//    
//    public Tokenizer(Source source,String[] operators,char[] punctuators,String[] keywords,CommentMarkers[] commentMarkers,boolean caseSensitive,boolean ignoreEndOfLine)
//    {
//        this.ignoreEndOfLine=ignoreEndOfLine;
//        this.source=source;
//        this.punctuators=new HashSet<>();
//        if (punctuators!=null)
//        {
//            for (char separator:punctuators)
//            {
//                this.punctuators.add(separator);
//            }
//        }
//        this.operatorTree=new OperatorTree();
//        if (operators!=null)
//        {
//            for (String operator:operators)
//            {
//                this.operatorTree.add(operator);
//            }
//        }
//        this.caseSensitive=caseSensitive;
//        this.keywords=new HashSet<>();
//        if (keywords!=null)
//        {
//            for (String keyword:keywords)
//            {
//                if (caseSensitive)
//                {
//                    this.keywords.add(keyword);
//                }
//                else
//                {
//                    this.keywords.add(keyword.toLowerCase());
//                }
//            }
//        }
//        this.scanner=new Scanner(source);
//        this.lexemes=new ArrayList<>();
//    }
//    public Lexeme isWord(char c) throws Throwable
//    {
//        if (Character.isJavaIdentifierPart(c)==false)
//        {
//            return null;
//        }
//        for (c=this.source.next();c!=0;c=this.source.next())
//        {
//            if (Character.isJavaIdentifierPart(c)==false)
//            {
//                break;
//            }
//        }
//        Snippet snippet=this.source.endAndGetSnippet(1);
//        return new Lexeme(Token.TEXT, snippet);
//    }
//
//    
//    public void getLexemes() throws Throwable
//    {
//        for (;;)
//        {
//            this.scanner.skipWhiteSpace();
//            char c=this.scanner.begin();
//            if (c==0)
//            {
//                break;
//            }
//            if (this.punctuators.contains(c))
//            {
//                this.lexemes.add(new Lexeme(Token.PUNCTUATOR,String.valueOf(c),null,source.endAndGetSnippet(0)));
//                continue;
//            }
//            {
//                var lexeme=this.operatorTree.getOperator(c);
//                if (lexeme!=null)
//                {
//                    this.lexemes.add(lexeme);
//                    continue;
//                }
//            }
//        }
//    }
//    
//
//}



