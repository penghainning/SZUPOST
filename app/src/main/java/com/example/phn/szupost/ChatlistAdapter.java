package com.example.phn.szupost;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by PHN on 2016/7/22.
 */
public class ChatlistAdapter extends BaseAdapter {
    private LinkedList<HashMap<String,String>> maplist;
    private Context mContext;
    private ViewHolder holder = null;

    public ChatlistAdapter(LinkedList<HashMap<String,String>>maplist, Context mContext)
    {
        this.mContext=mContext;
        this.maplist=maplist;
    }
    public int getCount() {
        return maplist.size();
    }
    public HashMap<String,String> getItem(int position) {
        return maplist.get(position);
    }
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {


        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.chatlist_item, parent, false);
            holder = new ViewHolder();
            holder.chat_name = (TextView) convertView.findViewById(R.id.chat_username);
            holder.chat_content = (TextView) convertView.findViewById(R.id.chat_content);
            holder.chat_number = (TextView) convertView.findViewById(R.id.chat_number);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(maplist.get(position).get("number").equals("0"))
        {
            holder.chat_content.setText(maplist.get(position).get("content"));
            holder.chat_name.setText(maplist.get(position).get("name"));
            holder.chat_number.setVisibility(View.GONE);

        }
        else
        {
            holder.chat_content.setText(maplist.get(position).get("content"));
            holder.chat_name.setText(maplist.get(position).get("name"));
            holder.chat_number.setText(maplist.get(position).get("number"));
            holder.chat_number.setVisibility(View.VISIBLE);
        }

        return convertView;

    }
    static class ViewHolder{
        TextView chat_name;
        TextView chat_content;
        TextView chat_number;
    }



}
