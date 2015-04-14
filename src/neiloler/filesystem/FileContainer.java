package neiloler.filesystem;

import java.util.ArrayList;
import java.util.List;

public class FileContainer extends SimpleFile {

	private List<SimpleFile> _files;
	
	public FileContainer(FileType fileType, String fileName, String filePath) {
		_files = new ArrayList<>();
		
		_fileType = fileType;
		_fileName = fileName;
		_filePath = filePath;
	}
	
	@Override
	double getSize() {
		double total = 0;
		
		for (SimpleFile simpleFile : _files) {
			total += simpleFile.getSize();
		}
		
		return total;
	}

	@Override
	<T> T getContents() {
		return (T) _files;
	}
	
	public void addFile(SimpleFile file) {
		_files.add(file);
	}

}
