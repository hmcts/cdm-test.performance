package uk.gov.hmcts.ccd.corecasedata.scenarios

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment

object Search {
	val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime
  val CommonHeader = Environment.commonHeader
  val searchRequest = exec(http("TX10_HMCTSReform_CCD_UISearchAndViewCase_SearchRequest_ByJurisdiction_CaseType_State_option_work_basket_inputs")
			.options("/aggregated/caseworkers/:uid/jurisdictions/AUTOTEST2/case-types/ATCASETYPE4/work-basket-inputs")
			.resources(http("TX11_HMCTSReform_CCD_UISearchAndViewCase_SearchRequest_ByJurisdiction_CaseType_State_get_work_basket_inputs")
			.get("/aggregated/caseworkers/:uid/jurisdictions/AUTOTEST2/case-types/ATCASETYPE4/work-basket-inputs")
			.headers(CommonHeader)))
		.pause(MinThinkTime seconds, MaxThinkTime seconds)
		
		
	val searchResult = exec(http("TX12_HMCTSReform_CCD_UISearchAndViewCase_SearchResult_ByJurisdiction_CaseType_State_optionscases_view_WORKBASKE")
			.options("/aggregated/caseworkers/:uid/jurisdictions/AUTOTEST2/case-types/ATCASETYPE4/cases?view=WORKBASKET&state=TODO&page=1")
			.resources(http("TX13_HMCTSReform_CCD_UISearchAndViewCase_SearchResult_ByJurisdiction_CaseType_State_optionspagination_metadata")
			.options("/data/caseworkers/:uid/jurisdictions/AUTOTEST2/case-types/ATCASETYPE4/cases/pagination_metadata?state=TODO"),
            http("TX14_HMCTSReform_CCD_UISearchAndViewCase_SearchResult_ByJurisdiction_CaseType_State_getcases_view_WORKBASKE")
			.get("/aggregated/caseworkers/:uid/jurisdictions/AUTOTEST2/case-types/ATCASETYPE4/cases?view=WORKBASKET&state=TODO&page=1")
			.headers(CommonHeader)
			.check(jsonPath("$.results[*].case_id").saveAs("SearchParam_Case_Id")),
            http("TX15_HMCTSReform_CCD_UISearchAndViewCase_SearchResult_ByJurisdiction_CaseType_State_getpagination_metadata")
			.get("/data/caseworkers/:uid/jurisdictions/AUTOTEST2/case-types/ATCASETYPE4/cases/pagination_metadata?state=TODO")
			.headers(CommonHeader)))
		.pause(MinThinkTime seconds, MaxThinkTime seconds)
}