package at4091.bdshw3;

import java.io.File;

public class ResourceLoader {

	File file;
	ClassLoader classLoader;
	
	public ResourceLoader() {
		
		this.classLoader = getClass().getClassLoader();

	}
	
	public File getFile(String fileName) {
		this.file = new File(this.classLoader.getResource(fileName).getFile());
		return file;
	}
	
}
