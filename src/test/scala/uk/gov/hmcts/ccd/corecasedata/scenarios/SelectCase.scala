package uk.gov.hmcts.ccd.corecasedata.scenarios
import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment

object SelectCase {
	val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime
  val CommonHeader = Environment.commonHeader	
  val selectAndViewCase = exec(http("TX16_HMCTSReform_CCD_UISearchAndViewCase_SelectAndViewCase_options")
			.options("/aggregated/caseworkers/:uid/jurisdictions/AUTOTEST2/case-types/ATCASETYPE4/cases/${SearchParam_Case_Id}")
			.resources(http("TX17_HMCTSReform_CCD_UISearchAndViewCase_SelectAndViewCase_get")
			.get("/aggregated/caseworkers/:uid/jurisdictions/AUTOTEST2/case-types/ATCASETYPE4/cases/${SearchParam_Case_Id}")
			.headers(CommonHeader)))
		.pause(MinThinkTime seconds, MaxThinkTime seconds)
}