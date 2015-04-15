package neiloler.filesystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import neiloler.filesystem.SimpleFile.FileType;

public class FileSystemController {
	
	// TODO We could let the user set this up the first time the program is run/ prompt it for the first command
	private final String DEFAULT_DRIVE_NAME = "moto";
	
	private Map<String, FileContainer> _drives;
	private FileContainer _currentLocation;
	
	// SETUP HELPER
	StringBuilder helpDisplay = new StringBuilder(
			"\n\nSimpleFileSystem Help\n" +
			"------------------------------------------------\n" +
			"create [type] [name] [path]\n" +
			"\t type: drive, folder, zip, or text\n" +
			"\t name: name of file (name cannot contain '/' characters)\n" +
			"\t path: path, delineated by / characters (path should not exist if making new drive)\n\n"
			);
	
	public FileSystemController() {
		_drives = new HashMap<>();
		
		// Add a default drive to start with
		
		_currentLocation = new FileContainer(DEFAULT_DRIVE_NAME);
		_drives.put(_currentLocation.getName(), _currentLocation);
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
			return OpResult.BAD_COMMAND;
		}
		else {
			FileType fileType;
			String fileName;
			String path;
			List<String> pathTokens;
			
			// Get TYPE
			if (command[1].equals("drive")) fileType = FileType.Drive;
			else if (command[1].equals("folder")) fileType = FileType.Folder;
			else if (command[1].equals("text")) fileType = FileType.TextFile;
			else if (command[1].equals("zip")) fileType = FileType.ZipFile;
			else return OpResult.UNKNOWN_COMMNAND;
			
			// Get NAME
			if (command[2].contains("/")) {
				return OpResult.FAILURE_ILLEGAL_FILE_SYSTEM_OPERATION;
			}
			else {
				fileName = command[2];
			}
			
			// Get PATH
			// TODO Do we need to protect against things like "//" (this will make a path level of "")
			if (command.length == 3) {
				// No path, should make in current path ('./')
				pathTokens = new ArrayList<>();
				pathTokens.add("");
				path = "";
			}
			else {
				path = command[3];
				pathTokens = new ArrayList<>();
				String[] pathArray = command[3].split("/");
				pathTokens.addAll(Arrays.asList(pathArray));
			}
			
			// Protect against making a drive inside anything else
			if (fileType == FileType.Drive && !path.equals("")) {
				return OpResult.FAILURE_ILLEGAL_FILE_SYSTEM_OPERATION;
			}
			
			// We'll catch all Drive creation requests here and assume anything afterwards is NOT a Drive request 
			if (fileType == FileType.Drive) {
				_drives.put(fileName, new FileContainer(fileName));
			}
			
			// Prep new file to be inserted in the specified place
			if (fileType == FileType.TextFile) {
				SimpleFile newFile;
				newFile = new TextFile(fileName, path);
				return createFileAtPath(newFile, _currentLocation, new LinkedList<String>(pathTokens));
			}
			else {
				try {
					SimpleFile newFile;
					newFile = new FileContainer(fileType, fileName, path);
					return createFileAtPath(newFile, _currentLocation, new LinkedList<String>(pathTokens));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return RESULT;
	}
	
	private OpResult createFileAtPath(SimpleFile fileToCreate, FileContainer node, Queue<String> path) {
		
		// Stop - We've reached our destination
		if (path.isEmpty()) {
			// Do we have something named this already?
			if (node.getContents().containsKey(fileToCreate.getName())) {
				return OpResult.FAILURE_PATH_ALREADY_EXISTS;
			}
			else {
				node.addFile(fileToCreate.getName(), fileToCreate);
				return OpResult.SUCCESS;
			}
		}
		
		// Do we already have something named this?
		if (node.getContents().containsKey(path.peek())) {
			// YES - Something along this path already exists
			if (node.getContents().get(path.peek()).getType() == FileType.TextFile) {
				// We have been given a path that is treating this token as a container, but this already exists and is a TextFile. This is a no-no.
				return OpResult.FAILURE_PATH_ALREADY_EXISTS;
			}
			else {
				// We have another FileContainer to dive into
				return createFileAtPath(fileToCreate, (FileContainer)node.getContents().get(path.poll()), path);
			}
			
		}
		else {
			// NO - We need to make a folder that matches this token of the path
			try {
				String newFolderName = path.poll();
				FileContainer newFolder;
				newFolder = new FileContainer(FileType.Folder, newFolderName, node.getPath() + "/" + newFolderName);
				node.getContents().put(newFolderName, newFolder);
				return createFileAtPath(fileToCreate, newFolder, path);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return OpResult.FAILURE_ILLEGAL_FILE_SYSTEM_OPERATION;
	}
}
