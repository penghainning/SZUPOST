package com.example.phn.szupost;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class Chat extends AppCompatActivity {

	private ListView msgListView;
    private SocketApplication socketapp;
	private EditText inputText;
	private Button send;
	private MsgAdapter adapter;
	private List<Msg> msgList;
	private String account;
	private String name;
	private ChatHelper chatHelper;
	private chatReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.chat_layout);
		ActivityManager.activityList.add(this);
		getIntent().getStringExtra("account");
		socketapp=(SocketApplication) getApplication();
		account=getIntent().getStringExtra("account");
		name=getIntent().getStringExtra("name");
		ActionBar acb=getSupportActionBar();
		acb.setTitle(name);
		chatHelper=new ChatHelper(this,null,1);
		chatHelper.getWritableDatabase();
		msgList= new ArrayList<Msg>();
		adapter = new MsgAdapter(Chat.this, R.layout.msg_item, msgList);
		inputText = (EditText) findViewById(R.id.input_text);
		send = (Button) findViewById(R.id.send);
		msgListView = (ListView) findViewById(R.id.msg_list_view);
		msgListView.setAdapter(adapter);
		update(0);

		//动态注册广播接收器
		receiver = new chatReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.example.phn.szupost.SEND");
		registerReceiver(receiver, intentFilter);

		send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String content = inputText.getText().toString();
				if (!"".equals(content)) {
					Msg msg = new Msg(content, Msg.TYPE_SENT);
					//插入数据库
					ContentValues values=new ContentValues();
					values.put("content",content);
					values.put("time",TimeManager.getTime());
					values.put("state",0);
					values.put("type",0);
					chatHelper.insert(values,account);
					msgList.add(msg);
					adapter.notifyDataSetChanged();
					msgListView.setSelection(msgList.size());
					inputText.setText("");
					new Thread(new SendThread(content)).start();

				}
			}
		});
	}


	class SendThread extends Thread {//接受服务器信息的线程
		private String msg;
		SendThread(String s1){
			msg=s1;

		}
		public void run() {
			try {
				String head = "SEND";
				JSONObject json = new JSONObject();
				json.put("head", head);
				json.put("to", account);
				json.put("name",socketapp.getUserdata(1));
				json.put("from",socketapp.getUserdata(0));
				json.put("msg",msg);
				socketapp.sendString(json.toString(),socketapp.getMysocket().getOutputStream());
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {    //接受服务器信息更新UI
			switch (msg.what) {
				case 0:
					update(1);
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
	public class chatReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			myHandler.sendEmptyMessage(0);
		}

	}

	//更新list
	public void update(int n)
	{
		if(chatHelper.myselect(account))
		{
			Cursor cursor=chatHelper.query(account,n);
			if(n==1)
			{
				if(cursor.moveToFirst())
				{
					do
					{
						//int state=cursor.getInt(cursor.getColumnIndex("state"));
						String content=cursor.getString(cursor.getColumnIndex("content"));
						Msg m = new Msg(content, Msg.TYPE_RECEIVED);
						msgList.add(m);

					}while (cursor.moveToNext());
					chatHelper.update(account);
					adapter.notifyDataSetChanged();
					msgListView.setSelection(msgList.size());
				}

				cursor.close();
			}
			else if(n==0)
			{

				if(cursor.moveToFirst())
				{
					do
					{
						int type=cursor.getInt(cursor.getColumnIndex("type"));
						String content=cursor.getString(cursor.getColumnIndex("content"));
						if(type==1)
						{
							Msg m = new Msg(content, Msg.TYPE_RECEIVED);
							msgList.add(m);
						}
						else
						{
							Msg m = new Msg(content, Msg.TYPE_SENT);
							msgList.add(m);
						}


					}while (cursor.moveToNext());
					adapter.notifyDataSetChanged();
					msgListView.setSelection(msgList.size());
				}
				cursor.close();
			}


		}
		else
		{
			chatHelper.createTable(account);
		}

	}




}
