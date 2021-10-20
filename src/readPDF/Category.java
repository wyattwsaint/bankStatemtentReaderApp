package readPDF;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.pdfbox.util.Vector;

import readPDF.cell.CategoryEditor;

public class Category {
	private Main parent;
	private JFrame frame;
	private JTable tblCats;
	DefaultTableModel model;
	PropertiesConfiguration config;
	String[][] categoryData; 
	
	public Category(String[][] catData, Main parent) {
		this.parent = parent;
		this.categoryData = catData;
		frame = new JFrame();
		frame.setBounds(50, 50, 800, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Container pane = frame.getContentPane();
		GroupLayout frameLayout = new GroupLayout(pane);
		pane.setLayout(frameLayout);
		
		JLabel lbl = new JLabel("Categories");
		
		String[] columnNames = {"Category","Description"};
		model = new DefaultTableModel(catData, columnNames);
				
		tblCats = new JTable(model);
		tblCats.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		tblCats.setGridColor(Color.BLACK);
		tblCats.setIntercellSpacing(new Dimension(8,5));
		tblCats.setAutoCreateRowSorter(true);
		tblCats.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane scrollPane = new JScrollPane( tblCats );
		pane.add(scrollPane);
		
		JButton newCatBtn = new JButton("Add Category");
		newCatBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				model.addRow(new Object[] {"temp", "temp"});
			}
		});
		
		JButton cancelBtn = new JButton("Cancel");
		cancelBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				parent.frame.setVisible(true);
				frame.setVisible(false);
			}
		});
		
		JButton deleteBtn = new JButton("Delete Row");
		deleteBtn.setEnabled(false);
		deleteBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//Delete Selected Row        
		        int row = tblCats.getSelectedRow();
		        //Check if their is a row selected
		        if (row >= 0) {
		            model.removeRow(row);
		            JOptionPane.showMessageDialog(null, "Row Deleted");
		        } else {
		            JOptionPane.showMessageDialog(null, "Unable To Delete");
		        }
			}
		});
		
		JButton saveBtn = new JButton("Save Categories");
		saveBtn.setEnabled(false);
		saveBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				saveCategories();
			}
		});

		JPanel btnPanel = new JPanel();
		GroupLayout btnLayout = new GroupLayout(btnPanel);
		btnLayout.setHorizontalGroup(
			btnLayout.createParallelGroup(GroupLayout.Alignment.LEADING, true)
				.addGroup(btnLayout.createSequentialGroup()
					.addComponent(newCatBtn)
					.addComponent(cancelBtn)
					.addComponent(deleteBtn)
					.addComponent(saveBtn)
				)
		);
		
		tblCats.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
	        public void valueChanged(ListSelectionEvent event) {
	        	int selected = tblCats.getSelectedRow();
	        	deleteBtn.setEnabled((selected >= 0 ? true : false));
	        }
	    });
		
		tblCats.getModel().addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				saveBtn.setEnabled(true);
			}
		});
		
		frameLayout.setHorizontalGroup(
			frameLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(frameLayout.createSequentialGroup()
				.addContainerGap()
				.addGroup(frameLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addGroup(frameLayout.createSequentialGroup()
						.addGap(6, 6, 6)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 634, Short.MAX_VALUE)
					)	
					.addGroup(frameLayout.createSequentialGroup()
						.addGap(0, 0, Short.MAX_VALUE)
					)
					.addComponent(btnPanel, GroupLayout.DEFAULT_SIZE, 634, Short.MAX_VALUE)
				)
				.addContainerGap()
			)
		);
		
		frameLayout.setVerticalGroup(
			frameLayout.createParallelGroup(GroupLayout.Alignment.LEADING, true)
			.addGroup(frameLayout.createSequentialGroup()
				.addComponent(btnPanel, 35, 35, 35)
				.addGap(5)
				.addComponent(scrollPane)
			)
		);
		
		frame.pack();
		
	}
	
	public void setVisible(boolean bool) {
		this.frame.setVisible(bool);
	}
	
	public void saveCategories() {
		int rowCount = this.tblCats.getRowCount();
		String key, value;
		ArrayList<String> categories = new ArrayList<String>();
		for(int i=0; i < rowCount; i++) {
			key = (String)tblCats.getValueAt(i, 0);
			value = (String)tblCats.getValueAt(i, 1);
			parent.config.setProperty(key, value);
			categories.add(key);
		}
		
		parent.config.setProperty("categories", categories);
		
		try {
			parent.config.save();
			parent.loadCategories();
			JOptionPane.showMessageDialog(null, "Categories saved");
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Categories could not be saved");
		}
	}
}
