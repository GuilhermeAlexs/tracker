package utils;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Properties;

public class NetworkUtils {
	public static String authUser = "c1278491";
	public static String authPassword = "91769457";
	
	public static void useProxy(){
		Properties systemSettings = System.getProperties();
		systemSettings.put("http.proxyHost", "localhost");
		systemSettings.put("http.proxyPort", "40080");

		Authenticator.setDefault(
		   new Authenticator() {
		      @Override
		      public PasswordAuthentication getPasswordAuthentication() {
		         return new PasswordAuthentication(
		               authUser, authPassword.toCharArray());
		      }
		   }
		);

		System.setProperty("http.proxyUser", authUser);
		System.setProperty("http.proxyPassword", authPassword);
	}
}
