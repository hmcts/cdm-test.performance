package uk.gov.hmcts.ccd.corecasedata.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment

import scala.concurrent.duration._

object SubmitCase {
  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime
  val CommonHeader = Environment.commonHeader	
  val CCDAPIEnvurl = Environment.baseURL
  
  val checkyouranswersNSubmit = group("AT_Create") {
		doIf(session => session("Document_ID").as[String].isEmpty())
		{
			exec(http("CDM_080_005_SubmitCaseNo")
				.options("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases?ignore-warning=false"))

				.exec(http("CDM_080_010_SubmitCaseNo")
					.post("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases?ignore-warning=false")
					.headers(CommonHeader)
					.check(jsonPath("$.id").saveAs("New_Case_Id"))
					.body(StringBody("""{"data": {"TextField": "Performance Testing First Page Text ${FirstpageText}","NumberField": "${FirstpageNumberField}","YesOrNoField": "Yes","PhoneUKField": "02020002002","EmailField": "confirmation${FirstpageEmailRandNumber}@confirmation.com","MoneyGBPField": "${FirstpageMoneyField}","DateField": "2008-12-12","TextAreaField": "Performance Testing Second Page Text Area - ${SecondpageText}","FixedListField": "","MultiSelectListField": [],"ComplexField": {"ComplexTextField": "Performance Testing Third page Text - Third page Text ${ThirdpageText}","ComplexFixedListField": "","ComplexNestedField": {"NestedNumberField": "${ThirdpageNestedNumberField}","NestedCollectionTextField": []}},"DocumentField": {"document_url": "http://${DMURL}/${Document_ID}","document_binary_url": "http://${DMURL}/${Document_ID}/binary","document_filename": "${filename}"}},"event": {"id": "${PickCaseEventType}","summary": "Performance Testing PerfTestCase ${CaseSummaryText}","description": "Performance Testing Event Description ${CaseDescriptionText}"},"event_token": """"  + "${New_Case_event_token}" +   """","ignore_warning": false}""")))

				.pause(MinThinkTime seconds, MaxThinkTime seconds)
		}
			.doIf(session => ! session("Document_ID").as[String].isEmpty())
			{
				exec(http("CDM_080_005_SubmitCaseYes")
					.options("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases?ignore-warning=false"))

					.exec(http("CDM_080_010_SubmitCaseYes")
						.post("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases?ignore-warning=false")
						.headers(CommonHeader)
						.check(jsonPath("$.id").saveAs("New_Case_Id"))
						.body(StringBody("""{"data": {"TextField": "Performance Testing First Page Text ${FirstpageText}","NumberField": "${FirstpageNumberField}","YesOrNoField": "Yes","PhoneUKField": "02020002002","EmailField": "confirmation${FirstpageEmailRandNumber}@confirmation.com","MoneyGBPField": "${FirstpageMoneyField}","DateField": "2008-12-12","TextAreaField": "Performance Testing Second Page Text Area - ${SecondpageText}","FixedListField": "","MultiSelectListField": [],"ComplexField": {"ComplexTextField": "Performance Testing Third page Text - Third page Text ${ThirdpageText}","ComplexFixedListField": "","ComplexNestedField": {"NestedNumberField": "${ThirdpageNestedNumberField}","NestedCollectionTextField": []}}},"event": {"id": "${PickCaseEventType}","summary": "Performance Testing PerfTestCase ${CaseSummaryText}","description": "Performance Testing Event Description ${CaseDescriptionText}"},"event_token": """"  + "${New_Case_event_token}" +   """","ignore_warning": false}""")))

					.pause(MinThinkTime seconds, MaxThinkTime seconds)
			}

			.exec(http("CDM_090_005_ViewNewCase")
				.options("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}"))

			.exec(http("CDM_090_010_ViewNewCase")
				.get("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}")
				.headers(CommonHeader))

			.pause(MinThinkTime seconds, MaxThinkTime seconds)

			.exec(http("CDM_100_ViewThirdTab")
				.get("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}#ThirdTab")
				.headers(CommonHeader))

			.pause(MinThinkTime seconds, MaxThinkTime seconds)

			.exec(http("CDM_110_ViewDocument")
				.get(CCDAPIEnvurl + "/documents/${Document_ID}/binary")
				.check(status in  (200)))

			.pause(MinThinkTime seconds, MaxThinkTime seconds)

			.doIfEquals("${PickCaseEventType}", "CREATE")
			{
				exec(http("CDM_120_005_UpdateCaseUpdateStart")
					.options("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}/event-triggers/START_PROGRESS?ignore-warning=false"))

					.exec(http("CDM_120_010_UpdateCaseUpdateStart")
						.get("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}/event-triggers/START_PROGRESS?ignore-warning=false")
						.check(jsonPath("$.event_token").saveAs("UpdateCase_START_PROGRESSevent_token"))
						.check(status.is(200))
						.headers(CommonHeader))

					.pause(MinThinkTime seconds, MaxThinkTime seconds)

					.exec(http("CDM_120_015_UpdateCaseUpdateStart")
						.options("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}/events"))

					.exec(http("CDM_120_020_UpdateCaseUpdateStart")
						.post("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}/events")
						.headers(CommonHeader)
						.body(StringBody("""{"data": {},"event": {"id": "START_PROGRESS","summary": "Event summary Update Start ${CaseSummaryText}","description": "Event description Update Start ${CaseDescriptionText}"},"event_token": """"  + "${UpdateCase_START_PROGRESSevent_token}" +   """","ignore_warning": false}""")))

					.pause(MinThinkTime seconds, MaxThinkTime seconds)

					.exec(http("CDM_130_005_ViewNewCase")
						.options("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}"))

					.exec(http("CDM_130_010_ViewNewCase")
						.get("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}")
						.headers(CommonHeader))

					.pause(MinThinkTime seconds, MaxThinkTime seconds)

					.exec(http("CDM_140_005_MarkAsDone")
						.options("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}/event-triggers/COMPLETE?ignore-warning=false"))

					.exec(http("CDM_140_010_MarkAsDone")
						.get("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}/event-triggers/COMPLETE?ignore-warning=false")
						.check(jsonPath("$.event_token").saveAs("UpdateCase_COMPLETE_event_token"))
						.check(status.is(200))
						.headers(CommonHeader))

					.pause(MinThinkTime seconds, MaxThinkTime seconds)

					.exec(http("CDM_140_015_MarkAsDone")
						.options("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}/events"))

					.exec(http("CDM_140_020_MarkAsDone")
						.post("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}/events")
						.headers(CommonHeader)
						.body(StringBody("""{"data": {},"event": {"id": "COMPLETE","summary": "Event summary - Update Complete ${CaseSummaryText}","description": "Event description - Update Complete ${CaseDescriptionText}"},"event_token": """"  + "${UpdateCase_COMPLETE_event_token}" +   """","ignore_warning": false}""")))

					.pause(MinThinkTime seconds, MaxThinkTime seconds)

					.exec(http("CDM_150_005_ViewNewCase")
						.options("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}"))

					.exec(http("CDM_150_010_ViewNewCase")
						.get("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}")
						.headers(CommonHeader))

					.pause(MinThinkTime seconds, MaxThinkTime seconds)
			}

			.doIfEquals("${PickCaseEventType}", "CREATEASPROG")
			{
				exec(http("CDM_120_005_UpdateCaseUpdateStart")
					.options("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}/event-triggers/COMPLETE?ignore-warning=false"))

					.exec(http("CDM_120_010_UpdateCaseUpdateStart")
						.get("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}/event-triggers/COMPLETE?ignore-warning=false")
						.check(jsonPath("$.event_token").saveAs("UpdateCase_event_token"))
						.check(status.is(200))
						.headers(CommonHeader))

					.pause(MinThinkTime seconds, MaxThinkTime seconds)

					.exec(http("CDM_120_015_UpdateCaseUpdateStart")
						.options("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}/events"))

					.exec(http("CDM_120_020_UpdateCaseUpdateStart")
						.post("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}/events")
						.headers(CommonHeader)
						.body(StringBody("""{"data": {},"event": {"id": "COMPLETE","summary": "Performance Testing PerfTestCase - CASE UPDATE PROGRESS COMPLETE ${CaseSummaryText}","description": "Performance Testing Event Description -CASE UPDATE PROGRESS COMPLETE ${CaseDescriptionText}"},"event_token": """"  + "${UpdateCase_event_token}" +   """","ignore_warning": false}""")))

					.pause(MinThinkTime seconds, MaxThinkTime seconds)

					.exec(http("CDM_130_005_ViewNewCase")
						.options("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}"))

					.exec(http("CDM_130_010_ViewNewCase")
						.get("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${New_Case_Id}")
						.headers(CommonHeader))

					.pause(MinThinkTime seconds, MaxThinkTime seconds)
			}
	}
}