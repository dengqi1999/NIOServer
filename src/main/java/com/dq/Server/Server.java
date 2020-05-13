package com.dq.Server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dq.Server.Entity.ReceiveStructure;
import com.dq.Server.Entity.User;
import com.dq.Server.controller.UserController;
import com.dq.Server.function.CleanOnlineUser;
import com.dq.Server.function.Function;
import com.dq.Server.controller.FunctionController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server {
    private static final int BUF_SIZE=1024;
    private static final int PORT = 9000;
    private static final int TIMEOUT = 3000;
    //线程池，负责执行后续任务
    public static ThreadPoolExecutor pool=new ThreadPoolExecutor(4,4,10, TimeUnit.MINUTES,new LinkedBlockingQueue<Runnable>());

    private Selector selector;
    private SelectionKey serverKey;

    public Server(){
        init();
    }

    public void init(){
        try {
            selector = Selector.open();
            //创建serverSocketChannel
            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress(PORT));
            //加入到selector中
            serverChannel.configureBlocking(false);
            serverKey = serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("server starting.......");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while(true){
                //获取就绪channel
                int count = selector.select();
                if(count > 0){
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while(iterator.hasNext()){
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        //若此key的通道是等待接受新的套接字连接
                        if(key.isAcceptable()){
                            System.out.println(key.toString() + " : 接收");
                            ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
                            //接受socket
                            SocketChannel socket = serverChannel.accept();
                            socket.configureBlocking(false);
                            //将channel加入到selector中，并一开始读取数据
                            socket.register(selector, SelectionKey.OP_READ,new ReceiveStructure());
                        }
                        //若此key的通道是有数据可读状态
                        if(key.isValid() && key.isReadable()){
                            System.out.println(key.toString() + " : 读");
                            readMsg(key);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readMsg(SelectionKey key) {
        SocketChannel channel = null;
        try {
            channel = (SocketChannel) key.channel();
            //获取消息结构
            ReceiveStructure receiveStructure = (ReceiveStructure) key.attachment();
            //假如客户端关闭了通道，这里在对该通道read数据，会发生IOException，捕获到Exception后，关闭掉该channel，取消掉该key
            int count = channel.read(receiveStructure.buffer);

            while(count > 0){//读取到数据或者数据未处理完毕
                receiveStructure.buffer.flip();
                while(receiveStructure.buffer.hasRemaining()){
                    splitMsgAndExecute(key,receiveStructure,receiveStructure.buffer.remaining());
                }
                receiveStructure.buffer.clear();
                count = channel.read(receiveStructure.buffer);
            }
            //重新监听读取事件
            key.interestOps(key.interestOps() | SelectionKey.OP_READ);
            key.selector().wakeup();
        } catch (IOException e) {
            //当客户端关闭channel时，服务端再往通道缓冲区中写或读数据，都会报IOException，解决方法是：在服务端这里捕获掉这个异常，并且关闭掉服务端这边的Channel通道
            key.cancel();
            pool.execute(new CleanOnlineUser());
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
    public void splitMsgAndExecute(SelectionKey key,ReceiveStructure receiveStructure,int count) throws UnsupportedEncodingException, InstantiationException, IllegalAccessException {
        int start=0;
        if(receiveStructure.size==0){
            byte[]bytes=new byte[2];
            receiveStructure.buffer.get(bytes,0,2);
            receiveStructure.size=(bytes[0]&0xff)+((bytes[1]&0xff)<<8);
            System.out.println(receiveStructure.size);
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
            JSONObject infor=JSON.parseObject(receiveStructure.information.toString());
            Function function=FunctionController.getFunction(infor.getInteger("code"));
            function.setObject(infor);
            function.setKey(key);
            pool.execute(function);
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
            Function function=FunctionController.getFunction(infor.getInteger("code"));
            function.setObject(infor);
            function.setKey(key);
            pool.execute(function);
            receiveStructure.size=0;
            receiveStructure.information.setLength(0);
        }
    }
    public static void main(String[] args) {
        Server server=new Server();
        server.run();
    }
}