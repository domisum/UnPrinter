package io.domisum.unprinter;

import io.domisum.lib.auxiliumlib.datacontainers.math.Coordinate2DInt;
import lombok.Getter;

import java.util.Map;

public class ContentBounds
{
	
	@Getter
	private final Map<Corner, Coordinate2DInt> corners;
	
	
	// INIT
	public ContentBounds(Map<Corner, Coordinate2DInt> corners)
	{
		this.corners = Map.copyOf(corners);
		
		if(this.corners.size() > 4)
			throw new IllegalArgumentException("Need 4 corners, but got "+corners.size());
		for(var corner : corners.keySet())
			if(!corners.containsKey(corner))
				throw new IllegalArgumentException("Missing corner: "+corner);
	}
	
	
	// GETTERS
	public Coordinate2DInt getCorner(Corner corner)
	{
		return corners.get(corner);
	}
	
}
