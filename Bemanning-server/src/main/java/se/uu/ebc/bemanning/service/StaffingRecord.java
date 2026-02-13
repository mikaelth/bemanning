package se.uu.ebc.bemanning.service;

import java.util.List;
import java.util.Map;

import se.uu.ebc.bemanning.entity.staff.Staff;
import se.uu.ebc.bemanning.entity.OrganisationUnit;

public record StaffingRecord (

//	String budgetDept,
	OrganisationUnit budgetDept,
	Map<OrganisationUnit, List<Staff>> ous,
	List<Staff> staffing

)	{}
