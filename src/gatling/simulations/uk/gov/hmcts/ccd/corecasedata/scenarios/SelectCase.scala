package uk.gov.hmcts.ccd.corecasedata.scenarios

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment

object SelectCase {
	val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime
  val CommonHeader = Environment.commonHeader
	val CCDCreateCaseFeeder = csv("CCD_CreateCase_TestData.csv").circular

  val selectAndViewCase = group("AT_View") {
		exec(http("CDM_180_005_OpenNewCase")
			.options("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${SearchParam_Case_Id}"))

			.exec(http("CDM_180_010_OpenNewCase")
				.get("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/${SearchParam_Case_Id}")
				.headers(CommonHeader))

			.pause(MinThinkTime seconds, MaxThinkTime seconds)
	}
}