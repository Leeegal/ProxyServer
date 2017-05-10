package domain;

public class RkVo {
	private String fileName;
	private String author;
	private String receiver;
	private String size;
	private String date;
	private String path;
	private String desPath;
	private String rkPath;

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
	
	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
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
	
	public String getrkPath() {
		return rkPath;
	}

	public void setrkPath(String rkPath) {
		this.rkPath = rkPath;
	}
}
