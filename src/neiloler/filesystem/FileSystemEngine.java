package neiloler.filesystem;

import java.util.Scanner;

import neiloler.filesystem.FileSystemController.OpResult;

public class FileSystemEngine {

	public static void main(String[] args) {
		
		boolean runEngine = true;
		Scanner input = new Scanner(System.in);
		
		FileSystemController controller = new FileSystemController();
		
		// Simple intro
		System.out.println("Welcome to SimpleFileSystem!\nFor a list of commands, run '-h' or 'help'.");
		System.out.println("Default drive created: " + controller.getCurrentDirectory());
		
		while(runEngine) {
			System.out.print("{" + controller.getCurrentDirectory() + "}>> ");
			
			String rawInputString = input.nextLine();
			String[] command = rawInputString.split(" ");
						
			OpResult result = OpResult.UNKNOWN_COMMNAND;
			
			// Parse command
			if (command[0].equals("-h") || command[0].equals("help")) {
				result = controller.showHelp();
			}
			else if (command[0].equals("exit")) {
				runEngine = false;
			}
			else if (command[0].equals("create")) {
				result = controller.create(command);
			}
			else if ((command[0].equals("ls") || command[0].equals("dir")) && command.length == 1) {
				result = controller.showCurrentLocationContents();
			}
			else if (command[0].equals("pwd")) {
				result = controller.showCurrentLocationPath();
			}
			else if (command[0].equals("cd")) {
				result = controller.changeDirectory(command);
			}
			else if (command[0].equals("delete")) {
				result = controller.delete(command);
			}
			else if (command[0].equals("move")) {
				result = controller.move(command);
			}
			else if (command[0].equals("writeToFile") || command[0].equals("write")) {
				result = controller.writeToFile(command, rawInputString);
			}
			
			// Handle result
			switch (result) {
			
				// FAILURES
				case FAILURE_PATH_NOT_FOUND:
					System.out.println("Path not found!");
					break;
				case FAILURE_PATH_ALREADY_EXISTS:
					System.out.println("Path already exists!");
					break;
				case FAILURE_ILLEGAL_FILE_SYSTEM_OPERATION:
					System.out.println("Illegal operation! Please see help for desired method.");
					break;
				case FAILURE_NOT_A_TEXTFILE:
					System.out.println("Illegal operation!");
					break;
				
				// SUCCESS
				case SUCCESS:
					// TODO Do we need this here?
					break;
				
				// MISC
				case UNKNOWN_COMMNAND:
					System.out.println("Unknown command. Try using '-h' or 'help' to learn about commands.");
					break;
				case BAD_COMMAND:
					System.out.println("Incorrect use of command. Try using '-h' or 'help' to learn about commands.");
					break;
			}
		}
		
		input.close();
		System.out.println("EXITING");
	}
}
