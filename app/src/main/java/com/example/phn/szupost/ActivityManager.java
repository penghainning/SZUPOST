package com.example.phn.szupost;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PHN on 2016/7/20.
 */
public class ActivityManager {
    //activity管理者

       public static List<Activity> activityList = new ArrayList<Activity>();
    //所有的activity构成的list
       public static void exitClient()
        {
            // 关闭所有Activity
            for (int i = 0; i < activityList.size(); i++)
            {
                if (null != activityList.get(i))
                {
                    activityList.get(i).finish();
                }
            }
        }

}
