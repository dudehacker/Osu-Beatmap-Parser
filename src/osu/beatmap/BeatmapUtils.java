package osu.beatmap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import osu.beatmap.hitobject.HitObject;

public class BeatmapUtils {

	// Constants
	public static final String nl = System.getProperty("line.separator");
	// private static int SUPPORTED_OSU_FILE_VERSION=14;
	public static final String defaultOsuPath = "C:/Program Files (x86)/osu!/Songs";
	public static final String startPath = System.getProperty("user.dir");

	public static String doubleToIntString(Double doubleValue) {
		return Double.toString(doubleValue).split(Pattern.quote(".0"))[0];
	}

	public static String convertListToString(List<?> al) {
		if (al == null)
			return "";
		String output = "";
		for (Object o : al) {
			output += o.toString() + nl;
		}
		return output;
	}

	public static List<Long> getDistinctStartTime(List<HitObject> hitObjects, List<HitObject> hitObjects2) {
		List<Long> output = new ArrayList<>();
		long t = -1;
		for (HitObject ho : hitObjects) {
			long startTime = ho.getStartTime();
			if (startTime != t) {
				t = startTime;
				output.add(t);
			}
		}
		t = -1;
		for (HitObject ho : hitObjects2) {
			long startTime = ho.getStartTime();
			if (startTime != t) {
				t = startTime;
				if (!output.contains(t)) {
					output.add(t);
				}

			}
		}
		return output;
	}

	public static File getOsuFile(String path) {
		File f = null;
		FileFilter filter = new FileNameExtensionFilter("OSU file", "osu");
		final JFileChooser jFileChooser1 = new JFileChooser(path);
		jFileChooser1.addChoosableFileFilter(filter);
		jFileChooser1.setFileFilter(filter);
		// Open details
		Action details = jFileChooser1.getActionMap().get("viewTypeDetails");
		details.actionPerformed(null);
		int returnVal = jFileChooser1.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			f = jFileChooser1.getSelectedFile();
		}
		return f;
	}

	public static List<HitObject> getChordByTime(List<HitObject> hitObjects, long startTime) {
		List<HitObject> output = new ArrayList<HitObject>();
		for (HitObject ho : hitObjects) {
			if (ho.getStartTime() == startTime) {
				output.add(ho.clone());
			}
		}
		output = sortChordByHSType(output);
		return output;
	}

	public static List<HitObject> sortChordByHSType(List<HitObject> hitObjects) {
		List<HitObject> output = new ArrayList<HitObject>();
		List<HitObject> wavHS = new ArrayList<HitObject>();
		List<HitObject> defaultHS = new ArrayList<HitObject>();
		for (HitObject ho : hitObjects) {
			if (ho.hasCustom_HS()) {
				wavHS.add(ho.clone());
			} else {
				if (ho.hasDefault_HS()) {
					output.add(ho.clone());
				} else {
					defaultHS.add(ho);
				}

			}
		}
		for (HitObject wav : wavHS) {
			output.add(wav);
		}
		for (HitObject d : defaultHS) {
			output.add(d);
		}
		return output;
	}

	public static int getChordSizeForTime(ArrayList<HitObject> hitObjects, long startTime) {
		int size = 0;
		for (HitObject ho : hitObjects) {
			if (ho.getStartTime() == startTime) {
				size++;
			}
		}
		return size;
	}

	public static int getDefaultHSChordSizeForTime(List<HitObject> hitObjects, long startTime) {
		int size = 0;
		for (HitObject ho : hitObjects) {
			if (ho.getStartTime() == startTime && ho.hasDefault_HS()) {
				size++;
			}
		}
		return size;
	}

}
