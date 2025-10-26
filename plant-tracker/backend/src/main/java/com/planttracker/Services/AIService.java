package com.planttracker.Services;

import java.io.IOException;

// Thêm 2 import này
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class AIService {

     @Value("${gemini.api.key}")
     private String geminiApiKey; // Sửa G -> g (theo chuẩn Java)

     private final OkHttpClient client = new OkHttpClient();

     // Khởi tạo ObjectMapper để parse JSON
     private final ObjectMapper objectMapper = new ObjectMapper();

     public String getCareAdvice(String prompt) throws IOException {
          MediaType mediaType = MediaType.parse("application/json");
          String escapedPrompt = prompt.replace("\\", "\\\\").replace("\"", "\\\"");

          String jsonBody = "{"
                    + "\"contents\": ["
                    + "  {\"parts\": [{\"text\": \"" + escapedPrompt + "\"}]}"
                    + "]"
                    + "}";

          RequestBody body = RequestBody.create(jsonBody, mediaType);
          String model = "gemini-2.0-flash";
          HttpUrl url = new HttpUrl.Builder().scheme("https")
                    .host("generativelanguage.googleapis.com")
                    .addPathSegment("v1")
                    .addPathSegment("models")
                    .addPathSegment(model + ":generateContent")
                    .addQueryParameter("key", geminiApiKey) // Sửa G -> g
                    .build();

          Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

          try (Response response = client.newCall(request).execute()) {

               // Lấy body một lần duy nhất. Rất quan trọng!
               String responseString = (response.body() != null) ? response.body().string() : null;

               if (!response.isSuccessful()) {
                    // Sửa lỗi chính tả "Gemnini" -> "Gemini"
                    // In ra body lỗi (nếu có) để debug
                    throw new IOException("Lỗi từ Gemini API: " + response.code() + " - " + responseString);
               }

               if (responseString == null) {
                    throw new IOException("Phản hồi từ Gemini API rỗng");
               }

               // === SỬA LỖI PARSING BẰNG OBJECTMAPPER ===
               try {
                    JsonNode root = objectMapper.readTree(responseString);

                    // Đi theo cấu trúc JSON: candidates -> [0] -> content -> parts -> [0] -> text
                    JsonNode textNode = root.path("candidates")
                              .path(0)
                              .path("content")
                              .path("parts")
                              .path(0)
                              .path("text");

                    if (textNode.isMissingNode() || textNode.isNull()) {
                         // Lỗi nếu cấu trúc JSON trả về không như mong đợi
                         throw new IOException("Không tìm thấy trường 'text' trong phản hồi của Gemini API. Response: "
                                   + responseString);
                    }

                    // .asText() sẽ tự động xử lý các ký tự escape
                    return textNode.asText()
                              .replace("\\n", "\n")
                              .replace("\\\"", "\"");

               } catch (Exception e) {
                    // Ném lỗi nếu JSON bị lỗi (invalid)
                    throw new IOException("Lỗi khi phân tích JSON phản hồi từ Gemini API: " + e.getMessage()
                              + " | Response: " + responseString);
               }
          }
     }
}