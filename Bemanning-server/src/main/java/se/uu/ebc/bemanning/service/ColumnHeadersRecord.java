package se.uu.ebc.bemanning.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.ArrayList;

//@Component
@ConfigurationProperties(prefix = "bemanning.upload.header")
public record ColumnHeadersRecord (

	String seActivity, 
	String seStaff, 
	String seTime, 
	String seCourse,

	String enActivity, 
	String enStaff, 
	String enTime, 
	String enCourse
	
) { 
 
	public List<String> asList () {
		List theList = seList();
		theList.addAll(enList());
		return theList;
	}
 
	
	public List<String> seList () {
		List theList = new ArrayList<String>();
		theList.add(seStaff);
		theList.add(seCourse);
		theList.add(seActivity);
		theList.add(seTime);
		
		return theList;
	}
	
	public List<String> enList () {
		List theList = new ArrayList<String>();
		theList.add(enStaff);
		theList.add(enCourse);
		theList.add(enActivity);
		theList.add(enTime);
		
		return theList;
	}
	
 }
