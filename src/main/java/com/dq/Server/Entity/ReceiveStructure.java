package com.dq.Server.Entity;

import com.sun.org.apache.xpath.internal.operations.String;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * 针对每个连接的数据结构，储存断包后的部分，保证在消息完整后，才提交任务。
 */
public class ReceiveStructure {
    public Integer size;//未读的消息的长度
    public byte[] information;
    public int length;//记录information中已经记录的数据的位置
    public ByteBuffer buffer;
    public boolean isProcessed;//防止调用多个线程处理同一个任务
    public ReceiveStructure(){
        size=0;
        buffer=ByteBuffer.allocate(128);
        length=0;
        isProcessed=false;
    }
}
