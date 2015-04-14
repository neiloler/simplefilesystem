package neiloler.filesystem;

import java.util.Scanner;

public class FileSystemEngine {

	public static void main(String[] args) {
		
		boolean runEngine = true;
		Scanner input = new Scanner(System.in);
		
		FileSystemController controller = new FileSystemController();
		
		while(runEngine) {
			System.out.print("Command: ");
			
			String[] command = input.nextLine().split(" ");
			
			for (String piece : command) {
				System.out.println(piece);
			}
			
			
			// TODO Parse out commands into string[], like args[]
			// look at the first command of each arg
				// if unknown, respond with error
			
			OpResult result;
			
			// Parse command
			if (command[0].equals("exit")) {
				runEngine = false;
			}
			else if (command[0].equals("create")) {
				System.out.println("RESULT: " + controller.create(command));
			}
			
			// Handle result
			if
		}
		
		input.close();
		System.out.println("EXITING");
	}
}
