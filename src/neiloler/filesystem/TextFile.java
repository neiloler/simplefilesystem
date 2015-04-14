package neiloler.filesystem;

public class TextFile extends SimpleFile {

	private String _contents;
	
	public TextFile(String fileName, String filePath, String contents) {
		_fileType = FileType.TextFile;
		_fileName = fileName;
		_filePath = filePath;
		_contents = contents;
	}
	
	@Override
	double getSize() {
		return _contents.length();
	}

	@Override
	<T> T getContents() {
		return (T) _contents;
	}
	
	public void writeToFile(String newContents) {
		_contents = newContents;
	}

}
