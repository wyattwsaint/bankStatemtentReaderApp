package readPDF;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.configuration.PropertiesConfiguration;

import readPDF.celleditor.CategoryEditor;

public class Category {
	private JFrame parent;
	private JFrame frame;
	private JTable tblCats;
	DefaultTableModel model;
	PropertiesConfiguration config;
	String[][] categoryData; 
	
	public Category(String[][] catData, JFrame parent) {
		this.parent = parent;
		this.categoryData = catData;
		frame = new JFrame();
		frame.setBounds(100, 100, 707, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		SpringLayout layout = new SpringLayout();
		Container pane = frame.getContentPane();
		pane.setLayout(layout);
		
		JLabel lbl = new JLabel("Categories");
		pane.add(lbl);
		
		String[] columnNames = {"Category","Description"};
		model = new DefaultTableModel(catData, columnNames);
				
		tblCats = new JTable(model);
		tblCats.setBorder(new LineBorder(new Color(0, 0, 0)));
		tblCats.setAutoCreateRowSorter(true);
		
		JScrollPane scrollPane = new JScrollPane( tblCats );

		pane.add(scrollPane);
		
		JButton newCatBtn = new JButton("Add Category");
		newCatBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				model.addRow(new Object[] {"temp", "temp"});
			}
		});
		pane.add(newCatBtn);
		
		JButton cancelBtn = new JButton("Cancel");
		cancelBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				parent.setVisible(true);
				frame.setVisible(false);
			}
		});
		pane.add(cancelBtn);
		
		
		layout.putConstraint(SpringLayout.NORTH, lbl, 20, SpringLayout.NORTH, pane);
		layout.putConstraint(SpringLayout.WEST, lbl, 20, SpringLayout.WEST, pane);
		layout.putConstraint(SpringLayout.NORTH, scrollPane, 40, SpringLayout.NORTH, pane);
		layout.putConstraint(SpringLayout.WEST, scrollPane, 20, SpringLayout.WEST, pane);

		layout.putConstraint(SpringLayout.SOUTH, newCatBtn, -20, SpringLayout.SOUTH, pane);
		layout.putConstraint(SpringLayout.WEST, newCatBtn, 20, SpringLayout.WEST, pane);
		
		layout.putConstraint(SpringLayout.SOUTH, cancelBtn, -20, SpringLayout.SOUTH, pane);
		layout.putConstraint(SpringLayout.WEST, cancelBtn, 180, SpringLayout.WEST, pane);
	}
	
	public void setVisible(boolean bool) {
		this.frame.setVisible(bool);
	}
}
