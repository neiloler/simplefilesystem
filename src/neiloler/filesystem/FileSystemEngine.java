package neiloler.filesystem;

import java.util.Scanner;

public class FileSystemEngine {

	public static void main(String[] args) {
		
		boolean runEngine = true;
		Scanner input = new Scanner(System.in);
		
		while(runEngine) {
			System.out.print("Command: ");
			
			String command = input.nextLine();
			
			System.out.println("COMMAND: " + command);
			
			// Parse command
			if (command.equals("exit")) {
				runEngine = false;
			}
		}
		
		input.close();
		System.out.println("EXITING");
	}

}
