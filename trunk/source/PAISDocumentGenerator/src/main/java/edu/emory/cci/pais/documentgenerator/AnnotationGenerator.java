package edu.emory.cci.pais.documentgenerator;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import edu.emory.cci.pais.model.*;

/**
 * @author Fusheng Wang, Center for Comprehensive Informatics, Emory University
 * @version 1.0 
 * This class provides interfaces to construct Annotation object.
 */

public class AnnotationGenerator {
	private Annotation annotation = new Annotation();
	
	public AnnotationGenerator(BigInteger id, String uid, String name){
		this(id);
		annotation.setUid(uid);
		annotation.setName(name);	
	}
	
	public AnnotationGenerator(BigInteger id){
		annotation.setId(id);
		annotation.setMarkupCollection(new Annotation.MarkupCollection());
		annotation.setCalculationCollection(new Annotation.CalculationCollection());
		annotation.setObservationCollection(new Annotation.ObservationCollection());
	}
	
	@Deprecated
	public void setMarkupCollection(List<Markup> markups){
		Annotation.MarkupCollection annMarkupCol = new Annotation.MarkupCollection();
		ArrayList<Markup> markupList = (ArrayList<Markup>) annMarkupCol.getMarkup();
		markupList.addAll(markups);
		annotation.setMarkupCollection(annMarkupCol);
	}
	
	public void addMarkup(Markup markup) {
		annotation.getMarkupCollection().getMarkup().add(markup);
	}
	
	public void addCalculation(Calculation calculation){
		annotation.getCalculationCollection().getCalculation().add(calculation);
	}
	
	public void addObservation(Observation observation) {
		annotation.getObservationCollection().getObservation().add(observation);
	}
	
	public Annotation getAnnotation(){
		return annotation;
	}
	
}
