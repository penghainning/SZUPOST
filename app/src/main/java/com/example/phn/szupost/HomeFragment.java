package com.example.phn.szupost;

/**
 * Created by PHN on 2016/7/20.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeFragment extends Fragment {
    private LinearLayout mysetting;
    private LinearLayout mymessage;
    private TextView myname;
    private SocketApplication socketapp;
    private TextView myaccount;
    private Button rabout;
    private Button sabout;

    public HomeFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment_layout, container, false);
        myaccount=(TextView)view.findViewById(R.id.myaccount);
        myname=(TextView)view.findViewById(R.id.myname);
        socketapp=(SocketApplication)getActivity().getApplication();
        myaccount.setText("账号： "+socketapp.getUserdata(0));
        myname.setText(socketapp.getUserdata(1));
        mymessage=(LinearLayout)view.findViewById(R.id.mymessage);
        mymessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Setting.class);
                startActivityForResult(intent,1);
            }
        });
        mysetting=(LinearLayout)view.findViewById(R.id.mysetting);
        mysetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Systemsetting.class);
                startActivity(intent);
            }
        });

        sabout=(Button)view.findViewById(R.id.sabout);
       sabout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SList.class);
                startActivity(intent);

            }
        });
        rabout=(Button)view.findViewById(R.id.rabout);
        rabout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RList.class);
                startActivity(intent);
            }
        });




        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode,data);
        switch (requestCode){
            case 1:
                if(resultCode==0)
                {
                    myname.setText(data.getExtras().get("name").toString());
                }
                break;
            default:
                break;
        }

    }
}
