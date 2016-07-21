package com.example.phn.szupost;

/**
 * Created by PHN on 2016/7/20.
 */
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONObject;

import java.util.ArrayList;

public class ReceiveFragment extends Fragment implements AdapterView.OnItemClickListener {
    //接单界面的fragment
    private ArrayList<Data> mData = new ArrayList<Data>();//加载的数据;
    private String ID;//加载更多时当前最后的订单id
    private ListView list = null;
    private String[] dd = new String[5];//刷新时从服务器接收到的数据数组
    PostAdapter myAdapter;
    private boolean flush = true;
    private boolean success=false;
    SocketApplication socketapp;
    private PullToRefreshListView mPullRefreshListView;//滑动刷新的listview


    public ReceiveFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.receive_fragment_layout, container, false);
        bindview(view);
        return view;
    }


    //接完单之后返回时自动刷新页面
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == 0) {
                    //下拉刷新
                    success=false;
                    new Thread(new GetHeaderDataTask()).start();
                    new Thread(new timeError()).start();
                }
                break;
            default:
                break;
        }

    }

    //界面初始化
    public void bindview(View view) {
        myAdapter = new PostAdapter(mData, getActivity());//绑定listview适配器
        socketapp = (SocketApplication) getActivity().getApplication();
        mPullRefreshListView = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_list);
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);//下拉刷新，下拉加载更多
        list = mPullRefreshListView.getRefreshableView();
        list.setAdapter(myAdapter);
        if (flush) {
            success=false;
            new Thread(new GetHeaderDataTask()).start();
            new Thread(new timeError()).start();
            flush = false;
        }
        list.setOnItemClickListener(this);//listview点击事件
        //下拉与上拉的相关设置
        // 下拉刷新时的提示文本设置
        mPullRefreshListView.getLoadingLayoutProxy(true, false).setLastUpdatedLabel("下拉刷新");
        mPullRefreshListView.getLoadingLayoutProxy(true, false).setPullLabel("");
        mPullRefreshListView.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在刷新");
        mPullRefreshListView.getLoadingLayoutProxy(true, false).setReleaseLabel("放开以刷新");
        // 上拉加载更多时的提示文本设置
        mPullRefreshListView.getLoadingLayoutProxy(false, true).setLastUpdatedLabel("上拉加载");
        mPullRefreshListView.getLoadingLayoutProxy(false, true).setPullLabel("");
        mPullRefreshListView.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
        mPullRefreshListView.getLoadingLayoutProxy(false, true).setReleaseLabel("放开以加载");
        // 添加 一个下拉刷新事件
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            public void onRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                if (refreshView.isHeaderShown()) {
                    //下拉刷新
                    success=false;
                    new Thread(new GetHeaderDataTask()).start();
                    new Thread(new timeError()).start();
                } else {
                    //加载更多
                    success=false;
                    new Thread(new GetBottomDataTask()).start();
                    new Thread(new timeError()).start();
                }
            }
        });

    }

    //点击进入订单详情
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), Receive_listdetail.class);
        Data d = (Data) list.getAdapter().getItem(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable("date", d);
        intent.putExtras(bundle);
        this.startActivityForResult(intent, 1);
        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    //下拉刷新线程
    public class GetHeaderDataTask extends Thread {
        public void run() {
            if (!socketapp.isNetflag()) {
                if(socketapp.init()==-1)
                receiveHandler.sendEmptyMessage(-1);
            } else {
                try {
                    String head = "FLUSH";
                    JSONObject json = new JSONObject();
                    json.put("head", head);
                    socketapp.sendString(json.toString(), socketapp.getMysocket().getOutputStream());
                    Log.i("ReceiveFragment", "发送成功！" + json.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //上滑加载线程
    public class GetBottomDataTask extends Thread {
        public void run() {
            if (!socketapp.isNetflag()) {
                if(socketapp.init()==-1)
                receiveHandler.sendEmptyMessage(-1);
            }
                try {
                    String head = "MORE";
                    JSONObject json = new JSONObject();
                    json.put("head", head);
                    json.put("id",ID);
                    socketapp.sendString(json.toString(), socketapp.getMysocket().getOutputStream());
                    Log.i("ReceiveFragment", "发送成功！" + json.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

        }
    }

    Handler receiveHandler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                //网络连接不上
                case -1:
                    Toast toast = Toast.makeText(getActivity(), "网络异常，请检查网络后重试！", Toast.LENGTH_SHORT);
                    toast.show();
                    mPullRefreshListView.onRefreshComplete();
                    break;
                //收到消息
                case 0:
                    try {
                        //将handler中发送过来的消息创建json对象
                        JSONObject jstring = new JSONObject(bundle.getString("return"));
                        if(jstring.get("head").equals("FLUSH"))
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
                    mPullRefreshListView.onRefreshComplete();
                    break;
                case 2:
                    Toast.makeText(getActivity(), "请求超时，请重试！", Toast.LENGTH_SHORT).show();
                    mPullRefreshListView.onRefreshComplete();
                default:
                    break;

            }
            super.handleMessage(msg);
        }
    };


    public void Flush(String jsonstring)
    {
        Message msg=new Message();
        Bundle bundle=new Bundle();
        bundle.putString("return",jsonstring);
        msg.setData(bundle);
        msg.what=0;
        success=true;
        receiveHandler.sendMessage(msg);
    }

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
                receiveHandler.sendEmptyMessage(2);
            }
        }

    }

}





