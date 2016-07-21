package com.example.phn.szupost;

import java.io.Serializable;

/**
 * Created by PHN on 2016/5/15.
 */
public class Data implements Serializable {

    private String name;//快递名称
    private String location;//地址信息
    private String address;//快递地址
    private String deadline;//截止日期
    private String id;//订单id
    private String onumber;//订单号
    public Data(){}

    public Data(String name, String location, String address, String deadline ) {
        this.name =name;
        this.location=location;
        this.address = address;
        this.deadline=deadline;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public String getDeadline() {
        return deadline;
    }
    public String getAddress() {
        return address;
    }

    public void setName(String name) { this.name=name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getOnumber() {
        return onumber;
    }

    public void setOnumber(String onumber) {
        this.onumber = onumber;
    }
}