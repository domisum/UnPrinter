package io.domisum.unprinter;

public enum Sign
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
