package com.sap.amd.utils.html;

import java.util.LinkedList;
import java.util.List;

public class Row extends LinkedList<Cell>
{
	private static final long serialVersionUID = -5699498049571264293L;

	public Row(List<Cell> cells)
	{
		super(cells);
	}
	
	public Row()
	{
		super();
	}
	
	public String toHtml()
	{
		String result = "<tr style='height:17.0pt'>";
		
		for (int i = 0; i < this.size(); i++)
		{
			result += this.get(i).toHtml();
		}
		
		result += "</tr>";
		
		return result;
	}
}
