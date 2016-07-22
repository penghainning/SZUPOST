package com.example.phn.szupost;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.jauker.widget.BadgeView;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;

public class HomePage extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener,ViewPager.OnPageChangeListener{

    private RadioGroup rg_tab_bar;
    private RadioButton receivelist;
    private RadioButton sendlist;
    private RadioButton rb_better;
    private ViewPager vpager;
    static int msgi=0;
    private ActionBar acb;
    private SocketApplication socketapp;
    private MessageService msgservice;
    private MyFragmentPagerAdapter mAdapter;
    private HashMap<String,List<String>> hmap;
    private ChatHelper chatHelper;
    public static final int PAGE_ONE = 0;
    public static final int PAGE_TWO = 1;
    public static final int PAGE_THREE = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_layout);
        init();
    }


    public void init()
    {
        ActivityManager.activityList.add(this);
        socketapp=(SocketApplication)getApplication();
        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());//适配器
        Intent intent = new Intent(this, MessageService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        rg_tab_bar = (RadioGroup) findViewById(R.id.rg_tab_bar);
        receivelist = (RadioButton) findViewById(R.id.receivelist);
        sendlist = (RadioButton) findViewById(R.id.sendlist);
        rb_better = (RadioButton) findViewById(R.id.rb_better);
        vpager = (ViewPager) findViewById(R.id.vpager);
        rg_tab_bar.setOnCheckedChangeListener(this);
        acb = getSupportActionBar();
        acb.setTitle("接单");
        acb.show();
        vpager.setAdapter(mAdapter);
        vpager.setCurrentItem(0);
        vpager.addOnPageChangeListener(this);
        receivelist.setChecked(true);
        chatHelper=new ChatHelper(this,null,1);
        chatHelper.getWritableDatabase();

    }


   //绑定服务所需要的类
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //返回一个Service对象
            msgservice = ((MessageService.msgbinder) service).getService();
            msgservice.receive(socketapp);
            //注册回调接口来接收信息
            msgservice.setListener(new msgListener() {
                public void onFinish(String content) {
                    Myhandel(content);
                }
            });

        }
    };

    //创建菜单
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.msg, menu);
        return true;
    }
    //菜单点击事件
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.message:
               Intent intent=new Intent(this,Chatlist.class);
                startActivity(intent);
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    //检查按钮是否按下，按下切换fragment
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.receivelist:
                vpager.setCurrentItem(PAGE_ONE);
                acb.setTitle("接单");
                break;
            case R.id.sendlist:
                vpager.setCurrentItem(PAGE_TWO);
                acb.setTitle("下单");
                break;
            case R.id.rb_better:
                vpager.setCurrentItem(PAGE_THREE);
                acb.setTitle("我");
                break;
        }
    }


    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }


    public void onPageSelected(int position) {
    }

    public void onPageScrollStateChanged(int state) {
        //state的状态有三个，0表示什么都没做，1正在滑动，2滑动完毕
        if (state == 2) {
            switch (vpager.getCurrentItem()) {
                case PAGE_ONE:
                    receivelist.setChecked(true);
                    break;
                case PAGE_TWO:
                    sendlist.setChecked(true);
                    break;
                case PAGE_THREE:
                    rb_better.setChecked(true);
                    break;

            }
        }
    }

    protected void onDestroy() {
        unbindService(conn);
        socketapp.closeMysocket();
        super.onDestroy();
    }

    public void Myhandel(String s)
    {
        Intent intent;
        String head=null;
        try {
            JSONObject json = new JSONObject(s);
             head = json.getString("head");
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        switch(head)
        {
            case "SUM":
                SendFragment sf=(SendFragment)vpager.getAdapter().instantiateItem(vpager,1);
                sf.Flush(s);
                break;
            case "FLUSH":
                ReceiveFragment rf=(ReceiveFragment)vpager.getAdapter().instantiateItem(vpager,0);
                rf.Flush(s);
                break;
            case "MORE":
                ReceiveFragment rf2=(ReceiveFragment)vpager.getAdapter().instantiateItem(vpager,0);
                rf2.Flush(s);
                break;
            case "GETA":
                //发送广播
                 intent = new Intent("com.example.phn.szupost.RECEIVER");
                intent.putExtra("type",2);
                intent.putExtra("returndata",s);
                sendBroadcast(intent);
                break;
            case "ACCEPT":
                //发送广播
                intent = new Intent("com.example.phn.szupost.RECEIVER");
                intent.putExtra("type",1);
                intent.putExtra("returndata",s);
                sendBroadcast(intent);
                break;
            case "SETTING":
                //发送广播
                 intent = new Intent("com.example.phn.szupost.SETTING");
                 intent.putExtra("returndata",s);
                sendBroadcast(intent);
                break;
            case "JMORE":
                //发送广播
                intent = new Intent("com.example.phn.szupost.RLIST");
                intent.putExtra("returndata",s);
                sendBroadcast(intent);
                break;
            case "JFLUSH":
                //发送广播
                intent = new Intent("com.example.phn.szupost.RLIST");
                intent.putExtra("returndata",s);
                sendBroadcast(intent);
                break;
            case "XMORE":
                //发送广播
                intent = new Intent("com.example.phn.szupost.SLIST");
                intent.putExtra("returndata",s);
                sendBroadcast(intent);
                break;
            case "XFLUSH":
                //发送广播
                intent = new Intent("com.example.phn.szupost.SLIST");
                intent.putExtra("returndata",s);
                sendBroadcast(intent);
                break;
            case "XDETAIL":
                //发送广播
                intent = new Intent("com.example.phn.szupost.XDETAIL");
                intent.putExtra("returndata",s);
                sendBroadcast(intent);
                break;
            case "JDETAIL":
                //发送广播
                intent = new Intent("com.example.phn.szupost.JDETAIL");
                intent.putExtra("returndata",s);
                intent.putExtra("type","1");
                sendBroadcast(intent);
                break;
            case "JFLAG":
                //发送广播
                intent = new Intent("com.example.phn.szupost.JDETAIL");
                intent.putExtra("returndata",s);
                intent.putExtra("type","2");
                sendBroadcast(intent);
                break;
            case "SEND":
                try{
                     JSONObject json=new JSONObject(s);
                    String tablename=json.getString("from");
                    String username=json.getString("name");
                    String msg=json.getString("msg");
                    if(!chatHelper.myselect(tablename))
                    {
                        chatHelper.createTable(tablename);
                    }
                    //插入数据库
                    ContentValues values=new ContentValues();
                    values.put("content",msg);
                    values.put("time",TimeManager.getTime());
                    values.put("username",username);
                    values.put("state",1);
                    values.put("type",1);
                    chatHelper.insert(values,tablename);
                    View view = findViewById(R.id.message);
                    BadgeView badgeview = new BadgeView(HomePage.this);
                    badgeview.setTargetView(view);
                    badgeview.setBadgeCount(msgi);
                    msgi++;
                    //判断聊天界面是否存在
                    Intent tent = new Intent();
                    tent.setClassName(getPackageName(),"Chat");
                    if (getPackageManager().resolveActivity(tent, 0) != null)
                    {
                        //发送广播
                        intent = new Intent("com.example.phn.szupost.SEND");
                        sendBroadcast(intent);
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                break;
            default:
                break;

        }
    }

}
