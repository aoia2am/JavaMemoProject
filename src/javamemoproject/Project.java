package javamemoproject;

public class Project {
	private int projectId;
	private String name;
	private boolean completed;
	private int projectOrder;

	public Project(int projectId, String name, int projectOrder) {
		this.projectId = projectId;
		this.name = name;
		this.completed = false;
		this.projectOrder = projectOrder;
	}

	public int getProjectId() {
		return projectId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public int getProjectOrder() {
		return projectOrder;
	}

	public void setProjectOrder(int projectOrder) {
		this.projectOrder = projectOrder;
	}
}
