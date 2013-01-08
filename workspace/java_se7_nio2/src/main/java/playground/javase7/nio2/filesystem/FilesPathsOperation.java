package playground.javase7.nio2.filesystem;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class FilesPathsOperation {
	public static void main(String[] args) throws IOException {
		// path2からみた、path1の相対パス
		Path path1 = Paths.get("./src/main/java");
		System.out.println(path1.toUri());
		
		Path path2 = Paths.get(".", "src");
		System.out.println(path2.relativize(path1));

		
		System.out.println("====================");

		
		//  プロジェクトカレントの全classファイル取得 
		Files.walkFileTree(Paths.get("."), new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if (attrs.isRegularFile()) {
					if (file.getFileName().toString().endsWith(".class")) {
						System.out.println(file.getFileName().toString());
					}
				}
				return FileVisitResult.CONTINUE;
			}
		});
	}
}
