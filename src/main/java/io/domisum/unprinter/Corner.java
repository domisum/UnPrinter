package io.domisum.unprinter;

import io.domisum.lib.auxiliumlib.datacontainers.math.Coordinate2DInt;
import io.domisum.lib.snaporta.util.Sized;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Corner
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
