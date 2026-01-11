package se.uu.ebc.bemanning.service;


import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.stream.Collectors;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.NamingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.ContainerCriteria;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Service;
import static org.springframework.ldap.query.LdapQueryBuilder.query;

import lombok.extern.slf4j.Slf4j;

/* 
import se.uu.ebc.luntan.enums.EduBoard;
import se.uu.ebc.luntan.entity.Examiner;
import se.uu.ebc.luntan.entity.Course;
import se.uu.ebc.luntan.entity.ExaminersDecision;
import se.uu.ebc.luntan.entity.EconomyDocument;
import se.uu.ebc.luntan.entity.ExternalTeacher;
import se.uu.ebc.luntan.repo.ExternalTeacherRepo;
import se.uu.ebc.luntan.repo.ExaminerRepo;
import se.uu.ebc.luntan.repo.CourseInstanceRepo;
import se.uu.ebc.luntan.repo.ExaminersListRepo;
 */

//import se.uu.ebc.ldap.Staff;

@Service
@Slf4j
public class AKKAService {

    @Autowired
	LdapTemplate ldapTemplate;

/* 
    @Autowired
	ExternalTeacherRepo externalsRepo;

    @Autowired
	ExaminerRepo examinerRepo;

    @Autowired
	ExaminersListRepo elRepo;

    @Autowired
	CourseInstanceRepo ciRepo;

	private final String BASE_DN = "cn=People,dc=uu,dc=se";


	public Staff findStaff(String dn) {
      return ldapTemplate.lookup(dn, new StaffAttributesMapper());
   }

	public Staff findbyEmployeeNumber (String employeeNumber) {
		Staff s = new Staff();
		try {
			s = ldapTemplate.lookup(LdapNameBuilder.newInstance(BASE_DN).add("employeeNumber", employeeNumber).build(), new StaffAttributesMapper());
		} catch (Exception e) {
			s.setEmployeeNumber(employeeNumber);
			s.setFamilyName(employeeNumber);
			s.setGivenName(employeeNumber);
			s.setName(employeeNumber);
		} finally {
			return s;
		}
   }

	public List<Staff> getTeachersBiology () {
		List<Staff> theStaff = getBiologyTeachers();
		theStaff.addAll(getBiologyOtherStaff());
		theStaff.addAll(getIBGStaff());
		theStaff.addAll(getOtherDeptsTeachers());
		theStaff.addAll(getOtherDeptsOtherStaff());

		return theStaff;
	}

	public List<Staff> getTeachersExtended () {
		List<Staff> theStaff = getTeachersBiology();
		theStaff.addAll(getItImbimIgpKemiTeachers());

		return theStaff;
	}


	@Cacheable("examinermap")
	public Map<String, Staff> getDesignatedExaminers() {
		Map<String, Staff> staffMap = new HashMap<String, Staff>();
		Set<String> designatedStaff = examinerRepo.findDesignatedLDAPEntries();
		for (String employeeNumber : designatedStaff) {
			staffMap.put(employeeNumber,findbyEmployeeNumber(employeeNumber));
		}
		return staffMap;
	}

	@Cacheable("ciexaminermap")
	public Map<Course, Staff> getCIExaminers(EconomyDocument eDoc) {
		List<Examiner> examiners = new ArrayList<Examiner>();
		Map<Course, Staff> staffMap = new HashMap<Course,Staff>();

		for (EduBoard edb : ciRepo.findExamEDUBoards(eDoc)) {
			log.debug("EduBoard " + edb.toString()+", "+ edb.name());
			ExaminersDecision ed = elRepo.findPreceeding(edb.name(), new Date());
			log.debug("Examiner decision " + ed);
			examiners.addAll(examinerRepo.findPrimariesByDecision(ed));
		}
		log.debug("Examiners " + examiners);
		for (Examiner ex : examiners) {
			staffMap.put(ex.getCourse(), findbyEmployeeNumber(ex.getExaminer()));
		}
		return staffMap;
	}

	@Cacheable("courseleadermap")
	public Map<String, Staff> getCourseLeaders(EconomyDocument eDoc) {
		Map<String, Staff> staffMap = new HashMap<String, Staff>();
		Set<String> courseLeaders = ciRepo.findCourseLeaders(eDoc);
		for (String employeeNumber : courseLeaders) {
			staffMap.put(employeeNumber,findbyEmployeeNumber(employeeNumber));
		}
		return staffMap;
	}

	private List<Staff> getBiologyTeachers () {

        LdapQuery query = query()
        		.base(BASE_DN)
                .where("objectclass").is("person")
                .and(query()
                	.where("title").like("universitetslektor*")
                	.or("title").like("professor*")
                	.or("title").like("seniorprofessor*")
                	.or("title").like("biträdande universitetslektor*")
                	.or("title").like("forskarassistent*")
                	.or("title").like("adjunkt*")
                	.or("title").like("universitetsadjunkt*")
                	.or("title").like("stipendiat*")
                )
                .and(query()
                	.where("department").like("Institutionen för organismbiologi*")
                	.or("department").like("Institutionen för cell- och molekylärbiologi*")
                	.or("department").like("Institutionen för ekologi och genetik*")
 					.or("department").like("Institutionen för biologisk grundutbildning*")
				)
				.and(query()
					.where("title").not().like("professor emer*")
				);

        return ldapTemplate.search(query, new StaffAttributesMapper(true));
    }

	private List<Staff> getItImbimIgpKemiTeachers () {

        LdapQuery query = query()
        		.base(BASE_DN)
                .where("objectclass").is("person")
                .and(query()
                	.where("title").like("universitetslektor*")
                	.or("title").like("professor*")
                	.or("title").like("seniorprofessor*")
                	.or("title").like("biträdande universitetslektor*")
                	.or("title").like("forskarassistent*")
                	.or("title").like("adjunkt*")
                	.or("title").like("universitetsadjunkt*")
                	.or("title").like("stipendiat*")
                	.or("title").like("forskare*")
               		.or("title").like("Postdoktor*")
               		.or("title").like("post doc*")
               		.or("title").like("doktorand*")
                )
                .and(query()
                	.where("department").like("Institutionen för immunologi, genetik och patologi*")
                	.or("department").like("Institutionen för medicinsk biokemi och mikrobiologi*")
                	.or("department").like("Institutionen för informationsteknologi*")
 					.or("department").like("Institutionen för kemi*")
				)
				.and(query()
					.where("title").not().like("professor emer*")
				);

        return ldapTemplate.search(query, new StaffAttributesMapper(false,false));
    }

	private List<Staff> getBiologyOtherStaff () {

        LdapQuery query = query()
        		.base(BASE_DN)
                .where("objectclass").is("person")
                .and(query()
                	.where("title").like("forskare*")
               		.or("title").like("Postdoktor*")
               		.or("title").like("post doc*")
               		.or("title").like("doktorand*")
                  )
                .and(query()
                	.where("department").like("Institutionen för organismbiologi*")
                	.or("department").like("Institutionen för cell- och molekylärbiologi*")
                	.or("department").like("Institutionen för ekologi och genetik*")
 					.or("department").like("Institutionen för biologisk grundutbildning*")
				);

        return ldapTemplate.search(query, new StaffAttributesMapper(false));
    }

	private List<Staff> getIBGStaff () {

        LdapQuery query = query()
        		.base(BASE_DN)
                .where("objectclass").is("person")
                .and(query()
                	.where("title").like("kursledare*")
                	.or("title").like("kursassistent*")
                	.or("title").like("projektledare*")
                	.or("title").like("timlärare*")
                	.or("title").like("utbildningssamordnare*")
                	.or("title").like("föreståndare*")
                	.or("title").like("kommunikatör*")
                 )
                .and(query()
                	.where("department").like("Institutionen för biologisk grundutbildning*")
				);

        return ldapTemplate.search(query, new StaffAttributesMapper(false));
    }

	private List<Staff> getOtherDeptsTeachers () {

 		List<ExternalTeacher> teachers = externalsRepo.findAll();
		ContainerCriteria criteria = null;
		for (ExternalTeacher teacher : teachers) {
			if (criteria == null) {
				criteria = query().where("cn").is(teacher.getName()).and(query().where("ou").is(teacher.getDepartment()));
			} else {
				criteria = criteria.or(query().where("cn").is(teacher.getName()).and(query().where("ou").is(teacher.getDepartment())));
			}
		}

        LdapQuery query = query()
        		.base(BASE_DN)
                .where("objectclass").is("person")
                .and(query()
                	.where("title").like("universitetslektor*")
                	.or(query().where("title").like("professor*").and("title").not().like("professor emer*"))
                	.or("title").like("seniorprofessor*")
                	.or("title").like("biträdande universitetslektor*")
                	.or("title").like("forskarassistent*")
                	.or("title").like("adjunkt*")
                )
                .and(criteria);


        return ldapTemplate.search(query, new StaffAttributesMapper(true));
    }

	private List<Staff> getOtherDeptsOtherStaff () {

 		List<ExternalTeacher> teachers = externalsRepo.findAll();
		ContainerCriteria criteria = null;
		for (ExternalTeacher teacher : teachers) {
			if (criteria == null) {
				criteria = query().where("cn").is(teacher.getName()).and(query().where("ou").is(teacher.getDepartment()));
			} else {
				criteria = criteria.or(query().where("cn").is(teacher.getName()).and(query().where("ou").is(teacher.getDepartment())));
			}
		}

        LdapQuery query = query()
        		.base(BASE_DN)
                .where("objectclass").is("person")
                .and(query()
                	.where("title").like("museiintendent*")
                	.or("title").like("1:e museiintendent*")
					.or("title").like("forskare*")
                )
                .and(criteria);


        return ldapTemplate.search(query, new StaffAttributesMapper(false));
    }

 	public List<Staff> findByName(String name){

        LdapQuery query = query()
        		.base(BASE_DN)
                .where("objectclass").is("person")
                .and(query().where("cn").is( "*" + name + "*" ));


        return ldapTemplate.search(query, new StaffAttributesMapper());

 	}
*/
    /**
     * Custom staff attributes mapper, maps the attributes to the staff POJO
     */
/*
    private class StaffAttributesMapper implements AttributesMapper<Staff> {
        private boolean eligible;
		private boolean biology = true;

        public Staff mapFromAttributes(Attributes attrs) throws NamingException {
            Staff person = new Staff();
 			try {
				person.setDn(LdapNameBuilder.newInstance(BASE_DN).add("employeeNumber", (String)attrs.get("employeeNumber").get()).build());
				person.setName((String)attrs.get("cn").get());
				person.setGivenName((String)attrs.get("givenname").get());
				person.setFamilyName((String)attrs.get("sn").get());
				person.setUsername((String)attrs.get("uid").get());
				person.setEmployeeNumber((String)attrs.get("employeeNumber").get());

				Attribute ou = attrs.get("ou");
				Attribute title = attrs.get("title");
				Attribute phone = attrs.get("telephonenumber");
				Attribute mail = attrs.get("mail");
				Attribute department = attrs.get("department");

				if (ou != null){
					person.setDepartment((String)ou.get());
				}
				if (title != null){
					person.setTitle((String)title.get());
				}
				if (phone != null){
					person.setPhone((String)phone.get());
				}
				if (mail != null){
					person.setMail((String)mail.get());
				}
				if (department != null){
					person.setFullDepartment((String)department.get());
				}

				person.setExaminerEligible(eligible);
				person.setBiologySection(biology);

            } catch (Exception e) {
				log.error("Got a pesky exception: "  + e);
            } finally {
				log.debug("Find: " + person.toString());
            	return person;
            }

        }

        public StaffAttributesMapper (boolean eligible) {
        	this.eligible = eligible;
        }

        public StaffAttributesMapper (boolean eligible, boolean biology) {
        	this.eligible = eligible;
        	this.biology = biology;
        }

         public StaffAttributesMapper () {}
   }

 */

}
