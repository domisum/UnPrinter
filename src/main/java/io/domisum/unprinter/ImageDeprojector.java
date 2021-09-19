package io.domisum.unprinter;

import io.domisum.lib.snaporta.Snaporta;
import io.domisum.lib.snaporta.snaportas.SnaportaPainter;
import io.domisum.lib.snaporta.snaportas.transform.interpolator.Interpolator;
import io.domisum.lib.snaporta.util.Sized;
import io.domisum.unprinter.model.ContentBounds;
import io.domisum.unprinter.model.Corner;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ImageDeprojector
{
	
	private final Interpolator interpolator;
	
	
	// DEPROJECT
	public Snaporta deproject(Snaporta image, ContentBounds contentBounds, Sized outputSize)
	{
		var painter = new SnaportaPainter(outputSize.getWidth(), outputSize.getHeight());
		
		var topLeft = contentBounds.getCornerVector(Corner.TOP_LEFT);
		var topRight = contentBounds.getCornerVector(Corner.TOP_RIGHT);
		var topLeftToRight = topRight.deriveSubtract(topLeft);
		
		var bottomLeft = contentBounds.getCornerVector(Corner.BOTTOM_LEFT);
		var bottomRight = contentBounds.getCornerVector(Corner.BOTTOM_RIGHT);
		var bottomLeftToRight = bottomRight.deriveSubtract(bottomLeft);
		
		for(int y = 0; y < outputSize.getHeight(); y++)
			for(int x = 0; x < outputSize.getWidth(); x++)
			{
				double relX = x/(double) outputSize.getWidth();
				double relY = y/(double) outputSize.getHeight();
				
				var topPoint = topLeft.deriveAdd(topLeftToRight.deriveMultiply(relX));
				var bottomPoint = bottomLeft.deriveAdd(bottomLeftToRight.deriveMultiply(relX));
				
				var topPointToBottomPoint = bottomPoint.deriveSubtract(topPoint);
				var deprojectedPoint = topPoint.deriveAdd(topPointToBottomPoint.deriveMultiply(relY));
				
				int argbAt = interpolator.interpolateARGBAt(image, deprojectedPoint.getX(), deprojectedPoint.getY());
				painter.setARGBAt(x, y, argbAt);
			}
		
		return painter.toSnaporta();
	}
	
}
