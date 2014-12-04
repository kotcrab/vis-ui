/**
 * Copyright 2014 Pawel Pastuszak
 * 
 * This file is part of VisEditor.
 * 
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.kotcrab.vis.editor.util;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class CopyFileVisitor extends SimpleFileVisitor<Path> {
	private final Path targetPath;
	private Path sourcePath = null;

	public CopyFileVisitor (Path targetPath) {
		this.targetPath = targetPath;
	}

	@Override
	public FileVisitResult preVisitDirectory (final Path dir, final BasicFileAttributes attrs) throws IOException {
		if (sourcePath == null) {
			sourcePath = dir;
		} else {
			Files.createDirectories(targetPath.resolve(sourcePath.relativize(dir)));
		}
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile (final Path file, final BasicFileAttributes attrs) throws IOException {
		Files.copy(file, targetPath.resolve(sourcePath.relativize(file)));
		return FileVisitResult.CONTINUE;
	}
}
