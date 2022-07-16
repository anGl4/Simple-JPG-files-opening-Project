package swingJpg;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

public class JpgFileVisitor extends SimpleFileVisitor<Path> {
	Map<Path, String> data = new HashMap<>();

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		String fileName = file.getFileName().toString();

		if (fileName.endsWith(".jpg")) {
			data.put(file.toAbsolutePath(), parseTitle(fileName));
		}

		return FileVisitResult.CONTINUE;
	}

	public String parseTitle(String pathName) {
		return pathName.substring(0, pathName.indexOf("."));
	}

	public Map<Path, String> getData() {
		return data;
	}

//	Testing the functionality of file visitor
//	
//	public static void main(String[] args) throws IOException {
//		Path path = Path.of("C:\\VariousData");
//		JpgFileVisitor visitor = new JpgFileVisitor();
//		Files.walkFileTree(path, visitor);
//		Map<Path, String> files = visitor.getData();
//		for (Map.Entry<Path, String> entry : files.entrySet()) {
//			System.out.println(entry.getKey().toString() + ", title: " + entry.getValue());
//		}
//	}
}
