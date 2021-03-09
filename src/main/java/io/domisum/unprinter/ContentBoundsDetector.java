package io.domisum.unprinter;

import io.domisum.lib.auxiliumlib.PHR;
import io.domisum.lib.auxiliumlib.datacontainers.math.Coordinate2DInt;
import io.domisum.lib.snaporta.Snaporta;
import io.domisum.lib.snaporta.color.Color;
import io.domisum.lib.snaporta.util.ArgbUtil;
import io.domisum.lib.snaporta.util.Sized;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;

public class ContentBoundsDetector
{
	
	public ContentBounds detect(Snaporta image)
	{
		var cornersCoordinates = new ArrayList<Coordinate2DInt>();
		for(var corner : Corner.values())
		{
			var cornerCoords = detectCornerCoordinates(image, corner);
			cornersCoordinates.add(cornerCoords);
		}
		
		return new ContentBounds(cornersCoordinates);
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
		int argb = image.getArgbAt(x, y);
		int blue = ArgbUtil.getRedComponent(argb);
		
		return blue > Color.COLOR_COMPONENT_MAX/3;
	}
	
	
	@RequiredArgsConstructor
	private enum Corner
	{
		
		TOP_LEFT(Sign.POSITIVE, Sign.POSITIVE),
		TOP_RIGHT(Sign.NEGATIVE, Sign.POSITIVE),
		BOTTOM_RIGHT(Sign.NEGATIVE, Sign.NEGATIVE),
		BOTTOM_LEFT(Sign.POSITIVE, Sign.NEGATIVE);
		
		
		@Getter
		private final Sign awaySignX;
		@Getter
		private final Sign awaySignY;
		
		
		// GETTERS
		public Coordinate2DInt getPixelCoordinates(Sized sized)
		{
			int xCoord = awaySignX == Sign.NEGATIVE ? sized.getWidth()-1 : 0;
			int yCoord = awaySignY == Sign.NEGATIVE ? sized.getHeight()-1 : 0;
			
			return new Coordinate2DInt(xCoord, yCoord);
		}
		
	}
	
	private enum Sign
	{
		
		POSITIVE,
		NEGATIVE;
		
		
		// GETTERS
		public int getFactor()
		{
			if(this == POSITIVE)
				return 1;
			else
				return -1;
		}
		
	}
	
}
