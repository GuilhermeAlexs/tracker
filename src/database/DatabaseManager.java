package database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import model.Configurations;
import model.StretchType;
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
	
	public void saveConfigurations(Configurations conf){
		try (
			ObjectOutputStream oos =
				new ObjectOutputStream(new FileOutputStream("database/tracker.conf"))) {

			oos.writeObject(conf);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public Configurations loadConfigurations(){
		Configurations conf = null;

		File file = new File("database/tracker.conf");

		if(!file.exists())
			return null;

		try (
			ObjectInputStream ois
			= new ObjectInputStream(new FileInputStream("database/tracker.conf"))) {

			conf = (Configurations) ois.readObject();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return conf;
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
	
	public void renameTrail(String oldName, String newName){
		String names = infoProperties.getProperty("files");
		names = names.replace(oldName, newName);
		File f = new File("database/" + oldName + ".mtl");
		f.renameTo(new File("database/" + newName + ".mtl"));
		infoProperties.put("files", names);
		
		saveDatabaseInfo();
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
		
		if(listNames.contains(name))
			listNames = listNames.replaceAll(name + ";", "");
		
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
	
	public void saveStretchTypes(Map<String, StretchType> types){
		try (
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("database/types.sty"))) {
			oos.writeObject(types);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, StretchType> loadStretchTypes(){	
		Map<String, StretchType> types = null;

		File file = new File("database/types.sty");

		if(file.exists()){
			try (
				ObjectInputStream ois
				= new ObjectInputStream(new FileInputStream(file))) {
	
				types = (Map<String, StretchType>) ois.readObject();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return types;
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
		
		if(listNames == null || listNames.equals(""))
			return null;
		
		String [] names = listNames.split(";");
		
		return names;
	}
}
