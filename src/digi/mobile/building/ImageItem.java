package digi.mobile.building;

/**
 * @author javatechig {@link http://javatechig.com}
 * 
 */
public class ImageItem {
	private String imagePath;
	private String title;
	private boolean selected;

	public ImageItem(String imagePath, String title, boolean selected) {
		super();
		this.imagePath = imagePath;
		this.title = title;
		this.selected = selected;
	}

	public String getImage() {
		return imagePath;
	}

	public void setImage(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
