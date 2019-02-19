package osu;

import org.junit.Test;

import osu.beatmap.Chord;
import osu.beatmap.event.Sample;
import osu.beatmap.hitobject.Addition;
import osu.beatmap.hitobject.HitObject;
import osu.beatmap.hitobject.HitsoundType;
import osu.beatmap.hitobject.SampleSet;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;


public class TestChord {
	
	@Test(expected = IllegalArgumentException.class)
	public void Test_Add_Ho_With_Different_StartTime(){
		Chord chord = new Chord();
		chord.add(new HitObject(0, 0, null, 0, HitsoundType.HITFINISH, 0, Addition.NORMAL, SampleSet.DRUM));
		chord.add(new HitObject(0, 100, null, 0, HitsoundType.HITCLAP, 0, Addition.AUTO, SampleSet.NORMAL));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void Test_Add_Sample_With_Different_StartTime(){
		Chord chord = new Chord();
		chord.add(new Sample(0,"piano.wav",100));	
		chord.add(new Sample(100,"piano.wav",100));	
	}
	
	@Test 
	public void Test_GetHashCode() {
		Chord chord1 = new Chord();
		chord1.add(new HitObject(0, 0, null, 0, HitsoundType.HITFINISH, 0, Addition.NORMAL, SampleSet.DRUM));
		chord1.add(new HitObject(0, 0, null, 0, HitsoundType.HITCLAP, 0, Addition.AUTO, SampleSet.NORMAL));
		
		Chord chord2= new Chord();
		chord2.add(new HitObject(0, 0, null, 0, HitsoundType.HITFINISH_CLAP, 0, Addition.AUTO, SampleSet.NORMAL));
		
		assertEquals(chord1.hashCode() , chord2.hashCode());
	}
	
	
	@Test
	public void Test_Equals() {
		Chord chord1 = new Chord();
		chord1.add(new HitObject(0, 0, null, 0, HitsoundType.HITCLAP, 0, Addition.AUTO, SampleSet.NORMAL));
		chord1.add(new HitObject(0, 0, null, 0, HitsoundType.HITFINISH, 0, Addition.NORMAL, SampleSet.DRUM));

		
		Chord chord2= new Chord();
		chord2.add(new HitObject(0, 0, null, 0, HitsoundType.HITFINISH_CLAP, 0, Addition.AUTO, SampleSet.NORMAL));
		
		assertTrue(chord1.equals(chord2));
	}
	
	@Test
	public void Test_getHitsounds(){
		Chord source = new Chord();
		source.add(new HitObject(0, 0, null, 0, HitsoundType.HITFINISH, 0, Addition.AUTO, SampleSet.NORMAL));
		source.add(new HitObject(0, 0, null, 0, HitsoundType.HITCLAP, 0, Addition.AUTO, SampleSet.NORMAL));
		source.add(new HitObject(0, 0, "kick.wav", 80, HitsoundType.HITNORMAL, 0, Addition.AUTO, SampleSet.AUTO));
		source.add(new Sample(0,"piano.wav",100));		
		
		Set<String> expected = new HashSet<>();
		expected.add("normal-hitclap.wav");
		expected.add("normal-hitfinish.wav");
		expected.add("kick.wav");
		expected.add("piano.wav");
		
		assertEquals(expected, source.getHitsounds());
	}
	
	@Test
	public void Test_CopyHS_S3DW3A_T1() {
		Chord source = new Chord();
		HitObject x = new HitObject(0, 0, "kick.wav", 80, HitsoundType.HITNORMAL, 0, Addition.AUTO, SampleSet.AUTO);
		source.add(x);
		source.add(new HitObject(0, 0, null, 0, HitsoundType.HITCLAP, 0, Addition.SOFT, SampleSet.NORMAL));
		HitObject ho_add = new HitObject(0, 0, null, 0, HitsoundType.HITWHISTLE, 0, Addition.DRUM, SampleSet.SOFT);
		source.add(ho_add);
		
		Chord target= new Chord();
		target.add(new HitObject(0, 0, null, 0, HitsoundType.HITNORMAL, 0, Addition.AUTO, SampleSet.NORMAL));

		target.copyHitsound(source, false);
		assertEquals(3,source.getHitsounds().size());
		assertEquals(3,target.getHitsounds().size());
		assertEquals(2,target.getSB().size());
		assertNotEquals(x,target.getHitObjectByIndex(0));
		assertEquals(source.getHitsounds(), target.getHitsounds());
	}
	
	@Test
	public void Test_CopyHS_S3D_2Unorder_T1() {
		Chord source = new Chord();
		source.add(new HitObject(0, 0, null, 0, HitsoundType.HITFINISH, 0, Addition.DRUM, SampleSet.NORMAL));
		source.add(new HitObject(0, 0, null, 0, HitsoundType.HITCLAP, 0, Addition.SOFT, SampleSet.NORMAL));
		HitObject ho_add = new HitObject(0, 0, null, 0, HitsoundType.HITWHISTLE, 0, Addition.DRUM, SampleSet.SOFT);
		source.add(ho_add);
		
		Chord target= new Chord();
		target.add(new HitObject(0, 0, null, 0, HitsoundType.HITNORMAL, 0, Addition.AUTO, SampleSet.NORMAL));

		target.copyHitsound(source, false);
		assertEquals(3,source.getHitsounds().size());
		assertEquals(3,target.getHitsounds().size());
		assertEquals(1,target.getSB().size());
		assertEquals(source.getHitsounds(), target.getHitsounds());
	}
	
	@Test
	public void Test_CopyHS_S3DW2A_T1() {
		Chord source = new Chord();
		source.add(new HitObject(0, 0, null, 0, HitsoundType.HITFINISH, 0, Addition.AUTO, SampleSet.NORMAL));
		source.add(new HitObject(0, 0, null, 0, HitsoundType.HITCLAP, 0, Addition.SOFT, SampleSet.NORMAL));
		HitObject ho_add = new HitObject(0, 0, null, 0, HitsoundType.HITWHISTLE, 0, Addition.DRUM, SampleSet.SOFT);
		source.add(ho_add);
		
		Chord target= new Chord();
		target.add(new HitObject(0, 0, null, 0, HitsoundType.HITNORMAL, 0, Addition.AUTO, SampleSet.NORMAL));

		target.copyHitsound(source, false);
		assertEquals(3,source.getHitsounds().size());
		assertEquals(3,target.getHitsounds().size());
		assertEquals(2,target.getSB().size());
		assertEquals(source.getHitsounds(), target.getHitsounds());
	}
	
	@Test
	public void Test_CopyHS_S3DW1A_T1() {
		Chord source = new Chord();
		source.add(new HitObject(0, 0, null, 0, HitsoundType.HITFINISH, 0, Addition.AUTO, SampleSet.NORMAL));
		source.add(new HitObject(0, 0, null, 0, HitsoundType.HITCLAP, 0, Addition.AUTO, SampleSet.NORMAL));
		HitObject ho_add = new HitObject(0, 0, null, 0, HitsoundType.HITWHISTLE, 0, Addition.DRUM, SampleSet.NORMAL);
		source.add(ho_add);
		
		Chord target= new Chord();
		target.add(new HitObject(0, 0, null, 0, HitsoundType.HITNORMAL, 0, Addition.AUTO, SampleSet.NORMAL));

		target.copyHitsound(source, false);
		assertEquals(3,source.getHitsounds().size());
		assertEquals(3,target.getHitsounds().size());
		assertEquals(1,target.getSB().size());
		assertEquals(ho_add.toHitsoundString().iterator().next(),target.getSB().get(0).gethitSound());
		assertEquals(source.getHitsounds(), target.getHitsounds());
	}
	
	@Test
	public void Test_CopyHS_S3D_T1() {
		Chord source = new Chord();
		source.add(new HitObject(0, 0, null, 0, HitsoundType.HITFINISH, 0, Addition.AUTO, SampleSet.NORMAL));
		source.add(new HitObject(0, 0, null, 0, HitsoundType.HITCLAP, 0, Addition.AUTO, SampleSet.NORMAL));
		source.add(new HitObject(0, 0, null, 0, HitsoundType.HITWHISTLE, 0, Addition.AUTO, SampleSet.NORMAL));
		
		Chord target= new Chord();
		target.add(new HitObject(0, 0, null, 0, HitsoundType.HITNORMAL, 0, Addition.AUTO, SampleSet.NORMAL));

		target.copyHitsound(source, false);
		assertEquals(3,source.getHitsounds().size());
		assertEquals(3,target.getHitsounds().size());
		assertEquals(0,target.getSB().size());
		assertEquals(source.getHitsounds(), target.getHitsounds());
	}
	
	@Test
	public void Test_CopyHS_S3D_T2() {
		Chord source = new Chord();
		source.add(new HitObject(0, 0, null, 0, HitsoundType.HITFINISH, 0, Addition.AUTO, SampleSet.NORMAL));
		source.add(new HitObject(0, 0, null, 0, HitsoundType.HITCLAP, 0, Addition.AUTO, SampleSet.NORMAL));
		source.add(new HitObject(0, 0, null, 0, HitsoundType.HITWHISTLE, 0, Addition.AUTO, SampleSet.NORMAL));
		
		Chord target= new Chord();
		target.add(new HitObject(0, 0, null, 0, HitsoundType.HITNORMAL, 0, Addition.AUTO, SampleSet.NORMAL));
		target.add(new HitObject(0, 0, null, 0, HitsoundType.HITNORMAL, 0, Addition.AUTO, SampleSet.NORMAL));

		target.copyHitsound(source, false);
		assertEquals(3,source.getHitsounds().size());
		assertEquals(3,target.getHitsounds().size());
		assertEquals(0,target.getSB().size());
		assertEquals(source.getHitsounds(), target.getHitsounds());
	}
	
	@Test
	public void Test_CopyHS_S1_Normal_T1() {
		Chord source = new Chord();
		source.add(new HitObject(0, 0, null, 0, HitsoundType.HITNORMAL, 0, Addition.AUTO, SampleSet.NORMAL));
		source.add(new HitObject(0, 0, null, 0, HitsoundType.HITNORMAL, 0, Addition.AUTO, SampleSet.NORMAL));
		source.add(new HitObject(0, 0, null, 0, HitsoundType.HITNORMAL, 0, Addition.AUTO, SampleSet.NORMAL));
		Chord target= new Chord();
		target.add(new HitObject(0, 0, null, 0, HitsoundType.HITNORMAL, 0, Addition.AUTO, SampleSet.NORMAL));
		target.copyHitsound(source, false);
		assertEquals(1,source.getHitsounds().size());
		assertEquals(1,target.getHitsounds().size());
		assertEquals(0,target.getSB().size());
		assertEquals(source.getHitsounds(), target.getHitsounds());
	}
	
	@Test
	public void Test_CopyHS_S2D_T0() {
		Chord source = new Chord();
		source.add(new HitObject(0, 0, null, 0, HitsoundType.HITFINISH, 0, Addition.AUTO, SampleSet.NORMAL));
		source.add(new HitObject(0, 0, null, 0, HitsoundType.HITCLAP, 0, Addition.AUTO, SampleSet.NORMAL));
		Chord target= new Chord();

		target.copyHitsound(source, false);
		assertEquals(2,source.getHitsounds().size());
		assertEquals(0,target.getHitsounds().size());
		assertEquals(0,target.getSB().size());
		assertNotEquals(source.getHitsounds(), target.getHitsounds());
	}
	
	@Test
	public void Test_CopyHS_S2D_T0_True() {
		Chord source = new Chord();
		source.add(new HitObject(0, 0, null, 0, HitsoundType.HITFINISH, 0, Addition.AUTO, SampleSet.NORMAL));
		source.add(new HitObject(0, 0, null, 0, HitsoundType.HITCLAP, 0, Addition.AUTO, SampleSet.NORMAL));
		Chord target= new Chord();

		target.copyHitsound(source, true);
		assertEquals(2,source.getHitsounds().size());
		assertEquals(2,target.getHitsounds().size());
		assertEquals(2,target.getSB().size());
		assertEquals(source.getHitsounds(), target.getHitsounds());
	}
	
	@Test
	public void Test_CopyHS_S2D_T1() {
		Chord source = new Chord();
		source.add(new HitObject(0, 0, null, 0, HitsoundType.HITFINISH, 0, Addition.AUTO, SampleSet.NORMAL));
		source.add(new HitObject(0, 0, null, 0, HitsoundType.HITCLAP, 0, Addition.AUTO, SampleSet.NORMAL));
		
		
		Chord target= new Chord();
		target.add(new HitObject(0, 0, null, 0, HitsoundType.HITNORMAL, 0, Addition.AUTO, SampleSet.NORMAL));

		target.copyHitsound(source, false);
		
		assertEquals(source.getHitsounds().size(), 2);
		assertEquals(source.getHitsounds(), target.getHitsounds());
	}
	

	@Test
	public void Test_CopyHS_S2Dup_T1() {
		Chord source = new Chord();
		source.add(new HitObject(0, 0, null, 0, HitsoundType.HITFINISH, 0, Addition.AUTO, SampleSet.NORMAL));
		source.add(new HitObject(0, 0, null, 0, HitsoundType.HITFINISH, 0, Addition.AUTO, SampleSet.NORMAL));
		
		Chord target= new Chord();
		target.add(new HitObject(0, 0, null, 0, HitsoundType.HITNORMAL, 0, Addition.AUTO, SampleSet.NORMAL));

		target.copyHitsound(source, false);
		
		assertEquals(1, source.getHitsounds().size());
		assertEquals(1, target.getHitsounds().size());
		assertEquals(source.getHitsounds(), target.getHitsounds());
	}
	
	@Test
	public void Test_CopyHS_S1D_T1() {
		Chord source = new Chord();
		source.add(new HitObject(0, 0, null, 0, HitsoundType.HITFINISH, 0, Addition.AUTO, SampleSet.NORMAL));
		
		
		Chord target= new Chord();
		target.add(new HitObject(0, 0, null, 0, HitsoundType.HITNORMAL, 0, Addition.AUTO, SampleSet.NORMAL));

		target.copyHitsound(source, false);
		
		assertEquals(source.getHitsounds().size(), 1);
		assertEquals(source.getHitsounds(), target.getHitsounds());
	}
	
	@Test
	public void Test_CopyHS_S1_HitNormal_Sampleset_T1() {
		Chord source = new Chord();
		source.add(new HitObject(0, 0, null, 0, HitsoundType.HITNORMAL, 0, Addition.AUTO, SampleSet.DRUM));
		
		Chord target= new Chord();
		target.add(new HitObject(0, 0, null, 0, HitsoundType.HITNORMAL, 0, Addition.AUTO, SampleSet.NORMAL));

		target.copyHitsound(source, false);
		
		assertEquals(source.getHitsounds().size(), 1);
		assertEquals(source.getHitsounds(), target.getHitsounds());
	}
	
	@Test
	public void Test_CopyHS_S1_HitNormal_Sampleset_Addition_T1() {
		Chord source = new Chord();
		source.add(new HitObject(0, 0, null, 0, HitsoundType.HITNORMAL, 0, Addition.SOFT, SampleSet.DRUM));
		
		Chord target= new Chord();
		target.add(new HitObject(0, 0, null, 0, HitsoundType.HITNORMAL, 0, Addition.AUTO, SampleSet.NORMAL));

		target.copyHitsound(source, false);
		
		assertEquals(source.getHitsounds().size(), 1);
		assertEquals(source.getHitsounds(), target.getHitsounds());
	}
	
	@Test
	public void Test_Equals_With_SB() {
		Chord chord1 = new Chord();
		chord1.add(new HitObject(0, 0, null, 0, HitsoundType.HITFINISH, 0, Addition.AUTO, SampleSet.NORMAL));
		chord1.add(new HitObject(0, 0, "kick.wav", 80, HitsoundType.HITNORMAL, 0, Addition.AUTO, SampleSet.AUTO));
		
		
		Chord chord2= new Chord();
		chord2.add(new Sample(0,"kick.wav",100));
		chord2.add(new HitObject(0, 0, null, 0, HitsoundType.HITFINISH, 0, Addition.AUTO, SampleSet.NORMAL));

		
		assertTrue(chord1.equals(chord2));
	}
	
	@Test
	public void Test_Equals_False() {
		Chord chord1 = new Chord();
		chord1.add(new HitObject(0, 0, null, 0, HitsoundType.HITFINISH, 0, Addition.NORMAL, SampleSet.DRUM));
		
		Chord chord2= new Chord();
		chord2.add(new HitObject(0, 0, null, 0, HitsoundType.HITFINISH_CLAP, 0, Addition.AUTO, SampleSet.NORMAL));
		
		assertFalse(chord1.equals(chord2));
	}
	
	@Test
	public void Test_Equals_False2() {
		Chord chord1 = new Chord();
		chord1.add(new HitObject(0, 0, null, 0, HitsoundType.HITFINISH, 0, Addition.NORMAL, SampleSet.DRUM));
		chord1.add(new HitObject(0, 0, null, 0, HitsoundType.HITCLAP, 0, Addition.AUTO, SampleSet.NORMAL));
		chord1.add(new HitObject(0, 0, null, 0, HitsoundType.HITCLAP, 0, Addition.AUTO, SampleSet.DRUM));
		
		Chord chord2= new Chord();
		chord2.add(new HitObject(0, 0, null, 0, HitsoundType.HITFINISH_CLAP, 0, Addition.AUTO, SampleSet.NORMAL));
		
		assertFalse(chord1.equals(chord2));
	}
}
