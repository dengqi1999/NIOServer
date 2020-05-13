package com.dq.Server.function;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dq.Server.Entity.ReceiveStructure;
import com.dq.Server.Server;
import com.dq.Server.controller.FunctionController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class Function implements Runnable{
    private SelectionKey key;

    public SelectionKey getKey() {
        return key;
    }

    public void setKey(SelectionKey key) {
        this.key = key;
    }

    @Override
    public void run() {
        readMsg(key);
    }
    /**
     * 负责读取指定通道的数据，并执行对应任务
     * @param key
     */
    public void readMsg(SelectionKey key) {
        SocketChannel channel = null;
        try {
            channel = (SocketChannel) key.channel();
            //获取消息结构
            ReceiveStructure receiveStructure = (ReceiveStructure) key.attachment();
            //假如客户端关闭了通道，这里在对该通道read数据，会发生IOException，捕获到Exception后，关闭掉该channel，取消掉该key
            int count = channel.read(receiveStructure.buffer);

            while(count > 0){//读取到数据
                receiveStructure.buffer.flip();//切换buffer状态
                while(receiveStructure.buffer.hasRemaining()){//buffer数据未处理完毕
                    splitMsgAndExecute(key,receiveStructure,receiveStructure.buffer.remaining());
                }
                receiveStructure.buffer.clear();//清空buffer
                count = channel.read(receiveStructure.buffer);
            }
            //重新监听读取事件
            key.interestOps(key.interestOps() | SelectionKey.OP_READ);
            key.selector().wakeup();
        } catch (IOException e) {
            //当客户端关闭channel时，服务端再往通道缓冲区中写或读数据，都会报IOException，解决方法是：在服务端这里捕获掉这个异常，并且关闭掉服务端这边的Channel通道
            key.cancel();
            //清空用户在线引用
            Server.pool.execute(new CleanOnlineUser());
            try {
                channel.socket().close();
                channel.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 从指定的数据结构中读取数据并执行消息对应任务
     * @param key
     * @param receiveStructure
     * @param count
     * @throws UnsupportedEncodingException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public void splitMsgAndExecute(SelectionKey key,ReceiveStructure receiveStructure,int count) throws UnsupportedEncodingException, InstantiationException, IllegalAccessException {
        int start=0;
        if(receiveStructure.size==0){
            byte[]bytes=new byte[2];
            receiveStructure.buffer.get(bytes,0,2);
            receiveStructure.size=(bytes[0]&0xff)+((bytes[1]&0xff)<<8);
            start=2;
        }
        if(receiveStructure.size>count){//发生断包的问题或者buffer太小，不能一次性接受完成
            byte[]dst=new byte[count];
            receiveStructure.buffer.get(dst,0,count-start);
            receiveStructure.information.append(new String(dst,"UTF-8"));
            receiveStructure.size=receiveStructure.size-count+start;
        }else if(receiveStructure.size<receiveStructure.buffer.remaining()){//发生粘包
            byte[]dst=new byte[receiveStructure.size];
            receiveStructure.buffer.get(dst,0,receiveStructure.size);
            receiveStructure.information.append(new String(dst,"UTF-8"));
            //转换消息并提交执行
            System.out.println(receiveStructure.information.toString());
            JSONObject infor= JSON.parseObject(receiveStructure.information.toString());
            MyFunction myfunction= FunctionController.getFunction(infor.getInteger("code"));
            myfunction.run(key,infor);
            //更新结构体状态
            receiveStructure.size=0;
            receiveStructure.information.setLength(0);
        }else{//刚刚好
            byte[]dst=new byte[count];
            receiveStructure.buffer.get(dst,0,count-start);
            receiveStructure.information.append(new String(dst,"UTF-8"));
            //转换消息并提交执行
            System.out.println(receiveStructure.information.toString());
            JSONObject infor=JSON.parseObject(receiveStructure.information.toString());
            MyFunction myfunction= FunctionController.getFunction(infor.getInteger("code"));
            myfunction.run(key,infor);
            receiveStructure.size=0;
            receiveStructure.information.setLength(0);
        }
    }
}
