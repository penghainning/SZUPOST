package com.example.phn.szupost;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by PHN on 2016/7/22.
 */
public class ChatHelper extends SQLiteOpenHelper {

    private List<String> tableList = new ArrayList<String>();
    private Context mcontext;
    //SQLiteDatabase实例
    private SQLiteDatabase db;
    //数据库名称
    private static final String DB_NAME="chat.db";

    ChatHelper(Context contex, SQLiteDatabase.CursorFactory factory,int version)
    {
        super(contex,DB_NAME,factory,version);
        this.mcontext=contex;

    }

    //创建
    public void onCreate(SQLiteDatabase db)
    {
        this.db=getWritableDatabase();
        Log.i("chathelper: ","建表成功！");
    }

    //升级
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

    /*
     * 插入方法
     */
    public void insert(ContentValues values, String tablename)
    {
        //获得SQLiteDatabase实例
        SQLiteDatabase db=getWritableDatabase();
        //插入
        db.insert(tablename, null, values);
        Log.i("ChatHelper", "插入成功");
        //关闭
        db.close();
    }
    /*
     * 查询方法
     */
    public Cursor query(String tablename,int type)
    {
        Cursor c;
        //获得SQLiteDatabase实例
        SQLiteDatabase db=getWritableDatabase();
        //查询获得Cursor
        if(type==0)
        {
            c=db.query(tablename, null, null, null, null, null, "time");
        }

        else
        {
            c=db.query(tablename, null,"state=?",new String[]{"1"}, null, null, "time");
        }
        return c;
    }

    /*
     * 关闭数据库
     */
    public void colse()
    {
        if(db!=null)
        {
            db.close();
        }
    }
    /*
     * 建表方法
     */
    public void createTable(String tablename)
    {
        String createString="create table '"+tablename+" '("
                            +"time text, "
                            +"content text, "
                            +"username text, "
                            +"state integer, "
                            +"type integer)";
        //state=1:未读
        //type=1:收到的信息
        db.execSQL(createString);
        Log.i("ChatHelper", "创建成功" + tablename);
    }
    /**
     *查是否存在表面
     */
    public boolean  myselect(String tablename) {
        db=getWritableDatabase();
        Cursor cursor = db.rawQuery("select name from sqlite_master where type='table' order by name", null);
        while (cursor.moveToNext()) {
            //遍历出表名
            String name = cursor.getString(0);
            if (name.equals(tablename)) {
                Log.i("ChatHelper", "存在表" + name);
                return true;
            }

        }
        return false;
    }

    //更新数据
    public void update(String tablename)
    {
        ContentValues values=new ContentValues();
        values.put("state",0);
        db.update(tablename,values,"state=?",new String[]{"1"});
        db.close();
        Log.i("ChatHelper", "更新成功");

    }

    public List<String>gettableList()
    {
        tableList.clear();
        Cursor cursor = db.rawQuery("select name from sqlite_master where type='table' order by name", null);
        while (cursor.moveToNext()) {
            //遍历出表名
               tableList.add(cursor.getString(0));
            }

        return tableList;
    }



}
