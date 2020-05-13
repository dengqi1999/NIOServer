package com.dq.client;

import com.dq.client.Entity.User;

import javax.swing.*;
import java.awt.*;

public class CellRenderUser extends JLabel implements ListCellRenderer{
    Font font = new Font("宋体", Font.BOLD, 15);
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        User user=(User)value;
        if(cellHasFocus){
            //setBackground(Color.BLUE);
            setText(user.getName()+"聊天中");
        }else{
            setText(user.getName());
        }
        setFont(font);

        return this;
    }
}
