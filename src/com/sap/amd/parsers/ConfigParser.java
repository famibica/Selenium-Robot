package com.sap.amd.parsers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import com.sap.amd.dispatcher.ConfigIDs;
import com.sap.amd.utils.Log;

public class ConfigParser {
	
	private File file;
	
	public ConfigParser() {
		this.file = new File(".\\resources\\ConfigIds.xls");
	}
	
	public boolean verifyFile(){
		Log.write("Verify if the file to parse exist...");
		if (!file.exists()){
			creatingDefaultSheet();
			return false;
		}
		else {
			Log.write("The file already exists!");
			return true;
		}
	}
	
	public void creatingDefaultSheet(){
		try{
			Log.write("Creating the file...");
			WritableWorkbook workbook = Workbook.createWorkbook(file);
			List<WritableCell> cells = new LinkedList<WritableCell>();

			//Authorized  Sheet
			workbook.createSheet("Config Ids", 0);
			WritableSheet sheet = workbook.getSheet(0);
			sheet = workbook.getSheet(0);

			cells.add(new jxl.write.Label(0, 0, "SHARED SEARCH ID"));
			cells.add(new jxl.write.Label(1, 0, "SAVED  SEARCH ID"));
			cells.add(new jxl.write.Label(2, 0, "BUTTON EXPORT EXCEL"));
			cells.add(new jxl.write.Label(3, 0, "BUTTON EDIT"));
			cells.add(new jxl.write.Label(4, 0, "BUTTON SAVE"));
			cells.add(new jxl.write.Label(5, 0, "PROCESSOR TEXT"));
			cells.add(new jxl.write.Label(6, 0, "SERVICE TEXT"));

				for (WritableCell cell : cells){
					sheet.addCell(cell);
				}
				
			cells.clear();
			workbook.write();
			workbook.close();
		}
		catch (IOException | WriteException e){
			Log.write(e);
		}
		Log.write("file created");
	}
		
	
	public List<ConfigIDs> parseIds()
	{
		List<ConfigIDs> config = new LinkedList<ConfigIDs>();
		
		if (verifyFile() == true){
			Log.write("Parsing Ids from file");
	
	
			try
			{
				FileInputStream stream = new FileInputStream(file);
				Workbook workbook = Workbook.getWorkbook(stream);
				Sheet sheet = workbook.getSheet(0);
	
				int rows = sheet.getRows();
	
				int ignored = 0;
				int parsed = 0;
	
	
				for (int i = 1; i < rows; i++)
				{
					try
					{
						Cell sharedSearchId = sheet.getCell(0, i);
						Cell savedSearchId = sheet.getCell(1, i);
						Cell buttonExportExcel = sheet.getCell(2, i);
						Cell buttonEdit = sheet.getCell(3, i);
						Cell buttonSave = sheet.getCell(4, i);
						Cell currentProcessorText = sheet.getCell(5, i);
						Cell serviceTeamText = sheet.getCell(6, i);
						
						ConfigIDs id = new ConfigIDs();
	
						id.setSharedSearchesIDArea(sharedSearchId.getContents());
						id.setSavedSearchesIDArea(savedSearchId.getContents());
						id.setButtonExportExcel(buttonExportExcel.getContents());
						id.setButtonEdit(buttonEdit.getContents());
						id.setButtonSave(buttonSave.getContents());
						id.setCurrenteProcessorText(currentProcessorText.getContents());
						id.setServiceTeamText(serviceTeamText.getContents());
	
						config.add(id);
						parsed++;
					}
					catch (Exception e)
					{
						ignored++;
	
						Log.write("A Config ID has been ignored due to the following exception:");
						Log.write(e);
					}
				}
	
				workbook.close();
				stream.close();
	
				Log.write(parsed + " out of " + (rows - 1) + " Config(s) parsed, " + ignored + " Config(s) ignored.");
			}
			catch (Exception e)
			{
				Log.write(e);
			}
			return config;
		}
		else {
			return null;
		}
	}
}
