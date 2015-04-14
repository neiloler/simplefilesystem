package neiloler.filesystem;

public abstract class SimpleFile {

	public enum FileType {
		Drive,
		Folder,
		TextFile,
		ZipFile
	};
	
	private FileType _fileType;
	private String _fileName;
	private String _filePath;
	
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
	abstract <T> T getContents();
}
