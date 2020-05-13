package com.dq.client;

import com.dq.client.Entity.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Chart extends JFrame {

	private JPanel contentPane;
	DefaultListModel<DisplayData> listModel;
	DefaultListModel<User> listModelUser;
	Client client;
	JList<User> listUser;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		final Client client=new Client();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//加载主页面
					Chart frame = new Chart();
					//设置依赖
					frame.client=client;
					frame.setResizable(false); 
					frame.setVisible(true);
					//设置依赖
					client.setChart(frame);
					//启动监听线程
					client.launch();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Chart() {
		setBounds(250, 100, 825, 600);
		getContentPane().setLayout(null);
		//消息列表
		JPanel panel1 = new JPanel();
		panel1.setBounds(176, 13, 617, 464);
		Dimension dims = new Dimension(617, 464);
		listModel = new DefaultListModel<DisplayData>();
		JList<DisplayData> list = new JList<DisplayData>(listModel);
		list.setCellRenderer(new CellRender());
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setPreferredSize(dims);
		panel1.add(scrollPane);
		getContentPane().add(panel1);
		
		//消息输入框
		JPanel panel2=new JPanel();
		Dimension dimt = new Dimension(523, 30);
		panel2.setBounds(176, 490, 523, 50);
		final JTextField textName = new JTextField();
		textName.setPreferredSize(dimt);
		panel2.add(textName);
		getContentPane().add(panel2);
		
		//发送按钮
		JPanel panel3=new JPanel();
		panel3.setBounds(713, 490, 80, 50);
		JButton jb1 = new JButton("发送");
		jb1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String text=textName.getText();
				User user=listModelUser.getElementAt(listUser.getSelectedIndex());
				client.sendSingle(text,user.getId());
				DisplayData displayData=new DisplayData();
				displayData.setIsLeft(false);
				displayData.setData(text);
				listModel.addElement(displayData);
			}
		});
		panel3.add(jb1);
		jb1.setPreferredSize(dimt);
		getContentPane().add(panel3);
		
		
		//在线列表
		JPanel panel4 = new JPanel();
		Font font4 = new Font("宋体", Font.BOLD, 20);
		panel4.setBounds(14, 13, 154, 527);
		listModelUser = new DefaultListModel<User>();
		listUser = new JList<User>(listModelUser);
		listUser.setCellRenderer(new CellRenderUser());
		listUser.setFont(font4);

		JScrollPane scrollPaneUser = new JScrollPane(listUser);
		Dimension dimU = new Dimension(154, 527);
		scrollPaneUser.setPreferredSize(dimU);
		panel4.add(scrollPaneUser);
		getContentPane().add(panel4);
	}
}
