package io.domisum.unprinter.cli;

import com.google.common.collect.Iterables;
import io.domisum.lib.auxiliumlib.PHR;
import io.domisum.lib.auxiliumlib.util.FileUtil;
import io.domisum.lib.auxiliumlib.util.FileUtil.FileType;
import io.domisum.lib.auxiliumlib.util.StringReportUtil;
import io.domisum.lib.auxiliumlib.util.StringUtil;
import io.domisum.lib.snaporta.CardinalRotation;
import io.domisum.lib.snaporta.Snaporta;
import io.domisum.lib.snaporta.formatconversion.io.SnaportaReader;
import io.domisum.lib.snaporta.snaportas.color.AutomaticWhiteBalanceSnaporta;
import io.domisum.lib.snaporta.snaportas.transform.CardinallyRotatedSnaporta;
import io.domisum.lib.snaporta.snaportas.transform.interpolator.ClosestPixelInterpolator;
import io.domisum.lib.snaporta.util.Sized;
import io.domisum.unprinter.ContentBoundsDetector;
import io.domisum.unprinter.ImagePdfWriter;
import io.domisum.unprinter.image.ImageDeprojector;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
public class PdfCommand
{
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	
	// CONSTANT SETTINGS
	private static final double OUTPUT_DPI = 300;
	
	// CONSTANTS
	private static final Sized A4_SIZE_IN_MM = Sized.sized(210, 297);
	private static final double MM_PER_INCH = 25.4;
	private static final Sized A4_300_DPI_RESOLUTION = Sized.sized(
		(int) Math.round(A4_SIZE_IN_MM.getWidth()/MM_PER_INCH*OUTPUT_DPI),
		(int) Math.round(A4_SIZE_IN_MM.getHeight()/MM_PER_INCH*OUTPUT_DPI));
	
	// DEPENDENCIES
	private final ContentBoundsDetector contentBoundsDetector = new ContentBoundsDetector();
	private final ImageDeprojector deprojector = new ImageDeprojector(new ClosestPixelInterpolator());
	private final ImagePdfWriter imagePdfWriter = new ImagePdfWriter();
	
	// SETTINGS
	private final File inputDir;
	private final File outputDir;
	
	// INPUT
	private final List<String> args;
	
	// SHARED
	private final List<Snaporta> images = new ArrayList<>();
	
	
	// EXECUTE
	public void execute()
	{
		String documentName = args.get(0);
		logger.info("Document name: {}", documentName);
		
		var docArgs = args.subList(1, args.size());
		for(String arg : docArgs)
			handleArg(arg);
		
		var outputPdfFile = new File(outputDir, documentName+".pdf");
		logger.info("Writing {} image(s) to pdf file: {}", images.size(), outputPdfFile);
		if(outputPdfFile.exists())
			logger.warn("Output pdf file already exists, overwriting");
		imagePdfWriter.write(outputPdfFile, images);
		logger.info("Pdf done\n");
	}
	
	private void handleArg(String arg)
	{
		logger.info("Pdf arg: {}", arg);
		var argSplit = StringUtil.split(arg, ":");
		
		var file = findFile(argSplit.get(0));
		logger.info("Query matched file: {}", file.getName());
		var inputImage = SnaportaReader.readFromFile(file);
		
		var rotation = parseRotation(argSplit);
		logger.info("Rotation: {}", rotation);
		inputImage = new CardinallyRotatedSnaporta(inputImage, rotation);
		
		var contentBounds = contentBoundsDetector.detect(inputImage);
		if(contentBounds.getTopWidth() > contentBounds.getLeftHeight())
			logger.warn("Top width ({}) is greater than left height ({}). Not rotated correctly?".toUpperCase(Locale.ROOT),
				Math.round(contentBounds.getTopWidth()), Math.round(contentBounds.getLeftHeight()));
		var deprojectedImage = deprojector.deproject(inputImage, contentBounds, A4_300_DPI_RESOLUTION);
		
		if(shouldWhiteBalance(argSplit))
			deprojectedImage = new AutomaticWhiteBalanceSnaporta(deprojectedImage);
		else
			logger.info("No white balancing");
		
		images.add(deprojectedImage);
	}
	
	private File findFile(String query)
	{
		var matchingFiles = new ArrayList<File>();
		
		for(var file : FileUtil.listFilesFlat(inputDir, FileType.FILE))
			if(doesFileMatchQuery(query, file))
				matchingFiles.add(file);
		
		if(matchingFiles.size() == 0)
			throw new IllegalArgumentException("No file matches query: "+query);
		else if(matchingFiles.size() > 1)
			throw new IllegalArgumentException(PHR.r("Multiple files match query '{}':\n{}",
				query, StringReportUtil.report(matchingFiles)));
		
		return Iterables.getOnlyElement(matchingFiles);
	}
	
	private boolean doesFileMatchQuery(String query, File file)
	{
		query = query.toLowerCase(Locale.ROOT);
		
		String fileName = file.getName().toLowerCase(Locale.ROOT);
		return fileName.contains(query);
	}
	
	private CardinalRotation parseRotation(List<String> argSplit)
	{
		if(argSplit.size() == 1)
			return CardinalRotation.NONE;
		
		for(String s : argSplit)
			if("rcw".equalsIgnoreCase(s))
				return CardinalRotation.CLOCKWISE_90;
			else if("rccw".equalsIgnoreCase(s))
				return CardinalRotation.COUNTERCLOCKWISE_90;
			else if("rusd".equalsIgnoreCase(s))
				return CardinalRotation._180;
		
		return CardinalRotation.NONE;
	}
	
	private boolean shouldWhiteBalance(List<String> argSplit)
	{
		for(String s : argSplit)
			if("nwb".equalsIgnoreCase(s))
				return false;
		
		return true;
	}
	
}
