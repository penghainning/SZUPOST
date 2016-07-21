package com.example.phn.szupost;

/**
 * Created by PHN on 2016/7/20.
 */
public interface SocketCallbackListener {
    void OnFinish(String s);
    void OnError(Exception e);
}
