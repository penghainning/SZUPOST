package com.example.phn.szupost;

/**
 * Created by PHN on 2016/7/20.
 */
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Calendar;

//下单界面
public class SendFragment extends Fragment {
    private Spinner postlocation;
    private ScrollView sv;
    private Spinner usraddress;
    private NumberPicker month;
    private NumberPicker day;
    private NumberPicker hour;
    private EditText postname;
    private  EditText number;
    private  EditText usrname;
    private  EditText phonenumber;
    private Button clear;
    private Button  send;
    private boolean success=false;
    SocketApplication socketapp;

    public SendFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.send_fragment_layout, container, false);
        socketapp=(SocketApplication)getActivity().getApplication();
        bindspinner(view);//绑定spiner
        bindnumberclick(view);//绑定数字选择器
        bind(view);//绑定其余的控件
        return view;
    }

    //绑定spinner
    public  void  bindspinner(View view){
        postlocation =(Spinner) view.findViewById(R.id.postlocation);
        String[] mItems = getResources().getStringArray(R.array.location);//获取数据数组
        ArrayAdapter<String> mAdapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, mItems);//绑定spinner数据适配器
        postlocation.setAdapter(mAdapter);

        usraddress =(Spinner) view.findViewById(R.id.usraddress);
        String[] mItems2 = getResources().getStringArray(R.array.address);
        ArrayAdapter<String> mAdapter2=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, mItems2);
        usraddress.setAdapter(mAdapter2);
    }

    //绑定数字选择器
    public void bindnumberclick(View view){
        hour=(NumberPicker)view.findViewById(R.id.hourpicker);
        month=(NumberPicker)view.findViewById(R.id.monthpicker);
        day=(NumberPicker)view.findViewById(R.id.daypicker);
        Calendar c = Calendar.getInstance();
        hour.setMaxValue(24);
        hour.setMinValue(0);
        hour.setValue(c.get(Calendar.HOUR_OF_DAY));
        month.setMaxValue(12);
        month.setMinValue(1);
        month.setValue(c.get(Calendar.MONTH)+1);
        day.setMaxValue(31);
        day.setMinValue(1);
        day.setValue(c.get(Calendar.DAY_OF_MONTH));
    }

    //绑定视图
    public  void  bind(View view){
        postname=(EditText) view.findViewById(R.id.postname);
        number=(EditText) view.findViewById(R.id.number);
        usrname=(EditText) view.findViewById(R.id.usrname);
        phonenumber=(EditText) view.findViewById(R.id.phonenumber);
        sv=(ScrollView)view.findViewById(R.id.sv);
        send=(Button)view.findViewById(R.id.send);
        clear=(Button)view.findViewById(R.id.clear);
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if ((phonenumber.length() == 0) || (postname.length() == 0) || number.length() == 0||usrname.length()==0) {
                    Toast toast = Toast.makeText(getActivity(), "上述内容不能为空！请重新输入", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else
                {
                    String d[]=senddate();
                    new MyThread(d).start();
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
                            if(i==10)
                            {
                                receiveHandler.sendEmptyMessage(2);
                            }
                        }
                    }).start();

                }
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postname.setText("");
                number.setText("");
                usrname.setText("");
                phonenumber.setText("");
                postlocation.setSelection(0);
                usraddress.setSelection(0);
                sv.fullScroll(View.FOCUS_UP);
            }
        });
    }
    //要发送的数据
    public String[]senddate(){
        final  String Postname = postname.getText().toString().trim();
        final  String Number = number.getText().toString().trim();
        final  String Usrname = usrname.getText().toString().trim();
        final  String Phonenumber =phonenumber.getText().toString().trim();
        String []a=new String[7];
        a[0]=Postname;
        a[1]=Number;
        a[2]=Usrname;
        a[3]=Phonenumber;
        a[4]=(String) usraddress.getSelectedItem();
        a[5]=(String) postlocation.getSelectedItem();
        a[6]=String.valueOf(month.getValue())+"-"+String.valueOf(day.getValue())+"-"+String.valueOf(hour.getValue());
        for(int i=0;i<7;i++)
            Log.i("date",a[i]);
        return a;

    }

    //发送事件线程
    public class MyThread extends Thread {
        String []d;
        MyThread(String []d)
        {
            this.d=d;
        }
        public void run() {

            //如果权限不够
            if(!socketapp.isUserflag())
            {
                receiveHandler.sendEmptyMessage(1);
            }
            else
            {
                if (!socketapp.isNetflag()) {
                    if (socketapp.init() == -1)
                        receiveHandler.sendEmptyMessage(-1);
                }
                try {

                    String head = "SUM";
                    JSONObject json = new JSONObject();
                    json.put("head", head);
                    json.put("postname", d[0]);
                    json.put("number", d[1]);
                    json.put("usrname", d[2]);
                    json.put("phonenumber",d[3]);
                    json.put("adress", d[4]);
                    json.put("postlocation", d[5]);
                    json.put("deadline", d[6]);
                    socketapp.sendString(json.toString(), socketapp.getMysocket().getOutputStream());
                    Log.i("SendFragment", "发送成功！" + json.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                    break;
                //收到消息
                case 0:
                    try {
                        //将handler中发送过来的消息创建json对象
                        JSONObject jstring = new JSONObject(bundle.getString("return"));
                        if(jstring.getBoolean("isSuccess"))
                        {
                            Toast.makeText(getActivity(), "提交成功！", Toast.LENGTH_SHORT).show();
                            postname.setText("");
                            number.setText("");
                            usrname.setText("");
                            phonenumber.setText("");
                            postlocation.setSelection(0);
                            usraddress.setSelection(0);
                            sv.fullScroll(View.FOCUS_UP);//全部清空并且视图在最上方
                        }
                        else {
                            Toast.makeText(getActivity(), "下单失败，请稍后重试！", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    //    设置Title的内容
                    builder.setTitle("权限不够");
                    //    设置Content来显示一个信息
                    builder.setMessage("请先完善好个人资料！");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    });
                case 2:
                    Toast.makeText(getActivity(), "连接超时，请重试！", Toast.LENGTH_SHORT).show();
                    break;
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
}
