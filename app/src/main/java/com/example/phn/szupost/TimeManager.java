package com.example.phn.szupost;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by PHN on 2016/7/22.
 */
public class TimeManager {
    public static String getTime()
    {
        SimpleDateFormat formatter = new   SimpleDateFormat    ("MM月dd日  HH:mm:ss     ");
        Date curDate =  new   Date(System.currentTimeMillis());//获取当前时间
        String  time =  formatter.format(curDate);
        return time;
    }

}
