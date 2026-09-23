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

	// 1〜max：正常入力 / 0：戻る / -1：不正入力
	public static int readNumber(Scanner scanner, String prompt, int max) {

		System.out.print(prompt);

		String input = scanner.nextLine().trim();

		if (input.equals("0")) {
			return 0;
		}

		try {

			int number = Integer.parseInt(input);

			if (number < 1 || number > max) {

				System.out.println();
				System.out.println(
						"表示されている番号を入力してください。");

				waitForEnter(scanner, "Enterで前の画面に戻る > ");
				return -1;
			}

			return number;

		} catch (NumberFormatException e) {

			System.out.println();
			System.out.println(
					"番号を入力してください。");

			waitForEnter(scanner, "Enterで前の画面に戻る > ");
			return -1;
		}
	}
}
