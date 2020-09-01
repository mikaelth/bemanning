package se.uu.ebc.bemanning.service;

import java.util.Collection;

import se.uu.ebc.bemanning.entity.Person;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;

import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;

import org.apache.log4j.Logger;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Service
public class TEService {

    
    private static Logger logger = Logger.getLogger(TEService.class.getName());

	@Value("${timeedit.url}")
	String teUrl;

	public void extractPersonTE (String username) {

		logger.debug("extractPersonTE, username "+username);
	
		try {

			Document doc = Jsoup.connect(teUrl + "?sid=3&type=personnel&startdate=20180101&enddate=20181231&object=akka." + username).get();

//			logger.debug("extractPersonTE, doc "+ReflectionToStringBuilder.toString(doc, ToStringStyle.MULTI_LINE_STYLE));
	
			Elements trs = doc.select("table.restable tr");

//			logger.debug("extractPersonTE, trs "+ReflectionToStringBuilder.toString(trs, ToStringStyle.MULTI_LINE_STYLE));
	
			for (Element tr : trs) {
				Elements tds = tr.getElementsByTag("td");
				logger.debug("extractPersonTE, td "+ReflectionToStringBuilder.toString(tds, ToStringStyle.MULTI_LINE_STYLE));
	//   			Element td = tds.first();
				System.out.println("Blog: " + tds.text());
				if (tds.size() > 5) {
					System.out.println("Entry: " + tds.get(1).text() +", "+tds.get(2).text()+", "+tds.get(5).text());
				}
				
			}
		} catch (Exception ne) {
			ne.printStackTrace();
			logger.error("extractPersonTE experienced pesky exception "+ ne);
		}

	}
}
