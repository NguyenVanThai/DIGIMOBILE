package digi.mobile.building;

public class History {
	private String date;
	private String name;
	private String id;
	private String type;

	public History() {

	}

	public History(String date, String name, String id, String type) {
		super();
		this.date = date;
		this.name = name;
		this.id = id;
		this.type = type;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
