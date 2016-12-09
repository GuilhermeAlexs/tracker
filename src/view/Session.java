package view;
import java.io.File;
import java.util.List;

import de.micromata.opengis.kml.v_2_2_0.Kml;
import model.TPLocation;

public class Session {
	public static Kml currentKML;
	public static File currentSourceFile;
	public static List<TPLocation> currentTrail;
}
