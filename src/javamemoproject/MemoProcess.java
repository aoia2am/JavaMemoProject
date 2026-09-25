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
	public void openProjectMemoMenu() {

		// メモ一覧を閉じたらプロジェクト一覧へ戻る
		while (true) {

			Project project = projectProcess.selectProject();

			// プロジェクト一覧で0が入力されたらHOMEへ
			if (project == null) {
				return;
			}

			showProjectMemoMenu(project);
		}
	}

	// =========================
	// メモ一覧を表示して
	// 番号でメモを1件選ぶ
	// 対象なし・0入力ならnull
	// =========================
	private Memo showAndSelectMemo(
			Project project,
			String emptyMessage,
			String promptMessage,
			boolean showHierarchyNumber) {

		ArrayList<Memo> projectMemos = getProjectMemosInTreeOrder(
				project.getProjectId());

		ConsoleUtil.showDivider();

		if (projectMemos.isEmpty()) {

			System.out.println(emptyMessage);

			ConsoleUtil.waitForEnter(scanner, "Enterで前の画面に戻る > ");
			return null;
		}

		System.out.println(promptMessage);

		System.out.println();

		for (int i = 0; i < projectMemos.size(); i++) {

			Memo memo = projectMemos.get(i);

			// 階層番号を出す画面だけ「1-(1)」を付ける
			String hierarchyNumber = "";

			if (showHierarchyNumber) {

				hierarchyNumber = getMemoDisplayNumber(memo) + " ";
			}

			System.out.println(
					(i + 1)
							+ ". "
							+ hierarchyNumber
							+ memo.getText());
		}

		System.out.println();
		System.out.println("0. 戻る");
		System.out.println();

		int number;

		// 不正入力ならこの場で再入力
		while (true) {

			number = ConsoleUtil.readNumber(
					scanner,
					"番号を入力 > ",
					projectMemos.size());

			if (number != -1) {
				break;
			}
		}

		if (number == 0) {
			return null;
		}

		return projectMemos.get(number - 1);
	}

	// =========================
	// メモを編集する
	// =========================
	public void updateMemo(Project project) {

		Memo memo = showAndSelectMemo(
				project,
				"編集できるメモはありません。",
				"編集するメモを選んでください。",
				false);

		if (memo == null) {
			return;
		}

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

			ConsoleUtil.waitForEnter(scanner, "Enterで前の画面に戻る > ");
			return;
		}

		memo.setText(newText);
		FileManager.saveMemos(memos);

		System.out.println();
		System.out.println("メモを変更しました。");

		System.out.println();
		System.out.println("Before：" + oldText);
		System.out.println("After ：" + newText);

		ConsoleUtil.waitForEnter(scanner, "Enterで前の画面に戻る > ");
	}

	// =========================
	// メモを削除する
	// =========================
	public void deleteMemo(Project project) {

		Memo targetMemo = showAndSelectMemo(
				project,
				"削除できるメモはありません。",
				"削除するメモを選んでください。",
				false);

		if (targetMemo == null) {
			return;
		}

		int projectId = targetMemo.getProjectId();

		int parentMemoId = targetMemo.getParentMemoId();

		ArrayList<Memo> descendants = getDescendants(targetMemo);

		// 一覧に表示されていた番号
		int number = getProjectMemosInTreeOrder(
				projectId).indexOf(targetMemo) + 1;

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

			ConsoleUtil.waitForEnter(scanner, "Enterで前の画面に戻る > ");
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

		ConsoleUtil.waitForEnter(scanner, "Enterで前の画面に戻る > ");
	}

	public void showProjectMemoMenu(Project project) {

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
				boolean projectCompleted = completeCurrentMemo(project);

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
				showMemoEditMenu(project);
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
				ConsoleUtil.waitForEnter(scanner, "Enterで前の画面に戻る > ");
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

		showMemoInputGuide();

		ArrayList<String> texts = readMemoTexts();

		if (texts.isEmpty()) {

			System.out.println();
			System.out.println("メモは追加されませんでした。");
			ConsoleUtil.waitForEnter(scanner, "Enterで前の画面に戻る > ");
			return;
		}

		ArrayList<Memo> addedMemos = addMemos(
				texts,
				project.getProjectId(),
				0);

		showAddedMemos(addedMemos);

		// 不正入力ならこのメニューに留まって再入力
		while (true) {

			System.out.println();
			System.out.println("1. 続けて書き込む");
			System.out.println("0. プロジェクトへ戻る");
			System.out.println();

			System.out.print("番号を入力 > ");
			String input = scanner.nextLine().trim();

			if (input.equals("1")) {
				createMemo(project);
				return;
			}

			if (input.equals("0")) {
				return;
			}

			System.out.println();
			System.out.println(
					"0 または 1 を入力してください。");

			ConsoleUtil.waitForEnter(scanner, "Enterで前の画面に戻る > ");
		}
	}

	// =========================
	// メモを細かくする
	// =========================
	public void createChildMemo(Project project) {

		Memo parentMemo = showAndSelectMemo(
				project,
				"細かくできるメモはありません。",
				"細かくするメモを選んでください。",
				true);

		if (parentMemo == null) {
			return;
		}

		ConsoleUtil.showDivider();

		System.out.println(
				"「" + parentMemo.getText()
						+ "」を細かくします。");

		System.out.println();

		System.out.println(
				"必要なことを、思いつくまま書いてください。");

		showMemoInputGuide();

		ArrayList<String> texts = readMemoTexts();

		if (texts.isEmpty()) {

			System.out.println();
			System.out.println(
					"メモは追加されませんでした。");

			ConsoleUtil.waitForEnter(scanner, "Enterで前の画面に戻る > ");
			return;
		}

		ArrayList<Memo> addedMemos = addMemos(
				texts,
				project.getProjectId(),
				parentMemo.getMemoId());

		showAddedMemos(addedMemos);

		ConsoleUtil.waitForEnter(scanner, "Enterで前の画面に戻る > ");
	}

	// =========================
	// メモの書き方の案内
	// =========================
	private void showMemoInputGuide() {

		System.out.println();
		System.out.println(
				"「。」「！」「？」または改行で");
		System.out.println(
				"1件ずつのメモに分かれます。");

		System.out.println();
		System.out.println(
				"入力を終えるときは、Enterを2回押してください。");

		System.out.println();
	}

	// =========================
	// 追加したメモを表示する
	// =========================
	private void showAddedMemos(
			ArrayList<Memo> addedMemos) {

		System.out.println();
		System.out.println(
				addedMemos.size()
						+ "件のメモを追加しました。");

		System.out.println();

		for (Memo memo : addedMemos) {

			System.out.println(
					"・" + memo.getText());
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
						a.getSiblingOrder(),
						b.getSiblingOrder()));

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
							+ memo.getSiblingOrder()
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
	private int getNextMemoOrder(
			int projectId,
			int parentMemoId) {

		int maxOrder = 0;

		for (Memo memo : memos) {

			if (memo.getProjectId() == projectId
					&& memo.getParentMemoId() == parentMemoId
					&& memo.getSiblingOrder() > maxOrder) {

				maxOrder = memo.getSiblingOrder();
			}
		}

		return maxOrder + 1;
	}

	// =========================
	// 複数行の入力を受け取り
	// メモ単位に分割する
	// =========================
	private ArrayList<String> readMemoTexts() {

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

		return splitMemo(inputText.toString());
	}

	// =========================
	// メモを作って保存する
	// =========================
	private ArrayList<Memo> addMemos(
			ArrayList<String> texts,
			int projectId,
			int parentMemoId) {

		ArrayList<Memo> addedMemos = new ArrayList<>();

		for (String text : texts) {

			int memoId = getNextMemoId();

			int order = getNextMemoOrder(
					projectId,
					parentMemoId);

			Memo memo = new Memo(
					memoId,
					text,
					projectId,
					parentMemoId,
					order);

			memos.add(memo);
			addedMemos.add(memo);
		}

		FileManager.saveMemos(memos);

		return addedMemos;
	}

	// =========================
	// 白紙に書き出す
	// =========================
	public void inputMemo() {

		boolean running = true;

		while (running) {

			ConsoleUtil.showDivider();

			showBlankPaperGuide();

			ArrayList<String> texts = readMemoTexts();

			// 何も入力されなかった場合
			if (texts.isEmpty()) {

				System.out.println();
				System.out.println("メモは保存されませんでした。");

				// 不正入力ならこのメニューに留まって再入力
				boolean inMenu = true;

				while (inMenu) {

					System.out.println();
					System.out.println("1. もう一度書く");
					System.out.println("0. HOMEへ戻る");
					System.out.println();

					System.out.print("番号を入力 > ");
					String input = scanner.nextLine().trim();

					if (input.equals("1")) {

						inMenu = false;

					} else if (input.equals("0")) {

						running = false;
						inMenu = false;

					} else {

						System.out.println();
						System.out.println(
								"0 または 1 を入力してください。");

						ConsoleUtil.waitForEnter(scanner, "Enterで前の画面に戻る > ");
					}
				}

				continue;
			}

			// 未整理メモとして保存
			// projectId = 0 → 未整理 / parentMemoId = 0
			addMemos(texts, 0, 0);

			System.out.println();
			System.out.println(
					texts.size()
							+ "件のメモを「未整理」に保存しました。");

			// 不正入力ならこのメニューに留まって再入力
			boolean inMenu = true;

			while (inMenu) {

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
					inMenu = false;
					break;

				case "2":
					ConsoleUtil.showDivider();
					assignUnorganizedMemos();
					running = false;
					inMenu = false;
					break;

				case "0":
					running = false;
					inMenu = false;
					break;

				default:
					System.out.println();
					System.out.println(
							"0〜2の番号を入力してください。");
					ConsoleUtil.waitForEnter(scanner, "Enterで前の画面に戻る > ");
					break;
				}
			}
		}

	}

	// =========================
	// 白紙画面の案内
	// =========================
	private void showBlankPaperGuide() {

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
	}

	// =========================
	// 未整理メモを整理する
	// =========================
	public void assignUnorganizedMemos() {

		ArrayList<Memo> unorganizedMemos = getUnorganizedMemos();

		if (unorganizedMemos.isEmpty()) {

			ConsoleUtil.showDivider();

			System.out.println("「未整理」のメモはありません。");

			ConsoleUtil.waitForEnter(scanner, "Enterで前の画面に戻る > ");
			return;
		}

		int index = 0;

		while (index < unorganizedMemos.size()) {

			Memo memo = unorganizedMemos.get(index);

			ArrayList<Project> projects = projectProcess.getActiveProjects();

			int createNumber = projects.size() + 1;
			int skipNumber = projects.size() + 2;

			ConsoleUtil.showDivider();

			showUnorganizedMemoScreen(
					memo,
					index,
					unorganizedMemos.size(),
					projects,
					createNumber,
					skipNumber);

			int number = ConsoleUtil.readNumber(
					scanner,
					"番号を入力 > ",
					skipNumber);

			// 不正入力なら同じメモをもう一度表示
			if (number == -1) {
				continue;
			}

			// HOME
			if (number == 0) {
				return;
			}

			// =========================
			// 既存プロジェクトへ保存
			// =========================
			if (number <= projects.size()) {

				Project project = projects.get(number - 1);

				boolean continueSorting = assignMemoToProject(
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

					boolean continueSorting = assignMemoToProject(
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
		}

		ConsoleUtil.showDivider();

		System.out.println(
				"未整理メモの確認が終わりました。");

		ConsoleUtil.waitForEnter(scanner, "Enterで前の画面に戻る > ");
	}

	// =========================
	// 未整理メモの振り分け画面
	// =========================
	private void showUnorganizedMemoScreen(
			Memo memo,
			int index,
			int total,
			ArrayList<Project> projects,
			int createNumber,
			int skipNumber) {

		System.out.println(
				"「" + memo.getText() + "」 "
						+ "(" + (index + 1)
						+ "/" + total + ")");

		System.out.println();
		System.out.println("保存先を選んでください。");
		System.out.println();

		for (int i = 0; i < projects.size(); i++) {

			System.out.println(
					(i + 1) + ". "
							+ projects.get(i).getName());
		}

		System.out.println();

		System.out.println(
				createNumber + ". ＋新しいプロジェクト");

		System.out.println(
				skipNumber + ". スキップ");

		System.out.println(
				"0. HOMEへ戻る");

		System.out.println();
	}

	// =========================
	// メモをプロジェクトへ保存し
	// 次の操作を聞く
	// =========================
	private boolean assignMemoToProject(
			Memo memo,
			Project project) {

		memo.setProjectId(
				project.getProjectId());

		FileManager.saveMemos(memos);

		return showNextActionMenu(
				memo,
				project);
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
	public boolean completeCurrentMemo(Project project) {

		Memo currentTask = findCurrentTask(project.getProjectId());

		ConsoleUtil.showDivider();

		if (currentTask == null) {

			System.out.println(
					"完了できるメモはありません。");

			ConsoleUtil.waitForEnter(scanner, "Enterで前の画面に戻る > ");
			return false;
		}

		// 完了するか確認する
		if (!confirmCompleteMemo(currentTask)) {
			return false;
		}

		// 今やることを完了
		currentTask.setCompleted(true);

		System.out.println();
		System.out.println(
				"●「"
						+ currentTask.getText()
						+ "」を完了しました。");

		// 親メモへ完了を連鎖させる
		ArrayList<Memo> completedParents = completeParentMemos(
				currentTask);

		for (Memo parentMemo : completedParents) {

			System.out.println();
			System.out.println(
					"●「" + parentMemo.getText()
							+ "」が完了しました。");
		}

		// メモの状態を保存
		FileManager.saveMemos(memos);

		// プロジェクト全体が終わったか確認
		if (isProjectCompleted(
				project.getProjectId())) {

			completeProjectAndShow(project);

			return true;
		}

		showNextTask(project);

		return false;
	}

	// =========================
	// 完了するか確認する
	// 完了する：true / 中止：false
	// =========================
	private boolean confirmCompleteMemo(Memo currentTask) {

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

			ConsoleUtil.waitForEnter(scanner, "Enterで前の画面に戻る > ");
			return false;
		}

		return true;
	}

	// =========================
	// 子が全部完了した親メモを
	// 上へたどって完了にする
	// 完了させた親を順番に返す
	// =========================
	private ArrayList<Memo> completeParentMemos(Memo memo) {

		ArrayList<Memo> completedParents = new ArrayList<>();

		int parentMemoId = memo.getParentMemoId();

		while (parentMemoId != 0) {

			Memo parentMemo = findMemoById(parentMemoId);

			if (parentMemo == null) {
				break;
			}

			// 子が残っていたらここで止める
			if (!areAllChildrenCompleted(
					parentMemo.getMemoId())) {
				break;
			}

			parentMemo.setCompleted(true);

			completedParents.add(parentMemo);

			parentMemoId = parentMemo.getParentMemoId();
		}

		return completedParents;
	}

	// =========================
	// プロジェクトを完了にして
	// 結果を表示する
	// =========================
	private void completeProjectAndShow(Project project) {

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

		ConsoleUtil.waitForEnter(scanner, "EnterでHOMEに戻る > ");
	}

	// =========================
	// 次にやることを表示する
	// =========================
	private void showNextTask(Project project) {

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

		ConsoleUtil.waitForEnter(scanner, "Enterでプロジェクト画面に戻る > ");
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
	private boolean showNextActionMenu(
			Memo memo,
			Project project) {

		System.out.println();

		System.out.println(
				"「" + memo.getText() + "」を");

		System.out.println(
				"「" + project.getName()
						+ "」に保存しました。");

		// 不正入力ならこのメニューに留まって再入力
		while (true) {

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
				showProjectMemoMenu(project);

				// メモ画面を閉じたらプロジェクト一覧へ
				openProjectMemoMenu();

				// プロジェクト一覧で0が押されたらHOMEへ
				return false;

			case "0":
				return false;

			default:
				System.out.println();
				System.out.println(
						"0〜2の番号を入力してください。");

				ConsoleUtil.waitForEnter(scanner, "Enterで前の画面に戻る > ");

				break;
			}
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

			numbers.add(0, currentMemo.getSiblingOrder());

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

			siblings.get(i).setSiblingOrder(i + 1);
		}

		FileManager.saveMemos(memos);
	}

	// =========================
	// メモ編集メニュー
	// =========================
	public void showMemoEditMenu(Project project) {

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
				ConsoleUtil.waitForEnter(scanner, "Enterで前の画面に戻る > ");
				break;
			}
		}
	}

	// =========================
	// メモの順番を並び替える
	// =========================
	public void reorderMemo(Project project) {

		Memo targetMemo = showAndSelectMemo(
				project,
				"並び替えできるメモはありません。",
				"並び替えるメモを選んでください。",
				true);

		if (targetMemo == null) {
			return;
		}

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

		int newPosition;

		// 不正入力でも対象は選び直さず、移動先だけ再入力
		while (true) {

			newPosition = ConsoleUtil.readNumber(
					scanner,
					"移動先の番号を入力 > ",
					siblings.size());

			if (newPosition != -1) {
				break;
			}
		}

		if (newPosition == 0) {
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

			siblings.get(i).setSiblingOrder(i + 1);
		}

		FileManager.saveMemos(memos);

		System.out.println();
		System.out.println(
				"メモの順番を変更しました。");

		ConsoleUtil.waitForEnter(scanner, "Enterで前の画面に戻る > ");
	}
}