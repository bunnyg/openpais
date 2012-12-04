package edu.emory.cci.pais.documentgenerator;
import java.math.BigInteger;
import java.util.ArrayList;
import edu.emory.cci.pais.model.Annotation;
import edu.emory.cci.pais.model.Circle;
import edu.emory.cci.pais.model.DICOMSegmentation;
import edu.emory.cci.pais.model.Ellipse;
import edu.emory.cci.pais.model.GenericSurface;
import edu.emory.cci.pais.model.Line;
import edu.emory.cci.pais.model.Markup;
import edu.emory.cci.pais.model.Mask;
import edu.emory.cci.pais.model.MultiLine;
import edu.emory.cci.pais.model.MultiPoint;
import edu.emory.cci.pais.model.MultiPolygon;
import edu.emory.cci.pais.model.PAIS;
import edu.emory.cci.pais.model.Point;
import edu.emory.cci.pais.model.Polygon;
import edu.emory.cci.pais.model.Polyline;
import edu.emory.cci.pais.model.Rectangle;

/**
 * @author Fusheng Wang, Center for Comprehensive Informatics, Emory University
 * @version 1.0 
 * This class is used to construct Annotation object.
 */

public class MarkupGenerator {
	private static Markup markup = new Markup();
	
	public MarkupGenerator(BigInteger id, String uid, String name, BigInteger annotationId,  BigInteger [] imageReferenceId){
		markup.setId(id);
		markup.setUid(uid);
		markup.setAnnotation(createAnnotation(annotationId) );
		if (imageReferenceId != null)
			markup.setImageReferenceCollection(createImageReferenceCollection(imageReferenceId)  );
	}
	
	public MarkupGenerator(BigInteger id){
		markup.setId(id);
	}
	
	
	Markup.Annotation createAnnotation(BigInteger id){
		//TODO
		return null;
	}
	
	Markup.ImageReferenceCollection createImageReferenceCollection(BigInteger [] imageReferenceId){
		 //TODO
			return null;
		}
	
	// Polygon 
	public Markup createPolygonMarkup(BigInteger geoId, String points, String format){				
    	Polygon plgn = new Polygon();
    	plgn.setFormat(format);
    	plgn.setId( geoId );    	
    	plgn.setPoints(points);
    	markup.setGeometricShape(new Markup.GeometricShape());			        	
    	Markup.GeometricShape mshape = markup.getGeometricShape();
    	mshape.setGeometricShape(plgn);
    	return markup;
	}
	
	// MultiplePolygon 
	public Markup createMultiPolygonMarkup(BigInteger geoId, String points, String format){
		MultiPolygon multiPlgn = new MultiPolygon();
		multiPlgn.setId(geoId);
		multiPlgn.setFormat(format);
		multiPlgn.setPoints(points);
		markup.setGeometricShape(new Markup.GeometricShape());
		Markup.GeometricShape mshape = markup.getGeometricShape();
		mshape.setGeometricShape(multiPlgn);
		return markup;
	}
	
	//  Polyline
	public Markup createPolylineMarkup(BigInteger geoId, String points, String format){
		Polyline polyline = new Polyline();
		polyline.setId(geoId);
		polyline.setFormat(format);
		polyline.setPoints(points);
		markup.setGeometricShape(new Markup.GeometricShape());
		Markup.GeometricShape mshape = markup.getGeometricShape();
		mshape.setGeometricShape(polyline);
		return markup;
	}
	
	// Point
	public Markup createPointMarkup(BigInteger geoId, Double x, Double y){
		Point point = new Point();
		point.setId(geoId);
		point.setX(x);
		point.setY(y);
		markup.setGeometricShape(new Markup.GeometricShape());
		Markup.GeometricShape mshape = markup.getGeometricShape();
		mshape.setGeometricShape(point);
		return markup;
	}
	
	// MultiPoint
	public Markup createMultiPointMarkup(BigInteger geoId, String points){
		MultiPoint multipoint = new MultiPoint();
		multipoint.setId(geoId);
		multipoint.setPoints(points);
		markup.setGeometricShape(new Markup.GeometricShape());
		Markup.GeometricShape mshape = markup.getGeometricShape();
		mshape.setGeometricShape(multipoint);
		return markup;
	}
	
	// Line
	public Markup createLineMarkup(BigInteger geoId, Double startX, Double startY, Double endX, Double endY){
		Line line = new Line();
		line.setId(geoId);
		line.setStartX(startX);
		line.setStartY(startY);
		line.setEndX(endX);
		line.setEndY(endY);
		markup.setGeometricShape(new Markup.GeometricShape());
		Markup.GeometricShape mshape = markup.getGeometricShape();
		mshape.setGeometricShape(line);
		return markup;
	}
	
	// MultiLine
	public Markup createMultiLineMarkup(BigInteger geoId, String points, String format){
		MultiLine multiline = new MultiLine();
		multiline.setId(geoId);
		multiline.setFormat(format);
		multiline.setPoints(points);
		markup.setGeometricShape(new Markup.GeometricShape());
		Markup.GeometricShape mshape = markup.getGeometricShape();
		mshape.setGeometricShape(multiline);
		return markup;
	}
	
	// Rectangle 
	public Markup createRectangleMarkup(BigInteger geoId, Double height, Double width, Double x, Double y){
		Rectangle rec = new Rectangle();
		rec.setId(geoId);
		rec.setHeight(height);
		rec.setWidth(width);
		rec.setX(x);
		rec.setY(y);
		markup.setGeometricShape(new Markup.GeometricShape());
		Markup.GeometricShape mshape = markup.getGeometricShape();
		mshape.setGeometricShape(rec);
		return markup;
	}
	
	// Circle
	public Markup createCircleMarkup(BigInteger geoId, Double centerX, Double centerY, Double radius){
		Circle circle = new Circle();
		circle.setId(geoId);
		circle.setCenterX(centerX);
		circle.setCenterY(centerY);
		circle.setRadius(radius);
		markup.setGeometricShape(new Markup.GeometricShape());
		Markup.GeometricShape mshape = markup.getGeometricShape();
		mshape.setGeometricShape(circle);
		return markup;
	}
	
	// Elipse
	public Markup createEllipseMarkup(BigInteger geoId, Double centerX, Double centerY, Double radiusX, Double radiusY){
		Ellipse ellipse = new Ellipse();
		ellipse.setId(geoId);
		ellipse.setCenterX(centerX);
		ellipse.setCenterY(centerY);
		ellipse.setRadiusX(radiusX);
		ellipse.setRadiusY(radiusY);
		markup.setGeometricShape(new Markup.GeometricShape());
		Markup.GeometricShape mshape = markup.getGeometricShape();
		mshape.setGeometricShape(ellipse);
		return markup;
	}
	
	// Mask
	public Markup createMaskMarkup(BigInteger fieldId, Double x, Double y)
	{
		Mask mask = new Mask();
		mask.setId(fieldId);
		mask.setX(x);
		mask.setY(y);
		markup.setField(new Markup.Field());
		Markup.Field mField = markup.getField();
		mField.setField(mask);
		return markup;
	}
	
	// Generic surface
	public Markup createGenericSurfaceMarkup(BigInteger SurfaceId, String type)
	{
		GenericSurface genericSurface = new GenericSurface();
		genericSurface.setId(SurfaceId);
		genericSurface.setType(type);
		markup.setSurface(new Markup.Surface());
		Markup.Surface mSurface = markup.getSurface();
		mSurface.setSurface(genericSurface);
		return markup;
	}
	
	// DICOM Segmentation
	public Markup createDICOMSegmentationMarkup(BigInteger SurfaceId, String type, String sopInstanceUID, String sopClassUID, 
			String referencedSOPInstanceUID, BigInteger segmentNumber)
	{
		DICOMSegmentation dicomSegmentation = new DICOMSegmentation();
		dicomSegmentation.setId(SurfaceId);
		dicomSegmentation.setType(type);
		dicomSegmentation.setSopInstanceUID(sopInstanceUID);
		dicomSegmentation.setSopClassUID(sopClassUID);
		dicomSegmentation.setReferencedSOPInstanceUID(referencedSOPInstanceUID);
		dicomSegmentation.setSegmentNumber(segmentNumber);
		markup.setSurface(new Markup.Surface());
		Markup.Surface mSurface = markup.getSurface();
		mSurface.setSurface(dicomSegmentation);
		return markup;
	}
	
	public void setAnnotation(Annotation annotation){
		Markup.Annotation mAnn = new Markup.Annotation();
		mAnn.setAnnotation(annotation);
		markup.setAnnotation(mAnn);
	}
	
	public Markup getMarkup(){
		return markup;
	}
	
	public static void main(String[] args) {
		
		// Polygon Testing : writing time, 0.644 sec for 100 samples 

		int TRY_SIZE = 100;
		PAIS doc = new PAIS();		        
		doc.setUid("fakeduid");
		PAIS.MarkupCollection markupCollection = new PAIS.MarkupCollection();
		doc.setMarkupCollection(markupCollection);

		ArrayList markupList =  (ArrayList) markupCollection.getMarkup();
		for (int i = 0; i < TRY_SIZE; i++) {
			Polygon plgn = new Polygon();
			plgn.setFormat("svg");
			plgn.setId( BigInteger.valueOf(i) );
			String value = "1914,31685 1887,31684 1877,31685 1871,31687 1868,31689 1862,31691 1857,31692 1853,31694 1852,31696 1846,31698 1845,31700 1841,31701 1837,31703 1836,31705 1830,31707 1827,31709 1823,31710 1820,31712 1816,31714 1814,31716 1812,31719 1811,31721 1809,31725 1807,31730 1811,31735 1812,31737 1834,31739 1852,31741 1859,31739 1868,31737 1877,31735 1895,31734 1900,31735 1902,31746 1896,31750 1895,31751 1891,31753 1882,31757 1880,31759 1875,31760 1871,31762 1870,31764 1866,31767 1862,31769 1861,31771 1859,31773 1855,31775 1853,31776 1852,31778 1850,31780 1848,31784 1846,31789 1845,31791 1843,31798 1841,31819 1846,31826 1848,31828 1852,31826 1855,31825 1857,31823 1861,31821 1864,31817 1868,31816 1870,31812 1875,31810 1878,31807 1884,31805 1889,31803 1893,31801 1898,31800 1905,31801 1907,31803 1909,31805 1911,31807 1912,31810 1914,31817 1916,31821 1918,31823 1920,31825 1921,31826 1925,31828 1927,31830 1932,31832 1941,31834 1962,31835 1966,31834 1973,31832 1978,31830 1980,31828 1986,31826 1991,31823 1995,31809 1993,31805 1989,31801 1986,31800 1984,31796 1978,31792 1977,31791 1975,31789 1973,31785 1971,31773 1973,31771 1975,31769 1977,31767 1980,31764 1986,31762 1989,31757 1991,31753 1993,31748 1989,31730 1987,31728 1986,31726 1984,31725 1982,31723 1977,31717 1975,31716 1973,31714 1971,31712 1970,31710 1964,31707 1959,31703" + i + ","+  i;
			plgn.setPoints(value);
			value = null;

			Markup markup = new Markup();
			markup.setGeometricShape(new Markup.GeometricShape());			        	
			Markup.GeometricShape mshape = markup.getGeometricShape();
			mshape.setGeometricShape(plgn);
			markupList.add(markup);
		}		

		XMLMarshaller tester = new XMLMarshaller();
		String outputFile = "dummyPolygon.xml";        
		tester.generateXML(PAIS.class, "PAIS", outputFile, doc);			

		// MultiplePolygon Test
/*		
		BigInteger geoID = BigInteger.valueOf(100);
		String points = "1915,31685 1887,31684 1877,31685 1871,31687 1868,31689 1862,31691 1857,31692 1853,31694 1852,31696 1846,31698 1845,31700 1841,31701 1837,31703 1836,31705 1830,31707 1827,31709 1823,31710 1820,31712 1816,31714 1814,31716 1812,31719 1811,31721 1809,31725 1807,31730 1811,31735 1812,31737 1834,31739 1852,31741 1859,31739 1868,31737 1877,31735 1895,31734 1900,31735 1902,31746 1896,31750 1895,31751 1891,31753 1882,31757 1880,31759 1875,31760 1871,31762 1870,31764 1866,31767 1862,31769 1861,31771 1859,31773 1855,31775 1853,31776 1852,31778 1850,31780 1848,31784 1846,31789 1845,31791 1843,31798 1841,31819 1846,31826 1848,31828 1852,31826 1855,31825 1857,31823 1861,31821 1864,31817 1868,31816 1870,31812 1875,31810 1878,31807 1884,31805 1889,31803 1893,31801 1898,31800 1905,31801 1907,31803 1909,31805 1911,31807 1912,31810 1914,31817 1916,31821 1918,31823 1920,31825 1921,31826 1925,31828 1927,31830 1932,31832 1941,31834 1962,31835 1966,31834 1973,31832 1978,31830 1980,31828 1986,31826 1991,31823 1995,31809 1993,31805 1989,31801 1986,31800 1984,31796 1978,31792 1977,31791 1975,31789 1973,31785 1971,31773 1973,31771 1975,31769 1977,31767 1980,31764 1986,31762 1989,31757 1991,31753 1993,31748 1989,31730 1987,31728 1986,31726 1984,31725 1982,31723 1977,31717 1975,31716 1973,31714 1971,31712 1970,31710 1964,31707 1959,31703";
		String format = "svg";
		
		MultiPolygon multiPlgn = new MultiPolygon();
		multiPlgn.setId(geoID);
		multiPlgn.setFormat(format);
		multiPlgn.setPoints(points);
		
		Markup markup = new Markup();
		markup.setGeometricShape(new Markup.GeometricShape());			        	
		Markup.GeometricShape mshape = markup.getGeometricShape();
		mshape.setGeometricShape(multiPlgn);

		XMLMarshaller tester = new XMLMarshaller();
		String outputFile = "MultiPolygon.xml";
		tester.generateXML(Markup.class, "Markup", outputFile, markup);		
*/
		// MultiPolygon Testing : writing time, 0.644 sec for 100 samples 
/*
		int TRY_SIZE = 100;
		PAIS doc = new PAIS();		        
		doc.setUid("fakeduid");
		PAIS.MarkupCollection markupCollection = new PAIS.MarkupCollection();
		doc.setMarkupCollection(markupCollection);

		ArrayList markupList =  (ArrayList) markupCollection.getMarkup();
		for (int i = 0; i < TRY_SIZE; i++) {
			BigInteger geoID = BigInteger.valueOf(100);
			String points = "1915,31685 1887,31684 1877,31685 1871,31687 1868,31689 1862,31691 1857,31692 1853,31694 1852,31696 1846,31698 1845,31700 1841,31701 1837,31703 1836,31705 1830,31707 1827,31709 1823,31710 1820,31712 1816,31714 1814,31716 1812,31719 1811,31721 1809,31725 1807,31730 1811,31735 1812,31737 1834,31739 1852,31741 1859,31739 1868,31737 1877,31735 1895,31734 1900,31735 1902,31746 1896,31750 1895,31751 1891,31753 1882,31757 1880,31759 1875,31760 1871,31762 1870,31764 1866,31767 1862,31769 1861,31771 1859,31773 1855,31775 1853,31776 1852,31778 1850,31780 1848,31784 1846,31789 1845,31791 1843,31798 1841,31819 1846,31826 1848,31828 1852,31826 1855,31825 1857,31823 1861,31821 1864,31817 1868,31816 1870,31812 1875,31810 1878,31807 1884,31805 1889,31803 1893,31801 1898,31800 1905,31801 1907,31803 1909,31805 1911,31807 1912,31810 1914,31817 1916,31821 1918,31823 1920,31825 1921,31826 1925,31828 1927,31830 1932,31832 1941,31834 1962,31835 1966,31834 1973,31832 1978,31830 1980,31828 1986,31826 1991,31823 1995,31809 1993,31805 1989,31801 1986,31800 1984,31796 1978,31792 1977,31791 1975,31789 1973,31785 1971,31773 1973,31771 1975,31769 1977,31767 1980,31764 1986,31762 1989,31757 1991,31753 1993,31748 1989,31730 1987,31728 1986,31726 1984,31725 1982,31723 1977,31717 1975,31716 1973,31714 1971,31712 1970,31710 1964,31707 1959,31703";
			String format = "svg";
			MultiPolygon multiPlgn = new MultiPolygon();
			multiPlgn.setId(geoID);
			multiPlgn.setFormat(format);
			multiPlgn.setPoints(points);
			
			Markup markup = new Markup();
			markup.setGeometricShape(new Markup.GeometricShape());			        	
			Markup.GeometricShape mshape = markup.getGeometricShape();
			mshape.setGeometricShape(multiPlgn);
			markupList.add(markup);
		}		

		XMLMarshaller tester = new XMLMarshaller();
		String outputFile = "dummyMultiPolygon.xml";        
		tester.generateXML(PAIS.class, "PAIS", outputFile, doc);		
*/
		// Mask Test	
/*
		BigInteger fieldId = BigInteger.valueOf(100);
		Double x = new Double("12.25");
		Double y = new Double("25.23");

		Mask mask = new Mask();
		mask.setId(fieldId);
		mask.setX(x);
		mask.setY(y);
		markup.setField(new Markup.Field());
		Markup.Field mField = markup.getField();
		mField.setField(mask);
		
		Markup markup = new Markup();
		markup.setField(new Markup.Field());
		Markup.Field mfield = markup.getField();
		mfield.setField(mask);
		

		XMLMarshaller tester = new XMLMarshaller();
		String outputFile = "Mask.xml";
		tester.generateXML(Markup.class, "Markup", outputFile, markup);		
*/	

		// DICOM Segmentation Test
/*
		BigInteger SurfaceId = BigInteger.valueOf(100);
		String type = "DICOM type";
		String sopInstanceUID = "FakeInstanceUID";
		String sopClassUID = "FakeClassUID";
		String referencedSOPInstanceUID = "FakeReferencedUID";
		BigInteger segmentNumber = BigInteger.valueOf(200);
		
		DICOMSegmentation dicomSegmentation = new DICOMSegmentation();
		dicomSegmentation.setId(SurfaceId);
		dicomSegmentation.setType(type);
		dicomSegmentation.setSopInstanceUID(sopInstanceUID);
		dicomSegmentation.setSopClassUID(sopClassUID);
		dicomSegmentation.setReferencedSOPInstanceUID(referencedSOPInstanceUID);
		dicomSegmentation.setSegmentNumber(segmentNumber);
		
		Markup markup = new Markup();
		markup.setSurface(new Markup.Surface());
		Markup.Surface msurface = markup.getSurface();
		msurface.setSurface(dicomSegmentation);
		
		XMLMarshaller tester = new XMLMarshaller();
		String outputFile = "DICOMSegmentation.xml";
		tester.generateXML(Markup.class, "Markup", outputFile, markup);	
/*
		// Polyline
/*
		BigInteger geoID = BigInteger.valueOf(100);
		String points = "1915,31685 1887,31684 1877,31685 1871,31687 1868,31689 1862,31691 1857,31692 1853,31694 1852,31696 1846,31698 1845,31700 1841,31701 1837,31703 1836,31705 1830,31707 1827,31709 1823,31710 1820,31712 1816,31714 1814,31716 1812,31719 1811,31721 1809,31725 1807,31730 1811,31735 1812,31737 1834,31739 1852,31741 1859,31739 1868,31737 1877,31735 1895,31734 1900,31735 1902,31746 1896,31750 1895,31751 1891,31753 1882,31757 1880,31759 1875,31760 1871,31762 1870,31764 1866,31767 1862,31769 1861,31771 1859,31773 1855,31775 1853,31776 1852,31778 1850,31780 1848,31784 1846,31789 1845,31791 1843,31798 1841,31819 1846,31826 1848,31828 1852,31826 1855,31825 1857,31823 1861,31821 1864,31817 1868,31816 1870,31812 1875,31810 1878,31807 1884,31805 1889,31803 1893,31801 1898,31800 1905,31801 1907,31803 1909,31805 1911,31807 1912,31810 1914,31817 1916,31821 1918,31823 1920,31825 1921,31826 1925,31828 1927,31830 1932,31832 1941,31834 1962,31835 1966,31834 1973,31832 1978,31830 1980,31828 1986,31826 1991,31823 1995,31809 1993,31805 1989,31801 1986,31800 1984,31796 1978,31792 1977,31791 1975,31789 1973,31785 1971,31773 1973,31771 1975,31769 1977,31767 1980,31764 1986,31762 1989,31757 1991,31753 1993,31748 1989,31730 1987,31728 1986,31726 1984,31725 1982,31723 1977,31717 1975,31716 1973,31714 1971,31712 1970,31710 1964,31707 1959,31703";
		String format = "svg";
		
		Polyline polyline = new Polyline();
		polyline.setId(geoID);
		polyline.setFormat(format);
		polyline.setPoints(points);
		
		Markup markup = new Markup();
		markup.setGeometricShape(new Markup.GeometricShape());			        	
		Markup.GeometricShape mshape = markup.getGeometricShape();
		mshape.setGeometricShape(polyline);

		XMLMarshaller tester = new XMLMarshaller();
		String outputFile = "Polyline.xml";
		tester.generateXML(Markup.class, "Markup", outputFile, markup);		
*/
		// Point
/*		
		BigInteger geoId = BigInteger.valueOf(100);
		Double x = new Double("12.25");
		Double y = new Double("25.23");
		
		Point point = new Point();
		point.setId(geoId);
		point.setX(x);
		point.setY(y);
		
		Markup markup = new Markup();
		markup.setGeometricShape(new Markup.GeometricShape());			        	
		Markup.GeometricShape mshape = markup.getGeometricShape();
		mshape.setGeometricShape(point);

		XMLMarshaller tester = new XMLMarshaller();
		String outputFile = "Point.xml";
		tester.generateXML(Markup.class, "Markup", outputFile, markup);		
*/
		
		// MultiPoint
/*
		BigInteger geoId = BigInteger.valueOf(100);
		String points = "1915,31685 1887,31684 1877,31685 1871,31687 1868,31689 1862,31691 1857,31692 1853,31694 1852,31696 1846,31698 1845,31700 1841,31701 1837,31703 1836,31705 1830,31707 1827,31709 1823,31710 1820,31712 1816,31714 1814,31716 1812,31719 1811,31721 1809,31725 1807,31730 1811,31735 1812,31737 1834,31739 1852,31741 1859,31739 1868,31737 1877,31735 1895,31734 1900,31735 1902,31746 1896,31750 1895,31751 1891,31753 1882,31757 1880,31759 1875,31760 1871,31762 1870,31764 1866,31767 1862,31769 1861,31771 1859,31773 1855,31775 1853,31776 1852,31778 1850,31780 1848,31784 1846,31789 1845,31791 1843,31798 1841,31819 1846,31826 1848,31828 1852,31826 1855,31825 1857,31823 1861,31821 1864,31817 1868,31816 1870,31812 1875,31810 1878,31807 1884,31805 1889,31803 1893,31801 1898,31800 1905,31801 1907,31803 1909,31805 1911,31807 1912,31810 1914,31817 1916,31821 1918,31823 1920,31825 1921,31826 1925,31828 1927,31830 1932,31832 1941,31834 1962,31835 1966,31834 1973,31832 1978,31830 1980,31828 1986,31826 1991,31823 1995,31809 1993,31805 1989,31801 1986,31800 1984,31796 1978,31792 1977,31791 1975,31789 1973,31785 1971,31773 1973,31771 1975,31769 1977,31767 1980,31764 1986,31762 1989,31757 1991,31753 1993,31748 1989,31730 1987,31728 1986,31726 1984,31725 1982,31723 1977,31717 1975,31716 1973,31714 1971,31712 1970,31710 1964,31707 1959,31703";
		
		MultiPoint multipoint = new MultiPoint();
		
		multipoint.setId(geoId);
		multipoint.setPoints(points);
		
		Markup markup = new Markup();
		markup.setGeometricShape(new Markup.GeometricShape());			        	
		Markup.GeometricShape mshape = markup.getGeometricShape();
		mshape.setGeometricShape(multipoint);

		XMLMarshaller tester = new XMLMarshaller();
		String outputFile = "MultiPoint.xml";
		tester.generateXML(Markup.class, "Markup", outputFile, markup);	
	*/
		// Line
/*	
		BigInteger geoId = BigInteger.valueOf(100);
		Double startX = new Double("232.4343");
		Double startY = new Double("434.2323");
		Double endX = new Double("323.2323");
		Double endY = new Double("545.4343");
		
		Line line = new Line();
		line.setId(geoId);
		line.setStartX(startX);
		line.setStartY(startY);
		line.setEndX(endX);
		line.setEndY(endY);

		Markup markup = new Markup();
		markup.setGeometricShape(new Markup.GeometricShape());			        	
		Markup.GeometricShape mshape = markup.getGeometricShape();
		mshape.setGeometricShape(line);

		XMLMarshaller tester = new XMLMarshaller();
		String outputFile = "Line.xml";
		tester.generateXML(Markup.class, "Markup", outputFile, markup);	
*/
		// MultiLine
/*		
		BigInteger geoID = BigInteger.valueOf(100);
		String points = "1915,31685 1887,31684 1877,31685 1871,31687 1868,31689 1862,31691 1857,31692 1853,31694 1852,31696 1846,31698 1845,31700 1841,31701 1837,31703 1836,31705 1830,31707 1827,31709 1823,31710 1820,31712 1816,31714 1814,31716 1812,31719 1811,31721 1809,31725 1807,31730 1811,31735 1812,31737 1834,31739 1852,31741 1859,31739 1868,31737 1877,31735 1895,31734 1900,31735 1902,31746 1896,31750 1895,31751 1891,31753 1882,31757 1880,31759 1875,31760 1871,31762 1870,31764 1866,31767 1862,31769 1861,31771 1859,31773 1855,31775 1853,31776 1852,31778 1850,31780 1848,31784 1846,31789 1845,31791 1843,31798 1841,31819 1846,31826 1848,31828 1852,31826 1855,31825 1857,31823 1861,31821 1864,31817 1868,31816 1870,31812 1875,31810 1878,31807 1884,31805 1889,31803 1893,31801 1898,31800 1905,31801 1907,31803 1909,31805 1911,31807 1912,31810 1914,31817 1916,31821 1918,31823 1920,31825 1921,31826 1925,31828 1927,31830 1932,31832 1941,31834 1962,31835 1966,31834 1973,31832 1978,31830 1980,31828 1986,31826 1991,31823 1995,31809 1993,31805 1989,31801 1986,31800 1984,31796 1978,31792 1977,31791 1975,31789 1973,31785 1971,31773 1973,31771 1975,31769 1977,31767 1980,31764 1986,31762 1989,31757 1991,31753 1993,31748 1989,31730 1987,31728 1986,31726 1984,31725 1982,31723 1977,31717 1975,31716 1973,31714 1971,31712 1970,31710 1964,31707 1959,31703";
		String format = "svg";
		
		MultiLine multiline= new MultiLine();
		multiline.setId(geoID);
		multiline.setFormat(format);
		multiline.setPoints(points);
		
		Markup markup = new Markup();
		markup.setGeometricShape(new Markup.GeometricShape());			        	
		Markup.GeometricShape mshape = markup.getGeometricShape();
		mshape.setGeometricShape(multiline);

		XMLMarshaller tester = new XMLMarshaller();
		String outputFile = "Multiline.xml";
		tester.generateXML(Markup.class, "Markup", outputFile, markup);		
*/	
		// Rectangle
/*
		BigInteger geoId = BigInteger.valueOf(100);
		Double height = new Double("100.0000");
		Double width = new Double("200.0000");
		Double x = new Double("323.2323");
		Double y = new Double("545.4343");
		
		Rectangle rec = new Rectangle();
		rec.setId(geoId);
		rec.setHeight(height);
		rec.setWidth(width);
		rec.setX(x);
		rec.setY(y);
		
		Markup markup = new Markup();
		markup.setGeometricShape(new Markup.GeometricShape());			        	
		Markup.GeometricShape mshape = markup.getGeometricShape();
		mshape.setGeometricShape(rec);

		XMLMarshaller tester = new XMLMarshaller();
		String outputFile = "Rectangle.xml";
		tester.generateXML(Markup.class, "Markup", outputFile, markup);	
*/		
		// Circle
/*		
		BigInteger geoId = BigInteger.valueOf(100);
		Double centerX = new Double("100.0000");
		Double centerY = new Double("200.0000");
		Double radius = new Double("323.2323");
		
		Circle circle = new Circle();
		circle.setId(geoId);
		circle.setCenterX(centerX);
		circle.setCenterY(centerY);
		circle.setRadius(radius);
		markup.setGeometricShape(new Markup.GeometricShape());
		Markup.GeometricShape mshape = markup.getGeometricShape();
		mshape.setGeometricShape(circle);

		XMLMarshaller tester = new XMLMarshaller();
		String outputFile = "Circle.xml";
		tester.generateXML(Markup.class, "Markup", outputFile, markup);	
*/
		// Elipse
/*
		BigInteger geoId = BigInteger.valueOf(100);
		Double centerX = new Double("100.0000");
		Double centerY = new Double("200.0000");
		Double radiusX = new Double("300.0000");
		Double radiusY = new Double("400.0000");
		
		Ellipse ellipse = new Ellipse();
		ellipse.setId(geoId);
		ellipse.setCenterX(centerX);
		ellipse.setCenterY(centerY);
		ellipse.setRadiusX(radiusX);
		ellipse.setRadiusY(radiusY);
		markup.setGeometricShape(new Markup.GeometricShape());
		Markup.GeometricShape mshape = markup.getGeometricShape();
		mshape.setGeometricShape(ellipse);
		
		XMLMarshaller tester = new XMLMarshaller();
		String outputFile = "Ellipse.xml";
		tester.generateXML(Markup.class, "Markup", outputFile, markup);	
*/		
	}

}
