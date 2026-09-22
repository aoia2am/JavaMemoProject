package javamemoproject;

import java.util.Scanner;

public class ConsoleUtil {

	public static void showDivider() {
		System.out.println();
		System.out.println("----------------");
		System.out.println();
	}

	public static void showLocation(String location) {
		showDivider();
		System.out.println("現在地：" + location);
		System.out.println();
	}

	public static void waitForEnter(Scanner scanner, String message) {
		System.out.println();
		System.out.print(message);
		scanner.nextLine();
	}
}
