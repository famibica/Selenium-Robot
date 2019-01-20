package com.sap.amd.utils.html;

public class Cell
{
	private String text;
	private String color;
	private int border;
	private String alignment;
	
	public Cell(String text, String color, int border)
	{
		this.text = text;
		this.color = color;
		this.border = border;
		this.alignment = "";
	}
	
	public Cell(String text, String color, int border, String alignment)
	{
		this.text = text;
		this.color = color;
		this.border = border;
		this.alignment = alignment;
	}
	
	public String toHtml()
	{
		return "<td style='" + Border.get(this.border) + "background:" + this.color + ";padding:0cm 5.4pt 0cm 5.4pt;height:17.0pt;'><p class=Cell " + this.alignment + "><span>" + this.text + "</span></p></td>";
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public String getColor()
	{
		return color;
	}

	public void setColor(String color)
	{
		this.color = color;
	}

	public int getBorder()
	{
		return border;
	}

	public void setBorder(int border)
	{
		this.border = border;
	}
}
