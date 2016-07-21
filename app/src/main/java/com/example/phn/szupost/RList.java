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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONObject;

import java.util.ArrayList;

public class RList extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ArrayList<Data> mData = new ArrayList<Data>();//加载的数据;
    private String ID;//加载更多时当前最后的订单id
    private ListView list = null;
    private String[] dd = new String[5];//刷新时从服务器接收到的数据数组
    PostAdapter myAdapter;
    SocketApplication socketapp;
    private  rlistReceiver receiver;//广播
    private PullToRefreshListView rRefreshListView;//滑动刷新的listview
    private boolean success=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rlist_layout);
        bindview();
    }



    //初始化
    public void bindview() {
        myAdapter = new PostAdapter(mData, this);//绑定listview适配器
        socketapp = (SocketApplication)getApplication();


        receiver = new rlistReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.phn.szupost.RLIST");
        registerReceiver(receiver, intentFilter);

        rRefreshListView = (PullToRefreshListView) findViewById(R.id.r_refresh_list);
        rRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);//下拉刷新，下拉加载更多
        list = rRefreshListView.getRefreshableView();
        list.setAdapter(myAdapter);
        success=false;
        UPorDownflush(1);
        new Thread(new timeError()).start();
        list.setOnItemClickListener(this);//listview点击事件
        //下拉与上拉的相关设置
        // 下拉刷新时的提示文本设置
        rRefreshListView.getLoadingLayoutProxy(true, false).setLastUpdatedLabel("下拉刷新");
        rRefreshListView.getLoadingLayoutProxy(true, false).setPullLabel("");
        rRefreshListView.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在刷新");
        rRefreshListView.getLoadingLayoutProxy(true, false).setReleaseLabel("放开以刷新");
        // 上拉加载更多时的提示文本设置
        rRefreshListView.getLoadingLayoutProxy(false, true).setLastUpdatedLabel("上拉加载");
        rRefreshListView.getLoadingLayoutProxy(false, true).setPullLabel("");
        rRefreshListView.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
        rRefreshListView.getLoadingLayoutProxy(false, true).setReleaseLabel("放开以加载");
        // 添加 一个下拉刷新事件
        rRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            public void onRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                if (refreshView.isHeaderShown()) {
                    //下拉刷新
                    success=false;
                    UPorDownflush(1);
                    new Thread(new timeError()).start();
                } else {
                    //加载更多
                    success=false;
                    UPorDownflush(2);
                    new Thread(new timeError()).start();
                }
            }
        });

    }


    //点击进入订单详情
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, JListdetail.class);
        Data d = (Data) list.getAdapter().getItem(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable("date", d);
        intent.putExtras(bundle);
        this.startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
    //刷新方法：
    public void UPorDownflush(final int n)
    {
        new Thread((new Runnable() {
            public void run() {
                if (!socketapp.isNetflag()) {
                    if(socketapp.init()==-1)
                        rlistHandler.sendEmptyMessage(-1);
                } else {
                    try {
                        if(n==1)
                        {
                            String head = "JFLUSH";
                            JSONObject json = new JSONObject();
                            json.put("head", head);
                            json.put("account",socketapp.getUserdata(0));
                            socketapp.sendString(json.toString(), socketapp.getMysocket().getOutputStream());
                            Log.i("RList", "发送成功！" + json.toString());
                        }
                        else
                        {
                            String head = "JMORE";
                            JSONObject json = new JSONObject();
                            json.put("head", head);
                            json.put("account",socketapp.getUserdata(0));
                            json.put("id",ID);
                            socketapp.sendString(json.toString(), socketapp.getMysocket().getOutputStream());
                            Log.i("RList", "发送成功！" + json.toString());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        })).start();
    }
    Handler rlistHandler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                //网络连接不上
                case -1:
                    Toast toast = Toast.makeText(RList.this, "网络异常，请检查网络后重试！", Toast.LENGTH_SHORT);
                    toast.show();
                    rRefreshListView.onRefreshComplete();
                    break;
                //收到消息
                case 0:
                    try {
                        //将handler中发送过来的消息创建json对象
                        JSONObject jstring = new JSONObject(bundle.getString("return"));
                        if(jstring.get("head").equals("JFLUSH"))
                            mData.clear();
                        JSONObject[]js=new JSONObject[5];
                        for(int i=0;i<5;i++)
                        {
                            String mes=Integer.toString(i+1);

                            if(jstring.getString(mes)!=null)
                            {
                                dd[i]=jstring.getString(mes);
                                js[i] = new JSONObject(dd[i]);
                                String dead[]=js[i].getString("deadline").split("-");
                                String deadline=dead[0]+"月"+dead[1]+"日"+dead[2]+"时";
                                Data data=new Data(js[i].getString("postname"),js[i].getString("postlocation"),js[i].getString("adress"),deadline);
                                data.setId(js[i].getString("id"));
                                data.setOnumber(js[i].getString("onumber"));
                                mData.add(data);
                                ID=js[i].getString("id");
                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    myAdapter.notifyDataSetChanged();
                    // 停止刷新
                    rRefreshListView.onRefreshComplete();
                    break;
                case 2:
                    Toast.makeText(RList.this, "请求超时，请重试！", Toast.LENGTH_SHORT).show();
                    rRefreshListView.onRefreshComplete();
                default:
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
    public class rlistReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            Message msg=new Message();
            Bundle bundle=new Bundle();
            bundle.putString("return",intent.getStringExtra("returndata"));
            msg.setData(bundle);
            msg.what=0;
            success=true;
            rlistHandler.sendMessage(msg);
        }

    }

    //c超时线程
    public class timeError extends Thread
    {
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
                rlistHandler.sendEmptyMessage(2);
            }
        }

    }





}
