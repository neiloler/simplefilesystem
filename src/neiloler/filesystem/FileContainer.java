package neiloler.filesystem;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FileContainer extends SimpleFile {

	private Map<String, SimpleFile> _files;
	
	public FileContainer(FileType fileType, String fileName, String filePath) {
		_files = new HashMap<>();
		
		_fileType = fileType;
		_fileName = fileName;
		_filePath = filePath;
	}
	
	@Override
	double getSize() {
		double total = 0;
		Collection<SimpleFile> files = _files.values();
		for (SimpleFile simpleFile : files) {
			total += simpleFile.getSize();
		}
		
		if (_fileType == FileType.ZipFile) {
			return total / 2;
		}
		else {
			return total;
		}
	}

	public Map<String, SimpleFile> getContents() {
		return _files;
	}
	
	public void addFile(String fileName, SimpleFile file) {
		_files.put(fileName, file);
	}

}
