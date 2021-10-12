package readPDF;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import readPDF.celleditor.CategoryEditor;

import javax.swing.JButton;
import javax.swing.JTable;
import java.awt.CardLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.border.LineBorder;

public class Main {

	JFrame frame;
	private Category catFrame;
	private JTable tblTransactions;
	private JLabel lblSummary;
	DefaultTableModel model;
	PropertiesConfiguration config;
	String[][] categoryData;
	ArrayList<String> categories;

	/**
	 * Launch the application.
	 * @throws InterruptedException 
	 * @throws InvocationTargetException 
	 */
	public static void main(String[] args) throws InvocationTargetException, InterruptedException {
		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws ConfigurationException 
	 */
	private void initialize() {
		categories = new ArrayList<String>();
		
		loadCategories();
		
		frame = new JFrame("Saint Statements");
		frame.setBounds(50, 50, 800, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new CardLayout(0, 0));
		
		JDesktopPane desktopPane = new JDesktopPane();
		desktopPane.setBackground(Color.WHITE);
		frame.getContentPane().add(desktopPane, "name_34411139674794");

		JLabel lbl = new JLabel("Summary of transactions");
		lbl.setBounds(6, 42, 157, 25);
		desktopPane.add(lbl);
		
		JPanel panel = new JPanel();
		panel.setBounds(6, 64, 800, 44);
		
		lblSummary = new JLabel("");
		panel.add(lblSummary);
		
		desktopPane.add(panel);

		JLabel lblTransactions = new JLabel("Transactions");
		lblTransactions.setBounds(6, 131, 98, 16);
		desktopPane.add(lblTransactions);

		JPanel transactionPanel = new JPanel();
		transactionPanel.setBounds(6, 150, 800, 700);
	
		String[] columnNames = {"Category","Description","Amount"};
		model = new DefaultTableModel(columnNames, 0);
		
		tblTransactions = new JTable(model);
		tblTransactions.setBorder(new LineBorder(new Color(0, 0, 0)));
		tblTransactions.setAutoCreateRowSorter(true);
		tblTransactions.getColumnModel().getColumn(0).setCellEditor(new CategoryEditor(categories));
		tblTransactions.getColumnModel().getColumn(0).setPreferredWidth(100);
		tblTransactions.getColumnModel().getColumn(1).setPreferredWidth(500);
		tblTransactions.getColumnModel().getColumn(2).setPreferredWidth(100);
		
		JScrollPane scrollPane = new JScrollPane( tblTransactions );
		scrollPane.setPreferredSize(new Dimension(700, 500));
		
		transactionPanel.add(scrollPane);
		desktopPane.add(transactionPanel);

		JButton btnNewButton = new JButton("Open Statement");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JFileChooser chooser = new JFileChooser();
		        // optionally set chooser options ...
		        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
		            File f = chooser.getSelectedFile();
		            try {
		            		parseFile(f);
		            } catch(Exception ex) {
		            		ex.printStackTrace();
		            }
		        } else {
		            // user changed their mind
		        }
			}
		});
		btnNewButton.setBounds(6, 6, 131, 29);
		desktopPane.add(btnNewButton);
		
		JButton catBtn = new JButton("View Categories");
		catBtn.setBounds(138, 6, 140, 29);
		catBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				catFrame.setVisible(true);
				frame.setVisible(false);
			}
		});
		desktopPane.add(catBtn);
		
		frame.setVisible(true);
		
		catFrame = new Category(categoryData, this);
	}
	
	public void parseFile(File file) throws IOException, ParseException {
        
		PDFTextStripper stripper = new PDFTextStripper();
		String pdfText = stripper.getText(PDDocument.load(file)).toUpperCase().replaceAll("\n", "");

		HashMap<String, Double> summary = new HashMap<>();

		for(String cat : categories) {
			summary.put(cat, 0.00);
		}
		
		int beginIdx = pdfText.indexOf("CHECKING  ID 0004");
		int endIdx = pdfText.indexOf("SUMMER PAY SHARES  ID 0005");
		System.out.println("Begin: " + beginIdx + "   End: " + endIdx);
		
		pdfText = pdfText.substring(beginIdx + 17, endIdx - 1);
		
		String description = null;
		double amount = 0.00;
		double currAmount = 0.00;
		String category = null;
		
		String regex = "(.+?(?=[0-9][,]?[0-9]{3}.[0-9]{2}))([0-9][,]?[0-9]{3}.[0-9]{2})([0-9]{2}\\/[0-9]{2}\\s)(-[0-9]?[,]?[0-9]{0,3}.[0-9]{2})";
		Matcher m = Pattern.compile(regex).matcher(pdfText);
		while(m.find()) {
			//System.out.println(m.group(0));
			description = m.group(1);
			amount = DecimalFormat.getNumberInstance().parse(m.group(4)).doubleValue();
			
			// Check if transaction is a bill
			category = getCategory(description);
			
			Object[] newRow = new Object[3];
			newRow[0] = category;
			newRow[1] = description;
			newRow[2] = String.valueOf(amount);
			model.addRow(newRow);
			
			currAmount = summary.get(category).doubleValue();
			summary.put(category, currAmount + amount);
		}
		
		String summaryStr = "";
		for(String key : summary.keySet()) {
			summaryStr += key + ": " + summary.get(key).toString() + "\t\t";
		}
		summaryStr += "";
		
		lblSummary.setText(summaryStr);
	}
	
	private String getCategory(String description) {
		String category = null;
		
		for(String[] theCat : categoryData) {
			if(Arrays.stream(theCat[1].toUpperCase().split(",")).anyMatch(description::contains)) {
				category = theCat[0].toString();
				break;
			}
		}
		
		return (category == null) ? "misc" : category;
	}
	
	public void loadCategories() {
		try {
			config = new PropertiesConfiguration("categories.properties");
			String[] catProp = config.getStringArray("categories");
			categories.addAll(Arrays.asList(catProp));
			
			categoryData = new String[categories.size()][2];
			int idx = 0;
			for(String category : categories) {
				categoryData[idx][0] = category;
				categoryData[idx][1] = StringUtils.join(config.getStringArray(category), ",");
				idx++;
			}
			categories.add("misc");
		
		} catch (ConfigurationException e1) {
			e1.printStackTrace();
		}
	}
}
