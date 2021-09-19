package io.domisum.unprinter;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.property.UnitValue;
import io.domisum.lib.auxiliumlib.exceptions.ProgrammingError;
import io.domisum.lib.snaporta.Snaporta;
import io.domisum.lib.snaporta.formatconversion.io.SnaportaWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

public class ImagePdfWriter
{
	
	// WRITE
	public void write(File pdfFile, Snaporta... images)
	{
		write(pdfFile, Arrays.asList(images));
	}
	
	public void write(File pdfFile, List<Snaporta> images)
	{
		pdfFile.getParentFile().mkdirs();
		
		try(var pdfDoc = new PdfDocument(new PdfWriter(pdfFile));
			var doc = new Document(pdfDoc, PageSize.A4))
		{
			writeUncaught(doc, images);
		}
		catch(FileNotFoundException e)
		{
			throw new ProgrammingError(e);
		}
	}
	
	private void writeUncaught(Document doc, List<Snaporta> images)
	{
		doc.setMargins(0, 0, 0, 0);
		
		for(var snaporta : images)
		{
			var imgRaw = SnaportaWriter.writeToRaw(snaporta, "jpg");
			var imageData = ImageDataFactory.createJpeg(imgRaw);
			
			var img = new Image(imageData);
			img.setWidth(UnitValue.createPercentValue(100));
			img.setHeight(UnitValue.createPercentValue(100));
			
			doc.add(img);
		}
	}
	
}
