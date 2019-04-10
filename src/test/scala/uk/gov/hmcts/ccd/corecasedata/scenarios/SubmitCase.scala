package uk.gov.hmcts.ccd.corecasedata.scenarios

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment

object SubmitCase {
  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime
  val CommonHeader = Environment.commonHeader	
  val CCDAPIEnvurl = Environment.baseURL
  
  val checkyouranswersNSubmit =  doIfOrElse(session => !session("Document_ID").equals(Nil)) { // this doesn't work 
         exec(http("TX08_HMCTSReform_CCD_CreateCase_SubmitCase_options_cases?ignore-warning=false")
			.options("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases?ignore-warning=false")
			.resources(http("TX08_HMCTSReform_CCD_CreateCase_SubmitCase_post_cases?ignore-warning=false")
			.post("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases?ignore-warning=false")
			.headers(CommonHeader)
			.check(jsonPath("$.id").saveAs("New_Case_Id"))
			.body(StringBody("""{"data": {"TextField": "Performance Testing First Page Text ${FirstpageText}","NumberField": "${FirstpageNumberField}","YesOrNoField": "Yes","PhoneUKField": "02020002002","EmailField": "confirmation${FirstpageEmailRandNumber}@confirmation.com","MoneyGBPField": "${FirstpageMoneyField}","DateField": "2008-12-12","TextAreaField": "Performance Testing Second Page Text Area - ${SecondpageText}","FixedListField": "","MultiSelectListField": [],"ComplexField": {"ComplexTextField": "Performance Testing Third page Text - Third page Text ${ThirdpageText}","ComplexFixedListField": "","ComplexNestedField": {"NestedNumberField": "${ThirdpageNestedNumberField}","NestedCollectionTextField": []}},"DocumentField": {"document_url": "http://${DMURL}/${Document_ID}","document_binary_url": "http://${DMURL}/${Document_ID}/binary","document_filename": "${filename}"}},"event": {"id": "${PickCaseEventType}","summary": "Performance Testing PerfTestCase ${CaseSummaryText}","description": "Performance Testing Event Description ${CaseDescriptionText}"},"event_token": """"  + "${New_Case_event_token}" +   """","ignore_warning": false}"""))))
			.pause(MinThinkTime seconds, MaxThinkTime seconds)
      }
      {
         exec(http("TX08_HMCTSReform_CCD_CreateCase_SubmitCase_options_cases?ignore-warning=false")
			.options("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases?ignore-warning=false")
			.resources(http("TX08_HMCTSReform_CCD_CreateCase_SubmitCase_post_cases?ignore-warning=false")
			.post("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases?ignore-warning=false")
			.headers(CommonHeader)
			.check(jsonPath("$.id").saveAs("New_Case_Id"))
			.body(StringBody("""{"data": {"TextField": "Performance Testing First Page Text ${FirstpageText}","NumberField": "${FirstpageNumberField}","YesOrNoField": "Yes","PhoneUKField": "02020002002","EmailField": "confirmation${FirstpageEmailRandNumber}@confirmation.com","MoneyGBPField": "${FirstpageMoneyField}","DateField": "2008-12-12","TextAreaField": "Performance Testing Second Page Text Area - ${SecondpageText}","FixedListField": "","MultiSelectListField": [],"ComplexField": {"ComplexTextField": "Performance Testing Third page Text - Third page Text ${ThirdpageText}","ComplexFixedListField": "","ComplexNestedField": {"NestedNumberField": "${ThirdpageNestedNumberField}","NestedCollectionTextField": []}}},"event": {"id": "${PickCaseEventType}","summary": "Performance Testing PerfTestCase ${CaseSummaryText}","description": "Performance Testing Event Description ${CaseDescriptionText}"},"event_token": """"  + "${New_Case_event_token}" +   """","ignore_warning": false}"""))))
			.pause(MinThinkTime seconds, MaxThinkTime seconds)
      }
		  
		.exec(http("TX09_HMCTSReform_CCD_CreateCase_NewCaseDisplay_options_get")
			.options("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}")
			.resources(http("TX09_HMCTSReform_CCD_CreateCase_NewCaseDisplay_get")
			.get("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}")
			.headers(CommonHeader)))
		.pause(MinThinkTime seconds, MaxThinkTime seconds)
		
		.exec(http("TX09.1.1_HMCTSReform_CCD_CreateCase_NewCaseDisplayThirdTab_get")
			.get("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}#ThirdTab")
			.headers(CommonHeader))
		.pause(MinThinkTime seconds, MaxThinkTime seconds)
		
		.exec(http("TX09.1.2_HMCTSReform_CCD_CreateCase_get_${filename}_ViewDocument")
			.get(CCDAPIEnvurl + "/documents/${Document_ID}/binary")
			.check(status in  (200)))
		.pause(MinThinkTime seconds, MaxThinkTime seconds)
		.pause(MinThinkTime seconds, MaxThinkTime seconds)
	
		
		
		.doIfEquals("${PickCaseEventType}", "CREATE") {
       exec(http("TX09.1_HMCTSReform_CCD_UpdateCase_UpdateStart_options_start")
			.options("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}/event-triggers/START_PROGRESS?ignore-warning=false")
			.resources(http("TX09.2_HMCTSReform_CCD_UpdateCase_UpdateStart_get_start")
			.get("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}/event-triggers/START_PROGRESS?ignore-warning=false")
			.check(jsonPath("$.event_token").saveAs("UpdateCase_START_PROGRESSevent_token"))
			.check(status.is(200))
			.headers(CommonHeader)))
		  .pause(MinThinkTime seconds, MaxThinkTime seconds)
		
		
		  .exec(http("TX09.3_HMCTSReform_CCD_UpdateCase_UpdateStart_POST_options_updatepoststart")
			.options("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}/events")
			.resources(http("TX09.4_HMCTSReform_CCD_UpdateCase_UpdateStart_POST_updatepoststart")
			.post("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}/events")
			.headers(CommonHeader)
			.body(StringBody("""{"data": {},"event": {"id": "START_PROGRESS","summary": "Event summary Update Start ${CaseSummaryText}","description": "Event description Update Start ${CaseDescriptionText}"},"event_token": """"  + "${UpdateCase_START_PROGRESSevent_token}" +   """","ignore_warning": false}"""))))
	    .pause(MinThinkTime seconds, MaxThinkTime seconds)
			
			.exec(http("TX09_HMCTSReform_CCD_CreateCase_NewCaseDisplay_options_get")
			.options("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}")
			.resources(http("TX09_HMCTSReform_CCD_CreateCase_NewCaseDisplay_get")
			.get("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}")
			.headers(CommonHeader)))
		  .pause(MinThinkTime seconds, MaxThinkTime seconds)
		  
		  
		  .exec(http("TX09.4_HMCTSReform_CCD_UpdateCase_UpdateMarkAsDone_options_start")
			.options("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}/event-triggers/COMPLETE?ignore-warning=false")
			.resources(http("TX09.5_HMCTSReform_CCD_UpdateCase_UpdateMarkAsDone_get_start")
			.get("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}/event-triggers/COMPLETE?ignore-warning=false")
			.check(jsonPath("$.event_token").saveAs("UpdateCase_COMPLETE_event_token"))
			.check(status.is(200))
			.headers(CommonHeader)))
		  .pause(MinThinkTime seconds, MaxThinkTime seconds)
		
		  .exec(http("TX09.6_HMCTSReform_CCD_UpdateCase_UpdateMarkAsDone_POST_options_updatepoststart")
			.options("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}/events")
			.resources(http("TX09.7_HMCTSReform_CCD_UpdateCase_UpdateMarkAsDone_POST_updatepoststart")
			.post("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}/events")
			.headers(CommonHeader)
			.body(StringBody("""{"data": {},"event": {"id": "COMPLETE","summary": "Event summary - Update Complete ${CaseSummaryText}","description": "Event description - Update Complete ${CaseDescriptionText}"},"event_token": """"  + "${UpdateCase_COMPLETE_event_token}" +   """","ignore_warning": false}"""))))
	    .pause(MinThinkTime seconds, MaxThinkTime seconds)
			
			.exec(http("TX09_HMCTSReform_CCD_CreateCase_NewCaseDisplay_options_get")
			.options("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}")
			.resources(http("TX09_HMCTSReform_CCD_CreateCase_NewCaseDisplay_get")
			.get("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}")
			.headers(CommonHeader)))
		  .pause(MinThinkTime seconds, MaxThinkTime seconds)
     }

     .doIfEquals("${PickCaseEventType}", "CREATEASPROG"){
     
		  exec(http("TX09.4_HMCTSReform_CCD_UpdateCase_UpdateMarkAsDone_options_start")
			.options("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}/event-triggers/COMPLETE?ignore-warning=false")
			.resources(http("TX09.5_HMCTSReform_CCD_UpdateCase_UpdateMarkAsDone_get_start")
			.get("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}/event-triggers/COMPLETE?ignore-warning=false")
			.check(jsonPath("$.event_token").saveAs("UpdateCase_event_token"))
			.check(status.is(200))
			.headers(CommonHeader)))
		.pause(MinThinkTime seconds, MaxThinkTime seconds)
		
		.exec(http("TX09.6_HMCTSReform_CCD_UpdateCase_UpdateMarkAsDone_POST_options_updatepoststart")
			.options("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}/events")
			.resources(http("TX09.7_HMCTSReform_CCD_UpdateCase_UpdateMarkAsDone_POST_updatepoststart")
			.post("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}/events")
			.headers(CommonHeader)
			.body(StringBody("""{"data": {},"event": {"id": "COMPLETE","summary": "Performance Testing PerfTestCase - CASE UPDATE PROGRESS COMPLETE ${CaseSummaryText}","description": "Performance Testing Event Description -CASE UPDATE PROGRESS COMPLETE ${CaseDescriptionText}"},"event_token": """"  + "${UpdateCase_event_token}" +   """","ignore_warning": false}"""))))
		  .pause(MinThinkTime seconds, MaxThinkTime seconds)
      
       .exec(http("TX09_HMCTSReform_CCD_CreateCase_NewCaseDisplay_options_get")
			.options("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}")
			.resources(http("TX09_HMCTSReform_CCD_CreateCase_NewCaseDisplay_get")
			.get("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}")
			.headers(CommonHeader)))
		  .pause(MinThinkTime seconds, MaxThinkTime seconds)
		  
     } 
}