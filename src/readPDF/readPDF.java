package readPDF;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import com.itextpdf.text.Font;

public class readPDF {

	public static String data;

	public static void main(String[] args) throws IOException, InterruptedException {

		File file = new File("C:\\Users\\wmsai\\Desktop\\BankStatement.pdf");
		PDFTextStripper stripper = new PDFTextStripper();
		BufferedWriter bw = new BufferedWriter(new FileWriter("bankData", StandardCharsets.UTF_8));
		String temp = stripper.getText(PDDocument.load(file)).toUpperCase();

		bw.write(temp);
		bw.flush();
		bw.close();

		readPDF.data = temp;

		BufferedReader buffRead = new BufferedReader(new FileReader("bankData"));
		BufferedWriter buffWrite = new BufferedWriter(new FileWriter("pdfText"));
		String tempLine = null;
		while ((tempLine = buffRead.readLine()) != null) {
			String input2 = tempLine;
			String regex2 = "REGULAR 2 OF";
			Matcher m2 = Pattern.compile(regex2).matcher(input2);
			if (m2.find()) {
				while ((tempLine = buffRead.readLine()) != null) {
					buffWrite.write(tempLine);
					buffWrite.newLine();
					buffWrite.flush();
				}
				break;
			} else if (!m2.find()) {
				continue;
			}
		}

		HashMap<String, String> hashMap = new HashMap<>();
		HashMap<String, String> hashMap2 = new HashMap<>();

		Properties properties1 = new Properties();
		properties1.load(new FileInputStream("data.properties"));

		for (String key : properties1.stringPropertyNames()) {
			hashMap.put(key, properties1.get(key).toString());
		}
		Scanner scanner = new Scanner(System.in);
		BufferedWriter clearData = new BufferedWriter(new FileWriter("csvFile"));
		LineNumberReader lineNum = new LineNumberReader(new FileReader("bankData"));
		String[] billNames = { "ROYAL FARMS", "SUNOCO", "PRIME", "CAPITAL REGION", "AMAZON.COM", "CHECK", "DFAS",
				"PROGRESS", "ASPEN", "DUNKIN", "PENN WASTE", "PP ELEC", "EZPASS", "PA DRIVER", "QUIK QUALITY",
				"PHS SBO", "AAFES", "BILL SERVICING", "SUSQUEHANNA TOWNSHIP", "COSTCO", "ALDI", "TRS TRR",
				"WM SUPERCENTER", "WAL-MART", "PIZZA GRILLE", "LOWE'S", "DAIRY QUEEN", "PAYPAL", "BANNER LIFE",
				"NAVIGATORS", "TITHE.LY", "7-ELEVEN", "CLOUD 10", "TURKEY HILL", "INSTACART", "34474 HARRISBURG",
				"IRS TREAS", "WAWA", "FREEDOM", "ONCE UPON A CHILD", "SHEETZ", "GIANT", "DISCIPLEMAKERS", "USAA",
				"LOAN 004", "A1 EXPRESS", "PROTECTIVE", "RUTTER'S", "MALICKS", "GET GO", "PANERA", "MCDONALD'S",
				"PEPBOYS", "AVEN", "VERIZON", "RETURN ADJUSTMENT", "MASA HIBACHI", "UNITED CONCORDIA", "SHARE 0001",
				"SHARE 0030", "WIRELESS SVCS", "THRIFT BOOKS", "HAND AND STONE" };
		String line;
		while ((line = lineNum.readLine()) != null) {
			BufferedWriter csvFile = new BufferedWriter(new FileWriter("csvFile", true));
			int lineNumber = lineNum.getLineNumber();
			String data = Files.readAllLines(Paths.get("bankData")).get(lineNumber - 1);
			boolean data2 = Arrays.stream(billNames).anyMatch(data::contains);
			if (data2 == true) {
				String input = line;
				String regex = "(\\D\\d{2}\\s-?\\d+\\D\\d{2})";
				Matcher m = Pattern.compile(regex).matcher(input);
				if (m.find()) {
					int newLineNumber = lineNum.getLineNumber();
					String transNamePlusPrice = Files.readAllLines(Paths.get("bankData")).get(newLineNumber - 1);
					BufferedWriter priceTemp = new BufferedWriter(new FileWriter("priceTemp"));

					String input1 = line;
					String regex1 = "(\\d\\d\\D\\d\\d\\D\\d\\d\\d\\d)";
					String regex2 = "\\w\\w\\w/\\w\\w\\w\\w";
					Matcher m1 = Pattern.compile(regex1).matcher(input1);
					Matcher m2 = Pattern.compile(regex2).matcher(input1);
					if (m1.find()) {
						String dashes = "------------------------------------------------------------";
						String[] trans10 = transNamePlusPrice.split("/", 4);
						String trans11 = trans10[0];
						String trans12 = trans11 + dashes;

						String trans13 = trans12.substring(0, 40);
						String trans14 = trans10[3].substring(3);

						// System.out.println(trans13);
						// System.out.println("Enter category");
						// String cat3 = scanner.next();
						// hashMap.put(trans13, "misc");
						String category = hashMap.get(trans13);
						hashMap2.put(trans13, category);

						csvFile.write(category + "," + trans13 + "," + trans14);
						csvFile.newLine();
						csvFile.flush();
						priceTemp.write(transNamePlusPrice);
						priceTemp.newLine();
						priceTemp.flush();
						continue;

					} else if (m2.find()) {
						String dashes = "------------------------------------------------------------";
						String[] tran = transNamePlusPrice.split("/", 3);
						String tran1 = tran[0];
						String tran2 = tran1 + dashes;
						String tran3 = tran2.substring(0, 40);
						String tran4 = tran[2].substring(3);

						// System.out.println(tran3);
						// System.out.println("Enter category");
						// String cat2 = scanner.next();
						// hashMap.put(tran3, "misc");
						String category = hashMap.get(tran3);
						hashMap2.put(tran3, category);

						csvFile.write(category + "," + tran3 + "," + tran4);
						csvFile.newLine();
						csvFile.flush();
						priceTemp.write(transNamePlusPrice);
						priceTemp.newLine();
						priceTemp.flush();
						continue;

					} else if (!m1.find()) {
						String dashes = "------------------------------------------------------------";
						String[] trans1 = transNamePlusPrice.split("/", 2);
						String trans2 = trans1[0];
						String trans3 = trans2 + dashes;
						String trans4 = trans3.substring(0, 40);
						String trans5 = trans1[1].substring(3);

						// System.out.println(trans4);
						// System.out.println("Enter category");
						// String cat1 = scanner.next();
						// hashMap.put(trans4, "misc");
						String category = hashMap.get(trans4);
						hashMap2.put(trans4, category);

						csvFile.write(category + "," + trans4 + "," + trans5);
						csvFile.newLine();
						csvFile.flush();
						priceTemp.write(transNamePlusPrice);
						priceTemp.newLine();
						priceTemp.flush();
						continue;
					}

				} else if (!m.find()) {
					String dashes = "------------------------------------------------------------";
					while ((line = lineNum.readLine()) != null) {
						String input2 = line;
						String regex2 = "(\\D\\d{2}\\s-?\\d+\\D\\d{2})";
						Matcher m2 = Pattern.compile(regex2).matcher(input2);
						if (m2.find()) {
							int newLineNumber = lineNum.getLineNumber();
							String priceLine = Files.readAllLines(Paths.get("bankData")).get(newLineNumber - 1);
							BufferedWriter priceTemp = new BufferedWriter(new FileWriter("priceTemp"));

							String data22 = data.replaceAll(":", "") + dashes;
							String data33 = data22.substring(0, 40);
							String[] data44 = priceLine.split("/", 2);
							String data55 = data44[1].substring(3);

							// System.out.println(data33);
							// System.out.println("Enter category");
							// String cat = scanner.next();
							// hashMap.put(data33, "misc");
							String category = hashMap.get(data33);
							hashMap2.put(data33, category);

							priceTemp.write(priceLine);
							priceTemp.newLine();
							priceTemp.flush();
							csvFile.write(category + "," + data33 + "," + data55);
							csvFile.newLine();
							csvFile.flush();
							break;
						} else if (!m2.find()) {
							continue;
						}
					}

				}

				else if (data2 == false) {
					continue;
				}

			}

		}
		System.out.println("Success");

		Properties properties = new Properties();
		for (Map.Entry<String, String> entry : hashMap.entrySet()) {
			properties.put(entry.getKey(), entry.getValue());
		}
		properties.store(new FileOutputStream("data.properties"), null);

		// setData();
		// gui();
	}

	public static String setData() {

		String data = readPDF.data;
		return data;

	}

	static void gui() {

		NewJFrame newFrame = new NewJFrame();
		newFrame.main(null);

	}

	static void parseCSV() throws IOException {

		String[] categories = { "TITHE", "ENTERTAINMENT", "GAS", "ELECTRIC", "WATER", "TRASH", "ARMYINCOME",
				"PROGRESSINCOME", "ASPENINCOME", "MISC", "EATOUT", "CAR", "CITY", "AMAZON", "RETURNS", "PAYPAL",
				"MANDYLIFE", "WYATTLIFE", "TRANSFER", "GIVING", "GROC", "MORTGAGE", "CHECK", "CARINSURANCE",
				"DENTALINSURANCE", "HOUSE" };
		
		LineNumberReader lineNum = new LineNumberReader(new FileReader("csvFile"));
		String line;	
		Integer amount = 0;
		Integer amount2 = 0;
		while ((line = lineNum.readLine()) != null) {
			String[] lineArray = line.split(",", 3);
			if (lineArray[0] == "TITHE") {
				amount.getInteger(lineArray[2]);
				int total = amount + amount2;
				amount2.equals(total);
			}
			else if (lineArray[0] != "TITHE") {
				continue;				
			}
			
		}
		System.out.println(amount2);
		
		
	}
}
