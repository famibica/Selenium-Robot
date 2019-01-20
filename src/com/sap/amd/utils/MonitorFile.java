package com.sap.amd.utils;
import java.io.File;  
import java.io.FileFilter;  

import javax.swing.JProgressBar;
  
public class MonitorFile implements Runnable {  
  
    private Thread threadMonitor;  
	private static String folder;
	private static String fileFinalType;
	private static JProgressBar progressBarExporting;

	public MonitorFile(String folder, String fileFinalType, JProgressBar progressBarExporting) {
		super();
		this.folder = folder;
		this.fileFinalType = fileFinalType;
		this.progressBarExporting = progressBarExporting; 
	}

    public static void main(String[] args) {  
        new MonitorFile(getFolder(), getFileFinalType(), progressBarExporting).init();  
    }  
    
    private void init() {  
        setFolder(folder);  
        this.threadMonitor = new Thread(this);  
        this.threadMonitor.start();  
    }  
      
    private void verifyFolder(String Folder) {  
        File folderReader = new File(Folder);  
        File arquivos[] = folderReader.listFiles(new FileFilter() {  
            public boolean accept(File pathname) {  
                return pathname.getName().toLowerCase().endsWith(getFileFinalType());  
            }  
        });  
        for (int i = 0; i < arquivos.length; i++) {    
            File file = arquivos[i];  
            newFile(file.getName());  
        }    
    }  
  
    public void newFile(String file) {  
        int value =  getProgressBarExporting().getMinimum();
        getProgressBarExporting().setValue(value + 1);
        value ++;
    }  
  
    @Override  
    public void run() {  
        Thread currentThread = Thread.currentThread();  
        while (this.threadMonitor == currentThread) {  
            verifyFolder(getFolder());  
            try {  
                Thread.sleep(1000 * 10); // 10 seconds 
            } catch (InterruptedException e) {  
            	Log.write("An error ocurred on monitor file thread... \n" + e);
            }  
        }  
    }  
  
    public static String getFolder() {  
        return folder;  
    }  
  
    public void setFolder(String newFolder) {  
        this.folder = newFolder;  
    }

	public static String getFileFinalType() {
		return fileFinalType;
	}

	public void setFileFinalType(String fileFinalType) {
		this.fileFinalType = fileFinalType;
	}  
	
    public static JProgressBar getProgressBarExporting() {
		return progressBarExporting;
	}

	public void setProgressBarExporting(JProgressBar progressBarExporting) {
		this.progressBarExporting = progressBarExporting;
	}
  
}  