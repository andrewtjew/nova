package org.nova.http.server;

public class ParsedParameters
{
    final private int contextParameterIndex;
    final private int stateParameterIndex;
    final private Object[] parameters;
    ParsedParameters(Object[] parameters,int contextParameterIndex,int stateParameterIndex)
    {
        this.parameters=parameters;
        this.contextParameterIndex=contextParameterIndex;
        this.stateParameterIndex=stateParameterIndex;
    }
    
}