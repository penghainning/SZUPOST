package com.example.phn.szupost;

import android.util.Log;

import java.net.Socket;

/**
 * Created by PHN on 2016/7/20.
 */
public class SocketUtil {

    //开启线程发送数据给服务器
    public static  void SendtoService(final SocketApplication socketApplication,final String data,final  SocketCallbackListener listener)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    socketApplication.sendString(data, socketApplication.getMysocket().getOutputStream());
                    String Backjson=socketApplication.recString(socketApplication.getMysocket().getInputStream());
                    Log.i("SocketUtil", "接收成功！" + Backjson);
                    if(!Backjson.equals(null))
                    {
                        listener.OnFinish(Backjson);
                    }
                }catch (Exception e)
                {
                    listener.OnError(e);
                }


            }
        }).start();
    }


}
