package com.itgowo.itgowodemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.itgowo.itgowolib.itgowoNetTool;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Test.testUpload();
//       Test.testDownloadFile();
//       Test.testHttpSync();
//        final String jsonstr = new Gson().toJson(new Entity().setAge(22).setName("aaaadfa"));
//        itgowo.netTool().initHttpClient(new itgowoNetTool.onRequestDataListener() {
//            @Override
//            public void onRequest(String url, Map head, String body, itgowoNetTool.onRequestDataListener onRequestDataListener, itgowoNetTool.onReceviceDataListener listener) {
//                itgowo.netTool().onRequestComplete("request", jsonstr, listener);
//            }
//        });
//        Map map = new HashMap();
//        map.put("aaa", 111);
//        map.put("bbb", 222);
//        map.put("ccc", 333);
//        itgowo.netTool().Request("url", map, "requestbody", new itgowoNetTool.onReceviceDataListener<Entity>() {
//
//            @Override
//            public void onResult(String requestStr, String responseStr, Entity result) {
//                System.out.println("requestStr = [" + requestStr + "], responseStr = [" + responseStr + "], result = [" + result + "]");
//            }
//
//            @Override
//            public void onError(Throwable throwable) {
//
//            }
//        });
    }

    public class A implements itgowoNetTool.onReceviceDataListener<Entity> {


        @Override
        public void onResult(String requestStr, String responseStr, Entity result) {
            System.out.println("requestStr = [" + requestStr + "], responseStr = [" + responseStr + "], result = [" + result + "]");
        }

        @Override
        public void onError(Throwable throwable) {

        }
    }
}
