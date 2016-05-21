package com.skyuma.myfragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class TestServiceActivity extends AppCompatActivity {
    private MsgService msgService;
    private int progress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_service);
        //绑定Service
        Intent intent = new Intent(TestServiceActivity.this, MsgService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);

        Button btnBind = (Button) findViewById(R.id.btnBind);
        btnBind.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                //开始下载
                msgService.startDownLoad();
            }
        });
        Button btnUnBind = (Button) findViewById(R.id.btnUnBind);
        btnUnBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (conn != null) {
                    unbindService(conn);
                    conn = null;
                }
            }
        });

    }

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //返回一个MsgService对象
            msgService = ((MsgService.MsgBinder)service).getService();
            //注册回调接口来接收下载进度的变化
            msgService.setOnProgressListener(new MsgService.OnProgressListener() {

                @Override
                public void onProgress(int progress) {
                    System.out.println("TestServiceActivity progress:" + progress);
                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        if (conn != null) {
            unbindService(conn);
            conn = null;
        }
        super.onDestroy();
    }
}
