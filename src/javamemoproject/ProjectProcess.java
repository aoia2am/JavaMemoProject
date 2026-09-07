package javamemoproject;

import java.util.ArrayList;
import java.util.Scanner;

public class ProjectProcess {

	private ArrayList<Project> projects;
	private Scanner scanner;

	public ProjectProcess(Scanner scanner) {
		this.projects = FileManager.loadProjects();
		this.scanner = scanner;
	}

	// =========================
	// プロジェクト管理画面
	// =========================
	public void manageProjects(
			MemoProcess memoProcess) {

		boolean running = true;

		while (running) {

			ConsoleUtil.showLocation(
					"HOME > プロジェクト管理");

			System.out.println("1. ＋新しいプロジェクトを作る");
			System.out.println("2. プロジェクト名を変更する");
			System.out.println("3. プロジェクトを削除する");
			System.out.println(
					"4. プロジェクトの順番を並び替える");
			System.out.println();

			System.out.println("0. HOMEへ戻る");
			System.out.println();

			System.out.print("番号を入力 > ");
			String input = scanner.nextLine().trim();

			switch (input) {

			case "1":
				createProject();
				waitForEnter();
				break;

			case "2":
				updateProject();
				break;

			case "3":
				deleteProject(memoProcess);
				break;

			case "4":
				reorderProjects();
				break;

			case "0":
				running = false;
				break;

			default:
				System.out.println();
				System.out.println("0〜4の番号を入力してください。");
				waitForEnter();
				break;
			}
		}
	}

	// =========================
	// プロジェクト作成
	// =========================
	public Project createProject() {

		ConsoleUtil.showDivider();

		System.out.println("新しいプロジェクトを作ります。");
		System.out.print("プロジェクト名 > ");

		String name = scanner.nextLine().trim();

		if (name.isEmpty()) {
			System.out.println();
			System.out.println("プロジェクト名が入力されていません。");
			return null;
		}

		int projectId = getNextProjectId();
		int order = getNextOrder();

		Project project = new Project(
				projectId,
				name,
				order);

		projects.add(project);
		FileManager.saveProjects(projects);

		System.out.println();
		System.out.println("「" + name + "」を作成しました。");

		return project;
	}

	// プロジェクトを選択する
	public Project selectProject() {

		ConsoleUtil.showLocation(
				"HOME > プロジェクト");

		ArrayList<Project> activeProjects = getActiveProjects();

		if (activeProjects.isEmpty()) {
			System.out.println("プロジェクトはありません。");
			waitForEnter();
			return null;
		}

		for (int i = 0; i < activeProjects.size(); i++) {
			System.out.println(
					(i + 1) + ". " + activeProjects.get(i).getName());
		}

		System.out.println();
		System.out.println("0. HOMEへ戻る");
		System.out.println();

		System.out.print("番号を入力 > ");
		String input = scanner.nextLine().trim();

		if (input.equals("0")) {
			return null;
		}

		try {

			int number = Integer.parseInt(input);

			if (number < 1 || number > activeProjects.size()) {
				System.out.println();
				System.out.println("正しい番号を入力してください。");
				waitForEnter();
				return null;
			}

			return activeProjects.get(number - 1);

		} catch (NumberFormatException e) {

			System.out.println();
			System.out.println("番号を入力してください。");
			waitForEnter();
			return null;
		}
	}

	// =========================
	// プロジェクト名変更
	// =========================
	public void updateProject() {

		ConsoleUtil.showDivider();

		ArrayList<Project> activeProjects = getActiveProjects();

		if (activeProjects.isEmpty()) {

			System.out.println("変更できるプロジェクトはありません。");
			waitForEnter();
			return;
		}

		System.out.println("変更するプロジェクトを選んでください。");
		System.out.println();

		for (int i = 0; i < activeProjects.size(); i++) {

			System.out.println(
					(i + 1) + ". " + activeProjects.get(i).getName());
		}

		System.out.println();
		System.out.println("0. 戻る");
		System.out.println();

		System.out.print("番号を入力 > ");
		String input = scanner.nextLine().trim();

		if (input.equals("0")) {
			return;
		}

		try {

			int number = Integer.parseInt(input);

			if (number < 1 || number > activeProjects.size()) {

				System.out.println();
				System.out.println("正しい番号を入力してください。");
				waitForEnter();
				return;
			}

			Project project = activeProjects.get(number - 1);

			String oldName = project.getName();

			ConsoleUtil.showDivider();

			System.out.println("現在のプロジェクト名：");
			System.out.println(oldName);
			System.out.println();

			System.out.print("新しい名前 > ");
			String newName = scanner.nextLine().trim();

			if (newName.isEmpty()) {

				System.out.println();
				System.out.println("名前が入力されていません。");
				waitForEnter();
				return;
			}

			project.setName(newName);
			FileManager.saveProjects(projects);

			System.out.println();
			System.out.println("プロジェクト名を変更しました。");
			System.out.println();

			System.out.println("Before：" + oldName);
			System.out.println("After ：" + newName);

			waitForEnter();

		} catch (NumberFormatException e) {

			System.out.println();
			System.out.println("番号を入力してください。");
			waitForEnter();
		}
	}

	// =========================
	// プロジェクト削除
	// =========================
	public void deleteProject(
			MemoProcess memoProcess) {
		ConsoleUtil.showDivider();

		ArrayList<Project> activeProjects = getActiveProjects();

		if (activeProjects.isEmpty()) {

			System.out.println("削除できるプロジェクトはありません。");
			waitForEnter();
			return;
		}

		System.out.println("削除するプロジェクトを選んでください。");
		System.out.println();

		for (int i = 0; i < activeProjects.size(); i++) {

			System.out.println(
					(i + 1) + ". " + activeProjects.get(i).getName());
		}

		System.out.println();
		System.out.println("0. 戻る");
		System.out.println();

		System.out.print("番号を入力 > ");
		String input = scanner.nextLine().trim();

		if (input.equals("0")) {
			return;
		}

		try {

			int number = Integer.parseInt(input);

			if (number < 1 || number > activeProjects.size()) {

				System.out.println();
				System.out.println("正しい番号を入力してください。");
				waitForEnter();
				return;
			}

			Project project = activeProjects.get(number - 1);

			ConsoleUtil.showDivider();

			System.out.println(
					number + ".「" + project.getName() + "」を削除します。");

			System.out.println();
			System.out.println("このプロジェクト内のメモも");
			System.out.println("すべて削除されます。");

			System.out.println();
			System.out.print("本当に削除しますか？ (y/n) > ");

			String confirm = scanner.nextLine().trim().toLowerCase();

			if (confirm.equals("y")) {

				memoProcess.deleteMemosByProjectId(
						project.getProjectId());

				projects.remove(project);
				FileManager.saveProjects(projects);

				System.out.println();
				System.out.println(
						"プロジェクト「"
								+ project.getName()
								+ "」を削除しました。");

				waitForEnter();

			} else if (confirm.equals("n")) {

				return;

			} else {

				System.out.println();
				System.out.println(
						"y または n を入力してください。");

				waitForEnter();
			}

		} catch (NumberFormatException e) {

			System.out.println();
			System.out.println("番号を入力してください。");
			waitForEnter();
		}
	}

	public ArrayList<Project> getActiveProjects() {

		ArrayList<Project> activeProjects = new ArrayList<>();

		for (Project project : projects) {

			if (!project.isCompleted()) {
				activeProjects.add(project);
			}
		}

		activeProjects.sort(
				(a, b) -> Integer.compare(
						a.getOrder(),
						b.getOrder()));

		return activeProjects;
	}

	// =========================
	// 次のprojectId
	// =========================
	private int getNextProjectId() {

		int maxId = 0;

		for (Project project : projects) {

			if (project.getProjectId() > maxId) {
				maxId = project.getProjectId();
			}
		}

		return maxId + 1;
	}

	// =========================
	// プロジェクトを完了する
	// =========================
	public void completeProject(int projectId) {

		for (Project project : projects) {

			if (project.getProjectId() == projectId) {

				project.setCompleted(true);

				FileManager.saveProjects(projects);

				return;
			}
		}
	}

	// =========================
	// 次の実行順
	// =========================
	private int getNextOrder() {

		int maxOrder = 0;

		for (Project project : projects) {

			if (project.getOrder() > maxOrder) {
				maxOrder = project.getOrder();
			}
		}

		return maxOrder + 1;
	}

	// =========================
	// Enter待ち
	// =========================
	private void waitForEnter() {

		System.out.println();
		System.out.print("Enterで前の画面に戻る > ");
		scanner.nextLine();
	}

	// =========================
	// アーカイブを見る
	// =========================
	public void showArchive() {

		ConsoleUtil.showLocation(
				"HOME > アーカイブ");

		int number = 1;

		for (Project project : projects) {

			if (project.isCompleted()) {

				System.out.println(
						project.getName() + " [完了]");

			}
		}

		if (number == 1) {
			System.out.println(
					"完了したプロジェクトはありません。");
		}

		System.out.println();
		System.out.print("EnterでHOMEに戻る > ");
		scanner.nextLine();
	}

	// =========================
	// プロジェクトを並び替える
	// =========================
	public void reorderProjects() {

		ArrayList<Project> activeProjects = getActiveProjects();

		ConsoleUtil.showDivider();

		if (activeProjects.size() < 2) {

			System.out.println(
					"並び替えできるプロジェクトがありません。");

			waitForEnter();
			return;
		}

		System.out.println(
				"並び替えるプロジェクトを選んでください。");

		System.out.println();

		for (int i = 0; i < activeProjects.size(); i++) {

			System.out.println(
					(i + 1)
							+ ". "
							+ activeProjects.get(i).getName());
		}

		System.out.println();
		System.out.println("0. 戻る");
		System.out.println();

		System.out.print("番号を入力 > ");

		String input = scanner.nextLine().trim();

		if (input.equals("0")) {
			return;
		}

		try {

			int number = Integer.parseInt(input);

			if (number < 1
					|| number > activeProjects.size()) {

				System.out.println();
				System.out.println(
						"表示されている番号を入力してください。");

				waitForEnter();
				return;
			}

			Project targetProject = activeProjects.get(number - 1);

			ConsoleUtil.showDivider();

			System.out.println(
					"「"
							+ targetProject.getName()
							+ "」を何番目にしますか？");

			System.out.println();

			for (int i = 0; i < activeProjects.size(); i++) {

				System.out.println(
						(i + 1)
								+ ". "
								+ activeProjects.get(i).getName());
			}

			System.out.println();
			System.out.println("0. 戻る");
			System.out.println();

			System.out.print(
					"移動先の番号を入力 > ");

			String newInput = scanner.nextLine().trim();

			if (newInput.equals("0")) {
				return;
			}

			int newPosition = Integer.parseInt(newInput);

			if (newPosition < 1
					|| newPosition > activeProjects.size()) {

				System.out.println();
				System.out.println(
						"表示されている番号を入力してください。");

				waitForEnter();
				return;
			}

			activeProjects.remove(targetProject);

			activeProjects.add(
					newPosition - 1,
					targetProject);

			for (int i = 0; i < activeProjects.size(); i++) {

				activeProjects
						.get(i)
						.setOrder(i + 1);
			}

			FileManager.saveProjects(projects);

			System.out.println();
			System.out.println(
					"プロジェクトの順番を変更しました。");

			waitForEnter();

		} catch (NumberFormatException e) {

			System.out.println();
			System.out.println(
					"番号を入力してください。");

			waitForEnter();
		}
	}
}