package javamemoproject;

public class Memo {

	private int memoId;
	private String text;
	private int projectId; // 0なら未整理
	private int parentMemoId; // 0なら一番上
	private boolean completed;
	private int order;

	public Memo(int memoId, String text, int projectId, int parentMemoId, int order) {
		this.memoId = memoId;
		this.text = text;
		this.projectId = projectId;
		this.parentMemoId = parentMemoId;
		this.completed = false;
		this.order = order;
	}

	public int getMemoId() {
		return memoId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public int getParentMemoId() {
		return parentMemoId;
	}

	public void setParentMemoId(int parentMemoId) {
		this.parentMemoId = parentMemoId;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
}
