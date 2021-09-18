package io.domisum.unprinter;

import io.domisum.lib.auxiliumlib.PHR;
import io.domisum.lib.auxiliumlib.datacontainers.math.Coordinate2DInt;
import io.domisum.lib.snaporta.Snaporta;
import io.domisum.lib.snaporta.color.Color;
import io.domisum.lib.snaporta.util.ArgbUtil;
import io.domisum.unprinter.model.ContentBounds;
import io.domisum.unprinter.model.Corner;

import java.util.HashMap;

public class ContentBoundsDetector
{
	
	public ContentBounds detect(Snaporta image)
	{
		var cornerCoordinates = new HashMap<Corner, Coordinate2DInt>();
		for(var corner : Corner.values())
		{
			var cornerCoords = detectCornerCoordinates(image, corner);
			cornerCoordinates.put(corner, cornerCoords);
		}
		
		return new ContentBounds(cornerCoordinates);
	}
	
	private Coordinate2DInt detectCornerCoordinates(Snaporta image, Corner corner)
	{
		int diagMax = Math.min(image.getWidth(), image.getHeight());
		for(int diag = 0; diag < diagMax; diag++)
			for(int inDiag = 0; inDiag <= diag; inDiag++)
			{
				var imageCornerCoordinates = corner.getPixelCoordinates(image);
				
				int dXAbs = inDiag;
				int dYAbs = diag-inDiag;
				
				int dX = corner.getAwaySignX().getFactor()*dXAbs;
				int dY = corner.getAwaySignY().getFactor()*dYAbs;
				
				var pixelCoordinate = imageCornerCoordinates.deriveAdd(dX, dY);
				
				if(doesPixelQualifyAsCorner(image, pixelCoordinate))
					return pixelCoordinate;
			}
		
		throw new IllegalArgumentException(PHR.r("No content corner found in {} corner", corner));
	}
	
	private boolean doesPixelQualifyAsCorner(Snaporta image, Coordinate2DInt cornerCandidateCoords)
	{
		if(!doesPixelQualifyAsContent(image, cornerCandidateCoords.getX(), cornerCandidateCoords.getY()))
			return false;
		
		int minImageDimension = Math.min(image.getWidth(), image.getHeight());
		int radius = minImageDimension/200;
		
		int sideLength = radius*2+1;
		int totalPixels = sideLength*sideLength;
		int minContentPixels = totalPixels/5;
		
		int contentPixelsCount = 0;
		for(int dX = -radius; dX <= radius; dX++)
			for(int dY = -radius; dY <= radius; dY++)
			{
				int x = cornerCandidateCoords.getX()+dX;
				int y = cornerCandidateCoords.getY()+dY;
				if(doesPixelQualifyAsContent(image, x, y))
				{
					contentPixelsCount++;
					if(contentPixelsCount >= minContentPixels)
						return true;
				}
			}
		
		return false;
	}
	
	private boolean doesPixelQualifyAsContent(Snaporta image, int x, int y)
	{
		if(image.isOutOfBounds(x, y))
			return false;
		
		int argb = image.getArgbAt(x, y);
		int blue = ArgbUtil.getRedComponent(argb);
		
		return blue > Color.COLOR_COMPONENT_MAX/3;
	}
	
}
