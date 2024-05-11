package org.nova.localization;

public class Currency_ISO_4217
{
    final public String name;
    final public String alphabeticCode;
    final public int numericCode;
    final public int digits;
    final public String symbol;
    
    public Currency_ISO_4217(String name,String alphabeticCode,int numericCode,int digits,String symbol)
    {
        this.name=name;
        this.alphabeticCode=alphabeticCode;
        this.numericCode=numericCode;
        this.digits=digits;
        this.symbol=symbol;
    }
}
