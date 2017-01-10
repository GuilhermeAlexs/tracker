package utils;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Properties;

public class NetworkUtils {
	public static void useProxy(String address, String port, String user, String password){
		Properties systemSettings = System.getProperties();
		systemSettings.put("http.proxyHost", address);
		systemSettings.put("http.proxyPort", port);
		systemSettings.put("https.proxyHost", address);
		systemSettings.put("https.proxyPort", port);
	
		Authenticator.setDefault(
		   new Authenticator() {
		      @Override
		      public PasswordAuthentication getPasswordAuthentication() {
		         return new PasswordAuthentication(
		        		 user, password.toCharArray());
		      }
		   }
		);

		System.setProperty("http.proxyUser", user);
		System.setProperty("http.proxyPassword", password);
		System.setProperty("https.proxyUser", user);
		System.setProperty("https.proxyPassword", password);
	}
}
