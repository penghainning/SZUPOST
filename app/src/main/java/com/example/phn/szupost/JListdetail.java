package com.example.phn.szupost;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class JListdetail extends AppCompatActivity {
    private TextView postnameview;
    private  TextView numberview;
    private TextView usrnameview;
    private TextView phonenumberview;
    private TextView onumberview;
    private TextView flagview;
    private TextView nameview;
    private TextView addressview;
    private TextView postlocationview;
    private TextView deadlineview;
    private Button chat;
    private Button changetype;
    private  Data d;
    private  SocketApplication myapp;
    private boolean success=false;
    private jdetailReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.activityList.add(this);
        setContentView(R.layout.jlistdetail_layout);
        myapp=(SocketApplication)getApplication();
        Intent intent = this.getIntent();
        d=(Data)intent.getSerializableExtra("date");
        bind();
        click();
    }


    public void bind()
    {


        receiver = new jdetailReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.phn.szupost.JDETAIL");
        registerReceiver(receiver, intentFilter);



        postnameview=(TextView)findViewById(R.id.postnameview);
        postnameview.setText(d.getName());
        addressview=(TextView)findViewById(R.id.addressview);
        addressview.setText(d.getAddress());
        postlocationview=(TextView)findViewById(R.id.postlocationview);
        postlocationview.setText(d.getLocation());
        deadlineview=(TextView)findViewById(R.id.deadlineview);
        deadlineview.setText(d.getDeadline());

        usrnameview=(TextView)findViewById(R.id.usrnameview);
        flagview=(TextView)findViewById(R.id.flagview);
        numberview=(TextView)findViewById(R.id.numberview);
        phonenumberview=(TextView)findViewById(R.id.phonenumberview);
        onumberview=(TextView)findViewById(R.id.onumberview);
        nameview=(TextView)findViewById(R.id.nameview);
        changetype=(Button)findViewById(R.id.changetype);
        chat=(Button)findViewById(R.id.chat);
        new Thread(new Runnable() {
            public void run() {
                try{
                    String head = "JDETAIL";
                    JSONObject json = new JSONObject();
                    json.put("head", head);
                    json.put("id",d.getId());
                    json.put("account",myapp.getUserdata(0));
                    myapp.sendString(json.toString(), myapp.getMysocket().getOutputStream());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(new timeError()).start();
    }

    //按钮事件
    public void click()
    {
        changetype.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(flagview.getText().equals("未代领"))
                {
                   new Thread(new Mythread(0)).start();
                }
                else if(flagview.getText().equals("已代领"))
                {
                    new Thread(new Mythread(1)).start();
                }

            }
        });

    }

    protected void onDestroy() {
        //注销广播
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    //接单的广播接收器
    public class jdetailReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            Message msg=new Message();
            Bundle bundle=new Bundle();
            bundle.putString("return",intent.getStringExtra("returndata"));
            msg.setData(bundle);
            if(intent.getStringExtra("type").equals("2"))
            msg.what=3;
            else
            msg.what=0;
            success=true;
            mHandler.sendMessage(msg);
        }

    }


    //Handler
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                //网络连接不上
                case -1:
                    Toast toast = Toast.makeText(JListdetail.this, "网络异常，请检查网络后重试！", Toast.LENGTH_SHORT);
                    toast.show();
                    break;
                //收到消息
                case 0:
                    try {
                        //将handler中发送过来的消息创建json对象
                        JSONObject jstring = new JSONObject(bundle.getString("return"));
                        nameview.setText(jstring.getString("name"));
                        numberview.setText(jstring.getString("number"));
                        usrnameview.setText(jstring.getString("usrname"));
                        onumberview.setText(jstring.getString("onumber"));
                        phonenumberview.setText(jstring.getString("phonenumber"));
                        int flag=jstring.getInt("flag");
                         if(flag==0)
                         {
                             flagview.setText("未代领");
                             changetype.setText("已代领");
                         }
                        else if(flag==1)
                         {
                             flagview.setText("已代领");
                             changetype.setText("已完成");
                         }

                        else if(flag==2)
                         {
                             flagview.setText("已完成");
                             changetype.setText("已完成");
                             changetype.setClickable(false);
                         }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    Toast.makeText(JListdetail.this, "请求超时，请重试！", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    try{
                        JSONObject json = new JSONObject(bundle.getString("return"));
                        if(json.getBoolean("isSuccess"))
                        {
                            int flag=json.getInt("flag");
                            if(flag==-1)
                                flagview.setText("未接单");
                            else if(flag==0)
                                flagview.setText("未代领");
                            else if(flag==1)
                            {

                                flagview.setText("已代领");
                                changetype.setText("已完成");
                            }

                            else if(flag==2)
                            {
                                flagview.setText("已完成");
                                changetype.setClickable(false);
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;

            }
            super.handleMessage(msg);
        }
    };

    public class timeError extends Thread
    {
        public void run() {
            int i;
            for( i=0;i<10;i++) {
                try {
                    Thread.sleep(200);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (success)
                    break;
            }
            if(!success)
            {
                mHandler.sendEmptyMessage(2);
            }
        }

    }

    class Mythread extends Thread{
        int n;
        Mythread(int n )
        {
            this.n=n;
        }
        public void run() {
            try{
                String head = "JFLAG";
                JSONObject json = new JSONObject();
                json.put("head", head);
                json.put("onumber",d.getOnumber());
                json.put("flag",n);
                myapp.sendString(json.toString(), myapp.getMysocket().getOutputStream());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }



}
