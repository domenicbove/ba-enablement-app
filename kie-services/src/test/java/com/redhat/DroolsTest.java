package com.redhat;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

public class DroolsTest {

	private StatelessDecisionService service = BrmsHelper.newStatelessDecisionServiceBuilder().auditLogName("audit").build();

	@Test
	public void filterKansasTest() {
		// given
		Collection<Object> facts = new ArrayList<Object>();
		Business business = new Business();
		business.setStateCode("KS");
		facts.add(business);
		// when
		RuleResponse response = service.runRules(facts, "defaultPackage.Process", RuleResponse.class);
		// then
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getBusiness());
		Assert.assertEquals(response.getReasons().getResponseCode(), "filtered");
	}
	
	@Test
	public void validationFailTest() {
		// given
		Collection<Object> facts = new ArrayList<Object>();
		Business business = new Business();
		business.setStateCode("CA");
		facts.add(business);
		// when
		RuleResponse response = service.runRules(facts, "defaultPackage.Process", RuleResponse.class);
		// then
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getBusiness());
		Assert.assertEquals(response.getReasons().getResponseCode(), "validation error");
	}
	
	
	@Test
	public void validationPassTest(){
		// scenario: we need to enrich the taxId with the zipcode for system XYZ
		// given a business 
		Collection<Object> facts = new ArrayList<Object>();
		Business business = new Business();
		business.setName("biz1");
		business.setAddressLine1("addr1");
		business.setAddressLine2("addr2");
		business.setPhoneNumber("831");
		business.setStateCode("CA");
		business.setCity("SC");
		// and the business' zipcode is 10002
		business.setZipCode("10002");
		// and the business' taxId is 98765
		business.setFederalTaxId("98765");
		facts.add(business);
		// when I apply the enrichment rules
		RuleResponse response = service.runRules(facts, "defaultPackage.Process", RuleResponse.class);
		// then the business' taxId should be enriched to 98765-10002
		Assert.assertNotNull(response);
		Assert.assertEquals("98765-10002", response.getBusiness().getFederalTaxId());
	}
}
