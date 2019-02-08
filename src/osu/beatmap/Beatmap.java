package osu.beatmap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import osu.beatmap.difficulty.DifficultySection;
import osu.beatmap.editor.EditorSection;
import osu.beatmap.event.EventSection;
import osu.beatmap.event.Sample;
import osu.beatmap.general.GeneralSection;
import osu.beatmap.hitobject.HitObject;
import osu.beatmap.hitobject.HitObjectSection;
import osu.beatmap.metadata.MetadataSection;
import osu.beatmap.timing.TimingSection;

public final class Beatmap {

	// Instance Variables
	private int OSU_VERSION = 14;

	private List<Section> sections = new ArrayList<>();

	private static final String NEW_LINE = BeatmapUtils.nl;

	public Beatmap() {
		sections.add(new GeneralSection());
		sections.add(new EditorSection());
		sections.add(new MetadataSection());
		sections.add(new DifficultySection());
		sections.add(new EventSection());
		sections.add(new TimingSection());
		sections.add(new HitObjectSection());
	}

	public Beatmap(File file) throws ParseException, IOException {

		this();
		if (!file.exists() || !file.getAbsolutePath().endsWith(".osu")) {
			throw new IllegalArgumentException("Can't open file " + file.getAbsolutePath());
		}

		String text = "";
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
			String line;
			// read line by line
			while ((line = br.readLine()) != null) {
				text += line + System.lineSeparator();

			}
			if (text.contains("osu file format")) {
				OSU_VERSION = Integer.parseInt(text.split(System.lineSeparator())[0].split("osu file format v")[1]);
			}

			// separate to sections
			for (int i = 0; i < sections.size(); i++) {
				Section section1 = sections.get(i);
				Section section2 = null;
				if (i != sections.size() - 1) {
					section2 = sections.get(i + 1);
				}
				String sectionText = getSectionText(text, section1, section2);
				sections.get(i).init(sectionText.split(System.lineSeparator()));
			}
			
			// Set up HitObject hs based on timing points
			getHitObjectSection().addTimingHitsound(getTimingSection());

		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		} 
	}

	public GeneralSection getGeneralSection() {
		return (GeneralSection) sections.get(0);
	}

	public EditorSection getEditorSection() {
		return (EditorSection) sections.get(1);
	}

	public MetadataSection getMetadataSection() {
		return (MetadataSection) sections.get(2);
	}

	public DifficultySection getDifficultySection() {
		return (DifficultySection) sections.get(3);
	}

	public EventSection getEventSection() {
		return (EventSection) sections.get(4);
	}

	public TimingSection getTimingSection() {
		return (TimingSection) sections.get(5);
	}

	public HitObjectSection getHitObjectSection() {
		return (HitObjectSection) sections.get(6);
	}

	private String getSectionText(String text, Section section1, Section section2) {
		String split1 = text.split(Pattern.quote(section1.getHeader() + System.lineSeparator()))[1];
		if (section2 == null) {
			return split1;
		}
		return split1.split(Pattern.quote(section2.getHeader()))[0];
	}
	
	public Map<Long, Chord> getChords(){
		Map<Long, Chord> output = new TreeMap<>();
		for (Sample sample : getEventSection().getSamples()) {
			Chord chord = null;
			if (!output.containsKey(sample.getStartTime())) {
				chord = new Chord();
				output.put(sample.getStartTime(), chord);
			} else {
				chord = output.get(sample.getStartTime());
			}
			chord.add(sample);
		}

		for (HitObject ho : getHitObjectSection().getHitObjects()) {
			Chord chord = null;
			if (!output.containsKey(ho.getStartTime())) {
				chord = new Chord();
				output.put(ho.getStartTime(), chord);
			} else {
				chord = output.get(ho.getStartTime());
			}
			chord.add(ho);
		}
		return output;
		
	}
	
	public Beatmap clearHitsounds() {
		getEventSection().getSamples().clear();
		getHitObjectSection().clearHitsound();
		return this;
	}

	@Override
	public String toString() {

		String output = "osu file format v" + OSU_VERSION + NEW_LINE;
		for (Section section : sections) {
			output += NEW_LINE + section;
		}
		return output;
	}
	
	public void exportBeatmap(File file) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))){
			writer.write(toString());
		} 
	}
	
	public void copyHS(Beatmap source, boolean isKeysound) {
		getTimingSection().copyTimings(source);
		copyChords(source,isKeysound);
	}
	
	private void copyChords(Beatmap source, boolean isKeysound){
		Map<Long,Chord> targetChords = getChords();
		Map<Long,Chord> sourceChords = source.getChords();
		if (isKeysound){
			for (Map.Entry<Long, Chord> entry : sourceChords.entrySet())
			{
				Chord chord = targetChords.get(entry.getKey());
				if (chord != null) {
					chord.copyHitsound(entry.getValue(), isKeysound);
				}
			}
		} else {
			for (Map.Entry<Long, Chord> entry : targetChords.entrySet())
			{
				Chord chord = sourceChords.get(entry.getKey());
				if (chord != null) {
					entry.getValue().copyHitsound(chord, isKeysound);
				}
			}
		}
	}

	
}
