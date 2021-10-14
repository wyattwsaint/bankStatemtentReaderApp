package readPDF;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import readPDF.celleditor.CategoryEditor;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTable;
import java.awt.CardLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.border.Border;
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
	HashMap<String, Double> summary = new HashMap<>();

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
		panel.setBounds(6, 64, 800, 45);
		
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
		
		JButton reloadBtn = new JButton("Reload Data");
		reloadBtn.setBounds(280, 6, 140, 29);
		reloadBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					reloadData();
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
		});
		desktopPane.add(reloadBtn);
		
		JButton saveBtn = new JButton("Save Data");
		saveBtn.setBounds(430, 6, 140, 29);
		saveBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					save();
				} catch (ParseException e1) {
					e1.printStackTrace();
				} catch (DocumentException e1) {
					e1.printStackTrace();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		desktopPane.add(saveBtn);
		
		frame.setVisible(true);
		
		catFrame = new Category(categoryData, this);
	}
	
	public void parseFile(File file) throws IOException, ParseException {
		model.setNumRows(0);
		PDFTextStripper stripper = new PDFTextStripper();
		PDDocument pDoc = PDDocument.load(file);
		String pdfText = stripper.getText(pDoc).toUpperCase().replaceAll("\n", "");
		pDoc.close();

		summary = new HashMap<>();
		
		int beginIdx = pdfText.indexOf("CHECKING  ID 0004");
		int endIdx = pdfText.indexOf("SUMMER PAY SHARES  ID 0005");
		System.out.println("Begin: " + beginIdx + "   End: " + endIdx);
		
		pdfText = pdfText.substring(beginIdx + 17, endIdx - 1);
		System.out.println(pdfText);
		String description = null;
		BigDecimal amount;
		BigDecimal currAmount;
		BigDecimal defaultVal = new BigDecimal("0.00");
		String category = null;
		
		String regex = "(.+?(?=[0-9][,]?[0-9]{3}.[0-9]{2}))([0-9][,]?[0-9]{3}.[0-9]{2})([0-9]{2}\\/[0-9]{2}\\s)(-[0-9]?[,]?[0-9]{0,3}.[0-9]{2})";
		Matcher m = Pattern.compile(regex).matcher(pdfText);
		while(m.find()) {
			//System.out.println(m.group(0));
			description = m.group(1);
			
			amount = new BigDecimal(m.group(4).replace(",",""));
			amount.setScale(2, RoundingMode.UP);
			
			// Check if transaction is a bill
			category = getCategory(description);
			
			Object[] newRow = new Object[] {category, description, String.valueOf(amount) };
			model.addRow(newRow);
			
			currAmount = (summary.containsKey(category)) ? new BigDecimal(summary.get(category)) : defaultVal;
			currAmount.setScale(2, RoundingMode.UP);
			summary.put(category, (currAmount.add(amount)).doubleValue());
		}
		showSummary();
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
			categories = new ArrayList<String>();
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
	
	public void reloadData() throws ParseException {
		int rowCount = tblTransactions.getRowCount();
		summary = new HashMap<>();
		BigDecimal amount;
		BigDecimal currAmount;
		BigDecimal defaultVal = new BigDecimal("0.00");
		String category;
		
		for(int i=0; i < rowCount; i++) {
			category = getCategory(String.valueOf(model.getValueAt(i, 1)));
			model.setValueAt(category, i, 0);
			
			amount = new BigDecimal(model.getValueAt(i, 2).toString());
			
			currAmount = (summary.containsKey(category)) ? new BigDecimal(summary.get(category)) : defaultVal;
			summary.put(category, (currAmount.add(amount)).doubleValue());
		}
		showSummary();
	}
	
	public void showSummary() {
		String summaryStr = "<html>";
		int idx = 1;
		
		for(String key : summary.keySet()) {
			BigDecimal value = new BigDecimal(summary.get(key).toString());
			value.setScale(2, RoundingMode.CEILING);
			
			summaryStr += "<span style='float:left; padding-right:20px;'>" + key + ": " + String.format("%.2f", value) + "</span>"
				+ (idx % 4 == 0? "<br />" : "\t\t\t\t");
			idx++;
		}
		summaryStr += "</html>";
		
		lblSummary.setText(summaryStr);
	}
	
	public void save() throws ParseException, DocumentException, FileNotFoundException {
		File fileToSave;
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Specify a file to save");   
		String category;
		String amount;
		String desc;
		Font regularFont = new Font(FontFamily.HELVETICA, 10, Font.NORMAL);
		Font boldFont = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
		 
		int userSelection = fileChooser.showSaveDialog(frame);
		 
		if (userSelection == JFileChooser.APPROVE_OPTION) {
		    fileToSave = fileChooser.getSelectedFile();
		    
		    // create pdf here
		    Document doc = new Document();
		    PdfWriter.getInstance(doc, new FileOutputStream(fileToSave));
		    doc.open();
		    
		    PdfPTable summaryTable = new PdfPTable(3);
		    summaryTable.setTotalWidth(520);
		    summaryTable.setLockedWidth(true);
		    
		    for(String key : summary.keySet()) {
		    	summaryTable.addCell(new PdfPCell(new Paragraph(key + ": " + summary.get(key).toString())));
			}

		    doc.add(new Paragraph("Summary", boldFont));
		    doc.add(summaryTable);
		    
		    PdfPTable transactionTable = new PdfPTable(3);
		    transactionTable.setTotalWidth(new float[] {60, 400, 60});
		    transactionTable.setLockedWidth(true);
		    
		    doc.add(new Paragraph(""));
		    doc.add(new Paragraph("Transacations", boldFont));
		    transactionTable.addCell(new PdfPCell(new Paragraph("Category")));
		    transactionTable.addCell(new PdfPCell(new Paragraph("Description")));
		    transactionTable.addCell(new PdfPCell(new Paragraph("Amount")));
		    
		    for(int i=0; i < tblTransactions.getRowCount(); i++) {
				category = String.valueOf(model.getValueAt(i, 0));
				desc = String.valueOf(model.getValueAt(i, 1));
				amount = model.getValueAt(i, 2).toString();
				
				transactionTable.addCell(new PdfPCell(new Paragraph(category)));
				transactionTable.addCell(new PdfPCell(new Paragraph(desc)));
				transactionTable.addCell(new PdfPCell(new Paragraph(amount)));
			}
		    doc.add(transactionTable);
		    doc.close();
		    
		    JOptionPane.showMessageDialog(null, "File Saved");
		}
	}
}
