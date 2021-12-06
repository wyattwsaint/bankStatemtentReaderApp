package readPDF;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class updatedPDFReader {

	public static void main(String[] args) throws IOException {
		
		stripText();

	}
	
	static void stripText() throws IOException  {
		
		File file = new File("C:\\Users\\wmsai\\Desktop\\BankStatement.pdf");
		PDDocument doc = PDDocument.load(file);
		PDFTextStripper textStripper = new PDFTextStripper();
		String text = textStripper.getText(doc);
		System.out.println(text);
	}
	

}
