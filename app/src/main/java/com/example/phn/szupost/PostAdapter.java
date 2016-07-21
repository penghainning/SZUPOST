package com.example.phn.szupost;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;


public class PostAdapter extends BaseAdapter{

    private List<Data> mData;
    private Context mContext;

    public PostAdapter(List<Data> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.postlist_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.mylocation=(TextView)convertView.findViewById(R.id.mylocation);
            viewHolder.address=(TextView) convertView.findViewById(R.id.address);
            viewHolder.mydeadline=(TextView) convertView.findViewById(R.id.mydeadline);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(mData.get(position).getName());
        viewHolder.mylocation.setText(mData.get(position).getLocation());
        viewHolder.address.setText(mData.get(position).getAddress());
        viewHolder.mydeadline.setText(mData.get(position).getDeadline());
        return convertView;
    }

    private class ViewHolder{//listview里的数据
        TextView name;
        TextView mylocation;
        TextView address;
        TextView mydeadline;
    }

}
