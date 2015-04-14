package neiloler.filesystem;

import java.util.List;

public class FileSystemController {
	
	// SETUP HELPER
	StringBuilder helpDisplay = new StringBuilder(
			"SimpleFileSystem Help\n" +
			"------------------------------------------------\n" +
			"create [type] [name] [path]\n" +
			"\t type: drive, folder, zip, or text\n" +
			"\t name: name of file\n" +
			"\t path: path, delineated by \\ characters\n\n"
			);
	
	
	
	public enum OpResult {
		FAILURE_PATH_NOT_FOUND,
		FAILURE_PATH_ALREADY_EXISTS,
		FAILURE_ILLEGAL_FILE_SYSTEM_OPERATION,
		FAILURE_NOT_A_TEXTFILE,
		SUCCESS,
		UNKNOWN_COMMNAND,
		BAD_COMMAND
	}
	
	public void showHelp() {
		System.out.println(helpDisplay.toString());
	}
	
	public OpResult create(String[] command) {
		
		OpResult RESULT = OpResult.UNKNOWN_COMMNAND;
		
		if (command.length != 2 && command.length != 3) {
			RESULT = OpResult.BAD_COMMAND;
		}
		else {
			
		}
		
		return RESULT;
	}
}
