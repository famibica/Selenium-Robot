package com.sap.amd.importers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.sap.amd.Program;
import com.sap.amd.bcpandicp.Incident;
import com.sap.amd.bcpandicp.IncidentICP;
import com.sap.amd.bcpandicp.Region;
import com.sap.amd.dispatcher.Button;
import com.sap.amd.dispatcher.ConfigIDs;
import com.sap.amd.dispatcher.Context;
import com.sap.amd.dispatcher.Link;
import com.sap.amd.parsers.IncidentParser;
import com.sap.amd.utils.Log;
import com.sap.amd.utils.State;
import com.sap.amd.utils.types.TImportState;
import com.sap.amd.utils.types.TStream;

public class IncidentImporter implements Serializable {
	private static final long serialVersionUID = -769579144059946511L;

	private WebDriver driver;

	private State<TImportState> state;
	private List<Incident> resultBCP;
	private List<IncidentICP> resultICP;
	private boolean isBCP;

	private String search;
	private String folder;

	private File profileFolder;
	private int timeout;
	private ConfigIDs IDs;

	public IncidentImporter(String search, int timeout, ConfigIDs IDs, boolean isBCP) {
		this.search = search;
		this.folder = ".\\temp";
		this.timeout = timeout;
		this.IDs = IDs;
		this.isBCP = isBCP;
	}

	public IncidentImporter(String search, String folder, int timeout, ConfigIDs IDs, boolean isBCP) {
		this.search = search;
		this.folder = folder;
		this.timeout = timeout;
		this.IDs = IDs;
		this.isBCP = isBCP;
	}

	private boolean setFolder() {
		try {
			File folder = new File(this.folder);

			if (folder.exists()) {
				for (File file : folder.listFiles()) {
					if (file.getName().equals("export.csv")) {
						file.delete();
					}
				}

				this.folder = folder.getCanonicalPath();

				return true;
			} else {
				folder.mkdir();
				this.folder = folder.getCanonicalPath();
				return true;
			}
		} catch (Exception e) {
			Log.write(e);

			return false;
		}
	}

	public void importIncidents() {
		this.state = new State<TImportState>(0, TImportState.Importing, new String[] { "Starting process�" });
		if (isBCP == true) {
			Log.write("Importing incidents from BCP...");
		} else {
			Log.write("Importing incidents from ICP...");
		}

		if (setFolder()) {
			profileFolder = new File(System.getenv().get("APPDATA") + "\\Mozilla\\Firefox\\Profiles");
			profileFolder = getDefaultProfileFolder(profileFolder.listFiles());

			if (profileFolder != null) {
				this.state = new State<TImportState>(10, TImportState.Importing, new String[] { "Preparing Firefox�" });

				Log.write("Setting up profile, and opening Firefox...");

				changeMimeTypes();

				FirefoxProfile profile = new FirefoxProfile(profileFolder);
				profile.setPreference("browser.download.folderList", 2);
				profile.setPreference("browser.download.dir", folder);
				profile.setPreference("security.default_personal_cert", "Select Automatically");

				try {
					driver = new FirefoxDriver(profile);
				} catch (Exception e) {
					Log.write("Firefox could not be opened. Either you do not have it installed, or it is out of date.");
					return;
				}

				/* TODO if (!Program.getArguments("firefox").contains("displayWindow")) {
					Dimension dimension = driver.manage().window().getSize();
					driver.manage().window().setPosition(new Point(-10 - dimension.height, -10 - dimension.width));
				}*/

				Context workArea1 = null;
				Context workArea2 = null;
				if (isBCP == true) {
					Log.write("Accessing BCP...");
					driver.get("https://support.wdf.sap.corp/sap/crm_logon/default.htm?saprole=ZCSSNEXTPROC");

					Log.write("Creating areas and elements...");

					workArea1 = new Context(new String[] { "CRMApplicationFrame", "WorkAreaFrame1" },
							driver.getWindowHandle());
					workArea2 = new Context(new String[] { "CRMApplicationFrame", "WorkAreaFrame2" },
							driver.getWindowHandle());
				} else {
					Log.write("Accessing ICP...");
					driver.get("https://icp.wdf.sap.corp/sap/crm_logon/default.htm?saprole=ZSU_CIM");
				}

				// Initializing Links and Buttons
				Link link = null;
				Button export = null;

				// If it's a BCP search
				if (isBCP == true) {
					link = new Link(search, workArea1, driver);

					export = new Button(IDs.getButtonExportExcel(), workArea1, driver);

					Log.write("Acessing Search");

					link.updateContext(new Context[] { workArea1, workArea2 });
					
				} else { // If it's a ICP search

					// Search for the Search in the searches
					new WebDriverWait(driver, 30)
							.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("CRMApplicationFrame"));
					Log.write("Accessing first CRMApplicationFrame");

					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					new WebDriverWait(driver, 30).until(ExpectedConditions
							.frameToBeAvailableAndSwitchToIt(By.cssSelector("iframe[id*='s_001_ZSU_CIM']")));
					Log.write("Accessing s_001_ZSU_CIM_...");
					new WebDriverWait(driver, 30)
							.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("CRMApplicationFrame"));
					Log.write("Accessing second CRMApplicationFrame");
					new WebDriverWait(driver, 30)
							.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("FRAME_APPLICATION"));
					Log.write("Accessing FRAME_APPLICATION");
					new WebDriverWait(driver, 30)
							.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("WorkAreaFrame1"));
					Log.write("Accessing WorkAreaFrame1");

					Log.write("Clicking at GO button");
					driver.findElement(By.id(IDs.getButtonEdit())).click();

					try {
						Thread.sleep(40000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					try {
						Log.write("Clicking on export button");
						driver.findElement(By.id(IDs.getButtonExportExcel())).click();
					} catch (Exception e) {

						try {
							Thread.sleep(30000);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

						Log.write("First Click failed, Clicking on export button");
						driver.findElement(By.id(IDs.getButtonExportExcel())).click();
					}

					this.state = new State<TImportState>(55, TImportState.Importing,
							new String[] { "Accessing search�" });

				}

				if (isBCP) {

					try {
						link.changeTimeout(10);
						link.click(false);
					} catch (Exception e) {
						Log.write("Selected search '" + search + "' could not be found in BCP.");
						return;
					}

					export.updateContext(new Context[] { workArea1, workArea2 });
					export.click(false);
				}

				Log.write("Exporting Incidents");
				Log.write("Waiting for file to be downloaded and available...");

				File file = new File(folder + "\\export.csv");
				int tries = 0;

				if (isBCP) {
					while (!(file.exists() && file.canExecute())) {
						if (tries > timeout) {
							driver.quit();

							restoreMimeTypes();

							Log.write("Exported file '" + file.getName() + "' could not be found.");

							return;
						}

						try {
							Thread.sleep(1000);

						} catch (InterruptedException e) {
						}

						tries++;
					}
				} else {
					while (!(file.exists() && file.canExecute())) {
						if (tries > 40) {
							driver.quit();

							restoreMimeTypes();

							Log.write("Exported file '" + file.getName() + "' could not be found.");

							return;
						}

						try {
							Thread.sleep(15000);
						} catch (InterruptedException e) {
						}

						tries++;
					}
					timeout = 120;
				}

				File part = new File(folder + "\\export.csv.part");
				tries = 0;

				while (part.exists()) {
					if (tries > timeout) {
						driver.quit();

						restoreMimeTypes();

						Log.write("Integrity of exported file '" + file.getName() + "' cannot be assured.");

						return;
					}

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}

					tries++;
				}

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}

				driver.quit();

				Log.write("File saved as " + folder + "\\export.csv");

				Log.write("Importing incidents�");

				IncidentParser parser = new IncidentParser(file);

				if (isBCP == true) {
					this.resultBCP = parser.parseBCP(TStream.CSV);
				} else {
					this.resultICP = parser.parseICP(TStream.CSV);
				}

				restoreMimeTypes();
				removeTemporaryFiles();

				Log.write("Import process finished!");
			} else {
				Log.write("Firefox profile could not be found.\n\nMake sure you are not using a portable version of Firefox, and it is up-to-date.");
			}
		} else

		{
			Log.write("Folder could not be created");

			Log.write("Folder Error, Selected folder was not found, and could not be created.");
		}
	}

	public List<Incident> getResult() {
		if (resultBCP != null) {
			return resultBCP;
		} else {
			return new LinkedList<Incident>();
		}
	}

	public List<IncidentICP> getResultICP() {
		if (resultICP != null) {
			return resultICP;
		} else {
			return new LinkedList<IncidentICP>();
		}
	}

	private void removeTemporaryFiles() {
		Log.write("Removing temporary files...");
		File folder = new File(this.folder);

		if (folder.exists()) {
			for (File file : folder.listFiles()) {
				file.delete();
			}

			folder.delete();
		}
	}

	private File getDefaultProfileFolder(File[] folders) {
		for (File file : folders) {
			if (file.getName().endsWith(".default")) {
				return file;
			}
		}

		return null;
	}

	private void restoreMimeTypes() {
		Log.write("Restoring Mime Types...");

		File mimeTypes = getFile("mimeTypes.rdf", profileFolder);

		if (mimeTypes != null) {
			mimeTypes.delete();
		}

		mimeTypes = getFile("mimeTypes.rdf.bkp", profileFolder);

		if (mimeTypes != null) {
			mimeTypes.renameTo(new File(profileFolder.getPath() + "\\mimeTypes.rdf"));
		}
	}

	private void changeMimeTypes() {
		Log.write("Changing Mime Types...");

		File mimeTypes = getFile("mimeTypes.rdf", profileFolder);

		if (mimeTypes != null) {
			mimeTypes.renameTo(new File(profileFolder.getPath() + "\\mimeTypes.rdf.bkp"));
		}

		try {
			InputStream inputStream = IncidentImporter.class
					.getResourceAsStream("/com/sap/amd/importers/resources/mimeTypes.rdf");

			byte[] bytes;
			FileWriter writer;

			bytes = new byte[inputStream.available()];
			inputStream.read(bytes);

			String mt = new String(bytes);

			writer = new FileWriter(profileFolder.getPath() + "\\mimeTypes.rdf");
			writer.write(mt);
			writer.flush();
			writer.close();

		} catch (IOException e) {
			Log.write(e);
		}
	}

	private File getFile(String filename, File folder) {
		for (File file : folder.listFiles()) {
			if (file.getName().equals(filename)) {
				return file;
			}
		}

		return null;
	}

	public State<TImportState> getState() {
		return state;
	}
}
