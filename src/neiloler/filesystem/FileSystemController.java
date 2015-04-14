package neiloler.filesystem;

public class FileSystemController {
	
	public enum OpResult {
		FAILURE,
		SUCCESS,
		UNKNOWN,
		BAD_COMMAND
	}
	
	public OpResult create(String[] command) {
		if (command.length != 2 && command.length != 3) {
			return OpResult.BAD_COMMAND;
		}
		
		return OpResult.FAILURE;
	}
}
