package javamemoproject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

// dataフォルダのtxtから読み書きする
public class FileManager {

	// 実行時の作業ディレクトリ（Eclipseではプロジェクト直下）の data を見る
	private static final String DATA_DIRECTORY = "data";
	private static final String PROJECT_FILE = DATA_DIRECTORY + "/projects.txt";
	private static final String MEMO_FILE = DATA_DIRECTORY + "/memos.txt";

	private static void createDataDirectory() {
		File directory = new File(DATA_DIRECTORY);
		if (!directory.exists()) {
			directory.mkdirs();
		}
	}

	public static void saveProjects(ArrayList<Project> projects) {
		createDataDirectory();
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(PROJECT_FILE));
			for (int i = 0; i < projects.size(); i++) {
				Project p = projects.get(i);
				// タブ区切り
				pw.println(p.getProjectId() + "\t" + p.getName() + "\t"
						+ p.isCompleted() + "\t" + p.getOrder());
			}
			pw.close();
		} catch (IOException e) {
			System.out.println("プロジェクトの保存に失敗しました。");
		}
	}

	public static ArrayList<Project> loadProjects() {
		createDataDirectory();
		ArrayList<Project> projects = new ArrayList<Project>();
		File file = new File(PROJECT_FILE);
		if (!file.exists()) {
			return projects;
		}

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				String[] data = line.split("\t", -1);
				if (data.length != 4) {
					continue;
				}
				int projectId = Integer.parseInt(data[0]);
				String name = data[1];
				boolean completed = Boolean.parseBoolean(data[2]);
				int order = Integer.parseInt(data[3]);

				Project project = new Project(projectId, name, order);
				project.setCompleted(completed);
				projects.add(project);
			}
			br.close();
		} catch (IOException e) {
			System.out.println("プロジェクトの読み込みに失敗しました。");
		} catch (NumberFormatException e) {
			System.out.println("プロジェクトの読み込みに失敗しました。");
		}

		return projects;
	}

	public static void saveMemos(ArrayList<Memo> memos) {
		createDataDirectory();
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(MEMO_FILE));
			for (int i = 0; i < memos.size(); i++) {
				Memo m = memos.get(i);
				pw.println(m.getMemoId() + "\t" + m.getText() + "\t"
						+ m.getProjectId() + "\t" + m.getParentMemoId() + "\t"
						+ m.isCompleted() + "\t" + m.getOrder());
			}
			pw.close();
		} catch (IOException e) {
			System.out.println("メモの保存に失敗しました。");
		}
	}

	public static ArrayList<Memo> loadMemos() {
		createDataDirectory();
		ArrayList<Memo> memos = new ArrayList<Memo>();
		File file = new File(MEMO_FILE);
		if (!file.exists()) {
			return memos;
		}

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				String[] data = line.split("\t", -1);
				if (data.length != 6) {
					continue;
				}
				int memoId = Integer.parseInt(data[0]);
				String text = data[1];
				int projectId = Integer.parseInt(data[2]);
				int parentMemoId = Integer.parseInt(data[3]);
				boolean completed = Boolean.parseBoolean(data[4]);
				int order = Integer.parseInt(data[5]);

				Memo memo = new Memo(memoId, text, projectId, parentMemoId, order);
				memo.setCompleted(completed);
				memos.add(memo);
			}
			br.close();
		} catch (IOException e) {
			System.out.println("メモの読み込みに失敗しました。");
		} catch (NumberFormatException e) {
			System.out.println("メモの読み込みに失敗しました。");
		}

		return memos;
	}
}
