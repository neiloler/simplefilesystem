package neiloler.filesystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.BiConsumer;

import neiloler.filesystem.FileSystemController.OpResult;
import neiloler.filesystem.SimpleFile.FileType;

public class FileSystemController {
	
	public enum OpResult {
		FAILURE_PATH_NOT_FOUND,
		FAILURE_PATH_ALREADY_EXISTS,
		FAILURE_ILLEGAL_FILE_SYSTEM_OPERATION,
		FAILURE_NOT_A_TEXTFILE,
		SUCCESS,
		UNKNOWN_COMMNAND,
		BAD_COMMAND
	}
	
	// PRIVATE MEMBERS
	
	// TODO We could let the user set this up the first time the program is run/ prompt it for the first command
	private final String DEFAULT_DRIVE_NAME = "moto";
	
	private Map<String, FileContainer> _drives;
	private FileContainer _currentLocation;
	
	private StringBuilder helpDisplay = new StringBuilder(
			"\n\nSimpleFileSystem Help\n" +
			"------------------------------------------------\n" +
			"COMMAND: create [type] [name] [path]\n" +
			"\t type: drive, folder, zip, or text\n" +
			"\t name: name of file (name cannot contain '/' characters)\n" +
			"\t path: path, delineated by / characters (path should not exist if making new drive)\n\n" +
			
			"COMMAND: move [source path] [destination path]\n" +
			"\t source path: path of file to move (including name of file)\n" +
			"\t destination path: path to move file to (including name of file)\n\n" +
			
			"COMMAND: delete [path]\n" +
			"\t path: path of file to delete (including name of file)\n\n" +
			
			"COMMAND: writeToFile OR write [path] [new contents]\n" +
			"\t path: path of file to delete (including name of file)\n" +
			"\t new contents: new contents to write to text file (overwrites current context)\n\n" +
			
			"COMMAND: ls OR dir\n" +
			"\t show current directory contents\n\n" +
			
			"COMMAND: pwd\n" +
			"\t show current directory path\n\n" +
			
			"COMMAND: cd .. OR [path]\n" +
			"\t ..: navigate up a directory\n" +
			"\t path: path, delineated by / characters \n\n" +
			
			"COMMAND: exit\n" +
			"\t Exit SimpleFileSystem \n\n"
			);
	
	// CONSTRUCTOR
	
	public FileSystemController() {
		_drives = new HashMap<>();
		
		// Add a default drive to start with
		
		_currentLocation = new FileContainer(DEFAULT_DRIVE_NAME);
		_drives.put(_currentLocation.getName(), _currentLocation);
	}
	
	// OPERATIONS
	
	public OpResult showHelp() {
		System.out.println(helpDisplay.toString());
		return OpResult.SUCCESS;
	}
	
	public String getCurrentDirectory() {
		return _currentLocation.getName();
	}
	
	/**
	 * Create a file with the given parameters.
	 * 
	 * @param command Raw command from Engine.
	 * @return Result code of the operation
	 */
	// TODO This should be more loosely coupled, with the engine passing in stricter contract information (fileName, filePath, etc)
	public OpResult create(String[] command) {
		// create [type] [name] [path]
		
		OpResult RESULT = OpResult.BAD_COMMAND;
		
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
	
	/**
	 * Delete a file with the given parameters.
	 * 
	 * @param command Raw command from Engine.
	 * @return Result code of the operation
	 */
	// TODO This should be more loosely coupled, with the engine passing in stricter contract information (fileName, filePath, etc)
	public OpResult delete(String[] command) {
		
		if (command.length != 2) {
			return OpResult.BAD_COMMAND;
		}
		
		String[] tokenStrings = command[1].split("/");
		List<String> pathTokens = new LinkedList<String>(Arrays.asList(tokenStrings));
		
		String nameOfFileToDelete;
		if (pathTokens.size() > 1) {
			nameOfFileToDelete = new String(pathTokens.get(pathTokens.size() - 1));
			pathTokens.remove(pathTokens.size() - 1);
		}
		else {
			nameOfFileToDelete = pathTokens.get(0);
			pathTokens = new ArrayList<String>();
		}
		
		FileContainer targetContainer = traversePathToEnd(_currentLocation, new LinkedList<String>(pathTokens));
		
		if (targetContainer == null) {
			return OpResult.FAILURE_PATH_NOT_FOUND;
		}
		
		if (targetContainer.getContents().remove(nameOfFileToDelete) == null) {
				return OpResult.FAILURE_PATH_NOT_FOUND;
		}
		
		return OpResult.SUCCESS;
	}
	
	/**
	 * Move a file with the given parameters.
	 * 
	 * @param command Raw command from Engine.
	 * @return Result code of the operation
	 */
	// TODO This should be more loosely coupled, with the engine passing in stricter contract information (fileName, filePath, etc)
	public OpResult move(String[] command) {
		
		if (command.length != 3) {
			return OpResult.BAD_COMMAND;
		}
		
		// Get FROM path tokens and name
		String[] fromTokenStrings = command[1].split("/");
		List<String> fromPathTokens = new LinkedList<String>(Arrays.asList(fromTokenStrings));
		
		String nameOfFileToMove;
		if (fromPathTokens.size() > 1) {
			nameOfFileToMove = new String(fromPathTokens.get(fromPathTokens.size() - 1));
			fromPathTokens.remove(fromPathTokens.size() - 1);
		}
		else {
			nameOfFileToMove = fromPathTokens.get(0);
			fromPathTokens = new ArrayList<String>();
		}
		
		// Get TO path tokens and name
		String[] toTokenStrings = command[2].split("/");
		List<String> toPathTokens = new LinkedList<String>(Arrays.asList(toTokenStrings));
		
		String nameOfDestinationFile;
		if (toPathTokens.size() > 1) {
			nameOfDestinationFile = new String(toPathTokens.get(toPathTokens.size() - 1));
			toPathTokens.remove(toPathTokens.size() - 1);
		}
		else {
			nameOfDestinationFile = toPathTokens.get(0);
			toPathTokens = new ArrayList<String>();
		}
		
		// Find target to move FROM
		FileContainer fromContainer = traversePathToEnd(_currentLocation, new LinkedList<String>(fromPathTokens));
		
		if (fromContainer == null) {
			return OpResult.FAILURE_PATH_NOT_FOUND;
		}
		
		SimpleFile fileToMove = fromContainer.getContents().get(nameOfFileToMove);
		if (fileToMove == null) {
				return OpResult.FAILURE_PATH_NOT_FOUND;
		}
		
		// Find target to move TO
		OpResult result = createFileAtPath(fileToMove, _currentLocation, new LinkedList<String>(toPathTokens));
		
		if (result != OpResult.SUCCESS) {
			return result;
		}
		
		fileToMove.setName(nameOfDestinationFile);
		fileToMove = fromContainer.getContents().remove(nameOfFileToMove);
		
		return OpResult.SUCCESS;
	}
	
	/**
	 * Write to a file. Checks to make sure that we are only attempting to write to text files.
	 * 
	 * @param command Raw command from Engine.
	 * @param rawInputString The raw string of the command passed in the engine.
	 * @return Result code of the operation
	 */
	// TODO This should be more loosely coupled, with the engine passing in stricter contract information (fileName, filePath, etc)
	public OpResult writeToFile(String[] command, String rawInputString) {
		
		String[] pathTokenStrings = command[1].split("/");
		List<String> pathTokens = new LinkedList<String>(Arrays.asList(pathTokenStrings));
		
		String nameofTargetFile;
		if (pathTokens.size() > 1) {
			nameofTargetFile = new String(pathTokens.get(pathTokens.size() - 1));
			pathTokens.remove(pathTokens.size() - 1);
		}
		else {
			nameofTargetFile = pathTokens.get(0);
			pathTokens = new ArrayList<String>();
		}
		
		FileContainer targetContainer = traversePathToEnd(_currentLocation, new LinkedList<String>(pathTokens));
		
		if (targetContainer == null) {
			return OpResult.FAILURE_PATH_NOT_FOUND;
		}
		
		SimpleFile targetFile = targetContainer.getContents().get(nameofTargetFile);
		
		if (targetFile.getType() != FileType.TextFile) {
			return OpResult.FAILURE_NOT_A_TEXTFILE;
		}
		
		// Remove first two words (separated by " "), we're assuming by now they're the commands
		rawInputString = rawInputString.substring(rawInputString.indexOf(" ") + 1, rawInputString.length());
		rawInputString = rawInputString.substring(rawInputString.indexOf(" ") + 1, rawInputString.length());
		
		((TextFile)targetFile).writeToFile(rawInputString);
		
		return OpResult.SUCCESS;
	}
	
	/**
	 * Read from a file. Checks to make sure that we are only attempting to read from text files.
	 * Also currently simply prints out contents of text file, nothing more.
	 * 
	 * @return Result code of the operation
	 */
	// TODO This should be more loosely coupled, with the engine passing in stricter contract information (fileName, filePath, etc)
	public OpResult readFromFile(String[] command) {
		String[] pathTokenStrings = command[1].split("/");
		List<String> pathTokens = new LinkedList<String>(Arrays.asList(pathTokenStrings));
		
		String nameofTargetFile;
		if (pathTokens.size() > 1) {
			nameofTargetFile = new String(pathTokens.get(pathTokens.size() - 1));
			pathTokens.remove(pathTokens.size() - 1);
		}
		else {
			nameofTargetFile = pathTokens.get(0);
			pathTokens = new ArrayList<String>();
		}
		
		FileContainer targetContainer = traversePathToEnd(_currentLocation, new LinkedList<String>(pathTokens));
		
		if (targetContainer == null) {
			return OpResult.FAILURE_PATH_NOT_FOUND;
		}
		
		SimpleFile targetFile = targetContainer.getContents().get(nameofTargetFile);
		
		if (targetFile.getType() != FileType.TextFile) {
			return OpResult.FAILURE_NOT_A_TEXTFILE;
		}
		
		System.out.println("CONTENTS:");
		System.out.println(((TextFile)targetFile).getContents());
		
		return OpResult.SUCCESS;
	}
	
	/**
	 * Print current location's contents to the console.
	 * 
	 * @return Success, for now
	 */
	public OpResult showCurrentLocationContents() {
		if (_currentLocation.getContents().isEmpty()) {
			System.out.println("{empty}");
			return OpResult.SUCCESS;
		}
		else {
			Map<String, SimpleFile> contents = _currentLocation.getContents();
			contents.forEach(new BiConsumer<String, SimpleFile>() {

				@Override
				public void accept(String t, SimpleFile u) {
//					System.out.println(t + "\t\t\ttype: " + u.getType() + "\t\t\tsize: " + u.getSize());
					System.out.format("%-15s%-15s%-15s\n", t, u.getType().toString(), Double.toString(u.getSize()));
				}
				
			});
			
			return OpResult.SUCCESS;
		}
	}
	
	/**
	 * Print current location's path to the console.
	 * 
	 * @return Success, for now
	 */
	public OpResult showCurrentLocationPath() {
		System.out.println("/" + _currentLocation.getPath());
		return OpResult.SUCCESS;
	}

	/**
	 * Changes current path to the desired path.
	 * 
	 * @param command Raw command from engine.
	 * @return
	 */
	// TODO This should be more loosely coupled, with the engine passing in stricter contract information (fileName, filePath, etc)
	public OpResult changeDirectory(String[] command) {
		
		if (command.length != 2) {
			return OpResult.BAD_COMMAND;
		}
		
		List<String> pathTokens = new ArrayList<>();
		
		// 
		if (command[1].equals("..")) {
			if (_currentLocation.getParent() == null) {
				System.out.println("Already at drive root!");
			}
			else {
				_currentLocation = _currentLocation.getParent();
			}
			
			return OpResult.SUCCESS;
		}
		else {
			String[] pathArray = command[1].split("/");
			pathTokens.addAll(Arrays.asList(pathArray));
			
			FileContainer targetContainer = traversePathToEnd(_currentLocation, new LinkedList<String>(pathTokens));
			
			if (targetContainer == null) {
				return OpResult.FAILURE_PATH_NOT_FOUND;
			}
			else {
				_currentLocation = targetContainer;
				return OpResult.SUCCESS;
			}
		}
		
	}
	
	// LOGIC
	/**
	 * Inserts a file at the target path, creating folders as needed.
	 * 
	 * @param fileToCreate File to insert in target location.
	 * @param node The starting folder to place the path and file in.
	 * @param path The path to put the target folder in, this shouldn't contain the node's path token.
	 * @return Operation result status.
	 */
	private OpResult createFileAtPath(SimpleFile fileToCreate, FileContainer node, Queue<String> path) {
		
		// Stop - We've reached our destination
		if (path.isEmpty()) {
			// Do we have something named this already?
			if (node.getContents().containsKey(fileToCreate.getName())) {
				return OpResult.FAILURE_PATH_ALREADY_EXISTS;
			}
			else {
				node.addFile(fileToCreate.getName(), fileToCreate);
				fileToCreate.setParent(node);
				fileToCreate.setPath(node.getPath() + "/" + fileToCreate.getName());
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
			String newFolderName = path.poll();
			FileContainer newFolder;
			newFolder = new FileContainer(FileType.Folder, newFolderName, node.getPath() + "/" + newFolderName);
			newFolder.setParent(node);
			node.getContents().put(newFolderName, newFolder);
			return createFileAtPath(fileToCreate, newFolder, path);
		}
	}
	
	/**
	 * Start at a given node and traverse the given path down.
	 * 
	 * @param node FileContainer to start at.
	 * @param path Path to traverse.
	 * @return FileContainer if found, null if couldn't parse.
	 */
	private FileContainer traversePathToEnd(FileContainer node, Queue<String> path) {
		
		// Stop - We've reached our destination
		if (path.isEmpty()) {
			return node;
		}
		
		// Does this node contain the next token of the path?
		if (node.getContents().containsKey(path.peek())) {
			// YES - We have another FileContainer to dive into
			return traversePathToEnd((FileContainer)node.getContents().get(path.poll()), path);
		}
		else {
			// NO - We've been given an erroneous path
			return null;
		}
	}
}
