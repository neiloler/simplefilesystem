package neiloler.filesystem;

import java.util.Scanner;

import neiloler.filesystem.FileSystemController.OpResult;

public class FileSystemEngine {

	public static void main(String[] args) {
		
		boolean runEngine = true;
		Scanner input = new Scanner(System.in);
		
		FileSystemController controller = new FileSystemController();
		
		while(runEngine) {
			System.out.print("Command: ");
			
			String[] command = input.nextLine().split(" ");
						
			OpResult result = OpResult.UNKNOWN_COMMNAND;
			
			// Parse command
			if (command[0].equals("-h") || command[0].equals("help")) {
				controller.showHelp();
			}
			if (command[0].equals("exit")) {
				runEngine = false;
			}
			else if (command[0].equals("create")) {
				result = controller.create(command);
			}
			else if (command[0].equals("ls") || command[0].equals("dir")) {
				result = controller.showCurrentLocationContents();
			}
			else if (command[0].equals("pwd")) {
				result = controller.showCurrentLocationPath();
			}
			else if (command[0].equals("cd")) {
				result = controller.changeDirectory(command);
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
