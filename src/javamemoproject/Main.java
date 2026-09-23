package javamemoproject;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		ProjectProcess projectProcess = new ProjectProcess(scanner);
		MemoProcess memoProcess = new MemoProcess(scanner, projectProcess);

		boolean running = true;
		while (running) {
			showHome(memoProcess);
			System.out.print("番号を入力 > ");
			String input = scanner.nextLine().trim();

			switch (input) {

			case "1":
				memoProcess.inputMemo();
				break;

			case "2":
				memoProcess.organizeUnorganizedMemos();
				break;

			case "3":
				memoProcess.openProjectMemos();
				break;

			case "4":
				projectProcess.manageProjects(memoProcess);
				break;

			case "5":
				projectProcess.showArchive();
				break;

			case "0":
				running = false;
				break;

			default:
				System.out.println();
				System.out.println("0〜5の番号を入力してください。");
				ConsoleUtil.waitForEnter(scanner, "EnterでHOMEに戻る > ");
				break;
			}
		}

		ConsoleUtil.showDivider();
		System.out.println("終了します。");
		scanner.close();
	}

	private static void showHome(MemoProcess memoProcess) {
		ConsoleUtil.showLocation("HOME");
		System.out.println("1. 白紙に書き出す");
		System.out.println("2. 「未整理」メモを整理する");
		System.out.println("3. プロジェクト別のメモを見る");
		System.out.println("4. プロジェクトを管理する");
		System.out.println("5. アーカイブを見る");
		System.out.println();
		System.out.println("0. 終了する");
		System.out.println();
		System.out.println("----------------");
		System.out.println();
		System.out.println("今やること：" + memoProcess.getCurrentTaskText());
		System.out.println();
	}
}
