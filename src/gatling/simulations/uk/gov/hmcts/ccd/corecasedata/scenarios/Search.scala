package uk.gov.hmcts.ccd.corecasedata.scenarios

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment

object Search {
	val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime
  val CommonHeader = Environment.commonHeader
	val CCDCreateCaseFeeder = csv("CCD_CreateCase_TestData.csv").circular

  val searchRequest = group("AT_View") {
		exec(http("CDM_160_005_SearchRequest")
			.options("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/pagination_metadata"))

			.exec(http("CDM_160_010_SearchRequest")
				.get("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/pagination_metadata")
				.headers(CommonHeader))

			.pause(MinThinkTime seconds, MaxThinkTime seconds)
	}
		
	val searchResult = group("AT_View") {
		exec(http("CDM_170_005_SearchResult")
			.options("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases?view=WORKBASKET&page=1"))

			.exec(http("CDM_170_010_SearchResult")
				.options("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/pagination_metadata?state=TODO"))

			.exec(http("CDM_170_015_SearchResult")
				.get("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases?view=WORKBASKET&page=1")
				.headers(CommonHeader)
				.check(jsonPath("$.results[*].case_id").saveAs("SearchParam_Case_Id"))
				//.check(jsonPath("\"CaseReference\":\"[*]\"}").saveAs("SearchParam_Case_Id"))
			)

			.exec(http("CDM_170_020_SearchResult")
				.get("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/pagination_metadata?state=ANY")
				.headers(CommonHeader))

			.pause(MinThinkTime seconds, MaxThinkTime seconds)
	}
}
