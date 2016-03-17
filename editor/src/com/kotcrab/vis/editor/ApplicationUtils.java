package com.kotcrab.vis.editor;

import com.kotcrab.vis.editor.util.PlatformUtils;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.List;

public class ApplicationUtils {
	public static String getRestartCommand () {
		List<String> vmArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
		StringBuilder vmArgsOneLine = new StringBuilder();
		for (String arg : vmArguments) {
			if (arg.contains("-agentlib") == false)
				vmArgsOneLine.append(arg).append(" ");
		}

		final StringBuilder cmd = new StringBuilder(PlatformUtils.getJavaBinPath() + " " + vmArgsOneLine);

		String[] mainCommand = System.getProperty("sun.java.command").split(" ");

		if (mainCommand[0].endsWith(".jar"))
			cmd.append("-jar " + new File(mainCommand[0]).getPath());
		else
			cmd.append("-cp \"" + System.getProperty("java.class.path") + "\" " + mainCommand[0]);

		for (int i = 1; i < mainCommand.length; i++) {
			cmd.append(" ");
			cmd.append(mainCommand[i]);
		}

		//if launching from idea, not in debug mode
		String ideaLauncher = "-Didea.launcher.bin.path=";
		int ideaLauncherStart = cmd.indexOf(ideaLauncher);
		if (ideaLauncherStart != -1) {
			cmd.insert(ideaLauncherStart + ideaLauncher.length(), "\"");
			cmd.insert(cmd.indexOf("-cp ", ideaLauncherStart) - 1, "\"");
		}

		return cmd.toString();
	}

	public static void startNewInstance () {
		try {
			CommandLine cmdLine = CommandLine.parse(getRestartCommand());
			DefaultExecutor executor = new DefaultExecutor();
			executor.setStreamHandler(new PumpStreamHandler(null, null, null));
			executor.execute(cmdLine, new DefaultExecuteResultHandler());
		} catch (Exception e) {
			Log.exception(e);
		}
	}
}
