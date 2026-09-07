package javamemoproject;

import java.util.ArrayList;
import java.util.Scanner;

public class MemoProcess {

	private ArrayList<Memo> memos;
	private Scanner scanner;
	private ProjectProcess projectProcess;

	public MemoProcess(
			Scanner scanner,
			ProjectProcess projectProcess) {
		this.memos = FileManager.loadMemos();
		this.scanner = scanner;
		this.projectProcess = projectProcess;
	}

	// プロジェクトを選んでメモを見る
	public void openProjectMemos() {

		Project project = projectProcess.selectProject();

		if (project == null) {
			return;
		}

		openProjectMemos(project);
	}

	// =========================
	// メモを編集する
	// =========================
	public void updateMemo(Project project) {

		ArrayList<Memo> projectMemos = getProjectMemosInTreeOrder(
				project.getProjectId());
		ConsoleUtil.showDivider();

		if (projectMemos.isEmpty()) {
			System.out.println("編集できるメモはありません。");
			waitForEnter();
			return;
		}

		System.out.println("編集するメモを選んでください。");
		System.out.println();

		for (int i = 0; i < projectMemos.size(); i++) {

			Memo memo = projectMemos.get(i);

			String displayNumber = getMemoDisplayNumber(memo);

			System.out.println(
					(i + 1)
							+ ". "
							+ displayNumber
							+ " "
							+ memo.getText());
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

			if (number < 1 || number > projectMemos.size()) {

				System.out.println();
				System.out.println(
						"表示されている番号を入力してください。");

				waitForEnter();
				return;
			}

			Memo memo = projectMemos.get(number - 1);

			String oldText = memo.getText();

			ConsoleUtil.showDivider();

			System.out.println("現在のメモ：");
			System.out.println(oldText);

			System.out.println();
			System.out.println(
					"新しい内容を1文で入力してください。");

			System.out.println();
			System.out.print("> ");

			String newText = scanner.nextLine().trim();

			if (newText.isEmpty()) {

				System.out.println();
				System.out.println(
						"メモが入力されていません。");

				waitForEnter();
				return;
			}

			memo.setText(newText);
			FileManager.saveMemos(memos);

			System.out.println();
			System.out.println("メモを変更しました。");

			System.out.println();
			System.out.println("Before：" + oldText);
			System.out.println("After ：" + newText);

			waitForEnter();

		} catch (NumberFormatException e) {

			System.out.println();
			System.out.println(
					"番号を入力してください。");

			waitForEnter();
		}
	}

	// =========================
	// メモを削除する
	// =========================
	public void deleteMemo(Project project) {

		ArrayList<Memo> projectMemos = getProjectMemosInTreeOrder(
				project.getProjectId());

		ConsoleUtil.showDivider();

		if (projectMemos.isEmpty()) {

			System.out.println(
					"削除できるメモはありません。");

			waitForEnter();
			return;
		}

		System.out.println(
				"削除するメモを選んでください。");

		System.out.println();

		for (int i = 0; i < projectMemos.size(); i++) {

			Memo memo = projectMemos.get(i);

			String displayNumber = getMemoDisplayNumber(memo);

			System.out.println(
					(i + 1)
							+ ". "
							+ displayNumber
							+ " "
							+ memo.getText());
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
					|| number > projectMemos.size()) {

				System.out.println();
				System.out.println(
						"表示されている番号を入力してください。");

				waitForEnter();
				return;
			}

			Memo targetMemo = projectMemos.get(number - 1);

			int projectId = targetMemo.getProjectId();

			int parentMemoId = targetMemo.getParentMemoId();

			ArrayList<Memo> descendants = getDescendants(targetMemo);

			System.out.println(
					number + ".「"
							+ targetMemo.getText()
							+ "」を削除します。");

			// 子メモがある場合
			if (!descendants.isEmpty()) {

				System.out.println();
				System.out.println(
						"このメモの下にあるメモも");
				System.out.println(
						"すべて削除されます。");

				System.out.println();

				for (Memo memo : descendants) {

					System.out.println(
							"・" + memo.getText());
				}
			}

			System.out.println();
			System.out.print("本当に削除しますか？ (y/n) > ");

			String confirm = scanner.nextLine().trim().toLowerCase();

			if (confirm.equals("n")) {
				return;
			}

			if (!confirm.equals("y")) {

				System.out.println();
				System.out.println(
						"y または n を入力してください。");

				waitForEnter();
				return;
			}

			memos.removeAll(descendants);
			memos.remove(targetMemo);

			// 残った兄弟の番号を詰める
			normalizeSiblingOrders(
					projectId,
					parentMemoId);

			System.out.println();
			System.out.println(
					"「" + targetMemo.getText()
							+ "」を削除しました。");

			waitForEnter();

		} catch (NumberFormatException e) {

			System.out.println();
			System.out.println(
					"番号を入力してください。");

			waitForEnter();
		}
	}

	public void openProjectMemos(Project project) {

		boolean running = true;

		while (running) {

			ConsoleUtil.showLocation(
					"HOME > プロジェクト > "
							+ project.getName());

			showMemoList(project);

			System.out.println();

			Memo currentTask = findCurrentTask(
					project.getProjectId());

			if (currentTask == null) {
				System.out.println("今やること：なし");
			} else {
				System.out.println(
						"今やること："
								+ currentTask.getText());
			}

			System.out.println();
			System.out.println("----------------");
			System.out.println();

			System.out.println("1. 「今やること」を完了する");
			System.out.println("2. メモを細かくする");

			System.out.println();

			System.out.println("3. 新しいメモを追加する");
			System.out.println("4. メモを編集する");
			System.out.println("5. メモを削除する");

			System.out.println();
			System.out.println("0. プロジェクト一覧へ戻る");
			System.out.println();

			System.out.print("番号を入力 > ");
			String input = scanner.nextLine().trim();

			switch (input) {

			case "1":
				boolean projectCompleted = completeCurrentTask(project);

				if (projectCompleted) {
					running = false;
				}

				break;

			case "2":
				createChildMemo(project);
				break;

			case "3":
				createMemo(project);
				break;

			case "4":
				manageMemoEdit(project);
				break;

			case "5":
				deleteMemo(project);
				break;

			case "0":
				running = false;
				break;

			default:
				System.out.println();
				System.out.println(
						"0〜5の番号を入力してください。");
				waitForEnter();
				break;
			}
		}
	}

	// =========================
	// プロジェクト内のメモ一覧
	// =========================
	public void showMemoList(Project project) {

		ArrayList<Memo> topMemos = getChildMemos(
				project.getProjectId(),
				0);

		if (topMemos.isEmpty()) {
			System.out.println("メモはまだありません。");
			return;
		}

		// 今やることを取得
		Memo currentTask = findCurrentTask(
				project.getProjectId());

		int currentTaskId = 0;

		if (currentTask != null) {
			currentTaskId = currentTask.getMemoId();
		}

		for (Memo memo : topMemos) {

			showMemoTree(
					memo,
					0,
					currentTaskId);
		}
	}

	// 新しいメモを追加する
	public void createMemo(Project project) {

		ConsoleUtil.showDivider();

		System.out.println(
				project.getName()
						+ "に新しいメモを追加します。");

		System.out.println();
		System.out.println(
				"追加したいことを、思いつくまま書いてください。");

		System.out.println();
		System.out.println(
				"「。」「！」「？」または改行で");
		System.out.println(
				"1件ずつのメモに分かれます。");

		System.out.println();
		System.out.println(
				"入力を終えるときは、Enterを2回押してください。");

		System.out.println();

		StringBuilder inputText = new StringBuilder();

		while (true) {

			System.out.print("> ");
			String line = scanner.nextLine();

			// 空行が来たら入力終了
			if (line.isBlank()) {
				break;
			}

			inputText.append(line);
			inputText.append("\n");
		}

		ArrayList<String> texts = splitMemo(inputText.toString());

		if (texts.isEmpty()) {

			System.out.println();
			System.out.println("メモは追加されませんでした。");
			waitForEnter();
			return;
		}

		ArrayList<Memo> addedMemos = new ArrayList<>();

		for (String text : texts) {

			int memoId = getNextMemoId();
			int order = getNextOrder(
					project.getProjectId(),
					0);

			Memo memo = new Memo(
					memoId,
					text,
					project.getProjectId(),
					0,
					order);

			memos.add(memo);
			addedMemos.add(memo);
		}

		FileManager.saveMemos(memos);

		System.out.println();
		System.out.println(
				addedMemos.size()
						+ "件のメモを追加しました。");

		System.out.println();

		for (Memo memo : addedMemos) {
			System.out.println("・" + memo.getText());
		}

		System.out.println();
		System.out.println("1. 続けて書き込む");
		System.out.println("0. プロジェクトへ戻る");
		System.out.println();

		System.out.print("番号を入力 > ");
		String input = scanner.nextLine().trim();

		if (input.equals("1")) {
			createMemo(project);
		}
	}

	// =========================
	// メモを細かくする
	// =========================
	public void createChildMemo(Project project) {

		ArrayList<Memo> projectMemos = getProjectMemosInTreeOrder(
				project.getProjectId());

		ConsoleUtil.showDivider();

		if (projectMemos.isEmpty()) {

			System.out.println(
					"細かくできるメモはありません。");

			waitForEnter();
			return;
		}

		System.out.println(
				"細かくするメモを選んでください。");

		System.out.println();

		for (int i = 0; i < projectMemos.size(); i++) {

			Memo memo = projectMemos.get(i);

			String displayNumber = getMemoDisplayNumber(memo);

			System.out.println(
					(i + 1)
							+ ". "
							+ displayNumber
							+ " "
							+ memo.getText());
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
					|| number > projectMemos.size()) {

				System.out.println();
				System.out.println(
						"表示されている番号を入力してください。");

				waitForEnter();
				return;
			}

			Memo parentMemo = projectMemos.get(number - 1);

			ConsoleUtil.showDivider();

			System.out.println(
					"「" + parentMemo.getText()
							+ "」を細かくします。");

			System.out.println();

			System.out.println(
					"必要なことを、思いつくまま書いてください。");

			System.out.println();
			System.out.println(
					"「。」「！」「？」または改行で");
			System.out.println(
					"1件ずつのメモに分かれます。");

			System.out.println();
			System.out.println(
					"入力を終えるときは、Enterを2回押してください。");

			System.out.println();

			StringBuilder inputText = new StringBuilder();

			while (true) {

				System.out.print("> ");

				String line = scanner.nextLine();

				if (line.isBlank()) {
					break;
				}

				inputText.append(line);
				inputText.append("\n");
			}

			ArrayList<String> texts = splitMemo(
					inputText.toString());

			if (texts.isEmpty()) {

				System.out.println();
				System.out.println(
						"メモは追加されませんでした。");

				waitForEnter();
				return;
			}

			ArrayList<Memo> addedMemos = new ArrayList<>();

			for (String text : texts) {

				int memoId = getNextMemoId();

				int order = getNextOrder(
						project.getProjectId(),
						parentMemo.getMemoId());

				Memo childMemo = new Memo(
						memoId,
						text,
						project.getProjectId(),
						parentMemo.getMemoId(),
						order);

				memos.add(childMemo);
				addedMemos.add(childMemo);
			}

			FileManager.saveMemos(memos);

			System.out.println();
			System.out.println(
					addedMemos.size()
							+ "件のメモを追加しました。");

			System.out.println();

			for (Memo memo : addedMemos) {

				System.out.println(
						"・" + memo.getText());
			}

			waitForEnter();

		} catch (NumberFormatException e) {

			System.out.println();
			System.out.println(
					"番号を入力してください。");

			waitForEnter();
		}
	}

	// =========================
	// 子メモを取得
	// =========================
	private ArrayList<Memo> getChildMemos(
			int projectId,
			int parentMemoId) {

		ArrayList<Memo> children = new ArrayList<>();

		for (Memo memo : memos) {

			if (memo.getProjectId() == projectId
					&& memo.getParentMemoId() == parentMemoId) {

				children.add(memo);
			}
		}

		// order順に並べる
		children.sort(
				(a, b) -> Integer.compare(
						a.getOrder(),
						b.getOrder()));

		return children;
	}

	// =========================
	// ツリー状に表示
	// =========================
	private void showMemoTree(
			Memo memo,
			int depth,
			int currentTaskId) {

		ArrayList<Memo> children = getChildMemos(
				memo.getProjectId(),
				memo.getMemoId());

		String indent = "    ".repeat(depth);

		String completedText = "";

		if (memo.isCompleted()) {
			completedText = " [完了]";
		}

		// 今やることなら矢印
		String currentMark = "  ";

		if (memo.getMemoId() == currentTaskId) {

			currentMark = "→ ";
		}

		// 一番上のメモ
		if (depth == 0) {

			if (memo.getMemoId() == currentTaskId) {

				System.out.println(
						"▼ → "
								+ memo.getText()
								+ completedText);

			} else {

				System.out.println(
						"▼ "
								+ memo.getText()
								+ completedText);
			}

		} else {

			System.out.println(
					indent
							+ currentMark
							+ memo.getOrder()
							+ ". "
							+ memo.getText()
							+ completedText);
		}

		// さらに下の子メモも表示
		for (Memo child : children) {

			showMemoTree(
					child,
					depth + 1,
					currentTaskId);
		}
	}

	// =========================
	// プロジェクト内のメモを
	// ツリー順に取得
	// =========================
	private ArrayList<Memo> getProjectMemosInTreeOrder(
			int projectId) {

		ArrayList<Memo> result = new ArrayList<>();

		ArrayList<Memo> topMemos = getChildMemos(
				projectId,
				0);

		for (Memo memo : topMemos) {

			addMemoToTreeList(
					memo,
					result);
		}

		return result;
	}

	// =========================
	// メモとその子を順番に追加
	// =========================
	private void addMemoToTreeList(
			Memo memo,
			ArrayList<Memo> result) {

		result.add(memo);

		ArrayList<Memo> children = getChildMemos(
				memo.getProjectId(),
				memo.getMemoId());

		for (Memo child : children) {

			addMemoToTreeList(
					child,
					result);
		}
	}

	// 「。！？改行」で分割
	private ArrayList<String> splitMemo(String text) {

		ArrayList<String> result = new ArrayList<>();

		String[] parts = text.split("[。！？\\n]+");

		for (String part : parts) {

			String cleaned = part.trim();

			if (!cleaned.isEmpty()) {
				result.add(cleaned);
			}
		}

		return result;
	}

	// 次のmemoId
	private int getNextMemoId() {

		int maxId = 0;

		for (Memo memo : memos) {

			if (memo.getMemoId() > maxId) {
				maxId = memo.getMemoId();
			}
		}

		return maxId + 1;
	}

	// 同じ階層の最後のorderを取得
	private int getNextOrder(
			int projectId,
			int parentMemoId) {

		int maxOrder = 0;

		for (Memo memo : memos) {

			if (memo.getProjectId() == projectId
					&& memo.getParentMemoId() == parentMemoId
					&& memo.getOrder() > maxOrder) {

				maxOrder = memo.getOrder();
			}
		}

		return maxOrder + 1;
	}

	private void waitForEnter() {

		System.out.println();
		System.out.print("Enterで前の画面に戻る > ");
		scanner.nextLine();
	}

	// =========================
	// 白紙に書き出す
	// =========================
	public void inputMemo() {

		boolean running = true;

		while (running) {

			ConsoleUtil.showDivider();

			System.out.println("[白紙に書き出す]");
			System.out.println();
			System.out.println();
			System.out.println();

			System.out.println(
					"考えていることを、いったんここに置いていきましょう。");
			System.out.println(
					"まとまっていなくても大丈夫です。");

			System.out.println();
			System.out.println();
			System.out.println(
					"「。」「！」「？」または改行で");
			System.out.println(
					"1件ずつのメモに分かれます。");

			System.out.println();
			System.out.println();
			System.out.println(
					"入力を終えるときは、Enterを2回押してください。");

			System.out.println();
			System.out.println();

			StringBuilder inputText = new StringBuilder();

			while (true) {

				System.out.print("> ");
				String line = scanner.nextLine();

				if (line.isBlank()) {
					break;
				}

				inputText.append(line);
				inputText.append("\n");
			}

			ArrayList<String> texts = splitMemo(inputText.toString());

			// 何も入力されなかった場合
			if (texts.isEmpty()) {

				System.out.println();
				System.out.println("メモは保存されませんでした。");

				System.out.println();
				System.out.println("1. もう一度書く");
				System.out.println("0. HOMEへ戻る");
				System.out.println();

				System.out.print("番号を入力 > ");
				String input = scanner.nextLine().trim();

				if (input.equals("0")) {
					running = false;
				}

				continue;
			}

			// 未整理メモとして保存
			for (String text : texts) {

				int memoId = getNextMemoId();

				int order = getNextOrder(
						0,
						0);

				Memo memo = new Memo(
						memoId,
						text,
						0, // projectId = 0 → 未整理
						0, // parentMemoId = 0
						order);

				memos.add(memo);
			}

			FileManager.saveMemos(memos);

			System.out.println();
			System.out.println(
					texts.size()
							+ "件のメモを「未整理」に保存しました。");

			System.out.println();
			System.out.println("1. さらに入力する");

			System.out.println();
			System.out.println(
					"2. 「未整理」メモを整理する");
			System.out.println(
					"0. HOMEへ戻る");

			System.out.println();

			System.out.print("番号を入力 > ");
			String input = scanner.nextLine().trim();

			switch (input) {

			case "1":
				// whileの先頭へ戻る
				break;

			case "2":
				ConsoleUtil.showDivider();
				organizeUnorganizedMemos();
				running = false;
				break;

			case "0":
				running = false;
				break;

			default:
				System.out.println();
				System.out.println(
						"0〜2の番号を入力してください。");
				waitForEnter();
				running = false;
				break;
			}
		}

	}

	// =========================
	// 未整理メモを整理する
	// =========================
	public void organizeUnorganizedMemos() {

		ArrayList<Memo> unorganizedMemos = getUnorganizedMemos();

		if (unorganizedMemos.isEmpty()) {

			ConsoleUtil.showDivider();

			System.out.println("「未整理」のメモはありません。");

			waitForEnter();
			return;
		}

		int index = 0;

		while (index < unorganizedMemos.size()) {

			Memo memo = unorganizedMemos.get(index);

			ConsoleUtil.showDivider();

			System.out.println(
					"「" + memo.getText() + "」 "
							+ "(" + (index + 1)
							+ "/" + unorganizedMemos.size() + ")");

			System.out.println();
			System.out.println("保存先を選んでください。");
			System.out.println();

			ArrayList<Project> projects = projectProcess.getActiveProjects();

			for (int i = 0; i < projects.size(); i++) {

				System.out.println(
						(i + 1) + ". "
								+ projects.get(i).getName());
			}

			int createNumber = projects.size() + 1;
			int skipNumber = projects.size() + 2;

			System.out.println();

			System.out.println(
					createNumber + ". ＋新しいプロジェクト");

			System.out.println(
					skipNumber + ". スキップ");

			System.out.println(
					"0. HOMEへ戻る");

			System.out.println();

			System.out.print("番号を入力 > ");
			String input = scanner.nextLine().trim();

			// HOME
			if (input.equals("0")) {
				return;
			}

			try {

				int number = Integer.parseInt(input);

				// =========================
				// 既存プロジェクトへ保存
				// =========================
				if (number >= 1
						&& number <= projects.size()) {

					Project project = projects.get(number - 1);

					memo.setProjectId(
							project.getProjectId());

					FileManager.saveMemos(memos);

					boolean continueSorting = showAfterAssignMenu(
							memo,
							project);

					if (!continueSorting) {
						return;
					}

					index++;
				}

				// =========================
				// 新しいプロジェクト
				// =========================
				else if (number == createNumber) {

					ConsoleUtil.showDivider();

					Project newProject = projectProcess.createProject();

					if (newProject != null) {

						memo.setProjectId(
								newProject.getProjectId());

						FileManager.saveMemos(memos);

						boolean continueSorting = showAfterAssignMenu(
								memo,
								newProject);

						if (!continueSorting) {
							return;
						}

						index++;
					}
				}

				// =========================
				// スキップ
				// =========================
				else if (number == skipNumber) {

					index++;
				}

				// =========================
				// 間違った番号
				// =========================
				else {

					System.out.println();
					System.out.println(
							"表示されている番号を入力してください。");

					waitForEnter();
				}

			} catch (NumberFormatException e) {

				System.out.println();
				System.out.println(
						"番号を入力してください。");

				waitForEnter();
			}
		}

		ConsoleUtil.showDivider();

		System.out.println(
				"未整理メモの確認が終わりました。");

		waitForEnter();
	}

	// =========================
	// 未整理メモを取得
	// =========================
	private ArrayList<Memo> getUnorganizedMemos() {

		ArrayList<Memo> result = new ArrayList<>();

		for (Memo memo : memos) {

			if (memo.getProjectId() == 0) {
				result.add(memo);
			}
		}

		return result;
	}

	// =========================
	// 指定メモの下にある
	// 子・孫メモをすべて取得
	// =========================
	private ArrayList<Memo> getDescendants(
			Memo parentMemo) {

		ArrayList<Memo> result = new ArrayList<>();

		ArrayList<Memo> children = getChildMemos(
				parentMemo.getProjectId(),
				parentMemo.getMemoId());

		for (Memo child : children) {

			result.add(child);

			result.addAll(
					getDescendants(child));
		}

		return result;
	}

	// =========================
	// 「今やること」を完了する
	// =========================
	public boolean completeCurrentTask(Project project) {

		Memo currentTask = findCurrentTask(project.getProjectId());

		ConsoleUtil.showDivider();

		if (currentTask == null) {

			System.out.println(
					"完了できるメモはありません。");

			waitForEnter();
			return false;
		}

		System.out.println(
				"「" + currentTask.getText()
						+ "」を完了します。");

		System.out.println();

		System.out.println("1. 完了する");
		System.out.println("0. 戻る");

		System.out.println();

		System.out.print("番号を入力 > ");
		String input = scanner.nextLine().trim();

		if (input.equals("0")) {
			return false;
		}

		if (!input.equals("1")) {

			System.out.println();
			System.out.println(
					"0 または 1 を入力してください。");

			waitForEnter();
			return false;
		}

		// 今やることを完了
		currentTask.setCompleted(true);

		System.out.println();
		System.out.println(
				"●「"
						+ currentTask.getText()
						+ "」を完了しました。");

		// 親メモを確認
		int parentMemoId = currentTask.getParentMemoId();

		while (parentMemoId != 0) {

			Memo parentMemo = findMemoById(parentMemoId);

			if (parentMemo == null) {
				break;
			}

			if (areAllChildrenCompleted(
					parentMemo.getMemoId())) {

				parentMemo.setCompleted(true);

				System.out.println();
				System.out.println(
						"●「" + parentMemo.getText()
								+ "」が完了しました。");

				parentMemoId = parentMemo.getParentMemoId();

			} else {

				break;
			}
		}

		// メモの状態を保存
		FileManager.saveMemos(memos);

		// プロジェクト全体が終わったか確認
		if (isProjectCompleted(
				project.getProjectId())) {

			projectProcess.completeProject(
					project.getProjectId());

			System.out.println();
			System.out.println(
					"●プロジェクト「"
							+ project.getName()
							+ "」が完了しました。");

			System.out.println(
					"●アーカイブに移動しました。");

			System.out.println();

			System.out.println(
					"次にやること："
							+ getCurrentTaskText());

			System.out.println();

			System.out.print(
					"EnterでHOMEに戻る > ");

			scanner.nextLine();

			return true;
		}

		// 次のタスクを取得
		Memo nextTask = findCurrentTask(
				project.getProjectId());

		System.out.println();

		if (nextTask == null) {

			System.out.println(
					"次にやること：なし");

		} else {

			System.out.println(
					"次にやること："
							+ nextTask.getText());
		}

		System.out.println();

		System.out.print(
				"Enterでプロジェクト画面に戻る > ");

		scanner.nextLine();

		return false;
	}

	// =========================
	// memoIdからメモを探す
	// =========================
	private Memo findMemoById(int memoId) {

		for (Memo memo : memos) {

			if (memo.getMemoId() == memoId) {
				return memo;
			}
		}

		return null;
	}

	// =========================
	// 子メモが全部完了しているか
	// =========================
	private boolean areAllChildrenCompleted(
			int parentMemoId) {

		boolean hasChild = false;

		for (Memo memo : memos) {

			if (memo.getParentMemoId() == parentMemoId) {

				hasChild = true;

				if (!memo.isCompleted()) {
					return false;
				}
			}
		}

		return hasChild;
	}

	// =========================
	// プロジェクトが完了したか
	// =========================
	private boolean isProjectCompleted(
			int projectId) {

		ArrayList<Memo> topMemos = getChildMemos(projectId, 0);

		// メモ0件のプロジェクトは
		// 完了扱いにしない
		if (topMemos.isEmpty()) {
			return false;
		}

		for (Memo memo : topMemos) {

			if (!memo.isCompleted()) {
				return false;
			}
		}

		return true;
	}

	// =========================
	// プロジェクト内の
	// 「今やること」を取得
	// =========================
	public Memo findCurrentTask(int projectId) {

		ArrayList<Memo> topMemos = getChildMemos(projectId, 0);

		for (Memo memo : topMemos) {

			Memo currentTask = findCurrentTaskFromMemo(memo);

			if (currentTask != null) {
				return currentTask;
			}
		}

		return null;
	}

	// =========================
	// 親子をたどって
	// 最初の未完了タスクを探す
	// =========================
	private Memo findCurrentTaskFromMemo(
			Memo memo) {

		// 完了済みなら飛ばす
		if (memo.isCompleted()) {
			return null;
		}

		ArrayList<Memo> children = getChildMemos(
				memo.getProjectId(),
				memo.getMemoId());

		// 子がいなければ、
		// このメモ自体が「今やること」
		if (children.isEmpty()) {
			return memo;
		}

		// 子がいる場合は、
		// 上から最初の未完了を探す
		for (Memo child : children) {

			Memo currentTask = findCurrentTaskFromMemo(child);

			if (currentTask != null) {
				return currentTask;
			}
		}

		return null;
	}

	// =========================
	// アプリ全体の
	// 「今やること」を取得
	// =========================
	public String getCurrentTaskText() {

		ArrayList<Project> projects = projectProcess.getActiveProjects();

		for (Project project : projects) {

			Memo currentTask = findCurrentTask(
					project.getProjectId());

			if (currentTask != null) {
				return currentTask.getText();
			}
		}

		return "なし";
	}

	// =========================
	// プロジェクト内のメモを
	// すべて削除する
	// =========================
	public void deleteMemosByProjectId(int projectId) {

		memos.removeIf(
				memo -> memo.getProjectId() == projectId);

		FileManager.saveMemos(memos);
	}

	// =========================
	// プロジェクト保存後の画面
	// =========================
	private boolean showAfterAssignMenu(
			Memo memo,
			Project project) {

		System.out.println();

		System.out.println(
				"「" + memo.getText() + "」を");

		System.out.println(
				"「" + project.getName()
						+ "」に保存しました。");

		ConsoleUtil.showDivider();

		System.out.println("次にどうしますか？");
		System.out.println();

		System.out.println(
				"1. 次の未整理メモへ");

		System.out.println();

		System.out.println(
				"2. 「" + project.getName()
						+ "」を開く");

		System.out.println(
				"0. HOMEへ戻る");

		System.out.println();

		System.out.print("番号を入力 > ");
		String input = scanner.nextLine().trim();

		switch (input) {

		case "1":
			return true;

		case "2":
			openProjectMemos(project);

			// プロジェクトを見終わったらHOMEへ
			return false;

		case "0":
			return false;

		default:
			System.out.println();
			System.out.println(
					"0〜2の番号を入力してください。");

			waitForEnter();

			return false;
		}
	}

	// =========================
	// メモの階層番号を作る
	// 例：1 / 1-(1) / 1-(1)-(2)
	// =========================
	private String getMemoDisplayNumber(Memo memo) {

		ArrayList<Integer> numbers = new ArrayList<>();

		Memo currentMemo = memo;

		while (currentMemo != null) {

			numbers.add(0, currentMemo.getOrder());

			if (currentMemo.getParentMemoId() == 0) {
				break;
			}

			currentMemo = findMemoById(
					currentMemo.getParentMemoId());
		}

		if (numbers.isEmpty()) {
			return "";
		}

		StringBuilder result = new StringBuilder();

		// 一番上
		result.append(numbers.get(0));

		// 子以降
		for (int i = 1; i < numbers.size(); i++) {

			result.append("-(");
			result.append(numbers.get(i));
			result.append(")");
		}

		return result.toString();
	}

	// =========================
	// 同じ階層のorderを
	// 1から振り直す
	// =========================
	private void normalizeSiblingOrders(
			int projectId,
			int parentMemoId) {

		ArrayList<Memo> siblings = getChildMemos(
				projectId,
				parentMemoId);

		for (int i = 0; i < siblings.size(); i++) {

			siblings.get(i).setOrder(i + 1);
		}

		FileManager.saveMemos(memos);
	}

	// =========================
	// メモ編集メニュー
	// =========================
	public void manageMemoEdit(Project project) {

		boolean running = true;

		while (running) {

			ConsoleUtil.showLocation(
					"HOME > プロジェクト > "
							+ project.getName()
							+ " > メモ編集");

			System.out.println(
					"1. メモの内容を変更する");

			System.out.println(
					"2. メモの順番を並び替える");

			System.out.println();
			System.out.println("0. 戻る");
			System.out.println();

			System.out.print("番号を入力 > ");
			String input = scanner.nextLine().trim();

			switch (input) {

			case "1":
				updateMemo(project);
				break;

			case "2":
				reorderMemo(project);
				break;

			case "0":
				running = false;
				break;

			default:
				System.out.println();
				System.out.println(
						"0〜2の番号を入力してください。");
				waitForEnter();
				break;
			}
		}
	}

	// =========================
	// メモの順番を並び替える
	// =========================
	public void reorderMemo(Project project) {

		ArrayList<Memo> projectMemos = getProjectMemosInTreeOrder(
				project.getProjectId());

		ConsoleUtil.showDivider();

		if (projectMemos.isEmpty()) {

			System.out.println(
					"並び替えできるメモはありません。");

			waitForEnter();
			return;
		}

		System.out.println(
				"並び替えるメモを選んでください。");

		System.out.println();

		for (int i = 0; i < projectMemos.size(); i++) {

			Memo memo = projectMemos.get(i);

			String displayNumber = getMemoDisplayNumber(memo);

			System.out.println(
					(i + 1)
							+ ". "
							+ displayNumber
							+ " "
							+ memo.getText());
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
					|| number > projectMemos.size()) {

				System.out.println();
				System.out.println(
						"表示されている番号を入力してください。");

				waitForEnter();
				return;
			}

			Memo targetMemo = projectMemos.get(number - 1);

			// 同じ親を持つメモだけ取得
			ArrayList<Memo> siblings = getChildMemos(
					targetMemo.getProjectId(),
					targetMemo.getParentMemoId());

			ConsoleUtil.showDivider();

			System.out.println(
					"「" + targetMemo.getText()
							+ "」の順番を変更します。");

			System.out.println();
			System.out.println(
					"同じ階層の中で移動できます。");

			System.out.println();

			for (int i = 0; i < siblings.size(); i++) {

				System.out.println(
						(i + 1)
								+ ". "
								+ siblings.get(i).getText());
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
					|| newPosition > siblings.size()) {

				System.out.println();
				System.out.println(
						"表示されている番号を入力してください。");

				waitForEnter();
				return;
			}

			// 一度リストから外す
			siblings.remove(targetMemo);

			// 新しい位置へ入れる
			siblings.add(
					newPosition - 1,
					targetMemo);

			// orderを1から振り直す
			for (int i = 0; i < siblings.size(); i++) {

				siblings.get(i).setOrder(i + 1);
			}

			FileManager.saveMemos(memos);

			System.out.println();
			System.out.println(
					"メモの順番を変更しました。");

			waitForEnter();

		} catch (NumberFormatException e) {

			System.out.println();
			System.out.println(
					"番号を入力してください。");

			waitForEnter();
		}
	}
}