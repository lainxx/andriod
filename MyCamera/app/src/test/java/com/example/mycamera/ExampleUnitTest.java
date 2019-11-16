package com.example.mycamera;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {

        //assertEquals(4, 2 + 2);
        String info2 = "{\"error_code\":0,\"error_msg\":\"SUCCESS\",\"log_id\":1584942011010,\"timestamp\":1573735286,\"cached\":0,\"result\":{\"face_num\":1,\"face_list\":[{\"face_token\":\"a0a88a52b6216286139fff512846cc01\",\"location\":{\"left\":132.26,\"top\":51.26,\"width\":86,\"height\":85,\"rotation\":-8},\"face_probability\":1,\"angle\":{\"yaw\":24.3,\"pitch\":-2.46,\"roll\":-3.02},\"age\":22,\"beauty\":74.53,\"expression\":{\"type\":\"none\",\"probability\":1},\"face_shape\":{\"type\":\"square\",\"probability\":0.48},\"gender\":{\"type\":\"female\",\"probability\":1},\"glasses\":{\"type\":\"none\",\"probability\":1}}]}}";
        String info = "{\"error_code\":0,\"error_msg\":\"SUCCESS\",\"log_id\":9435201991535,\"timestamp\":1573733333,\"cached\":0,\"result\":{\"face_num\":1,\"face_list\":[{\"face_token\":\"a0a88a52b6216286139fff512846cc01\",\"location\":{\"left\":132.26,\"top\":51.26,\"width\":86,\"height\":85,\"rotation\":-8},\"face_probability\":1,\"angle\":{\"yaw\":24.3,\"pitch\":-2.46,\"roll\":-3.02}}]}}";
        JsonParser jp = new JsonParser();
        //parseJsonWithJsonObject(result);
        JsonObject jo = jp.parse(info2).getAsJsonObject();
        JsonObject msg = jo.get("result").getAsJsonObject();
        JsonArray jsonArray = msg.get("face_list").getAsJsonArray();
        for (int i = 0; i < jsonArray.size(); ++i) {
            //face_shape":{"type":"square","probability":0.48},"gender":{"type":"female","probability":1}
            JsonObject msg2 = (JsonObject) jsonArray.get(i);
            String face_token = msg2.get("face_token").getAsString();
            // System.out.println(face_token);
            JsonObject location = msg2.get("location").getAsJsonObject();
            String left = location.get("left").getAsString();
            int age = msg2.get("age").getAsInt();
            String beauty = msg2.get("beauty").getAsString();
            String expression = msg2.get("expression").getAsJsonObject().get("type").getAsString();
            String gender = msg2.get("gender").getAsJsonObject().get("type").getAsString();
            System.out.println(expression);
            System.out.println(gender);
        }


        //System.out.println(error_msg);

    }

    @Test
    public void faceCompare() {
        String res = "{\n" +
                "    \"score\": 44.3,\n" +
                "    \"face_list\": [  //返回的顺序与传入的顺序保持一致\n" +
                "        {\n" +
                "            \"face_token\": \"fid1\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"face_token\": \"fid2\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        JsonParser jp = new JsonParser();
        JsonObject jo = jp.parse(res).getAsJsonObject();


        String total = "";

        float score = jo.get("score").getAsFloat();

        total = "相似度为%" + String.valueOf(score);


        System.out.println(total);

    }

    @Test
    public void faceSearch() {
        String info = "{\n" +
                "     \"error_code\": 0,\n" +
                "     \"error_msg\": \"SUCCESS\",\n" +
                "     \"log_id\": 240483475,\n" +
                "     \"timestamp\": 1535533440,\n" +
                "     \"cached\": 0,\n" +
                "     \"result\": {\n" +
                "         \"face_num\": 2,\n" +
                "         \"face_list\": [\n" +
                "             {\n" +
                "                 \"face_token\": \"6fe19a6ee0c4233db9b5bba4dc2b9233\",\n" +
                "                 \"location\": {\n" +
                "                     \"left\": 31.95568085,\n" +
                "                     \"top\": 120.3764267,\n" +
                "                     \"width\": 87,\n" +
                "                     \"height\": 85,\n" +
                "                     \"rotation\": -5\n" +
                "                 },\n" +
                "                 \"user_list\": [\n" +
                "                     {\n" +
                "                         \"group_id\": \"group1\",\n" +
                "                         \"user_id\": \"5abd24fd062e49bfa906b257ec40d284\",\n" +
                "                         \"user_info\": \"userinfo1\",\n" +
                "                         \"score\": 69.85684967041\n" +
                "                     },\n" +
                "                     {\n" +
                "                         \"group_id\": \"group1\",\n" +
                "                         \"user_id\": \"2abf89cffb31473a9948268fde9e1c3f\",\n" +
                "                         \"user_info\": \"userinfo2\",\n" +
                "                         \"score\": 66.586112976074\n" +
                "                     }\n" +
                "                 ]\n" +
                "             },\n" +
                "             {\n" +
                "                 \"face_token\": \"fde61e9c074f48cf2bbb319e42634f41\",\n" +
                "                 \"location\": {\n" +
                "                     \"left\": 219.4467773,\n" +
                "                     \"top\": 104.7486954,\n" +
                "                     \"width\": 81,\n" +
                "                     \"height\": 77,\n" +
                "                     \"rotation\": 3\n" +
                "                 },\n" +
                "                 \"user_list\": [\n" +
                "                     {\n" +
                "                         \"group_id\": \"group1\",\n" +
                "                         \"user_id\": \"088717532b094c3990755e91250adf7d\",\n" +
                "                         \"user_info\": \"userinfo\",\n" +
                "                         \"score\": 65.154159545898\n" +
                "                     }\n" +
                "                 ]\n" +
                "             }\n" +
                "         ]\n" +
                "     }\n" +
                " }";

        JsonParser jp = new JsonParser();
        //parseJsonWithJsonObject(result);
        JsonObject jo = jp.parse(info).getAsJsonObject();
        JsonObject msg = jo.get("result").getAsJsonObject();
        JsonArray jsonArray = msg.get("face_list").getAsJsonArray();
        String error_msg = jo.get("error_msg").getAsString();
        String total = "";
        if (error_msg.equals("SUCCESS")) {
            //  Toast.makeText(faceIdentify.this, "检测成功", Toast.LENGTH_SHORT).show();
            for (int i = 0; i < jsonArray.size(); ++i) {
                JsonObject msg2 = (JsonObject) jsonArray.get(i);
                total += "第" + String.valueOf(i) + "个人是";
                String face_token = msg2.get("face_token").getAsString();
                JsonObject location = msg2.get("location").getAsJsonObject();
                String left = location.get("left").getAsString();
                JsonArray array = msg2.get("user_list").getAsJsonArray();
                JsonObject list=(JsonObject) array.get(0);
                String name = list.get("user_id").getAsString();
                total += name + "\n";

                // System.out.println(age);
                // System.out.println(beauty);
            }
            System.out.println(total);

        }
    }
}