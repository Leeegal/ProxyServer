package domain;

public class FileVo {
	private String id;
	private String fileName;
	private String date;
	private String size;
	private String path;
	private String desPath;
	private String conditionPath;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public String getDesPath() {
		return desPath;
	}

	public void setDesPath(String desPath) {
		this.desPath = desPath;
	}
	
	public String getConditionPath() {
		return conditionPath;
	}

	public void setConditionPath(String conditionPath) {
		this.conditionPath = conditionPath;
	}
}
