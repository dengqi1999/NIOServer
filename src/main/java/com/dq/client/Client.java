package com.dq.client;

import com.alibaba.fastjson.JSONObject;
import com.dq.FunctionType;
import com.dq.Msg;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class Client {
    public static final int DEFAULT_BUFFER_SIZE = 1024;
    private Selector selector;
    private SocketChannel clientChannel;
    private ByteBuffer buf;
    private String username;
    private boolean isLogin = false;
    private boolean isConnected = false;
    private ReceiverHandler listener;
    private Charset charset = StandardCharsets.UTF_8;

    Chart chart;

    public void setChart(Chart chart){
        this.chart=chart;
    }

    public Client(){
        initNetWork();
    }
    /**
     * 初始化网络模块
     */
    private void initNetWork() {
        try {
            selector = Selector.open();
            clientChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9000));
            //设置客户端为非阻塞模式
            clientChannel.configureBlocking(false);
            clientChannel.register(selector, SelectionKey.OP_READ);
            buf = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
            login();
            isConnected = true;
        } catch (ConnectException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void launch() {
        this.listener = new ReceiverHandler();
        new Thread(listener).start();
    }

    public void login() {
        String username = JOptionPane.showInputDialog("请输入用户Id");
        String password = JOptionPane.showInputDialog("请输入密码");
        Msg msg=new Msg();
        msg.setCode(FunctionType.LOGIN.getCode());
        msg.setFromId(Integer.valueOf(username));
        msg.setMsg(username+password);
        try {
            clientChannel.write(ByteBuffer.wrap(JSONObject.toJSONString(msg).getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.username = username;
    }
    public void logout() {
        if (!isLogin) {
            return;
        }
        System.out.println("客户端发送下线请求");
        Msg msg=new Msg();
        msg.setCode(FunctionType.QUIT.getCode());
        msg.setFromId(Integer.valueOf(username));
        try {
            clientChannel.write(ByteBuffer.wrap(JSONObject.toJSONString(msg).getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 发送信息，监听在回车键上
     *
     * @param content
     */
    public void sendSingle(String content,String toId) {
        if (!isLogin) {
            JOptionPane.showMessageDialog(null, "尚未登录");
            return;
        }
        try {
            Msg msg=new Msg();
            msg.setCode(FunctionType.SINGLE_CHART.getCode());
            msg.setFromId(Integer.valueOf(username));
            msg.setToId(Integer.valueOf(toId));
            clientChannel.write(ByteBuffer.wrap(JSONObject.toJSONString(msg).getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 用于接收信息的线程
     */
    private class ReceiverHandler implements Runnable {
        private boolean connected = true;

        public void shutdown() {
            connected = false;
        }
        public void run() {
            try {
                while (connected) {
                    int size = 0;
                    selector.select();
                    for (Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext(); ) {
                        SelectionKey selectionKey = it.next();
                        it.remove();
                        if (selectionKey.isReadable()) {
                            StringBuffer infor = new StringBuffer();
                            buf.clear();
                            while ((size = clientChannel.read(buf)) > 0) {
                                buf.flip();
                                infor.append(new String(buf.array(), 0, size));
                                buf.clear();
                            }
                            System.out.println(infor.toString());
                            JSONObject object= JSONObject.parseObject(infor.toString());

                            if(object.getInteger("code")==FunctionType.SINGLE_CHART.getCode()){
                                DisplayData data=new DisplayData();
                                data.setData(object.getString("msg"));
                                data.setIsLeft(true);
                                chart.listModel.addElement(data);
                            }else if(object.getInteger("code")==FunctionType.LOGIN.getCode()){

                            }else if(object.getInteger("code")==FunctionType.QUIT.getCode()){

                            }
                        }
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "服务器关闭，请重新尝试连接");
                isLogin = false;
            }
        }
    }
}