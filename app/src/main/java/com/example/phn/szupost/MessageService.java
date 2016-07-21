package com.example.phn.szupost;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;

public class MessageService extends Service {
    //后台接受消息的服务
    String content;
    msgListener listener;
    boolean flag=true;
    public MessageService() {
    }

    public class msgbinder extends Binder
    {
        public MessageService getService()
        {
            return MessageService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new msgbinder();
    }

    @Override
    public void onDestroy() {
        try {
             flag=false;
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    //接信息的方法
    public void receive(final SocketApplication application)
    {
        new Thread(new Runnable() {
            public void run() {
                Log.i("MessageService: ","收消息的线程开始工作！");
                while(flag)
                {
                try {

                    content=application.recString(application.getMysocket().getInputStream());
                    Log.i("msgService: ","收到消息："+content);
                    if(!content.equals(null))
                        listener.onFinish(content);

                }catch (Exception e)
                {
                    e.printStackTrace();
                }

                 }
            }
        }).start();

    }
    //设置回调接口
    public void setListener(msgListener listener)
    {
        this.listener=listener;
    }





}
