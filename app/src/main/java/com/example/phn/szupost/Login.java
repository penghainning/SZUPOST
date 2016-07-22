package com.example.phn.szupost;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

public class Login extends AppCompatActivity {
    //声明变量
    private SocketApplication socketapp;
    private EditText username;
    private EditText passwd ;
    private Button login;
    private Button regist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        //初始化
        Login_init();
        //设置点击事件
        setclick();
    }

    //界面初始化
    public void Login_init()
    {
        socketapp=(SocketApplication)getApplication();
        ActivityManager.activityList.add(this);
        username = (EditText) findViewById(R.id.username);
        passwd = (EditText) findViewById(R.id.passwd);
        login = (Button) findViewById(R.id.login);
        regist = (Button) findViewById(R.id.regist);

    }

    //按钮点击事件
    public void setclick()
    {
        //登录按钮
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String account = username.getText().toString().trim();
                String password = passwd.getText().toString().trim();
                if((username.length()==0)||(passwd.length()==0))
                {
                    Toast toast=Toast.makeText(Login.this, "用户名或密码不能为空！请重新输入", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else
                {
                    if(!socketapp.isNetflag())
                    {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                    if(socketapp.init()==-1)
                                        loginHandler.sendEmptyMessage(-1);
                            }
                        }).start();

                    }
                        try {
                            Thread.sleep(200);
                            String head = "LOG";
                            JSONObject json = new JSONObject();
                            json.put("head", head);
                            json.put("account", account);
                            json.put("password", password);
                            //发送账号密码给服务器
                            SocketUtil.SendtoService(socketapp, json.toString(), new SocketCallbackListener() {
                                @Override
                                public void OnFinish(String s) {
                                    Log.i("登录", "接收成功！" + s);
                                    Message msg=new Message();
                                    Bundle bundle=new Bundle();
                                    bundle.putString("return",s);
                                    msg.setData(bundle);
                                    msg.what=0;
                                    loginHandler.sendMessage(msg);
                                }
                                @Override
                                public void OnError(Exception e) {
                                    e.printStackTrace();
                                }
                            });

                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }



                }
            }
        });


        //注册按钮
        regist.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent2 = new Intent(Login.this, Regist.class);
                startActivity(intent2);
            }
        });

    }
    Handler loginHandler=new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                //网络连接不上
                case -1:
                    socketapp.setNetflag(false);
                    Toast.makeText(Login.this,"网络异常，请检查网络后重试！",Toast.LENGTH_SHORT).show();
                    break;
                //收到消息
                case 0:
                    try {
                        //将handler中发送过来的消息创建json对象
                        Bundle bundle = msg.getData();
                        JSONObject json = new JSONObject( bundle.getString("return"));
                        if (json.getString("head").equals("LOG")) {
                            if (json.getBoolean("isSuccess")) {
                                socketapp.setUserflag(json.getBoolean("flag"));
                                if(socketapp.isUserflag())
                                {
                                    String[] a = new String[4];
                                    a[0] = json.getString("account");
                                    a[1] = json.getString("name");
                                    a[2] = json.getString("sex");
                                    a[3] = json.getString("location");
                                    //设置用户数据
                                    socketapp.setUserdata(a);
                                }
                                else
                                {
                                    String[] a = new String[4];
                                    a[0] = json.getString("account");
                                    a[1] = "新用户"+a[0];
                                    a[2] = "";
                                    a[3] = "";
                                    //设置用户数据
                                    socketapp.setUserdata(a);
                                }
                                Intent intent1 = new Intent(Login.this, HomePage.class);
                                startActivity(intent1);
                                Login.this.finish();
                            }
                            else
                            {
                                passwd.setText("");
                                username.requestFocus();
                              Toast.makeText(Login.this, "用户名或者密码错误，请重新输入", Toast.LENGTH_SHORT).show();

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;

            }
            super.handleMessage(msg);
        }

    };

}
