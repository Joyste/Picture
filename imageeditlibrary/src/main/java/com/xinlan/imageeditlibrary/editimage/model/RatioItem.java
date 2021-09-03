package com.xinlan.imageeditlibrary.editimage.model;

public class RatioItem {
	private int icon;
	private String text;
	private Float ratio;
	private int index;

	public RatioItem(int icon,String text, Float ratio) {
		super();
		this.icon = icon;
		this.text = text;
		this.ratio = ratio;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Float getRatio() {
		return ratio;
	}

	public void setRatio(Float ratio) {
		this.ratio = ratio;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}// end class
