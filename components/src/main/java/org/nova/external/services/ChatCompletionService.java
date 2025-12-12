package org.nova.external.services;

public abstract class ChatCompletionService
{
    abstract public String complete(String systemMessage,String input);
}
