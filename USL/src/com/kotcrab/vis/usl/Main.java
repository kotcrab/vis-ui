package com.kotcrab.vis.usl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class Main {
	public static void main (String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: <input usl file> <output json file>");
			System.exit(-2);
		}

		File input = new File(args[0]);
		File output = new File(args[1]);

		if (input.exists() == false) {
			System.out.println("Input file does not exist");
			System.exit(-3);
		}

		String usl = USL.parse(input);

		try {
			if (output.exists() == false)
				output.createNewFile();

			PrintStream out = new PrintStream(new FileOutputStream(output));
			out.print(usl);
			out.close();
			System.out.println("Done.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
