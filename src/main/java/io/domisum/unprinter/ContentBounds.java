package io.domisum.unprinter;

import io.domisum.lib.auxiliumlib.datacontainers.math.Coordinate2DInt;
import lombok.Getter;

import java.util.List;

public class ContentBounds
{
	
	@Getter
	private final List<Coordinate2DInt> corners;
	
	
	// INIT
	public ContentBounds(List<Coordinate2DInt> corners)
	{
		if(corners.size() != 4)
			throw new IllegalArgumentException("Need 4 corner coords, but got "+corners.size());
		
		this.corners = List.copyOf(corners);
	}
	
}
