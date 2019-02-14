package osu.beatmap;

import org.junit.Test;

import osu.beatmap.hitobject.HitObject;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;

public class TestBeatmap {
	
	private static final String beatmap = "testBeatmap.osu";
	
	@Test
	public void Test_Clear_HS(){
		try {
			Beatmap b =  new Beatmap(new File(beatmap));
			b.clearHitsounds();
			assertEquals(b.getEventSection().getSamples().size(),0);
			for (HitObject ho : b.getHitObjectSection().getHitObjects()) {
				assertEquals(ho.hasHitsound(),false);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	
	@Test
	public void Test_Read_Beatmap_From_File() throws ParseException, IOException {
		File f = new File(beatmap);
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
		Beatmap b =  new Beatmap(f);
		assertEquals(text, b.toString());
	}
	
}
