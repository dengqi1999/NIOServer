package com.dq.Server.Entity;

import com.sun.org.apache.xpath.internal.operations.String;

import java.nio.ByteBuffer;

public class ReceiveStructure {
    public Integer size;
    public StringBuilder information;
    public ByteBuffer buffer;
    public ReceiveStructure(){
        size=0;
        information=new StringBuilder();
        buffer=ByteBuffer.allocate(128);
    }
}
