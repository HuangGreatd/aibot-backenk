package com.juzipi.springbootinit;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.juzipi.springbootinit.common.ForbiddenWordsDetector;
import com.juzipi.springbootinit.manager.AIManager;
import com.juzipi.springbootinit.utils.DelayQueueUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * 主类测试
 */
@SpringBootTest
class MainApplicationTests {

    @Resource
    private AIManager aiManager;

    @Test
    void getAi() {
        String question = "我想去遇龙河玩，有什么推荐>?";
        String questionByAi = aiManager.getQuestionByAi(question);
        System.out.println("questionByAi = " + questionByAi);
    }

    @Test
    void contextLoads() {
    }


    @Test
    void testAi() {
        String aiKey = "5KN6NHZ-PKNMJMD-J76Q67H-8T8WA2N";
        String workspace_id = "test";

        String api_address = "http://localhost:3001/api/v1/openai/chat/completions";
        //String api_address = "http://juzipi.natapp1.cc/api/v1/openai/chat/completions";
        // 构建请求体
        JSONObject requestBody = new JSONObject();
        requestBody.set("model", workspace_id);
        requestBody.set("stream", true);
        requestBody.set("temperature", 0.7);

        // 构建 messages 数组
        JSONArray messages = new JSONArray();
        messages.add(createMessage("system", "You are a helpful assistant"));
        messages.add(createMessage("user", "怎么购买竹筏漂？"));

        requestBody.set("messages", messages);

        // 发送 POST 请求
        HttpResponse response = HttpRequest.post(api_address)
                .header("accept", "text/event-stream")
                .header("Authorization", "Bearer " + aiKey)
                .header("Content-Type", "application/json")
                .body(requestBody.toString()) // 设置请求体
                .timeout(30000) // 设置超时时间（可选）
                .execute();

        // 处理响应
        if (response.isOk()) {
            String responseBody = response.body();
            System.out.println("Response: " + responseBody);
        } else {
            System.err.println("Error: " + response.getStatus());
            System.err.println("Response: " + response.body());
        }
    }

    // 辅助方法：创建消息对象
    private static JSONObject createMessage(String role, String content) {
        JSONObject message = new JSONObject();
        message.set("role", role);
        message.set("content", content);
        return message;
    }


    @Test
    void testAi2() {
        String aiKey = "N9WV3GW-2K94NGB-Q8J38Y9-EVDQN6D";
        String workspace_id = "de_v1";
        String api_address = "http://juzipi.natapp1.cc/api/v1/openai/chat/completions";

        // 构建请求体
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", workspace_id);
        requestBody.put("stream", true);
        requestBody.put("temperature", 0.7);

        // 构建 messages 数组
        JSONArray messages = new JSONArray();
        messages.add(createMessage("user", "阳朔湾码头电话是多少？"));

        requestBody.put("messages", messages);

        // 发送 POST 请求
        HttpResponse response = HttpRequest.post(api_address)
                .header("accept", "*/*")
                .header("Authorization", "Bearer " + aiKey)
                .header("Content-Type", "application/json")
                .body(requestBody.toString()) // 设置请求体
                .timeout(30000) // 设置超时时间（可选）
                .execute();

        // 处理响应
//        if (response.isOk()) {
//            try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.bodyStream()))) {
//                String line;
//                Gson gson = new Gson();
//                while ((line = reader.readLine()) != null) {
//                    if (line.startsWith("data: ")) {
//                        String jsonStr = line.substring("data: ".length()).trim();
//                        if (!jsonStr.equals("[DONE]")) {
//                            JsonObject jsonObject = gson.fromJson(jsonStr, JsonObject.class);
//                            JsonArray choices = jsonObject.getAsJsonArray("choices");
//                            if (choices != null && !choices.isEmpty()) {
//                                JsonObject choice = choices.get(0).getAsJsonObject();
//                                JsonObject delta = choice.getAsJsonObject("delta");
//                                if (delta != null) {
//                                    JsonElement contentElement = delta.get("content");
//                                    if (contentElement != null && !contentElement.isJsonNull()) {
//                                        String content = contentElement.getAsString();
//                                        System.out.print(content);
//                                        System.out.flush();
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//                System.out.println(); // 换行
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//            System.err.println("Error: " + response.getStatus());
//            System.err.println("Response: " + response.body());
//        }
        System.out.println("response = " + response);
    }


    @Test
    void getOpenIdByCode() {
        String APPID = "wx13b044f1a6f28508";
        String SECRET = "65f20e3d20de29e7f1739624a8eceb53";

        String tempCode = "0b3MPQ0w3Rjlt43HXH0w3glMN72MPQ0w";

        String authorization_code = "authorization_code";
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + APPID + "&secret=" + SECRET + "&js_code=" + tempCode + "&grant_type=" + authorization_code;
        String str = restTemplate.getForObject(url, String.class);
        System.out.println(str);

    }

    @Test
    void testLogin() {
        String str = "odKVS6Vp7Cvr2tydC3n-prfU2f60";


        // 将获取的到的json字符串转换为map集合
        // 此处这个方法的作用就是将微信发送过来的JSON数据转换为Map，使用Hutool的类也是可以实现的。
        Map<String, Object> stringToMap = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
    }


    private static final String QUEUE_NAME = "messageQueue";

    @Autowired
    private DelayQueueUtil delayQueueUtil;

    @Test
    void testRedisQueue() {
        for (int i = 0; i < 10; i++) {
            String message = "hello" + i;
            delayQueueUtil.addToDelayQueue(message);
        }

    }


    @Test
    void testRedisQueueConsumer(){
        String s = delayQueueUtil.pollFromDelayQueue();
        System.out.println("s = " + s);
    }
    @Test
    void forBiddenWords() {

    }
}
