package readPDF;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import readPDF.cell.CategoryEditor;
import readPDF.cell.DescriptionRenderer;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.border.LineBorder;

public class Main {

	JFrame frame;
	private Category catFrame;
	private JTable tblTransactions;
	TableRowSorter<TableModel> sorter;
	private JLabel lblSummary;
	DefaultTableModel model;
	PropertiesConfiguration config;
	String[][] categoryData;
	ArrayList<String> categories;
	HashMap<String, Double> summary = new HashMap<>();
	JLabel summaryLabel;

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
	@SuppressWarnings("static-access")
	private void initialize() {
		
		loadCategories();
		
		frame = new JFrame("Saint Statements");
		frame.setBounds(50, 50, 800, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GroupLayout frameLayout = new GroupLayout(frame.getContentPane());
		frame.getContentPane().setLayout(frameLayout);
		
		JButton newBtn = new JButton("Open Statement");
		newBtn.addMouseListener(new MouseAdapter() {
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
		newBtn.setBounds(6, 6, 131, 29);
		
		JButton catBtn = new JButton("View Categories");
		catBtn.setBounds(138, 6, 140, 29);
		catBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				catFrame.setVisible(true);
				frame.setVisible(false);
			}
		});
		
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
		

		JPanel btnPanel = new JPanel();
		GroupLayout btnLayout = new GroupLayout(btnPanel);
		btnLayout.setHorizontalGroup(
			btnLayout.createParallelGroup(GroupLayout.Alignment.LEADING, true)
				.addGroup(btnLayout.createSequentialGroup()
					.addComponent(newBtn)
					.addComponent(reloadBtn)
					.addComponent(catBtn)
					.addComponent(saveBtn)
				)
		);
		
		summaryLabel = new JLabel("Summary of transactions");
		summaryLabel.setVisible(false);
		
		lblSummary = new JLabel("");
		lblSummary.setVisible(false);

		JLabel transactionsLabel = new JLabel("Transactions");
		String[] columnNames = {"Category","Description","Amount"};
		model = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
			    return (column == 0);
			}
		};
		
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setVerticalAlignment(SwingConstants.TOP);
		
		tblTransactions = new JTable(model);
		tblTransactions.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		tblTransactions.setGridColor(Color.BLACK);
		tblTransactions.setIntercellSpacing(new Dimension(8,5));
		tblTransactions.getColumnModel().getColumn(0).setCellEditor(new CategoryEditor(categories));
		tblTransactions.getColumnModel().getColumn(0).setPreferredWidth(140);
		tblTransactions.getColumnModel().getColumn(0).setMaxWidth(180);
		tblTransactions.getColumnModel().getColumn(0).setCellRenderer(renderer);
		tblTransactions.getColumnModel().getColumn(1).setPreferredWidth(400);
		tblTransactions.getColumnModel().getColumn(1).setCellRenderer(new DescriptionRenderer());
		tblTransactions.getColumnModel().getColumn(2).setPreferredWidth(100);
		tblTransactions.getColumnModel().getColumn(2).setMaxWidth(100);
		tblTransactions.getColumnModel().getColumn(2).setCellRenderer(renderer);
		tblTransactions.getColumnModel().getColumn(2).setCellEditor(null);
	
		
		sorter = new TableRowSorter<>(tblTransactions.getModel());
		tblTransactions.setRowSorter(sorter);
		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		sorter.setSortKeys(sortKeys);
		sorter.sort();
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(tblTransactions);
		
		/*
		 * btnPanel
		 * summaryLabel
		 * transactionsLabel
		 * scrollPane
		 */
		frameLayout.setHorizontalGroup(
			frameLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(frameLayout.createSequentialGroup()
				.addContainerGap()
				.addGroup(frameLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addGroup(frameLayout.createSequentialGroup()
						.addGap(6, 6, 6)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 634, Short.MAX_VALUE)
					)	
					.addComponent(lblSummary, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGroup(frameLayout.createSequentialGroup()
						.addGroup(frameLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(transactionsLabel)
							.addComponent(summaryLabel)
						)
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
				.addComponent(btnPanel)
				.addComponent(summaryLabel)
				.addGap(5)
				.addComponent(lblSummary)
				.addGap(20)
				.addComponent(transactionsLabel)
				.addGap(5)
				.addComponent(scrollPane)
			)
		);
		
		frame.setVisible(true);
		frame.pack();
		
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
		
		String description = null;
		BigDecimal amount;
		BigDecimal currAmount;
		BigDecimal defaultVal = new BigDecimal("0.00");
		String category = null;
		
		String regex = "(.+?(?=[0-9][,]?[0-9]{3}.[0-9]{2}))([0-9][,]?[0-9]{3}.[0-9]{2})([0-9]{2}\\/[0-9]{2}\\s)([-]?[0-9]?[,]?[0-9]{0,3}.[0-9]{2})";
		Matcher m = Pattern.compile(regex).matcher(pdfText);
		while(m.find()) {
			//System.out.println(m.group(0));
			amount = new BigDecimal(m.group(4).replace(",",""));
			amount.setScale(2, RoundingMode.UP);
			
			if (amount.doubleValue() > 0) {
				description = m.group(1);
				int idx = description.indexOf("TRANSACTIONAMOUNTNEWBALANCE");
				description = (idx > 0 ) ? description.substring(idx + 27) : description;
				
				category = getCategory(description);
				
				Object[] newRow = new Object[] {category, description, String.valueOf(amount) };
				model.addRow(newRow);
				
				currAmount = (summary.containsKey(category)) ? new BigDecimal(summary.get(category)) : defaultVal;
				currAmount.setScale(2, RoundingMode.UP);
				summary.put(category, (currAmount.add(amount)).doubleValue());
			}
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
			tblTransactions.setValueAt(category, i, 0);
			
			amount = new BigDecimal(tblTransactions.getValueAt(i, 2).toString());
			
			currAmount = (summary.containsKey(category)) ? new BigDecimal(summary.get(category)) : defaultVal;
			summary.put(category, (currAmount.add(amount)).doubleValue());
		}
		sorter.sort();
		showSummary();
	}
	
	public void showSummary() {
		String summaryStr = "<html>";
		int idx = 1;
		
		for(String key : summary.keySet()) {
			BigDecimal value = new BigDecimal(summary.get(key).toString());
			value.setScale(2, RoundingMode.CEILING);
			
			summaryStr += "<span style='border:5px white; float:left; padding:20px; background-color:#ccc; overflow:hidden;margin-bottom:10px;'>" + key + ": " + String.format("%.2f", value) + "</span>"
				+ "<span style='width:10px; float:left; overflow:hidden;'>    </span>";
				//+ (idx % 4 == 0? "<br />" : "<span style='width:10px; float:left; overflow:hidden;'>    </span>");
			idx++;
		}
		summaryStr += "</html>";
		lblSummary.setText(summaryStr);
		
		summaryLabel.setVisible(true);
		lblSummary.setVisible(true);
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
		    
		    if (FilenameUtils.getExtension(fileToSave.getName()).equalsIgnoreCase("pdf")) {
		        // filename is OK as-is
		    } else {
		    	fileToSave = new File(fileToSave.toString() + ".pdf");  // append .xml if "foo.jpg.xml" is OK
		        //file = new File(file.getParentFile(), FilenameUtils.getBaseName(file.getName())+".xml"); // ALTERNATIVELY: remove the extension (if any) and replace it with ".xml"
		    }
		    
		    // create pdf here
		    Document doc = new Document();
		    PdfWriter.getInstance(doc, new FileOutputStream(fileToSave));
		    doc.open();
		    
		    doc.add(new Paragraph("Summary", boldFont));
		    
		    //String content = "";
		    Chunk c;
		    for(String key : summary.keySet()) {
		    	
		    	BigDecimal value = new BigDecimal(summary.get(key).toString());
				value.setScale(2, RoundingMode.CEILING);
		    	//content += key + ": " + String.format("%.2f", value) + "    ";
				c = new Chunk(key + ": " + summary.get(key).toString());
				c.setBackground(BaseColor.LIGHT_GRAY, 5, 0, 0, 5);
		    	doc.add(c);
		    	doc.add(new Chunk("       "));
		    }
		    //doc.add(new Paragraph(content));
		    
		    doc.add(new Paragraph(Chunk.NEWLINE));
		    doc.add(new Paragraph("Transacations", boldFont));
		    doc.add(new Paragraph(Chunk.NEWLINE));
		    
		    PdfPTable transactionTable = new PdfPTable(3);
		    transactionTable.setTotalWidth(new float[] {80, 360, 80});
		    transactionTable.setLockedWidth(true);
		    
		    PdfPCell catCell = new PdfPCell(new Paragraph("Category"));
		    catCell.setPadding(8);
		    
		    PdfPCell descCell = new PdfPCell(new Paragraph("Description"));
		    descCell.setPadding(8);
		    
		    PdfPCell amtCell = new PdfPCell(new Paragraph("Amount"));
		    amtCell.setPadding(8);
		    
		    transactionTable.addCell(catCell);
		    transactionTable.addCell(descCell);
		    transactionTable.addCell(amtCell);
		    
		    for(int i=0; i < tblTransactions.getRowCount(); i++) {
				category = String.valueOf(tblTransactions.getValueAt(i, 0));
				desc = String.valueOf(tblTransactions.getValueAt(i, 1));
				amount = tblTransactions.getValueAt(i, 2).toString();
				
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
