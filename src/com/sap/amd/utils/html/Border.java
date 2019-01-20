package com.sap.amd.utils.html;

public class Border
{
	public static final int Complete = 10238;
	public static final int None = 0;
	
	public static final int Left = 10;
	public static final int Right = 100;
	public static final int Bottom = 1000;
	public static final int Top = 10000;
	
	public static String get(int combination)
	{
		if (combination == Left)
		{
			return "border-top:none;border-left:solid windowtext 1.0pt;border-bottom:none;border-right:none;";
		}
		else if (combination == Right)
		{
			return "border-top:none;border-left:none;border-bottom:none;border-right:solid windowtext 1.0pt;";
		}
		else if (combination == Bottom)
		{
			return "border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:none;";
		}
		else if (combination == Top)
		{
			return "border-top:solid windowtext 1.0pt;border-left:none;border-bottom:none;border-right:none;";
		}
		//-----------------------------------------------------------------------------------------------------------------------------------------
		else if (combination == (Left | Top))
		{
			return "border-top:solid windowtext 1.0pt;border-left:solid windowtext 1.0pt;border-bottom:none;border-right:none;";
		}
		else if (combination == (Left | Bottom))
		{
			return "border-top:none;border-left:solid windowtext 1.0pt;border-bottom:solid windowtext 1.0pt;border-right:none;";
		}
		else if (combination == (Left | Right))
		{
			return "border-top:none;border-left:solid windowtext 1.0pt;border-bottom:none;border-right:solid windowtext 1.0pt;";
		}
		else if (combination == (Right | Top))
		{
			return "border-top:solid windowtext 1.0pt;border-left:none;border-bottom:none;border-right:solid windowtext 1.0pt;";
		}
		else if (combination == (Right | Bottom))
		{
			return "border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;";
		}
		else if (combination == (Top | Bottom))
		{
			return "border-top:solid windowtext 1.0pt;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:none;";
		}
		//-----------------------------------------------------------------------------------------------------------------------------------------
		else if (combination == (Left | Right | Top))
		{
			return "border-top:solid windowtext 1.0pt;border-left:solid windowtext 1.0pt;border-bottom:none;border-right:solid windowtext 1.0pt;";
		}
		else if (combination == (Left | Right | Bottom))
		{
			return "border-top:none;border-left:solid windowtext 1.0pt;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;";
		}
		else if (combination == (Left | Top | Bottom))
		{
			return "border-top:solid windowtext 1.0pt;border-left:solid windowtext 1.0pt;border-bottom:solid windowtext 1.0pt;border-right:none;";
		}
		else if (combination == (Right | Top | Bottom))
		{
			return "border-top:solid windowtext 1.0pt;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;";
		}
		//-----------------------------------------------------------------------------------------------------------------------------------------
		else if (combination == (Left | Right | Bottom | Top))
		{
			return  "border:solid windowtext 1.0pt;";
		}
		else if (combination == None)
		{
			return "";
		}
		//-----------------------------------------------------------------------------------------------------------------------------------------
		else
		{
			return null;
		}
	}
}
