package neiloler.filesystem;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FileContainer extends SimpleFile {

	private Map<String, SimpleFile> _files;
	private FileContainer _parent;
	
	/**
	 * Create a drive object.
	 * 
	 * @param driveName Name of drive.
	 */
	public FileContainer(String driveName) {

		_parent = null;
		_files = new HashMap<>();
		
		_fileType = FileType.Drive;
		_fileName = driveName;
		_filePath = driveName;
	}
	
	/**
	 * Create a folder or zip container
	 * 
	 * @param fileType The type of folder to make, Folder or Zip.
	 * @param fileName The name of this FileContainer.
	 * @param filePath Full path to this FileContainer, including its own name.
	 * @throws Exception If you try to pass in a FileType that doesn't make sense (Drive or TextFile), you'll blow up.
	 */
	// This is for folders and zips
	public FileContainer(FileType fileType, String fileName, String filePath) throws Exception {
		
		_files = new HashMap<>();
		
		if (fileType == FileType.Drive || fileType == FileType.TextFile) {
			// TODO THIS IS A PROBLEM, BARK AT USER/DEV TO NOT DO THIS, CREATE CUSTOM EXCEPTION
			throw new Exception("Cannot instantiate a TextFile or a Drive this way!");
		}
		
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
	
	public FileContainer getParent() {
		return _parent;
	}
	
	public void setParent(SimpleFile parent) {
		if (parent.getType() == FileType.TextFile) {
			throw new RuntimeException("A text file can't contain files!");
		}
			
		_parent = (FileContainer)parent;
	}
}
