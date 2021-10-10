package readPDF;


import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import com.google.gson.Gson;

import javax.swing.JButton;
import javax.swing.JTable;
import java.awt.CardLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.border.LineBorder;

public class Main {

	private JFrame frame;
	private JTable tblTransactions;
	private JLabel lblSummary;
	DefaultTableModel model;

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
	 */
	private void initialize() {
		frame = new JFrame("Saint Statements");
		frame.setBounds(100, 100, 707, 481);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new CardLayout(0, 0));
		
		JDesktopPane desktopPane = new JDesktopPane();
		desktopPane.setBackground(Color.WHITE);
		frame.getContentPane().add(desktopPane, "name_34411139674794");

		JLabel lbl = new JLabel("Summary of transactions");
		lbl.setBounds(6, 47, 157, 16);
		desktopPane.add(lbl);
		
		JPanel panel = new JPanel();
		panel.setBounds(6, 64, 685, 44);
		
		lblSummary = new JLabel("");
		panel.add(lblSummary);
		
		desktopPane.add(panel);

		JLabel lblTransactions = new JLabel("Transactions");
		lblTransactions.setBounds(6, 131, 98, 16);
		desktopPane.add(lblTransactions);

		JPanel transactionPanel = new JPanel();
		transactionPanel.setBounds(6, 150, 695, 303);
	
		String[] columnNames = {"Category","Description","Amount"};
		model = new DefaultTableModel(columnNames, 0);
		
		
		tblTransactions = new JTable(model);
		tblTransactions.setBorder(new LineBorder(new Color(0, 0, 0)));
		tblTransactions.setAutoCreateRowSorter(true);
		JScrollPane scrollPane = new JScrollPane( tblTransactions );
		
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
		
		/*JButton btnNewButton_1 = new JButton("Save");
		btnNewButton_1.setBounds(138, 6, 82, 29);
		desktopPane.add(btnNewButton_1);
		*/
		
		frame.setVisible(true);
		
	}
	
	public void parseFile(File file) throws IOException, ParseException {

		PDFTextStripper stripper = new PDFTextStripper();
		String pdfText = stripper.getText(PDDocument.load(file)).toUpperCase().replaceAll("\n", "");

		HashMap<String, Double> summary = new HashMap<>();
		summary.put("bill", 0.00);
		summary.put("misc", 0.00);
		
		//BufferedWriter csvFile = new BufferedWriter(new FileWriter("csvFile.csv", true));
		String[] billNames = { "DFAS", "AMAZON", "PRIME VIDEO" };
		
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
			System.out.println(m.group(0));
			description = m.group(1);
			amount = DecimalFormat.getNumberInstance().parse(m.group(4)).doubleValue();
			
			// Check if transaction is a bill
			boolean isBill = Arrays.stream(billNames).anyMatch(description::contains);
			category = isBill ? "bill" : "misc";
			
			Object[] newRow = new Object[3];
			newRow[0] = category;
			newRow[1] = description;
			newRow[2] = String.valueOf(amount);
			model.addRow(newRow);
			
			currAmount = summary.get(category).doubleValue();
			summary.put(category, currAmount + amount);
			
			//csvFile.write(category + "," + description + "," + Double.toString(amount));
			//csvFile.newLine();
			//csvFile.flush();
		}
		
		String summaryStr = "<html>";
		for(String key : summary.keySet()) {
			summaryStr += key + ": " + summary.get(key).toString() + "<br />";
		}
		summaryStr += "</html>";
		
		lblSummary.setText(summaryStr);
	}
}
