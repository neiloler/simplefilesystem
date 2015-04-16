package neiloler.filesystem;


public class TextFile extends SimpleFile {

	private FileContainer _parent;
	private String _contents;
	
	public TextFile(String fileName, String filePath) {
		_fileType = FileType.TextFile;
		_fileName = fileName;
		_filePath = filePath;
		_contents = "";
	}
	
	@Override
	double getSize() {
		return _contents.length();
	}

	public String getContents() {
		return _contents;
	}
	
	public void writeToFile(String newContents) {
		_contents = newContents;
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
