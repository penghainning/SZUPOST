package com.example.phn.szupost;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class Receive_listdetail extends AppCompatActivity {
    private TextView postnameview;
    private TextView addressview;
    private TextView postlocationview;
    private TextView deadlineview;
    private Button sure;
    private Button   chat;
    private  Data d;
    private  SocketApplication socketapp;
    private  String id;
    private  listdetailReceiver receiver;
    private boolean success=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receive_listdetail_layout);
        bind();
        click();
    }

    //初始化
    public void bind()
    {
        ActivityManager.activityList.add(this);
        socketapp=(SocketApplication) getApplication();

        //动态注册广播接收器
         receiver = new listdetailReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.phn.szupost.RECEIVER");
        registerReceiver(receiver, intentFilter);

        Intent intent = this.getIntent();
        d=(Data)intent.getSerializableExtra("date");
        postnameview=(TextView)findViewById(R.id.postnameview);
        postnameview.setText(d.getName());
        addressview=(TextView)findViewById(R.id.addressview);
        addressview.setText(d.getAddress());
        id=d.getId();
        postlocationview=(TextView)findViewById(R.id.postlocationview);
        postlocationview.setText(d.getLocation());
        deadlineview=(TextView)findViewById(R.id.deadlineview);
        deadlineview.setText(d.getDeadline());
        sure=(Button)findViewById(R.id.sure);
        chat=(Button)findViewById(R.id.chat);
    }
    public void click()
    {
        sure.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //    通过AlertDialog.Builder这个类来实例化我们的一个AlertDialog的对象
                final AlertDialog.Builder builder = new AlertDialog.Builder(Receive_listdetail.this);
                //    设置Title的内容
                builder.setTitle("信息确认");
                //    设置Content来显示一个信息
                builder.setMessage("是否确定确定接单？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(!socketapp.isUserflag())
                        {
                            myHandler.sendEmptyMessage(1);
                        }
                        else
                        {
                            success=false;
                            new MyThread().start();
                            dialog.dismiss();
                            new Thread(new Runnable() {
                                public void run() {
                                    for(int i=0;i<10;i++) {
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
                                        myHandler.sendEmptyMessage(2);
                                    }
                                }
                            }).start();

                          }

                    }
                });
                //    设置一个NegativeButton
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }

    //确认接单的线程
    class MyThread extends Thread {
        MyThread(){}
        public void run()
        {
            if (!socketapp.isNetflag()) {
                if(socketapp.init()==-1)
                myHandler.sendEmptyMessage(-1);
            }
                try {
                    String head = "ACCEPT";
                    JSONObject json = new JSONObject();
                    json.put("head", head);
                    json.put("id", id);
                    socketapp.sendString(json.toString(),socketapp.getMysocket().getOutputStream());
                    Log.i("Receive_listdetail","发送成功！"+head+" "+id);

                } catch (Exception e) {
                    e.printStackTrace();
                }

        }
    }


    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {    //接受服务器信息更新UI
            switch (msg.what) {
                case 0:
                    try {
                        //将handler中发送过来的消息创建json对象
                        Bundle bundle = msg.getData();
                        JSONObject json = new JSONObject( bundle.getString("return"));
                        Log.i("Receive_listdetail","接收成功！"+json.getString("head")+" "+json.getString("isSuccess"));
                        if (json.getString("head").equals("ACCEPT")) {
                            if (json.getBoolean("isSuccess")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Receive_listdetail.this);
                                //    设置Content来显示一个信息
                                builder.setMessage("接单成功！");
                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        Intent intent=new Intent();
                                        intent.putExtra("return","flush");
                                        setResult(0,intent);
                                        Receive_listdetail.this.finish();
                                    }
                                });
                                builder.show();
                            }
                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Receive_listdetail.this);
                                //    设置Content来显示一个信息
                                builder.setMessage("接单失败！请重新尝试");
                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case -1:
                    Toast toast=Toast.makeText(Receive_listdetail.this,"网络异常，请检查网络后重试！",Toast.LENGTH_SHORT);
                    toast.show();
                    break;
                case 1:
                    final AlertDialog.Builder builder = new AlertDialog.Builder(Receive_listdetail.this);
                    //    设置Title的内容
                    builder.setTitle("权限不够");
                    //    设置Content来显示一个信息
                    builder.setMessage("请先完善好个人资料！");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                    break;
                case 2:
                    Toast.makeText(Receive_listdetail.this, "连接超时，请重试！", Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    protected void onDestroy() {
        //注销广播
        unregisterReceiver(receiver);
        super.onDestroy();
    }


    //接单的广播接收器
    public class listdetailReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            Message msg=new Message();
            Bundle bundle=new Bundle();
            Log.i("listdatareceiver: ","收到消息："+intent.getStringExtra("returndata"));
            bundle.putString("return",intent.getStringExtra("returndata"));
            msg.setData(bundle);
            msg.what=0;
            success=true;
            myHandler.sendMessage(msg);
        }

    }

}
