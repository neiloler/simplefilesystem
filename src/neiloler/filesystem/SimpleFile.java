package neiloler.filesystem;

public abstract class SimpleFile {

	public enum FileType {
		Drive,
		Folder,
		TextFile,
		ZipFile
	};
	
	protected FileType _fileType;
	protected String _fileName;
	protected String _filePath;
	
	public FileType getType() {
		return _fileType;
	}
	
	public String getName() {
		return _fileName;
	}
	
	public String getPath() {
		return _filePath;
	}
	
	abstract double getSize();
}
