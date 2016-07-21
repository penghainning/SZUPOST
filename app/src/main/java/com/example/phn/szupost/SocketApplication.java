package com.example.phn.szupost;

import android.app.Application;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by PHN on 2016/7/20.
 */
public class SocketApplication extends Application {
    private Socket Mysocket;                        //共用socket
    private String[] Userdata= new String [4];         //用户资料
    private   boolean Userflag=false;                     //使用权限标志
    private boolean Netflag=false;
    public void onCreate(){
        super.onCreate();
    }

    //socket连接初始化
    public  int init(){
        try{
            Mysocket=new Socket();
            //SocketAddress socketAddress=new InetSocketAddress("119.29.178.134",9999);
            SocketAddress socketAddress=new InetSocketAddress("172.29.40.78", 9999);
            Mysocket.connect(socketAddress,500);
            Log.i("SocketApplication","connect成功！");
            Netflag=true;
            return 0;
        }catch(ConnectException e)
        {
            e.printStackTrace();
            Log.i("SocketApplication","无法连接");
            Netflag=false;
            return -1;
        }catch (IOException e1)
        {
            e1.printStackTrace();
            return -1;
        }
    }
    //获得socket实例
    public Socket getMysocket()
    {
        return Mysocket;
    }

    //设置权限标识
    public void setUserflag(Boolean flag)
    {
        this.Userflag=flag;
    }

    //获取权限标识
    public boolean isUserflag() {
        return Userflag;
    }


    //设置网络状态标志

    public void setNetflag(boolean netflag) {
        Netflag = netflag;
    }

    //获取网络标识

    public boolean isNetflag() {
        return Netflag;
    }

    //设置用户数据
    public void setUserdata(String []a)
    {
        int i;
        for(i=0;i<4;i++)
            Userdata[i]=a[i];
    }
    //获得用户数据
    public String getUserdata(int i)
    {
        return Userdata[i];
    }


    //关闭socket
    public  void closeMysocket()
    {
        try {
            Netflag=false;
            Mysocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //字符串发送方法
    public void sendString(String s,OutputStream os){
        try {
            os.write(s.getBytes());
            os.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //字符串接收方法
    public String recString(InputStream is){
        byte[] buf = new byte[1024];//缓冲区
        int len = 0;String msg=null;
        try {
            len = is.read(buf);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        if(len>0)msg=new String(buf,0,len);
        return msg;
    }

}
