package org.nova.parsing;

public record Enclosure(String open,String close,boolean nestable)
{
    static public Enclosure PARENTHESIS=new Enclosure("(",")",true);
    static public Enclosure BRACKETS=new Enclosure("[","]",true);
    static public Enclosure BRACES=new Enclosure("{","}",true);
    static public Enclosure ANGLE_BRACKETS=new Enclosure("<",">",true);
    static public Enclosure SINGLE_QUOTES=new Enclosure("'","'",false);
    static public Enclosure DOUBLE_QUOTES=new Enclosure("\"","\"",false);
    static public Enclosure BACKTICKS=new Enclosure("`","`",false);
    static public Enclosure MULTI_LINE_COMMENT=new Enclosure("/*","*/",true);
    static public Enclosure SINGLE_LINE_COMMENT=new Enclosure("//","\n",false);
}