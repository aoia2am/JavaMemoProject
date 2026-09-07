package javamemoproject;

public class Project {
	private int projectId;
	private String name;
	private boolean completed;
	private int order;

	public Project(int projectId, String name, int order) {
		this.projectId = projectId;
		this.name = name;
		this.completed = false;
		this.order = order;
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

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
}
