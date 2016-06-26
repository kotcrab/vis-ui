/*
 * Copyright 2014-2016 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kotcrab.vis.usl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/** Main class of ULS, allowing to use it from command line */
public class Main {
	public static void main (String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: <input usl file> <output json file>");
			System.exit(1);
		}

		File input = new File(args[0]);
		File output = new File(args[1]);

		if (input.exists() == false) {
			System.out.println("Input file does not exist");
			System.exit(2);
		}

		System.out.println("Parsing USL...");
		String usl = USL.parse(input);

		try {
			if (output.exists() == false) {
				output.createNewFile();
			}

			System.out.println("Writing JSON " + output.getPath() + "...");
			PrintStream out = new PrintStream(new FileOutputStream(output));
			out.print(usl);
			out.close();
			System.out.println("Success.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
