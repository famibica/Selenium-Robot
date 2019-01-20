package com.sap.amd.utils.html;

import java.util.LinkedList;
import java.util.List;

public class Table extends LinkedList<Row>
{
	private static final long serialVersionUID = 2069587096084282057L;
	
	public Table(List<Row> rows)
	{
		super(rows);
	}
	
	public Table()
	{
		super();
	}
	
	public String toHtml()
	{
		String result = "<table border=0 cellspacing=0 cellpadding=0 style='border-collapse:collapse;border:none;'>";
		
		for (int i = 0; i < this.size(); i++)
		{
			result += this.get(i).toHtml();
		}
		
		result += "</table>";
		
		return result;
	}
}
