package osu.beatmap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import osu.beatmap.event.Sample;
import osu.beatmap.hitobject.HitObject;
import osu.beatmap.hitobject.HitsoundType;

public class Chord {
	private ArrayList<HitObject> list_HO;
	private ArrayList<Sample> list_SB;

	private long startTime;

	public Chord() {
		list_HO = new ArrayList<>();
		list_SB = new ArrayList<>();
		startTime = -1;
	}
	
	public Chord removeDuplicateHS(){
		Set<String> set = new HashSet<>();
		Iterator<HitObject> ite = list_HO.iterator();
		while (ite.hasNext()){
			HitObject ho = ite.next();
			for (String s : ho.toHitsoundString()){
				if (set.contains(s)){
					ite.remove();
				} else {
					set.add(s);
				}
			}
		}
		return this;
	}

	public Chord copyHitsound(Chord sourceChord, boolean copySB) {
		sourceChord.removeDuplicateHS();
		Collections.sort(sourceChord.list_HO,HitObject.HitsoundComparator);
		int sourceSize = sourceChord.list_HO.size();
		int targetSize = list_HO.size();

		if (copySB) {
			list_SB.addAll(sourceChord.list_SB);
		}

		// CASE 1
		if (sourceSize == targetSize) {
			for (int i = 0; i < targetSize; i++) {
				HitObject sourceHO = sourceChord.list_HO.get(i);
				HitObject targetHO = list_HO.get(i);
				targetHO.copyHS(sourceHO);
			}

			// CASE 2
		} else if (sourceSize > targetSize) {
			if (targetSize == 0) {
				if (copySB) {
					for (int j = 0; j < sourceSize; j++) {
						HitObject sourceHO = sourceChord.list_HO.get(j);
						if (sourceHO.hasHitsound()) {
							list_SB.addAll(sourceHO.toSample());
						}
					}

				}

			} else {
				int defaultHitSoundSize = sourceChord.getDefaultHitsoundSize();
				switch (defaultHitSoundSize) {
				case 0:
				case 1:
					System.out.println("source size 0|1 at " + startTime);
					for (int i = 0; i < targetSize; i++) {
						HitObject source_ho = sourceChord.list_HO.get(i);
						HitObject target_ho = list_HO.get(i);
						target_ho.copyHS(source_ho);
					}
					for (int j = targetSize; j < sourceSize; j++) {
						HitObject source_ho = sourceChord.list_HO.get(j);
						list_SB.addAll(source_ho.toSample());
					}
					break;

				case 2: // Combine both default hitsounds into 1 HitObject
					// System.out.println("source size 2 at " + startTime);
					combineDefaultHS(sourceChord.list_HO, 2);
					break;

				case 3:
					System.out.println("source size 3 at " + startTime);
					// System.out.println("target size " + targetSize);
					if (targetSize >= 2) {
						combineDefaultHS(sourceChord.list_HO, 2);
					} else {
						combineDefaultHS(sourceChord.list_HO, 3);
					}
					break;
				}
			}

		} else {
			// CASE 3 sourceSize < targetSize
			// System.out.println("sourceSize < targetSize at " +t);
			for (int i = 0; i < sourceSize; i++) {
				HitObject source_ho = sourceChord.list_HO.get(i);
				HitObject target_ho = list_HO.get(i);
				target_ho.copyHS(source_ho);
			}
		}
		return this;
	}

	private void combineDefaultHS(List<HitObject> chord, int n) {
		System.out.println("combining default hs: " + n);
		int targetIndex = 0;
		@SuppressWarnings("unchecked")
		List<HitObject> sourceChord = ((List<HitObject>) ((ArrayList<HitObject>) chord).clone());
		HitObject source_ho1 = sourceChord.get(0);
		HitObject source_ho2 = sourceChord.get(1);
		HitObject newHO = source_ho1.clone();
		if (n == 2) {
			if (newHO.getAddition() == source_ho2.getAddition()) {
				newHO.setHitsoundType(HitsoundType.merge(newHO.getHitsoundType(), source_ho2.getHitsoundType()));
				HitObject target_ho1 = list_HO.get(0);
				target_ho1.copyHS(newHO);
				for (int x = 0; x < n; x++) {
					sourceChord.remove(0);
				}
				targetIndex++;
			}

		} else if (n == 3) {
			HitObject source_ho3 = sourceChord.get(2);
			if (source_ho3.getAddition() == source_ho2.getAddition()
					&& source_ho2.getAddition() == source_ho1.getAddition()) {
				newHO.setHitsoundType(HitsoundType.merge(source_ho1.getHitsoundType(), source_ho2.getHitsoundType(),
						source_ho3.getHitsoundType()));
				HitObject target_ho1 = list_HO.get(0);
				target_ho1.copyHS(newHO);
				for (int x = 0; x < n; x++) {
					sourceChord.remove(0);
				}
				targetIndex++;

			} else if (source_ho1.getAddition() == source_ho2.getAddition()) {
				newHO.setHitsoundType(HitsoundType.merge(source_ho1.getHitsoundType(), source_ho2.getHitsoundType()));
				HitObject target_ho1 = list_HO.get(0);
				target_ho1.copyHS(newHO);
				for (int x = 0; x < 2; x++) {
					sourceChord.remove(0);
				}
				targetIndex++;

			} else if (source_ho2.getAddition() == source_ho3.getAddition()) {
				newHO = source_ho2.clone();
				newHO.setHitsoundType(HitsoundType.merge(newHO.getHitsoundType(), source_ho3.getHitsoundType()));
				HitObject target_ho1 = list_HO.get(0);
				target_ho1.copyHS(newHO);
				sourceChord.remove(1);
				sourceChord.remove(1);
				targetIndex++;
			}

		} else {
			throw new IllegalArgumentException();
		}

		// copy rest of hitsounds
		while (sourceChord.size() > 0) {
			HitObject source_ho = sourceChord.get(0);
			if (targetIndex < list_HO.size()) {
				HitObject target_ho = list_HO.get(targetIndex);
				target_ho.copyHS(source_ho);
				targetIndex++;
			} else {
				list_SB.addAll(source_ho.toSample());
			}
			sourceChord.remove(0);
		}

	}

	private int getDefaultHitsoundSize() {
		return (int) list_HO.stream().filter(HitObject::hasDefault_HS).count();
	}

	public long getStartTime() {
		if (!list_HO.isEmpty())
			return list_HO.get(0).getStartTime();
		else if (!list_SB.isEmpty())
			return list_SB.get(0).getStartTime();
		else
			return -1;
	}

	public boolean containsDuplicateHitsound() {
		Set<String> set = new HashSet<>();
		for (Sample sample : list_SB) {
			if (!set.add(sample.gethitSound())) {
				return true;
			}
		}
		for (HitObject hitObject : list_HO) {

			if (hitObject.hasHitsound()) {
				for (String hs : hitObject.toHitsoundString()) {
					if (!set.add(hs)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * 
	 * @return Set of unique hitsounds
	 */
	public Set<String> getHitsounds() {
		Set<String> output = new HashSet<>();

		for (Sample sample : list_SB) {
			output.add(sample.gethitSound());
		}

		for (HitObject hitObject : list_HO) {
			if (hitObject.hasHitsound()) {
				output.addAll(hitObject.toHitsoundString());
			}
		}

		return output;
	}

	public boolean SbHasSoundWhenHoIsEmpty() {
		if (list_HO.isEmpty()) {
			return (!list_SB.isEmpty());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getHitsounds().hashCode();
	}

	public boolean containsHitsounds(Chord chord) {
		if (chord == null)
			return true;
		return this.getHitsounds().containsAll(chord.getHitsounds());
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Chord) {
			return getHitsounds().equals(((Chord) other).getHitsounds());
		}

		return false;
	}

	public void addALLSB(List<Sample> SB) {
		for (Sample s : SB) {
			list_SB.add(s.clone());
		}
	}

	public void add(HitObject ho) {
		if (startTime != -1 && ho.getStartTime() != startTime) {
			throw new IllegalArgumentException(ho.toString());
		} else {
			startTime = ho.getStartTime();
		}
		list_HO.add(ho);
	}

	public void add(Sample s) {
		if (startTime != -1 && s.getStartTime() != startTime) {
			throw new IllegalArgumentException(s.toString());
		} else {
			startTime = s.getStartTime();
		}
		list_SB.add(s);
	}

	public List<Sample> getSB() {
		return list_SB;
	}

	public HitObject getHitObjectByIndex(int i) {
		return list_HO.get(i);
	}

	public Chord clone() {
		Chord newChord = new Chord();
		for (HitObject ho : list_HO) {
			newChord.add(ho.clone());
		}
		newChord.startTime = startTime;
		return newChord;
	}

	@Override
	public String toString() {
		String output = "Hit Objects\n";
		for (HitObject ho : list_HO) {
			try {
				output += ho.toSample().toString() + "\n";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		output += "\nSamples\n";
		for (Sample s : list_SB) {
			output += s.toString() + "\n";
		}

		return output;
	}

	public int getSize() {
		return getHitsounds().size();
	}

}
