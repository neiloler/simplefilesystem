package neiloler.filesystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileSystemController {
	
	List<FileContainer> _drives;
	
	// SETUP HELPER
	StringBuilder helpDisplay = new StringBuilder(
			"\n\nSimpleFileSystem Help\n" +
			"------------------------------------------------\n" +
			"create [type] [name] [path]\n" +
			"\t type: drive, folder, zip, or text\n" +
			"\t name: name of file\n" +
			"\t path: path, delineated by \\ characters\n\n"
			);
	
	public FileSystemController() {
		_drives = new ArrayList<>();
	}
	
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
		// create [type] [name] [path]
		
		OpResult RESULT = OpResult.UNKNOWN_COMMNAND;
		
		if (command.length != 3 && command.length != 4) {
			RESULT = OpResult.BAD_COMMAND;
		}
		else {
			// TODO Do we need to protect against things like "//" (this will make a path level of "")
			
			List<String> path;
			if (command.length == 3) {
				path = new ArrayList<>();
				path.add("");
			}
			else {
				path = new ArrayList<>();
				String[] pathArray = command[3].split("/");
				path.addAll(Arrays.asList(pathArray));
			}
			
			for (String string : path) {
				System.out.println(string);
			}
			
		}
		
		return RESULT;
	}
}
