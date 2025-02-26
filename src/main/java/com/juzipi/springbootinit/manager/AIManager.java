package com.juzipi.springbootinit.manager;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.juzipi.springbootinit.config.AiConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.juzipi.springbootinit.constant.AiConstant.SYSTEM_PROMPT;

/**
 * @ClassName AIManager
 * @Description: AI接口
 * @Author: 橘子皮
 * @CreateDate: 2025/2/21 10:40
 */
@Slf4j
@Component
public class AIManager {

    @Resource
    private AiConfig aiConfig;


    /**
     * 同步请求问题
     * @param question
     * @return
     */
    public String getQuestionByAi(String question) {
        String accessKey = aiConfig.getAccessKey();
        String workspaceId = aiConfig.getWorkspaceId();
        String apiAddress = aiConfig.getApiAddress();
        Double temperature = aiConfig.getTemperature();
        StringBuilder stringBuilder = new StringBuilder();

        // 构建请求体
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", workspaceId);
        requestBody.put("stream", true);
        requestBody.put("temperature", temperature);
        // 构建 messages 数组
        JSONArray messages = new JSONArray();
        messages.add(createMessage("assistant",SYSTEM_PROMPT));
        messages.add(createMessage("user", question));
        requestBody.put("messages", messages);
        // 发送 POST 请求
        HttpResponse response = HttpRequest.post(apiAddress)
                .header("accept", "*/*")
                .header("Authorization", "Bearer " + accessKey)
                .header("Content-Type", "application/json")
                .body(requestBody.toString()) // 设置请求体
                .timeout(60000)  // 设置超时时间（可选）
                .execute();
        // 处理响应
        if (response.isOk()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.bodyStream()))) {
                String line;
                Gson gson = new Gson();
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("data: ")) {
                        String jsonStr = line.substring("data: ".length()).trim();
                        if (!jsonStr.equals("[DONE]")) {
                            JsonObject jsonObject = gson.fromJson(jsonStr, JsonObject.class);
                            JsonArray choices = jsonObject.getAsJsonArray("choices");
                            if (choices != null && !choices.isEmpty()) {
                                JsonObject choice = choices.get(0).getAsJsonObject();
                                JsonObject delta = choice.getAsJsonObject("delta");
                                if (delta != null) {
                                    JsonElement contentElement = delta.get("content");
                                    if (contentElement != null && !contentElement.isJsonNull()) {
                                        String content = contentElement.getAsString();
                                        stringBuilder.append(content);
                                    }
                                }
                            }
                        }
                    }
                }
                System.out.println(); // 换行
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Error: " + response.getStatus());
            System.err.println("Response: " + response.body());
        }
        return stringBuilder.toString();
    }




    // 辅助方法：创建消息对象
    private static JSONObject createMessage(String role, String content) {
        JSONObject message = new JSONObject();
        message.set("role", role);
        message.set("content", content);
        return message;
    }

}
