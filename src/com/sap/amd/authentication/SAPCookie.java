package com.sap.amd.authentication;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.Cookie;

import com.sap.amd.utils.types.TBrowser;
import com.sap.amd.utils.types.TCookie;
import com.sun.jna.platform.win32.Crypt32Util;

public class SAPCookie
{
	private String name;
	private byte[] value;
	private String domain;
	private String path;
	
	public SAPCookie(String name, byte[] value, String domain, String path)
	{
		this.name = name;
		this.value = value;
		this.domain = domain;
		this.path = path;
	}

	public String getName()
	{
		return name;
	}

	public String getValue()
	{
		return decrypt(value);
	}

	public String getDomain()
	{
		return domain;
	}

	public String getPath()
	{
		return path;
	}

	public Cookie toCookie()
	{
		Cookie cookie = new Cookie(name, decrypt(value), domain, path, new Date(0));
		return cookie;
	}

	public static List<Cookie> toCookies(List<SAPCookie> sapCookies)
	{
		List<Cookie> cookies = new ArrayList<Cookie>();

		for (SAPCookie sapCookie : sapCookies)
		{
			cookies.add(sapCookie.toCookie());
		}

		return cookies;
	}

	private String decrypt(byte[] encryptedValue)
	{
		byte[] chars = Crypt32Util.cryptUnprotectData(encryptedValue);

		String decryptedValue = "";

		for (byte b : chars)
		{
			decryptedValue += (char) b;
		}

		return decryptedValue;
	}

	public static List<SAPCookie> getCookies(ResultSet cookiesTable)
	{
		List<SAPCookie> cookies = new ArrayList<SAPCookie>();

		try
		{
			while (cookiesTable.next())
			{
				String name = cookiesTable.getString("name");
				byte[] encryptedValue = cookiesTable.getBytes("encrypted_value");
				String domain = cookiesTable.getString("host_key");
				String path = cookiesTable.getString("path");
				cookies.add(new SAPCookie(name, encryptedValue, domain, path));
			}
		}
		catch (SQLException e)
		{
			return null;
		}

		return cookies;
	}

	public static ResultSet getCookiesTable(TBrowser browser, String profile)
	{
		String cookiesPath = null;

		if (browser == TBrowser.Chrome)
		{
			cookiesPath = "jdbc:sqlite:" + System.getProperty("user.home") + "\\AppData\\Local\\Google\\Chrome\\User Data\\" + profile + "\\Cookies";
		}
		else
		{
			cookiesPath = "jdbc:sqlite:" + System.getProperty("user.home") + "\\AppData\\Roaming\\Mozilla\\Firefox\\Profiles\\" + profile + "\\cookies.sqlite";
		}

		try
		{
			Class.forName("org.sqlite.JDBC");
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
			return null;
		}

		ResultSet queryResult = null;

		try
		{
			Connection jdbcConnection = DriverManager.getConnection(cookiesPath);
			Statement sqlStatement = jdbcConnection.createStatement();

			if (browser == TBrowser.Chrome)
				queryResult = sqlStatement.executeQuery("select * from cookies where name = 'MYSAPSSO2' or name = 'sap-appcontext' or name = 'sap-usercontext' or name = 'SAP_SESSIONID_BCS_001';");
			else
				queryResult = sqlStatement.executeQuery("select * from moz_cookies where name = 'MYSAPSSO2' or name = 'sap-appcontext' or name = 'sap-usercontext' or name = 'SAP_SESSIONID_BCS_001';");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}

		return queryResult;
	}

	public static ResultSet getCookiesTable(TBrowser browser, String profile, List<TCookie> requiredCookies)
	{
		String cookiesPath = null;

		if (browser == TBrowser.Chrome)
		{
			cookiesPath = "jdbc:sqlite:C:\\Users\\i841748.GLOBAL\\AppData\\Local\\Google\\Chrome\\User Data\\" + profile + "\\Cookies";
		}
		else
		{
			cookiesPath = "C:\\Users\\i841748.GLOBAL\\AppData\\Roaming\\Mozilla\\Firefox\\Profiles\\" + profile + "\\cookies.sqlite";
		}

		try
		{
			Class.forName("org.sqlite.JDBC");
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
			return null;
		}

		ResultSet queryResult = null;

		try
		{
			Connection jdbcConnection = DriverManager.getConnection(cookiesPath);
			Statement sqlStatement = jdbcConnection.createStatement();

			String names = "";

			for (int i = 0; i < requiredCookies.size(); i++)
			{
				if (i != requiredCookies.size() - 1)
					names += "name like '%" + requiredCookies.get(i).name() + "%' or ";
				else
					names += "name like '%" + requiredCookies.get(i).name() + "%';";
			}

			if (browser == TBrowser.Chrome)
				queryResult = sqlStatement.executeQuery("select * from cookies where " + names);
			else
				queryResult = sqlStatement.executeQuery("select * from moz_cookies where " + names);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}

		return queryResult;
	}
}
