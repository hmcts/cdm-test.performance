package uk.gov.hmcts.ccd.corecasedata.scenarios

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import scala.util.Random

object CreateCaseObj {
  val IdamURL = "https://idam-test.dev.ccidam.reform.hmcts.net"
   val CCDEnvurl = "https://ccd-case-management-web-sprod.service.core-compute-sprod.internal"
  
    val MinThinkTime = 10
    val MaxThinkTime = 20
    val constantThinkTime = 5
    
    val headers_6 = Map(
		"Accept" -> "application/json",
		"Content-Type" -> "application/json")
		
	val caseTypeValue = Array("CREATE","CREATEASPROG","CREATEASDONE") 
	
  val randcaseType = new Random(System.currentTimeMillis()) 
  
	private def PickCaseType(): String = caseTypeValue(randcaseType.nextInt(caseTypeValue.length))	
	private val rng: Random = new Random()
  private def firstpageText(): String = rng.alphanumeric.take(10).mkString
  private def firstpageNumberField(): Int = rng.nextInt(999999999)
  private def firstpageEmailRandNumber(): Int = rng.nextInt(999999)
  private def firstpageMoneyField(): Int = rng.nextInt(9999999)
  private def secondpageText(): String = rng.alphanumeric.take(15).mkString
  private def thirdpageText(): String = rng.alphanumeric.take(15).mkString
  private def thirdpageNestedNumberField(): Int = rng.nextInt(99999999)
  private def caseSummaryText(): String = rng.alphanumeric.take(20).mkString
  private def caseDescriptionText(): String = rng.alphanumeric.take(30).mkString
  
    
  val selectJurisdiction = exec(_.setAll(
        ("FirstpageText", firstpageText()),
        ("FirstpageNumberField", firstpageNumberField()),
        ("FirstpageEmailRandNumber", firstpageEmailRandNumber()),
        ("FirstpageMoneyField",firstpageMoneyField()),
        ("SecondpageText",secondpageText()),
        ("ThirdpageText",thirdpageText()),
        ("ThirdpageNestedNumberField",thirdpageNestedNumberField()),
        ("CaseSummaryText",caseSummaryText()),
        ("CaseDescriptionText",caseDescriptionText()),
        ("PickCaseEventType",PickCaseType())
        ))
    .pause(constantThinkTime seconds)
		.exec(http("TX03_HMCTSReform_CCD_CreateCase_ClickCreqteCaseButton_options_case-types?access=create")
			.options("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types?access=create")
			.resources(http("TX03_HMCTSReform_CCD_CreateCase_ClickCreqteCaseButton_get_case-types?access=create")
			.get("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types?access=create")
			.headers(headers_6)))
		.pause(MinThinkTime seconds, MaxThinkTime seconds)
		
		val startNewCaseCreation = exec(http("TX04_HMCTSReform_CCD_CreateCase_startCaseCreation_options_ignore-warning=false")
			.options("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/event-triggers/${PickCaseEventType}?ignore-warning=false")
			.resources(http("TX04_HMCTSReform_CCD_CreateCase_startCaseCreation_get_ignore-warning=false")
			.get("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/event-triggers/${PickCaseEventType}?ignore-warning=false")
			.headers(headers_6),
            http("TX04_HMCTSReform_CCD_CreateCase_startCaseCreation_get_II_ignore-warning=false")
			.get("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/event-triggers/${PickCaseEventType}?ignore-warning=false")
			.check(jsonPath("$.event_token").saveAs("New_Case_event_token"))
			.headers(headers_6)))
		.pause(MinThinkTime seconds, MaxThinkTime seconds)
  
}