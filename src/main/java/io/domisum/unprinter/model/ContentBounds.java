package io.domisum.unprinter.model;

import io.domisum.lib.auxiliumlib.datacontainers.math.Coordinate2DInt;
import io.domisum.lib.auxiliumlib.datacontainers.math.Vector2D;
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
	
	public Vector2D getCornerVector(Corner corner)
	{
		var cornerCoord = getCorner(corner);
		return new Vector2D(cornerCoord.getX(), cornerCoord.getY());
	}
	
	public double getTopWidth()
	{
		var tl = getCornerVector(Corner.TOP_LEFT);
		var tr = getCornerVector(Corner.TOP_RIGHT);
		return tl.distanceTo(tr);
	}
	
	public double getLeftHeight()
	{
		var tl = getCornerVector(Corner.TOP_LEFT);
		var bl = getCornerVector(Corner.BOTTOM_LEFT);
		return tl.distanceTo(bl);
	}
	
}
