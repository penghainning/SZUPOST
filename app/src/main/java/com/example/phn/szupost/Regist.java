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

public class Regist extends AppCompatActivity {
    private SocketApplication socketapp;
    private EditText username;
    private EditText passwd;
    private EditText passwd2 ;
    private Button admit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist_layout);
        Regist_init();
    }


    //注册界面初始化
    public void Regist_init()
    {
        ActivityManager.activityList.add(this);
        socketapp=(SocketApplication) getApplication();
        username = (EditText) findViewById(R.id.editText);
        passwd = (EditText) findViewById(R.id.editText2);
        passwd2 = (EditText) findViewById(R.id.editText3);
        admit=(Button) findViewById(R.id.regist_admit);
        admit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setclick();
            }
        });

    }

    //点击事件
    public void setclick()
    {
         final String account = username.getText().toString().trim();
         final String password = passwd.getText().toString().trim();
         String password2 = passwd2.getText().toString().trim();
        //判断输入合法性
        if ((username.length() == 0) || (passwd.length() == 0) || passwd2.length() == 0) {
            Toast toast = Toast.makeText(this, "上述内容不能为空！请重新输入", Toast.LENGTH_SHORT);
            toast.show();
        } else if (username.length() > 11) {
            username.setText("");
            username.requestFocus();
            Toast toast = Toast.makeText(this, "用户名长度错误！请重新输入", Toast.LENGTH_SHORT);
            toast.show();
        } else if (passwd.length() > 17 || passwd.length() < 8) {
            passwd.setText("");
            passwd2.setText("");
            passwd.requestFocus();
            Toast toast = Toast.makeText(this, "密码长度错误！请重新输入", Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            char a[] = new char[passwd.length()];
            boolean flag = true;
            passwd.getText().getChars(0, passwd.length(), a, 0);
            for (int i = 0; i < passwd.length(); i++) {
                if (a[i] < '0' || a[i] > 'z') {
                    flag = false;
                    break;
                }
            }
            if (flag == false) {
                passwd.setText("");
                passwd2.setText("");
                passwd.requestFocus();
                Toast toast = Toast.makeText(this, "密码包含非法字符！请重新输入", Toast.LENGTH_SHORT);
                toast.show();
            } else if (!(password.equals(password2))) {
                passwd2.setText("");
                passwd2.requestFocus();
                Toast toast = Toast.makeText(this, "两次密码不相同！请重新输入", Toast.LENGTH_SHORT);
                toast.show();

            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(!socketapp.isNetflag())
                        {
                            if (socketapp.init() == -1)
                                registHandler.sendEmptyMessage(-1);
                        }
                            //给服务器发送消息
                            try {
                                String head = "REG";
                                JSONObject json = new JSONObject();
                                json.put("head", head);
                                json.put("account", account);
                                json.put("password", password);
                                //发送账号密码给服务器
                                SocketUtil.SendtoService(socketapp, json.toString(), new SocketCallbackListener() {
                                    @Override
                                    public void OnFinish(String s) {
                                        Log.i("注册", "接收成功！" + s);
                                        Message msg = new Message();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("return", s);
                                        msg.setData(bundle);
                                        msg.what = 0;
                                        registHandler.sendMessage(msg);
                                    }

                                    @Override
                                    public void OnError(Exception e) {
                                        e.printStackTrace();
                                    }
                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                    }
                }).start();
            }
        }
    }

    Handler registHandler=new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                //网络连接不上
                case -1:
                    socketapp.setNetflag(false);
                    Toast.makeText(Regist.this,"网络异常，请检查网络后重试！",Toast.LENGTH_SHORT).show();
                    break;
                //收到消息
                case 0:
                    try {
                        //将handler中发送过来的消息创建json对象
                        Bundle bundle = msg.getData();
                        JSONObject json = new JSONObject( bundle.getString("return"));
                        // Log.i("send","接收成功！"+json.getString("head")+" "+json.getString("isSuccess"));
                        if (json.getString("head").equals("REG")) {
                            if (json.getBoolean("isSuccess")) {
                                Toast toast=Toast.makeText(Regist.this, "注册成功！", Toast.LENGTH_SHORT);
                                toast.show();
                                Regist.this.finish();
                            }
                            else
                            {
                                username.setText("");
                                passwd.setText("");
                                passwd2.setText("");
                                Toast toast=Toast.makeText(Regist.this, "用户名已被占用，请重新输入", Toast.LENGTH_SHORT);
                                toast.show();

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
