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
	
	
	public static double tithe;
	public static double entertainment;
	public static double gas;
	public static double electric;
	public static double water;
	public static double trash;
	public static double army;
	public static double progress;
	public static double aspen;
	public static double misc;
	public static double eatout;
	public static double car;
	public static double city;
	public static double amazon;
	public static double returns;
	public static double paypal;
	public static double mlife;
	public static double wlife;
	public static double transfer;
	public static double giving;
	public static double groc;
	public static double mortgage;
	public static double check;
	public static double carins;
	public static double dentalins;
	public static double house;
	
	public static double[] data = {tithe, entertainment, gas, electric, water, trash, army, progress, aspen, misc,
			eatout, car, city, amazon, returns, paypal, mlife, wlife, transfer, giving, groc, mortgage, check, carins,
			dentalins, house};

	public static void main(String[] args) throws IOException, InterruptedException {

		File file = new File("C:\\Users\\wmsai\\Desktop\\BankStatement.pdf");
		PDFTextStripper stripper = new PDFTextStripper();
		BufferedWriter bw = new BufferedWriter(new FileWriter("bankData", StandardCharsets.UTF_8));
		String temp = stripper.getText(PDDocument.load(file)).toUpperCase();

		bw.write(temp);
		bw.flush();
		bw.close();


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
						String trans12 = trans11.replaceAll(",", "") + dashes;

						String trans13 = trans12.substring(0, 40);
						String trans14 = trans10[3].substring(3).replaceAll(",", "");

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
						String tran2 = tran1.replaceAll(",", "") + dashes;
						String tran3 = tran2.substring(0, 40);
						String tran4 = tran[2].substring(3).replaceAll(",", "");

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
						String trans3 = trans2.replaceAll(",", "") + dashes;
						String trans4 = trans3.substring(0, 40);
						String trans5 = trans1[1].substring(3).replaceAll(",", "");

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

							String data22 = data.replaceAll(":", "").replaceAll(",", "") + dashes;
							String data33 = data22.substring(0, 40);
							String[] data44 = priceLine.split("/", 2);
							String data55 = data44[1].substring(3).replaceAll(",", "");

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

		parseCSV();
		setData();
		gui();
	}

	public static double[] setData() {

		double[] data = readPDF.data;
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
		Double amount = 0.0;
		String [] lineArray;
		String transactionCategory;
		String trans1;
		double trans2;
		double total;
		
		while ((line = lineNum.readLine()) != null) {
			
			lineArray = line.split(",", 3);
			transactionCategory = lineArray[0];
			if (transactionCategory.equals("TITHE")) {
				trans1 = lineArray[2];
				trans2 = Double.valueOf(trans1);
				total = trans2 + amount;
				amount = total;
				
			}
			else if (lineArray[0] != "TITHE") {
				continue;				
			}
			
		}
		readPDF.tithe = amount;		
		System.out.println("Tithe" + amount);
		LineNumberReader lineNum1 = new LineNumberReader(new FileReader("csvFile"));
		amount = 0.0;
		while ((line = lineNum1.readLine()) != null) {
			lineArray = line.split(",", 3);
			transactionCategory = lineArray[0];
			if (transactionCategory.equals("ENTERTAINMENT")) {
				trans1 = lineArray[2];
				trans2 = Double.valueOf(trans1);
				total = trans2 + amount;
				amount = total;
			}
			else if (lineArray[0] != "ENTERTAINMENT") {
				continue;				
			}
			
			
		}
		readPDF.entertainment = amount;
		System.out.println("Entertainment" + amount);
		
		LineNumberReader lineNum2 = new LineNumberReader(new FileReader("csvFile"));
		amount = 0.0;
		
		while ((line = lineNum2.readLine()) != null) {
			lineArray = line.split(",", 3);
			transactionCategory = lineArray[0];
			if (transactionCategory.equals("GAS")) {
				trans1 = lineArray[2];
				trans2 = Double.valueOf(trans1);
				total = trans2 + amount;
				amount = total;
			}
			else if (lineArray[0] != "GAS") {
				continue;				
			}
			
			
		}
		readPDF.gas = amount;
		System.out.println("gas" + amount);
		LineNumberReader lineNum3 = new LineNumberReader(new FileReader("csvFile"));
		amount = 0.0;
		while ((line = lineNum3.readLine()) != null) {
			lineArray = line.split(",", 3);
			transactionCategory = lineArray[0];
			if (transactionCategory.equals("ELECTRIC")) {
				trans1 = lineArray[2];
				trans2 = Double.valueOf(trans1);
				total = trans2 + amount;
				amount = total;
			}
			else if (lineArray[0] != "ELECTRIC") {
				continue;				
			}
			
			
		}
		readPDF.electric = amount;
		System.out.println("elec" + amount);
		
				
		LineNumberReader lineNum4 = new LineNumberReader(new FileReader("csvFile"));
		amount = 0.0;
		while ((line = lineNum4.readLine()) != null) {
			lineArray = line.split(",", 3);
			transactionCategory = lineArray[0];
			if (transactionCategory.equals("WATER")) {
				trans1 = lineArray[2];
				trans2 = Double.valueOf(trans1);
				total = trans2 + amount;
				amount = total;
			}
			else if (lineArray[0] != "WATER") {
				continue;				
			}
			
			
		}
		readPDF.water = amount;
		System.out.println("water" + amount);
		LineNumberReader lineNum5 = new LineNumberReader(new FileReader("csvFile"));
		amount = 0.0;
		while ((line = lineNum5.readLine()) != null) {
			lineArray = line.split(",", 3);
			transactionCategory = lineArray[0];
			if (transactionCategory.equals("TRASH")) {
				trans1 = lineArray[2];
				trans2 = Double.valueOf(trans1);
				total = trans2 + amount;
				amount = total;
			}
			else if (lineArray[0] != "TRASH") {
				continue;				
			}
			
			
		}
		readPDF.trash = amount;
		System.out.println("trash" + amount);
		LineNumberReader lineNum6 = new LineNumberReader(new FileReader("csvFile"));
		amount = 0.0;
		while ((line = lineNum6.readLine()) != null) {
			lineArray = line.split(",", 4);
			transactionCategory = lineArray[0];
			if (transactionCategory.equals("ARMYINCOME")) {
				trans1 = lineArray[3];
				trans2 = Double.valueOf(trans1);
				total = trans2 + amount;
				amount = total;
			}
			else if (lineArray[0] != "ARMYINCOME") {
				continue;				
			}
			
			
		}
		readPDF.army = amount;
		System.out.println("army" + amount);
		LineNumberReader lineNum7 = new LineNumberReader(new FileReader("csvFile"));
		amount = 0.0;
		while ((line = lineNum7.readLine()) != null) {
			lineArray = line.split(",", 3);
			transactionCategory = lineArray[0];
			if (transactionCategory.equals("PROGRESSINCOME")) {
				trans1 = lineArray[2];
				trans2 = Double.valueOf(trans1);
				total = trans2 + amount;
				amount = total;
			}
			else if (lineArray[0] != "PROGRESSINCOME") {
				continue;				
			}
			
			
		}
		readPDF.progress = amount;
		System.out.println("Progress" + amount);
		LineNumberReader lineNum8 = new LineNumberReader(new FileReader("csvFile"));
		amount = 0.0;
		while ((line = lineNum8.readLine()) != null) {
			lineArray = line.split(",", 3);
			transactionCategory = lineArray[0];
			if (transactionCategory.equals("ASPENINCOME")) {
				trans1 = lineArray[2];
				trans2 = Double.valueOf(trans1);
				total = trans2 + amount;
				amount = total;
			}
			else if (lineArray[0] != "ASPENINCOME") {
				continue;				
			}
			
			
		}
		readPDF.aspen = amount;
		System.out.println("aspen" + amount);
		LineNumberReader lineNum9 = new LineNumberReader(new FileReader("csvFile"));
		amount = 0.0;
		while ((line = lineNum9.readLine()) != null) {
			lineArray = line.split(",", 3);
			transactionCategory = lineArray[0];
			if (transactionCategory.equals("MISC")) {
				trans1 = lineArray[2];
				trans2 = Double.valueOf(trans1);
				total = trans2 + amount;
				amount = total;
			}
			else if (lineArray[0] != "MISC") {
				continue;				
			}
			
			
		}
		readPDF.misc = amount;
		System.out.println("misc" + amount);
		LineNumberReader lineNum10 = new LineNumberReader(new FileReader("csvFile"));
		amount = 0.0;
		while ((line = lineNum10.readLine()) != null) {
			lineArray = line.split(",", 3);
			transactionCategory = lineArray[0];
			if (transactionCategory.equals("EATOUT")) {
				trans1 = lineArray[2];
				trans2 = Double.valueOf(trans1);
				total = trans2 + amount;
				amount = total;
			}
			else if (lineArray[0] != "EATOUT") {
				continue;				
			}
			
			
		}
		readPDF.eatout = amount;
		System.out.println("eatout" + amount);
		LineNumberReader lineNum11 = new LineNumberReader(new FileReader("csvFile"));
		amount = 0.0;
		while ((line = lineNum11.readLine()) != null) {
			lineArray = line.split(",", 3);
			transactionCategory = lineArray[0];
			if (transactionCategory.equals("CAR")) {
				trans1 = lineArray[2];
				trans2 = Double.valueOf(trans1);
				total = trans2 + amount;
				amount = total;
			}
			else if (lineArray[0] != "CAR") {
				continue;				
			}
			
			
		}
		readPDF.car = amount;
		System.out.println("Car" + amount);
		LineNumberReader lineNum12 = new LineNumberReader(new FileReader("csvFile"));
		amount = 0.0;
		while ((line = lineNum12.readLine()) != null) {
			lineArray = line.split(",", 3);
			transactionCategory = lineArray[0];
			if (transactionCategory.equals("CITY")) {
				trans1 = lineArray[2];
				trans2 = Double.valueOf(trans1);
				total = trans2 + amount;
				amount = total;
			}
			else if (lineArray[0] != "CITY") {
				continue;				
			}
			
			
		}
		readPDF.city = amount;
		System.out.println("City" + amount);
		LineNumberReader lineNum13 = new LineNumberReader(new FileReader("csvFile"));
		amount = 0.0;
		while ((line = lineNum13.readLine()) != null) {
			lineArray = line.split(",", 3);
			transactionCategory = lineArray[0];
			if (transactionCategory.equals("AMAZON")) {
				trans1 = lineArray[2];
				trans2 = Double.valueOf(trans1);
				total = trans2 + amount;
				amount = total;
			}
			else if (lineArray[0] != "AMAZON") {
				continue;				
			}
			
			
		}
		readPDF.amazon = amount;
		System.out.println("Amazon" + amount);
		LineNumberReader lineNum14 = new LineNumberReader(new FileReader("csvFile"));
		amount = 0.0;
		while ((line = lineNum14.readLine()) != null) {
			lineArray = line.split(",", 3);
			transactionCategory = lineArray[0];
			if (transactionCategory.equals("RETURNS")) {
				trans1 = lineArray[2];
				trans2 = Double.valueOf(trans1);
				total = trans2 + amount;
				amount = total;
			}
			else if (lineArray[0] != "RETURNS") {
				continue;				
			}
			
			
		}
		readPDF.returns = amount;
		System.out.println("Returns" + amount);
		LineNumberReader lineNum15 = new LineNumberReader(new FileReader("csvFile"));
		amount = 0.0;
		while ((line = lineNum15.readLine()) != null) {
			lineArray = line.split(",", 3);
			transactionCategory = lineArray[0];
			if (transactionCategory.equals("PAYPAL")) {
				trans1 = lineArray[2];
				trans2 = Double.valueOf(trans1);
				total = trans2 + amount;
				amount = total;
			}
			else if (lineArray[0] != "PAYPAL") {
				continue;				
			}
			
			
		}
		readPDF.paypal = amount;
		System.out.println("Paypal" + amount);
		LineNumberReader lineNum16 = new LineNumberReader(new FileReader("csvFile"));
		amount = 0.0;
		while ((line = lineNum16.readLine()) != null) {
			lineArray = line.split(",", 3);
			transactionCategory = lineArray[0];
			if (transactionCategory.equals("MANDYLIFE")) {
				trans1 = lineArray[2];
				trans2 = Double.valueOf(trans1);
				total = trans2 + amount;
				amount = total;
			}
			else if (lineArray[0] != "MANDYLIFE") {
				continue;				
			}
			
			
		}
		readPDF.mlife = amount;
		System.out.println("MLife" + amount);
		LineNumberReader lineNum17 = new LineNumberReader(new FileReader("csvFile"));
		amount = 0.0;
		while ((line = lineNum17.readLine()) != null) {
			lineArray = line.split(",", 3);
			transactionCategory = lineArray[0];
			if (transactionCategory.equals("WYATTLIFE")) {
				trans1 = lineArray[2];
				trans2 = Double.valueOf(trans1);
				total = trans2 + amount;
				amount = total;
			}
			else if (lineArray[0] != "WYATTLIFE") {
				continue;				
			}
			
			
		}
		readPDF.wlife = amount;
		System.out.println("WLife" + amount);
		LineNumberReader lineNum18 = new LineNumberReader(new FileReader("csvFile"));
		amount = 0.0;
		while ((line = lineNum18.readLine()) != null) {
			lineArray = line.split(",", 3);
			transactionCategory = lineArray[0];
			if (transactionCategory.equals("TRANSFER")) {
				trans1 = lineArray[2];
				trans2 = Double.valueOf(trans1);
				total = trans2 + amount;
				amount = total;
			}
			else if (lineArray[0] != "TRANSFER") {
				continue;				
			}
			
			
		}
		readPDF.transfer = amount;
		System.out.println("Transfer" + amount);
		LineNumberReader lineNum19 = new LineNumberReader(new FileReader("csvFile"));
		amount = 0.0;
		while ((line = lineNum19.readLine()) != null) {
			lineArray = line.split(",", 3);
			transactionCategory = lineArray[0];
			if (transactionCategory.equals("GIVING")) {
				trans1 = lineArray[2];
				trans2 = Double.valueOf(trans1);
				total = trans2 + amount;
				amount = total;
			}
			else if (lineArray[0] != "GIVING") {
				continue;				
			}
			
		}
		readPDF.giving = amount;
		System.out.println("Give" + amount);
		LineNumberReader lineNum20 = new LineNumberReader(new FileReader("csvFile"));
		amount = 0.0;
		while ((line = lineNum20.readLine()) != null) {
			lineArray = line.split(",", 3);
			transactionCategory = lineArray[0];
			if (transactionCategory.equals("GROC")) {
				trans1 = lineArray[2];
				trans2 = Double.valueOf(trans1);
				total = trans2 + amount;
				amount = total;
			}
			else if (lineArray[0] != "GROC") {
				continue;				
			}
			
			
		}
		readPDF.groc = amount;
		System.out.println("Groc" + amount);
		LineNumberReader lineNum21 = new LineNumberReader(new FileReader("csvFile"));
		amount = 0.0;
		while ((line = lineNum21.readLine()) != null) {
			lineArray = line.split(",", 3);
			transactionCategory = lineArray[0];
			if (transactionCategory.equals("MORTGAGE")) {
				trans1 = lineArray[2];
				trans2 = Double.valueOf(trans1);
				total = trans2 + amount;
				amount = total;
			}
			else if (lineArray[0] != "MORTGAGE") {
				continue;				
			}
			
			
		}
		readPDF.mortgage = amount;
		System.out.println("Mort" + amount);
		LineNumberReader lineNum22 = new LineNumberReader(new FileReader("csvFile"));
		amount = 0.0;
		while ((line = lineNum22.readLine()) != null) {
			lineArray = line.split(",", 3);
			transactionCategory = lineArray[0];
			if (transactionCategory.equals("CHECK")) {
				trans1 = lineArray[2];
				trans2 = Double.valueOf(trans1);
				total = trans2 + amount;
				amount = total;
			}
			else if (lineArray[0] != "CHECK") {
				continue;				
			}
			
			
		}
		readPDF.check = amount;
		System.out.println("Check" + amount);
		LineNumberReader lineNum23 = new LineNumberReader(new FileReader("csvFile"));
		amount = 0.0;
		while ((line = lineNum23.readLine()) != null) {
			lineArray = line.split(",", 3);
			transactionCategory = lineArray[0];
			if (transactionCategory.equals("CARINSURANCE")) {
				trans1 = lineArray[2];
				trans2 = Double.valueOf(trans1);
				total = trans2 + amount;
				amount = total;
			}
			else if (lineArray[0] != "CARINSURANCE") {
				continue;				
			}
			
			
		}
		readPDF.carins = amount;
		System.out.println("Car" + amount);
		LineNumberReader lineNum24 = new LineNumberReader(new FileReader("csvFile"));
		amount = 0.0;
		while ((line = lineNum24.readLine()) != null) {
			lineArray = line.split(",", 3);
			transactionCategory = lineArray[0];
			if (transactionCategory.equals("DENTALINS")) {
				trans1 = lineArray[2];
				trans2 = Double.valueOf(trans1);
				total = trans2 + amount;
				amount = total;
			}
			else if (lineArray[0] != "DENTALINS") {
				continue;				
			}
			
			
		}
		
		readPDF.dentalins = amount;
		System.out.println("Dental" + amount);
		
		LineNumberReader lineNum25 = new LineNumberReader(new FileReader("csvFile"));
		amount = 0.0;
		while ((line = lineNum25.readLine()) != null) {
			lineArray = line.split(",", 3);
			transactionCategory = lineArray[0];
			if (transactionCategory.equals("HOUSE")) {
				trans1 = lineArray[2];
				trans2 = Double.valueOf(trans1);
				total = trans2 + amount;
				amount = total;
			}
			else if (lineArray[0] != "HOUSE") {
				continue;				
			}
			
			
		}
		readPDF.house = amount;
		System.out.println("House" + amount);
		
		
		
		
	}
}
