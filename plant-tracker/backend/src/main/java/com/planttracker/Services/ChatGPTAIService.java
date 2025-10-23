package com.planttracker.Services;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import okhttp3.*;

@Service
public class ChatGPTAIService {

     @Value("${openai.api.key}")
     private String openaiApiKey;

     private final OkHttpClient client = new OkHttpClient();

     public String getCareAdvice(String prompt) throws IOException {
          MediaType mediaType = MediaType.parse("application/json");
          String jsonBody = "{"
                    + "\"model\": \"gpt-4\","
                    + "\"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}]"
                    + "}";

          RequestBody body = RequestBody.create(jsonBody, mediaType);

          Request request = new Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .post(body)
                    .addHeader("Authorization", "Bearer " + openaiApiKey)
                    .build();

          Response response = client.newCall(request).execute();
          if (!response.isSuccessful())
               throw new IOException("Unexpected code " + response);
          return response.body().string();
     }
}
