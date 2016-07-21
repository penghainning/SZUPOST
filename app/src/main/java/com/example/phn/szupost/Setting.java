package com.example.phn.szupost;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class Setting extends AppCompatActivity {
    private  TextView setsex;
    private TextView setaccount;
    private TextView setname;
    private TextView setlocation;
    private  TableRow ssex;
    private  TableRow slocation;
    private TableRow sname;
    private  Button set_sure;
    private Button set_back;
    private SocketApplication socketapp;
    private boolean success=false;
    private settingReceiver receiver;
    String []a=new String[4];
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.activityList.add(this);
        setContentView(R.layout.setting_layout);
        //动态注册广播接收器
        receiver = new settingReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.phn.szupost.SETTING");
        registerReceiver(receiver, intentFilter);

        setsex=(TextView)findViewById(R.id.setsex);
        ssex=(TableRow)findViewById(R.id.ssex);
        sname=(TableRow)findViewById(R.id.sname);
        setlocation=(TextView)findViewById(R.id.setlocation);
        slocation=(TableRow)findViewById(R.id.slocation);
        setname=(TextView)findViewById(R.id.setname);
        setaccount=(TextView)findViewById(R.id.setaccount);
        set_back=(Button)findViewById(R.id.set_back);
        set_sure=(Button)findViewById(R.id.set_sure);
        socketapp=(SocketApplication) getApplication();
        ActionBar bar=getSupportActionBar();
        bar.setTitle("个人信息");
        bar.setDisplayHomeAsUpEnabled(true);
        setclick();
        setaccount.setText(socketapp.getUserdata(0));
        setname.setText(socketapp.getUserdata(1));
        setlocation.setText(socketapp.getUserdata(3));
        if(socketapp.getUserdata(2).equals("f"))
        setsex.setText("女");
        else
            setsex.setText("男");

    }
    //更改昵称返回
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode,data);
        switch (requestCode){
            case 1:
                if(resultCode==0)
                {
                    Log.i("date",data.getExtras().get("name").toString());
                    setname.setText(data.getExtras().get("name").toString());
                }
                break;
            default:
                break;
        }

    }

    //返回箭头事件
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 点击返回图标事件
                this.finish();
            default:
                break;
        }
        return  super.onOptionsItemSelected(item);
    }

    //设置点击事件
    public void setclick()
    {
      ssex.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              AlertDialog.Builder builder = new AlertDialog.Builder(Setting.this);
              builder.setSingleChoiceItems(R.array.sex, 0, new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                      ListView lw = ((AlertDialog) dialog).getListView();
                      // which表示点击的条目
                      Object checkedItem = lw.getAdapter().getItem(which);
                      // 既然你没有cancel或者ok按钮，所以需要在点击item后使dialog消失
                      dialog.dismiss();
                      // 更新你的view
                      setsex.setText((String)checkedItem);
                  }
              });

              AlertDialog dialog = builder.create();
              dialog.show();
          }
      });

        slocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Setting.this);
                builder.setSingleChoiceItems(R.array.address, 0, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ListView lw = ((AlertDialog) dialog).getListView();
                        // which表示点击的条目
                        Object checkedItem = lw.getAdapter().getItem(which);
                        // 既然你没有cancel或者ok按钮，所以需要在点击item后使dialog消失
                        dialog.dismiss();
                        // 更新你的view
                        setlocation.setText((String)checkedItem);
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        sname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(Setting.this,Changename.class);
                intent.putExtra("name",setname.getText());
                Setting.this.startActivityForResult(intent,1);
            }
        });
        set_sure.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                a[0]=(String) setaccount.getText();
                a[1]=(String) setname.getText();
                a[2]=(String) setsex.getText();
                a[3]=(String) setlocation.getText();
               socketapp.setUserdata(a);
                new MyThread().start();
                success=false;
                new Thread(new Runnable() {
                    public void run() {
                        int i;
                        for(i=0;i<10;i++) {
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
                            settingHandler.sendEmptyMessage(2);
                        }
                    }
                }).start();
            }
        });
        set_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Setting.this.finish();
            }
        });

    }



    //发送个人信息
    class MyThread extends Thread {
        MyThread(){}
       public void run() {
           if (!socketapp.isNetflag()) {
               if(socketapp.init()==-1)
               settingHandler.sendEmptyMessage(-1);
           }
               try {
                   String head = "SETTING";
                   JSONObject json = new JSONObject();
                   json.put("head", head);
                   json.put("photo",null);
                   json.put("name",a[1]);
                   if(a[2].equals("男"))
                   json.put("sex","m");
                   else
                       json.put("sex","f");
                   json.put("location",a[3]);
                   socketapp.sendString(json.toString(), socketapp.getMysocket().getOutputStream());
                   Log.i("Setting", "发送成功！" + json.toString());
               } catch (Exception e) {
                   e.printStackTrace();
               }

       }
    }



    Handler settingHandler = new Handler() {
        public void handleMessage(Message msg) {    //接受服务器信息更新UI
            switch (msg.what) {
                case 0:
                    try {
                        //将handler中发送过来的消息创建json对象
                        Bundle bundle = msg.getData();
                        JSONObject json = new JSONObject( bundle.getString("return"));
                        Log.i("send","接收成功！"+json.getString("head")+" "+json.getString("isSuccess"));
                        if (json.getString("head").equals("SETTING")) {
                            if (json.getBoolean("isSuccess")) {
                                socketapp.setUserflag(true);
                                Toast.makeText(Setting.this,"设置成功",Toast.LENGTH_SHORT).show();
                                Intent intent1=new Intent();
                                intent1.putExtra("name",setname.getText());
                                setResult(0,intent1);
                                Setting.this.finish();
                            }
                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Setting.this);
                                //    设置Content来显示一个信息
                                builder.setMessage("设置失败！请重新尝试");
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
                    Toast toast=Toast.makeText(Setting.this,"网络异常，请检查网络后重试！",Toast.LENGTH_SHORT);
                    toast.show();
                    break;
                case 2:
                    Toast.makeText(Setting.this, "连接超时，请重试！", Toast.LENGTH_SHORT).show();
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
    public class settingReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            Message msg=new Message();
            Bundle bundle=new Bundle();
            bundle.putString("return",intent.getStringExtra("returndata"));
            msg.setData(bundle);
            msg.what=0;
            success=true;
            settingHandler.sendMessage(msg);
        }

    }

    public void onBackPressed() {
        Intent intent=new Intent();
        intent.putExtra("name",setname.getText());
        setResult(1,intent);
        this.finish();
    }


}
