package se.uu.ebc.bemanning.service;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@Service
@Slf4j
public class OrgHierarchyService {

	public record OrgHierarchy (String department) {}
//	public record OrgHierarchy (String department, String program) {}

    public List<OrgHierarchy> getOUs() throws IOException {
        log.debug("In OrgHierarchy");

        final String url = "https://www.uu.se/kontakt-och-organisation/organisation?query=";
		String unitPattern = ".*query=([\\d\\w]+)";

/*


        Document doc = Jsoup.connect(url)
    //            .userAgent("Mozilla/5.0")
    //            .timeout(10_000)
                .get();

        log.debug(doc.title());

        List<OrgHierarchy> result = new ArrayList<OrgHierarchy>();
        Elements institutionItems = doc.select("li:has(> ul)");
		log.debug(institutionItems.toString());
        for (Element institutionLi : institutionItems) {
            log.debug(institutionLi.toString());

            String institutionName = institutionLi.ownText().trim();
            log.debug(institutionName);

            // Filter only actual institutions
            if (!institutionName.contains("Institution")) {
                continue;
            }

            // Child list = smallest organisational units
            Elements smallestUnits = institutionLi.select("> ul > li");

            for (Element unitLi : smallestUnits) {
                String unitName = unitLi.text().trim();

                // Remove numeric prefixes like "35.1.2 "
                unitName = unitName.replaceAll("^\\d+[\\.0-9]*\\s+", "");

                result.add(new OrgHierarchy(institutionName, unitName));
            }
        }


        return result;
    }
 */


       // Fetch the HTML document
        Document doc = Jsoup.connect(url+"TB")
                             .userAgent("Mozilla/5.0")
                             .timeout(10 * 1000)
                             .get();
//        log.debug(doc.toString());

        List<OrgHierarchy> institutions = new ArrayList<OrgHierarchy>();

//        Elements listItems = doc.select("li, a");
		Elements listItems = doc.select("ul > li > a");
		Elements linkItems = doc.select("a[href*=/kontakt-och-organisation/organisation]");
			for (Element el : linkItems) {
				log.debug( el.attr("href").toString()) ;
				String unit = el.attr("href").toString().replaceAll(unitPattern, "$1");
				log.debug("Unit: " + unit);
   			 	Document docx = Jsoup.connect(url+unit)
                             .userAgent("Mozilla/5.0")
                             .timeout(10 * 1000)
                             .get();
				Elements listItemsAgain = docx.select("ul > li > a");
       			for (Element li : listItemsAgain) {
            		String text = li.text().trim();
					log.debug(text);
				}			
			}

        log.debug(listItems.toString());
        log.debug(linkItems.toString());

        for (Element li : listItems) {
            String text = li.text().trim();

              if (text.matches(".*Institutionen.*")) {
                text = text.replaceAll("^\\d+[\\.0-9]*\\s+", "");
				log.debug(text);
                institutions.add(new OrgHierarchy(text));
            }
        }
		log.debug(institutions.toString() );

        return institutions;

	}
}

