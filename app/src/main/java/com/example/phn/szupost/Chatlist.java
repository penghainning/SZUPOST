package com.example.phn.szupost;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Chatlist extends AppCompatActivity {
    private ListView chatlist;
    private ChatHelper chatHelper;
    private LinkedList<HashMap<String,String>> cList;
    private ChatlistAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chatlist_layout);
    }


    public void init()
    {
        ActivityManager.activityList.add(this);
        chatlist=(ListView)findViewById(R.id.chatlist);
        chatHelper=new ChatHelper(this,null,1);
        chatHelper.getWritableDatabase();
        cList= new LinkedList<HashMap<String,String>>();
        adapter=new ChatlistAdapter(cList,this);
        chatlist.setAdapter(adapter);
        chatlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String,String>m=(HashMap<String, String>) chatlist.getAdapter().getItem(position);
                Intent intent=new Intent(Chatlist.this,Chat.class);
                intent.putExtra("account",m.get("account"));
                intent.putExtra("name",m.get("name"));
                startActivity(intent);
            }
        });
        update();
    }

    public void update()
    {
        List<String>tablelist=chatHelper.gettableList();
        int noWatch=0;
        for(int i = 0 ; i< tablelist.size();i++)
        {
            String account=tablelist.get(i);
            Cursor cursor=chatHelper.query(account,0);
            if(cursor.moveToFirst())
            {
                do
                {
                    HashMap<String,String>m=new HashMap<>();
                    int state=cursor.getInt(cursor.getColumnIndex("state"));

                    if(state==1)
                    {
                        noWatch++;
                    }
                    if(cursor.moveToLast())
                    {
                        m.put("account",account);
                        m.put("content",  cursor.getString(cursor.getColumnIndex("content")));
                        m.put("name",  cursor.getString(cursor.getColumnIndex("username")));

                    }
                    m.put("number",String.valueOf(noWatch));
                    cList.add(m);
                }while (cursor.moveToNext());

            }
            cursor.close();
            noWatch=0;

        }
        adapter.notifyDataSetChanged();
        chatlist.setSelection(cList.size());
    }




}
