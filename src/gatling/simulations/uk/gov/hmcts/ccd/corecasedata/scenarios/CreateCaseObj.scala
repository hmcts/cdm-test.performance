package uk.gov.hmcts.ccd.corecasedata.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment

import scala.concurrent.duration._
import scala.util.Random

object CreateCaseObj {

  val IdamURL = Environment.idamURL
  val CCDEnvurl = Environment.ccdEnvurl
  val constantThinkTime = Environment.constantthinkTime
  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime
  val CommonHeader = Environment.commonHeader
	val caseTypeValue = Array("CREATE")//,"CREATEASPROG","CREATEASDONE")
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
    
  val selectJurisdiction = group("AT_Create") {
    exec(_.setAll(
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

      .exec(http("CDM_030_005_SelectJurisdiction")
        .options("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types?access=create"))

      .exec(http("CDM_030_010_SelectJurisdiction")
        .get("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types?access=create")
        .headers(CommonHeader))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)
  }
		
  val startNewCaseCreation = group("AT_Create") {
    exec(http("CDM_040_005_StartCreateCase")
      .options("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/event-triggers/${PickCaseEventType}?ignore-warning=false"))

      .exec(http("CDM_040_010_StartCreateCase")
        .get("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/event-triggers/${PickCaseEventType}?ignore-warning=false")
        .headers(CommonHeader))

      .exec(http("CDM_040_015_StartCreateCase")
        .get("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/event-triggers/${PickCaseEventType}?ignore-warning=false")
        .check(jsonPath("$.event_token").saveAs("New_Case_event_token"))
        .headers(CommonHeader))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)
  }
  
}