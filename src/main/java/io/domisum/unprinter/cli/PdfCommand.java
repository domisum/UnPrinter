package io.domisum.unprinter.cli;

import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.List;

@RequiredArgsConstructor
public class PdfCommand
{
	
	// SETTINGS
	private final File inputDir;
	private final File outputDir;
	
	// INPUT
	private final List<String> args;
	
	
	// EXECUTE
	public void execute()
	{
	
	}
	
}
