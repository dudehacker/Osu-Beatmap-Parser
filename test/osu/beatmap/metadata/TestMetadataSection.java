package osu.beatmap.metadata;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.regex.Pattern;

import org.junit.Test;

import osu.beatmap.Beatmap;

public class TestMetadataSection {
	
	@Test
	public void TestReadBeatmapFromFile() throws ParseException, IOException {
		File f = new File("testBeatmap.osu");
		String text = "";
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"))) {
			String line;
			// read line by line
			while ((line = br.readLine()) != null) {
				text += line + System.lineSeparator();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String split1 = text.split(Pattern.quote("[Metadata]" + System.lineSeparator()))[1];
		String split2 = System.lineSeparator() + split1.split(Pattern.quote("[Difficulty]"))[0];
		
		Beatmap b = new Beatmap(f);
		assertEquals("[Metadata]" + split2, b.getMetadataSection().toString() + System.lineSeparator());
	}
}
