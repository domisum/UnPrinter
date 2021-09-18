package io.domisum.unprinter;

import io.domisum.lib.auxiliumlib.datacontainers.math.Vector2D;
import io.domisum.lib.snaporta.Snaporta;
import io.domisum.lib.snaporta.snaportas.SnaportaPainter;
import io.domisum.lib.snaporta.snaportas.transform.interpolator.Interpolator;
import io.domisum.lib.snaporta.util.Sized;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ImageDeprojector
{
	
	private final Interpolator interpolator;
	
	
	// DEPROJECT
	public Snaporta deproject(Snaporta image, ContentBounds contentBounds, Sized outputSize)
	{
		var painter = new SnaportaPainter(outputSize.getWidth(), outputSize.getHeight());
		
		var topLeft = new Vector2D(contentBounds.getCorner(Corner.TOP_LEFT).getX(), contentBounds.getCorner(Corner.TOP_LEFT).getY());
		var topRight = new Vector2D(contentBounds.getCorner(Corner.TOP_RIGHT).getX(), contentBounds.getCorner(Corner.TOP_RIGHT).getY());
		var topLeftToRight = topRight.deriveSubtract(topLeft);
		
		var bottomLeft = new Vector2D(contentBounds.getCorner(Corner.BOTTOM_LEFT).getX(), contentBounds.getCorner(Corner.BOTTOM_LEFT).getY());
		var bottomRight = new Vector2D(contentBounds.getCorner(Corner.BOTTOM_RIGHT).getX(), contentBounds.getCorner(Corner.BOTTOM_RIGHT).getY());
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
