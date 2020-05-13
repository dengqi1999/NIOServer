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
            System.out.println("=======NIOServer已经启动=====");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while(true){
                //阻塞获取就绪channel
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
                            Function function=new Function();
                            function.setKey(key);
                            pool.execute(function);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server=new Server();
        server.run();
    }
}