package edu.emory.cci.pais.documentgenerator;
import java.util.ArrayList;
import java.util.Iterator;


public class SampleInputDataReader extends InputDataReader {
	
	private static final int COUNT = 3;
	private static final int CAL_COUNT = 3;
	private static final int OBSERVATION_COUNT = 3;
	
	private java.lang.String[] regions = {"TCGA-02-0001-01Z-00-DX1.svs-0000000000-0000032768.ppm.grid4.mat.grid4"};
	private java.lang.String[] cals = {"area", "perimeter", "eccentricity"," circularity", "major_axis", "minor_axis"};
	private java.lang.String[] calValues = {"1", "2", "3", "4", "5", "6"};
	private java.lang.String[] markups = {"100; (110 120, 110 140, 130 140, 130 120, 110 120)", "110,120 110,140 130,140 130,120 110,120", "110 120, 110 140, 130 140, 130 120, 110 120"};
	
	private java.lang.String[] nucleiScore = {"1", "2", "3"};
	private java.lang.String[] diffuseGliomaClassification = {"normal", "astro II", "gbm II"};

	SampleInputDataReader(String inputFileName){
		super(inputFileName);
	};
	
	public ArrayList<CalculationMetadata> getCalculationMetadata() {
		ArrayList<CalculationMetadata> metaList = new ArrayList<CalculationMetadata>();

		for (int i=0; i < cals.length; i++ ){
			String cal = cals[i];
			CalculationMetadata  meta = new InputDataReader.CalculationMetadata(cal, cal, "double");
			metaList.add(meta);
		}
		return metaList;
	}

	public ArrayList<ObservationMetadata> getObservationMetadata() {
		ObservationMetadata obs1 = new ObservationMetadata("diffuse glioma classification", "type", "nominal", "string");
		ObservationMetadata obs2 = new ObservationMetadata("nuclei score", "grade", "ordinal", "integer");
		ArrayList<ObservationMetadata> metaList = new ArrayList<ObservationMetadata>();
		metaList.add(obs1);
		metaList.add(obs2);		
		return metaList;
	}

	public String[] getRegionNames() {

		return regions;
	}


	@SuppressWarnings("hiding")
	public class MyMarkupIterator <String> implements Iterator <String> { 
		int count = 0; 
		public MyMarkupIterator(){
		}
		
		public boolean hasNext() {
			if (count < COUNT) return true;
			else return false;
		}
		@SuppressWarnings("unchecked")
		public String next() {
			count++;
			return (String) markups[count - 1];
		}
		public void remove() {		
		}		
	}

	public Iterator<String> getMarkupIterator() {
		return new MyMarkupIterator <String>();
	}
	
	@Override
	public Iterator<String[]> getCalculationIterator() {
		return new MyCalculationIterator();
	}

	public class  MyCalculationIterator implements Iterator <String[]> {
		private int count = 0; 

		MyCalculationIterator(){	
		}

		public boolean hasNext() {
			if (count < CAL_COUNT) return true;
			else return false;
		}

		public String[] next() {
			count++;
			return calValues;			 
		}
		
		public void remove() {		
		}			
	}
	

	@Override
	public Iterator<String[]> getObservationIterator() {	
		return new MyObservationIterator ();
	}
	

	public class  MyObservationIterator implements Iterator <String[]> {
		private int count = 0; 

		MyObservationIterator(){	
		}

		public boolean hasNext() {
			if (count < OBSERVATION_COUNT) return true;
			else return false;
		}

		public String[] next() {
			count++;
			String [] observations = new String[2];
			observations[0] = nucleiScore[count-1];
			observations[1] = diffuseGliomaClassification[count-1];
			return observations;
		}
		
		public void remove() {		
		}			
	}
	


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SampleInputDataReader reader = new SampleInputDataReader("TCGA-02-0001-01Z-00-DX1.svs-0000000000-0000032768.ppm.grid4.mat.grid4"); 

		Iterator<String[] > calItr = reader.getCalculationIterator();
		while (calItr.hasNext() ){
			String[] rst = calItr.next();
			for (int i = 0; i < rst.length; i++){
				System.out.println(rst[i]);
			}
		}			
		
		Iterator<String[] > obsItr = reader.getObservationIterator();
		
		while (obsItr.hasNext() ){
			String[] rst = obsItr.next();
			for (int i = 0; i < rst.length; i++){
				System.out.println(rst[i]);
			}
		}
		
	
		
		Iterator<String> mitr = reader.getMarkupIterator();
		while (mitr.hasNext() ){
			String rst = mitr.next();
			System.out.println(rst);
		}
		
	}





}
