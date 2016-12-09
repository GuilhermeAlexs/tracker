package database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Properties;

import model.TPLocation;

public class DatabaseManager {
	private static DatabaseManager INSTANCE;
	
	private Properties infoProperties;
	
	private DatabaseManager(){
		this.infoProperties = loadDatabaseInfo();
	}
	
	public static DatabaseManager getInstance(){
		if(INSTANCE == null)
			INSTANCE = new DatabaseManager();
		
		return INSTANCE;
	}
	
	private void saveDatabaseInfo(){
		try (
			ObjectOutputStream oos =
				new ObjectOutputStream(new FileOutputStream("database/tp.info"))) {

			oos.writeObject(infoProperties);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private Properties loadDatabaseInfo(){
		Properties info = null;
		
		File file = new File("database/tp.info");
		
		if(!file.exists()){
			return new Properties();
		}
		
		try (
			ObjectInputStream ois
			= new ObjectInputStream(new FileInputStream("database/tp.info"))) {

			info = (Properties) ois.readObject();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return info;
	}
	
	private void insertFileEntry(String name){		
		String listNames = infoProperties.getProperty("files");
		
		if(listNames == null)
			listNames = "";
		
		listNames = listNames.concat(name + ";");
		
		infoProperties.setProperty("files", listNames);
		saveDatabaseInfo();
	}
	
	private void deleteFileEntry(String name){
		String listNames = infoProperties.getProperty("files");
		listNames = listNames.replace(name + ";", "");
		
		infoProperties.setProperty("files", listNames);
		saveDatabaseInfo();
	}

	public boolean contains(String name){
		String files = infoProperties.getProperty("files");
		
		if(files == null || !files.contains(name))
			return false;
		
		return true;
	}
	
	public void insert(String name, List<TPLocation> trail){
		try (
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("database/" + name + ".mtl"))) {
			oos.writeObject(trail);
			
			insertFileEntry(name);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void delete(String name){
		try {
			File f = new File("database/" + name + ".mtl");
			f.delete();
			
			deleteFileEntry(name);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<TPLocation> load(String name) {
		List<TPLocation> trail = null;
	
		File file = new File("database/" + name + ".mtl");
		
		if(!file.exists())
			return null;
		
		try (
			ObjectInputStream ois
			= new ObjectInputStream(new FileInputStream(file))) {

			trail = (List<TPLocation>) ois.readObject();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return trail;
	}

	public String [] getAllTrailsNames(){
		String listNames = infoProperties.getProperty("files");
		String [] names = listNames.split(";");
		
		return names;
	}
}
