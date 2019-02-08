package osu.beatmap.timing;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import osu.beatmap.Beatmap;
import osu.beatmap.BeatmapUtils;
import osu.beatmap.Section;
import osu.beatmap.hitobject.SampleSet;

public final class TimingSection extends Section {

	private List<Timing> timings;

	public TimingSection() {
		super("[TimingPoints]");
		timings = new ArrayList<>();
	}

	@Override
	public void init(String[] lines) {
		for (String line : lines) {
			if (line.contains(",")) {
				String[] parts = line.split(",");
				if (parts[0].contains(".")) {
					parts[0] = parts[0].substring(0, parts[0].indexOf('.'));
				}
				long offset = Long.parseLong(parts[0]);
				double mspb = Double.parseDouble(parts[1]);
				int meter = Integer.parseInt(parts[2]);
				SampleSet sampleSet = SampleSet.createSampleSet(Integer.parseInt(parts[3]));
				int setID = Integer.parseInt(parts[4]);
				int volume = Integer.parseInt(parts[5]);
				int isInherited = Integer.parseInt(parts[6]);
				int isKiai = Integer.parseInt(parts[7]);
				Timing timing = new Timing(offset, mspb, meter, sampleSet, setID, volume, isInherited, isKiai);
				timings.add(timing);
			}
		}
	}

	@Override
	public final String toString() {
		return getHeader() + BeatmapUtils.nl + BeatmapUtils.convertListToString(timings);
	}

	public List<Timing> getTimingPoints() {
		return timings;
	}

	public void copyTimings(Beatmap source) {

		// only copy useful timing for default hitsounds to target difficulty
		List<Timing> sourceTimings = source.getTimingSection().getTimingPoints();
		Timing t1, t2;
		for (Timing t : timings) {
			for (int i = 0; i < sourceTimings.size() - 1; i++) {
				if (i < sourceTimings.size() - 1) {
					t1 = sourceTimings.get(i);
					t2 = sourceTimings.get(i + 1);
					if (t1.getOffset() <= t.getOffset() && t.getOffset() < t2.getOffset()) {
						t.copyHitsound(t1);
						break;
					}
				} else {
					// last timing
					t.copyHitsound(sourceTimings.get(i));
				}
			}
		}

		// add timings that exist in HS but not in target
		Set<Long> offsets = new TreeSet<>();

		for (Timing t_target : timings) {
			offsets.add(t_target.getOffset());
		}
		
		for (Timing t_source : sourceTimings) {
			if (!offsets.contains(t_source.getOffset())) {
				timings.add(t_source);
			}
		}

		timings.sort(Timing.StartTimeComparator);
	}
}
