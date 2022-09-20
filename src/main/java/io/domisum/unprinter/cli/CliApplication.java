package io.domisum.unprinter.cli;

import io.domisum.lib.auxiliumlib.thread.ticker.IntervalTaskTicker;
import io.domisum.lib.auxiliumlib.util.StringUtil;
import io.domisum.lib.auxiliumlib.util.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class CliApplication
{
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	
	// STATE
	private final IntervalTaskTicker ticker = new IntervalTaskTicker("ticker", null);
	private final Queue<String> pendingCommandInputs = new ConcurrentLinkedQueue<>();
	
	// SETTINGS
	private File inputDir;
	private File outputDir;
	
	
	// INIT
	public static void main(String[] args)
	{
		new CliApplication();
	}
	
	private CliApplication()
	{
		ticker.addTask("executeCommand", this::processCommand, Duration.ofMillis(100));
		ticker.start();
		
		startCliReadDaemon();
	}
	
	private void startCliReadDaemon()
	{
		ThreadUtil.createAndStartDaemonThread(this::cliRead, "cliRead");
	}
	
	private void cliRead()
	{
		logger.info("Waiting for command input...");
		try(var scanner = new Scanner(System.in))
		{
			while(scanner.hasNextLine())
			{
				String input = scanner.nextLine().trim();
				logger.info("Received input: {}", input);
				pendingCommandInputs.add(input);
			}
		}
	}
	
	
	// EXECUTE
	private void processCommand()
	{
		String commandInput = pendingCommandInputs.poll();
		if(commandInput == null)
			return;
		
		var segments = StringUtil.split(commandInput, " ");
		if(segments.isEmpty())
			return;
		
		String commandName = segments.get(0).toLowerCase();
		var args = segments.subList(1, segments.size());
		
		executeCommand(commandName, args);
	}
	
	private void executeCommand(String commandName, List<String> args)
	{
		try
		{
			if("inputDir".equalsIgnoreCase(commandName))
				inputDir(args);
			else if("outputDir".equalsIgnoreCase(commandName))
				outputDir(args);
			else if("pdf".equalsIgnoreCase(commandName))
				new PdfCommand(inputDir, outputDir, args).execute();
			else if("stop".equalsIgnoreCase(commandName) || "exit".equalsIgnoreCase(commandName))
				stop();
			else
				logger.error("Unknown command: {}", commandName);
		}
		catch(Exception e)
		{
			logger.error("Failed to execute command {}", commandName, e);
		}
	}
	
	private void inputDir(List<String> args)
	{
		inputDir = new File(args.get(0));
		logger.info("Input directory: {}", inputDir.getAbsolutePath());
	}
	
	private void outputDir(List<String> args)
	{
		outputDir = new File(args.get(0));
		logger.info("Output directory: {}", outputDir.getAbsolutePath());
	}
	
	private void stop()
	{
		logger.info("Stopping...");
		ticker.stopSoft();
	}
	
}
