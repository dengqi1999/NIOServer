package com.dq.client;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class CellRender extends JLabel implements ListCellRenderer {


	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		DisplayData data=(DisplayData)value;
		if(data.getIsLeft()) {
			setHorizontalAlignment(JLabel.LEFT);
		}else {
			setHorizontalAlignment(JLabel.RIGHT);
		}
		Font font = new Font("宋体", Font.BOLD, 20);
		setFont(font);
        setText(data.getData());
        return this;
	}
 
    
}
