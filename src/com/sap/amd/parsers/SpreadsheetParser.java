package com.sap.amd.parsers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.biff.EmptyCell;
import jxl.write.WritableCell;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import com.sap.amd.bcpandicp.Entity;
import com.sap.amd.bcpandicp.Processor;
import com.sap.amd.bcpandicp.Region;
import com.sap.amd.bcpandicp.Shift;
import com.sap.amd.bcpandicp.Team;
import com.sap.amd.utils.Log;
import com.sap.amd.utils.State;
import com.sap.amd.utils.StringUtils;
import com.sap.amd.utils.types.TState;

public class SpreadsheetParser implements Serializable
{
	private static final long serialVersionUID = 727728138805838832L;

	public static final String DefaultSpreadsheet = ".\\Processors.xls";

	private File file;

	public SpreadsheetParser(File file)
	{
		this.file = file;
	}
	

	private <T extends Entity<T>> boolean contains(T entity, List<T> entities)
	{
		for (int i = 0; i < entities.size(); i++)
		{
			if (entities.get(i).isEqualTo(entity))
			{
				return true;
			}
		}

		return false;
	}

	private <T extends Entity<T>> List<String> extractNames(List<T> entities)
	{
		List<String> names = new LinkedList<String>();

		for (int i = 0; i < entities.size(); i++)
		{
			names.add(entities.get(i).getName());
		}

		return names;
	}

	private List<Region> extractRegions(List<Shift> shifts)
	{
		LinkedList<Region> result = new LinkedList<Region>();

		for (int i = 0; i < shifts.size(); i++)
		{
			for (int j = 0; j < shifts.get(i).getRegions().size(); j++)
			{
				Region region = shifts.get(i).getRegions().get(j);

				if (!contains(region, result))
				{
					result.add(region);
				}
			}
		}

		return result;
	}

	private List<Shift> extractShifts(List<Team> teams)
	{
		LinkedList<Shift> result = new LinkedList<Shift>();

		for (int i = 0; i < teams.size(); i++)
		{
			for (int j = 0; j < teams.get(i).getShifts().size(); j++)
			{
				Shift shift = teams.get(i).getShifts().get(j);

				if (!contains(shift, result))
				{
					result.add(shift);
				}
			}
		}

		return result;
	}

	private List<Team> extractTeams(List<Processor> processors)
	{
		LinkedList<Team> result = new LinkedList<Team>();

		for (int i = 0; i < processors.size(); i++)
		{
			Team team = processors.get(i).getTeam();

			if (!contains(team, result))
			{
				result.add(team);
			}
		}

		return result;
	}

	private int findProcessor(List<Processor> processors, String inumber)
	{
		for (int i = 0; i < processors.size(); i++)
		{
			if (inumber.contains(processors.get(i).getINumber()))
			{
				return i;
			}
		}

		return -1;
	}

	public List<String> getInterestedParties()
	{
		Log.write("Parsing interested parties...");

		List<String> emailsList = new LinkedList<String>();

		try
		{
			FileInputStream stream = new FileInputStream(file);
			Workbook workbook = Workbook.getWorkbook(stream);
			Sheet sheet = workbook.getSheet(4);

			int ignored = 0;
			int parsed = 0;
			int rows = sheet.getRows();

			for (int i = 1; i < rows; i++)
			{
				try
				{
					Cell emails = sheet.getCell(0, i);

					if (!(emails instanceof EmptyCell) && !StringUtils.isEmpty(emails.getContents()))
					{
						emailsList.add(emails.getContents());
						parsed++;
					}
				}
				catch (Exception e)
				{
					Log.write("A party could not be parsed due to:");
					Log.write(e);
					ignored++;
				}
			}

			Log.write(parsed + " out of " + (rows - 1) + " email(s) parsed, " + ignored + " email(s) ignored.");
		}
		catch (Exception e)
		{
			Log.write(e);
		}

		return emailsList;
	}

	private List<Region> getRegions(String[] names, List<Region> regions)
	{
		LinkedList<Region> result = new LinkedList<Region>();

		for (int i = 0; i < regions.size(); i++)
		{
			for (int j = 0; j < names.length; j++)
			{
				if (regions.get(i).getName().equalsIgnoreCase(names[j]))
				{
					result.add(regions.get(i));
				}
			}
		}

		return result;
	}

	private List<Shift> getShifts(String[] names, List<Shift> shifts)
	{
		List<Shift> result = new LinkedList<Shift>();

		for (int i = 0; i < shifts.size(); i++)
		{
			for (int j = 0; j < names.length; j++)
			{
				if (shifts.get(i).getName().equalsIgnoreCase(names[j]))
				{
					result.add(shifts.get(i));
				}
			}
		}

		return result;
	}

	private Team getTeam(String name, List<Team> teams)
	{
		for (int i = 0; i < teams.size(); i++)
		{
			if (teams.get(i).getName().equalsIgnoreCase(name))
			{
				return teams.get(i);
			}
		}

		return null;
	}
	
	

	public List<Processor> parseProcessors()
	{
		Log.write("Parsing processors from file");

		List<Processor> result = null;

		try
		{
			FileInputStream stream = new FileInputStream(file);
			Workbook workbook = Workbook.getWorkbook(stream);
			Sheet sheet = workbook.getSheet(1);

			int rows = sheet.getRows();

			int ignored = 0;
			int parsed = 0;

			List<Team> teams = parseTeams();
			result = new LinkedList<Processor>();

			for (int i = 1; i < rows; i++)
			{
				try
				{
					Cell availability = sheet.getCell(0, i);
					Cell threshold = sheet.getCell(1, i);
					Cell inumber = sheet.getCell(2, i);
					Cell name = sheet.getCell(3, i);
					Cell email = sheet.getCell(4, i);
					Cell team = sheet.getCell(5, i);
					Cell components = sheet.getCell(6, i);
					Cell language = sheet.getCell(7, i);
					Cell componentsQS = sheet.getCell(8, i);
					Cell vh = sheet.getCell(9, i);
					Cell urgent = sheet.getCell(10, i);
					Cell nonSla = sheet.getCell(11, i);
					Cell sla = sheet.getCell(12, i);
					Cell session = sheet.getCell(13, i);
					

					Processor processor = new Processor();
					processor.setAvailable((availability.getContents().equals("0") ? false : true));
					processor.setThreshold(Integer.parseInt(threshold.getContents()));
					processor.setINumber(inumber.getContents());
					processor.setName(name.getContents());
					processor.setEmail(email.getContents());
					processor.setTeam(getTeam(team.getContents(), teams));
					processor.setShift(processor.getTeam().getDefaultShift());
					processor.setComponents(components.getContents());
					processor.setLanguage(language.getContents());
					processor.setComponentsQS(componentsQS.getContents());
					processor.setSession(session.getContents());
					
					String now = new SimpleDateFormat("HH-dd-MM-yyyy").format(new Date());
					String[] today = now.split("-");
					
					
				
					
					
					result.add(processor);
					parsed++;
				}
				catch (Exception e)
				{
					ignored++;

					Log.write("A processor has been ignored due to the following exception:");
					Log.write(e);
				}
			}

			workbook.close();
			stream.close();

			Log.write(parsed + " out of " + (rows - 1) + " processor(s) parsed, " + ignored + " processor(s) ignored.");
		}
		catch (Exception e)
		{
			Log.write(e);
		}
		return result;
	}

	public List<Region> parseRegions()
	{
		Log.write("Parsing regions from file");

		LinkedList<Region> result = null;

		try
		{
			FileInputStream stream = new FileInputStream(file);
			Workbook workbook = Workbook.getWorkbook(stream);
			Sheet sheet = workbook.getSheet(3);

			int rows = sheet.getRows();

			int ignored = 0;
			int parsed = 0;

			result = new LinkedList<Region>();

			for (int i = 1; i < rows; i++)
			{
				try
				{
					Cell name = sheet.getCell(0, i);
					Cell countries = sheet.getCell(1, i);
					Region region = new Region(name.getContents(), trimAll(countries.getContents().split(",")));

					result.add(region);
					parsed++;
				}
				catch (Exception e)
				{
					ignored++;

					Log.write("A region has been ignored due to the following exception:");
					Log.write(e);
				}
			}

			workbook.close();
			stream.close();

			Log.write(parsed + " out of " + (rows - 1) + " region(s) parsed, " + ignored + " region(s) ignored.");
		}
		catch (Exception e)
		{
			Log.write(e);
		}

		return result;
	}

	public List<Shift> parseShifts()
	{
		Log.write("Parsing shifts from file");

		List<Region> regions = parseRegions();
		LinkedList<Shift> result = null;

		try
		{
			FileInputStream stream = new FileInputStream(file);
			Workbook workbook = Workbook.getWorkbook(stream);
			Sheet sheet = workbook.getSheet(2);

			int rows = sheet.getRows();

			int ignored = 0;
			int parsed = 0;

			result = new LinkedList<Shift>();

			for (int i = 1; i < rows; i++)
			{
				try
				{
					Cell name = sheet.getCell(0, i);
					Cell region = sheet.getCell(1, i);

					Shift shift = new Shift(name.getContents(), getRegions(trimAll(region.getContents().split(",")), regions));

					result.add(shift);
					parsed++;
				}
				catch (Exception e)
				{
					ignored++;

					Log.write("A shift has been ignored due to the following exception:");
					Log.write(e);
				}
			}

			workbook.close();
			stream.close();

			Log.write(parsed + " out of " + (rows - 1) + " shift(s) parsed, " + ignored + " shift(s) ignored.");
		}
		catch (Exception e)
		{
			Log.write(e);
		}

		return result;
	}

	public List<Team> parseTeams()
	{
		Log.write("Parsing teams from file");

		List<Shift> shifts = parseShifts();
		LinkedList<Team> result = null;

		try
		{
			FileInputStream stream = new FileInputStream(file);
			Workbook workbook = Workbook.getWorkbook(stream);
			Sheet sheet = workbook.getSheet(0);

			int rows = sheet.getRows();

			int ignored = 0;
			int parsed = 0;

			result = new LinkedList<Team>();

			for (int i = 1; i < rows; i++)
			{
				try
				{
					Cell name = sheet.getCell(0, i);
					Cell shift = sheet.getCell(1, i);

					Team team = new Team(name.getContents(), getShifts(trimAll(shift.getContents().split(",")), shifts));

					result.add(team);
					parsed++;
				}
				catch (Exception e)
				{
					ignored++;

					Log.write("A team has been ignored due to the following exception:");
					Log.write(e);
				}
			}

			workbook.close();
			stream.close();

			Log.write(parsed + " out of " + (rows - 1) + " team(s) parsed, " + ignored + " team(s) ignored.");
		}
		catch (Exception e)
		{
			Log.write(e);
		}

		return result;
	}

	public State<TState> saveSpreadsheet(List<Processor> processors, boolean preserve)
	{
		if (preserve)
		{
			if (file.exists())
			{
				try
				{
					Workbook existingWorkbook = Workbook.getWorkbook(file);
					WritableWorkbook workbook = Workbook.createWorkbook(file, existingWorkbook);

					int sheets = workbook.getNumberOfSheets();

					if (sheets > 1)
					{
						WritableSheet sheet = workbook.getSheet(1);

						int rows = sheet.getRows();

						if (rows > 0)
						{
							for (int i = 1; i < rows; i++)
							{
								String inumber = sheet.getCell(2, i).getContents();

								int index = findProcessor(processors, inumber);

								if (index != -1)
								{
									Processor processor = processors.get(index);
									WritableCell availabilityCell = sheet.getWritableCell(0, i);
									WritableCell thresholdCell = sheet.getWritableCell(1, i);
									WritableCell vhCell = sheet.getWritableCell(9, i);
									WritableCell urgentCell = sheet.getWritableCell(10, i);
									WritableCell nonSlaCell = sheet.getWritableCell(11, i);
									WritableCell slaCell = sheet.getWritableCell(12, i);
									WritableCell sessionCell = sheet.getWritableCell(13, i);


									jxl.format.CellFormat availabilityFormat = availabilityCell.getCellFormat();
									WritableCellFeatures availabilityFeatures = availabilityCell.getWritableCellFeatures();

									jxl.format.CellFormat thresholdFormat = thresholdCell.getCellFormat();
									WritableCellFeatures thresholdFeatures = thresholdCell.getWritableCellFeatures();
									
									jxl.format.CellFormat vhFormat = vhCell.getCellFormat();
									WritableCellFeatures vhFeatures = vhCell.getWritableCellFeatures();
									
									jxl.format.CellFormat urgentFormat = urgentCell.getCellFormat();
									WritableCellFeatures urgentFeatures = urgentCell.getWritableCellFeatures();
									
									jxl.format.CellFormat nonSlaFormat = nonSlaCell.getCellFormat();
									WritableCellFeatures nonSlaFeatures = nonSlaCell.getWritableCellFeatures();
									
									jxl.format.CellFormat slaFormat = slaCell.getCellFormat();
									WritableCellFeatures slaFeatures = slaCell.getWritableCellFeatures();
									
									jxl.format.CellFormat sessionFormat = sessionCell.getCellFormat();
									WritableCellFeatures sessionFeatures = sessionCell.getWritableCellFeatures();
									

									availabilityCell = new jxl.write.Number(0, i, processor.isAvailable() ? 1 : 0);

									if (availabilityFormat != null)
									{
										availabilityCell.setCellFormat(availabilityFormat);
									}

									if (availabilityFeatures != null)
									{
										availabilityCell.setCellFeatures(availabilityFeatures);
									}

									thresholdCell = new jxl.write.Number(1, i, processor.getThreshold());

									if (thresholdFormat != null)
									{
										thresholdCell.setCellFormat(thresholdFormat);
									}

									if (thresholdFeatures != null)
									{
										thresholdCell.setCellFeatures(thresholdFeatures);
									}
									
									vhCell = new jxl.write.Number(9, i, processor.getVh());

									if (vhFormat != null)
									{
										vhCell.setCellFormat(vhFormat);
									}

									if (vhFeatures != null)
									{
										vhCell.setCellFeatures(vhFeatures);
									}
									
									urgentCell = new jxl.write.Number(10, i, processor.getUrgent());

									if (urgentFormat != null)
									{
										urgentCell.setCellFormat(urgentFormat);
									}

									if (urgentFeatures != null)
									{
										urgentCell.setCellFeatures(urgentFeatures);
									}
									
									nonSlaCell = new jxl.write.Number(11, i, processor.getNonSla());

									if (nonSlaFormat != null)
									{
										nonSlaCell.setCellFormat(nonSlaFormat);
									}

									if (nonSlaFeatures != null)
									{
										nonSlaCell.setCellFeatures(nonSlaFeatures);
									}
									
									slaCell = new jxl.write.Number(12, i, processor.getSla());

									if (slaFormat != null)
									{
										slaCell.setCellFormat(slaFormat);
									}

									if (slaFeatures != null)
									{
										slaCell.setCellFeatures(slaFeatures);
									}
									

									sessionCell = new jxl.write.Label(13, i, processor.getSession());

									if (sessionFormat != null)
									{
										sessionCell.setCellFormat(sessionFormat);
									}

									if (sessionFeatures != null)
									{
										sessionCell.setCellFeatures(sessionFeatures);
									}


									sheet.addCell(availabilityCell);
									sheet.addCell(thresholdCell);
									sheet.addCell(vhCell);
									sheet.addCell(urgentCell);
									sheet.addCell(nonSlaCell);
									sheet.addCell(slaCell);
									sheet.addCell(sessionCell);
								}
							}

							workbook.write();
							workbook.close();

							return new State<TState>(TState.Success);
						}
						else
						{
							return new State<TState>(TState.Error, new String[] { "Recreate File", "File '" + file.getPath() + "' is empty. Do you want to recreate the file?" });
						}
					}
					else
					{
						return new State<TState>(TState.Error, new String[] { "Recreate File", "File '" + file.getPath() + "' is empty. Do you want to recreate the file?" });
					}
				}
				catch (Exception e)
				{
					Log.write(e);

					return new State<TState>(TState.Error, new String[] { "Recreate File", "An error occurred while trying to save the file. Data may have been lost, do you want to recreate the file?" });
				}
			}
			else
			{
				return new State<TState>(TState.Error, new String[] { "Recreate File", "File '" + file.getPath() + "' was not found. Do you want to recreate the file?" });
			}
		}
		else
		{
			try
			{
				WritableWorkbook workbook = Workbook.createWorkbook(file);
				List<WritableCell> cells = new LinkedList<WritableCell>();

				// Teams Sheet
				workbook.createSheet("Teams", 0);
				WritableSheet sheet = workbook.getSheet(0);
				List<Team> teams = extractTeams(processors);

				cells.add(new jxl.write.Label(0, 0, "NAME"));
				cells.add(new jxl.write.Label(1, 0, "SHIFTS"));

				for (int i = 0; i < teams.size(); i++)
				{
					cells.add(new jxl.write.Label(0, i + 1, teams.get(i).getName()));
					cells.add(new jxl.write.Label(1, i + 1, StringUtils.join(", ", extractNames(teams.get(i).getShifts()))));
				}

				for (WritableCell cell : cells)
				{
					sheet.addCell(cell);
				}

				cells.clear();

				// Processors Sheet
				workbook.createSheet("Processors", 1);
				sheet = workbook.getSheet(1);

				cells.add(new jxl.write.Label(0, 0, "AVAILABLE"));
				cells.add(new jxl.write.Label(1, 0, "THRESHOLD"));
				cells.add(new jxl.write.Label(2, 0, "I-NUMBER"));
				cells.add(new jxl.write.Label(3, 0, "NAME"));
				cells.add(new jxl.write.Label(4, 0, "EMAIL"));
				cells.add(new jxl.write.Label(5, 0, "TEAM"));
				cells.add(new jxl.write.Label(6, 0, "COMPONENTS"));
				cells.add(new jxl.write.Label(7, 0, "LANGUAGE"));
				cells.add(new jxl.write.Label(8, 0, "COMPONENTS-QS "));
				cells.add(new jxl.write.Label(9, 0, "VH "));
				cells.add(new jxl.write.Label(10, 0, "URGENT"));
				cells.add(new jxl.write.Label(11, 0, "NON SLA"));
				cells.add(new jxl.write.Label(12, 0, "SLA"));
				cells.add(new jxl.write.Label(13, 0, "LAST SESSION"));

				for (int i = 0; i < processors.size(); i++)
				{
					cells.add(new jxl.write.Number(0, i + 1, processors.get(i).isAvailable() ? 1 : 0));
					cells.add(new jxl.write.Number(1, i + 1, processors.get(i).getThreshold()));
					cells.add(new jxl.write.Label(2, i + 1, processors.get(i).getINumber()));
					cells.add(new jxl.write.Label(3, i + 1, processors.get(i).getName()));
					cells.add(new jxl.write.Label(4, i + 1, processors.get(i).getEmail()));
					cells.add(new jxl.write.Label(5, i + 1, processors.get(i).regionsToString()));
					cells.add(new jxl.write.Label(6, i + 1, StringUtils.join(", ", processors.get(i).getComponents())));
					cells.add(new jxl.write.Label(7, i + 1, processors.get(i).getLanguage()));
					cells.add(new jxl.write.Label(8, i + 1, StringUtils.join(", ", processors.get(i).getComponentsQS())));
					cells.add(new jxl.write.Number(9, i + 1, processors.get(i).getVh()));
					cells.add(new jxl.write.Number(10, i + 1, processors.get(i).getUrgent()));
					cells.add(new jxl.write.Number(11, i + 1, processors.get(i).getNonSla()));
					cells.add(new jxl.write.Number(12, i + 1, processors.get(i).getSla()));
					cells.add(new jxl.write.Label(13, i + 1, processors.get(i).getSession()));
				}

				for (WritableCell cell : cells)
				{
					sheet.addCell(cell);
				}

				cells.clear();

				// SHIFTS SHEET
				workbook.createSheet("Shifts", 2);
				sheet = workbook.getSheet(2);
				List<Shift> shifts = extractShifts(teams);

				cells.add(new jxl.write.Label(0, 0, "NAME"));
				cells.add(new jxl.write.Label(1, 0, "REGIONS"));

				for (int i = 0; i < shifts.size(); i++)
				{
					cells.add(new jxl.write.Label(0, i + 1, shifts.get(i).getName()));
					cells.add(new jxl.write.Label(1, i + 1, StringUtils.join(", ", extractNames(shifts.get(i).getRegions()))));
				}

				for (WritableCell cell : cells)
				{
					sheet.addCell(cell);
				}

				cells.clear();

				// REGIONS SHEET
				workbook.createSheet("Regions", 3);
				sheet = workbook.getSheet(3);
				List<Region> regions = extractRegions(shifts);

				cells.add(new jxl.write.Label(0, 0, "NAME"));
				cells.add(new jxl.write.Label(1, 0, "COUNTRIES"));

				for (int i = 0; i < regions.size(); i++)
				{
					cells.add(new jxl.write.Label(0, i + 1, regions.get(i).getName()));
					cells.add(new jxl.write.Label(1, i + 1, StringUtils.join(", ", regions.get(i).getCountries())));
				}

				for (WritableCell cell : cells)
				{
					sheet.addCell(cell);
				}

				workbook.write();
				workbook.close();

				return new State<TState>(TState.Success);
			}
			catch (IOException | WriteException e)
			{
				Log.write(e);

				return new State<TState>(TState.Error, new String[] { "Writing Error", "An error occurred while trying to save " + file.getName() + ". Do you want to try again?" });
			}
		}
	}

	private String[] trimAll(String[] strings)
	{
		for (int i = 0; i < strings.length; i++)
		{
			strings[i] = strings[i].trim();
		}

		return strings;
	}
	
	
	public class dateParser {   
		
		private String hour;
		private String day;
		private String month;
		private String year;
		
		   
		public dateParser(String hour, String day, String month, String year) {
			super();
			this.hour = hour;
			this.day = day;
			this.month = month;
			this.year = year;
		}
		
		
		
		
		public boolean clearMDResult (dateParser now, dateParser processor, Shift shift) {
			if (Integer.valueOf(now.getYear()) < Integer.valueOf(processor.getYear())){
				return true;
			}
			else {
				if(Integer.valueOf(now.getMonth()) < Integer.valueOf(processor.getMonth())){
					return true;
				}
				else {
					if (Integer.valueOf(now.getDay()) < Integer.valueOf(processor.getMonth())){
						return true;
					}
					else {
						if(Integer.valueOf(now.getDay()) < Integer.valueOf(processor.getDay())){
							return true;
						}
						else {
							return false;
						}

					}
					
				}
			}
		}
		
		
		
		public String getHour() {
			return hour;
		}
		public void setHour(String hour) {
			this.hour = hour;
		}
		public String getDay() {
			return day;
		}
		public void setDay(String day) {
			this.day = day;
		}
		public String getMonth() {
			return month;
		}
		public void setMonth(String month) {
			this.month = month;
		}
		public String getYear() {
			return year;
		}
		public void setYear(String year) {
			this.year = year;
		}
		
		  
		 
		}  
}