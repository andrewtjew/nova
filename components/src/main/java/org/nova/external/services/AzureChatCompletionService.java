package org.nova.external.services;

import com.openai.azure.credential.AzureApiKeyCredential;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

public class AzureChatCompletionService extends ChatCompletionService
{
    final private OpenAIClient openAIClient; 
    final private String deploymentName;
    public AzureChatCompletionService(String endPoint,String apiKey,String deploymentName)
    {
        this.openAIClient = OpenAIOkHttpClient.builder()
                .baseUrl(endPoint)
                .credential(AzureApiKeyCredential.create(apiKey))
                .build();        
        this.deploymentName=deploymentName;
    }
    
    @Override
    public String complete(String systemMessage,String prompt)
    {
        ChatCompletionCreateParams createParams = ChatCompletionCreateParams.builder()
                .model(ChatModel.of(this.deploymentName))
                .addSystemMessage(systemMessage)
                .addUserMessage(prompt)
                .build();
        ChatCompletion chatCompletion = this.openAIClient.chat().completions().create(createParams);

        if (chatCompletion.choices().size()==0)
        {
            return null;
        }
        return chatCompletion.choices().getFirst().message().content().get();
    }
}
