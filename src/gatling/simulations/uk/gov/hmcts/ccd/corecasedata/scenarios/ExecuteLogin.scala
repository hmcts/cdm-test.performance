package uk.gov.hmcts.ccd.corecasedata.scenarios

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils._

object ExecuteLogin {

	val IdamURL = Environment.idamURL
	val CCDEnvurl = Environment.ccdEnvurl
	val BaseURL = Environment.baseURL

	val MinThinkTime = Environment.minThinkTime
	val MaxThinkTime = Environment.maxThinkTime
	val constantThinkTime = Environment.constantthinkTime
	val MinWaitForNextIteration = Environment.minWaitForNextIteration
	val MaxWaitForNextIteration = Environment.maxWaitForNextIteration

	val feedUserData = csv("CCDUserData.csv").circular
	//val CCDCreateCaseFeeder = csv("CCD_CreateCase_TestData.csv").circular

	val CommonHeader = Environment.commonHeader
	val idam_header = Environment.idam_header

  val submitLogin = group("CDM_Login") {

		exec(http("CDM_020_005_Login")
			.post(IdamURL + "/login?response_type=code&client_id=ccd_gateway&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
			.disableFollowRedirect
			.headers(idam_header)
			.formParam("username", "${CCDUserName}")
			.formParam("password", "${CCDUserPassword}")
			.formParam("save", "Sign in")
			.formParam("selfRegistrationEnabled", "false")
			.formParam("_csrf", "${csrf}")
			.check(headerRegex("Location", "(?<=code=)(.*)&scope").saveAs("authCode"))
			.check(status.in(200, 302))

			.resources(http("CDM_020_010_Login")
				.get(CCDEnvurl + "/config")
				.headers(CommonHeader)))

			.exec(http("CDM_020_015_Login")
				.options(BaseURL + "/oauth2?code=${authCode}&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
				.headers(CommonHeader))
			.exec(http("CDM_020_020_Login")
				.get(BaseURL + "/oauth2?code=${authCode}&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
				.headers(CommonHeader))
			.exec(http("CDM_020_025_Login")
				.get(CCDEnvurl + "/config")
				.headers(CommonHeader))

			.exec(http("CDM_020_030_Login")
				.options(BaseURL + "/data/caseworkers/:uid/profile"))

			.exec(http("CDM_020_035_Login")
				.get(BaseURL + "/data/caseworkers/:uid/profile")
				.headers(CommonHeader))

			.exec(http("CDM_020_040_Login")
				.options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types?access=read")
				.resources(http("CDM_020_045_Login")
					.get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types?access=read")
					.headers(CommonHeader)))

			.exec(http("CDM_020_050_Login")
				.options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/work-basket-inputs"))

			.exec(http("CDM_020_055_Login")
				.options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases?view=WORKBASKET&state=TODO&page=1"))

			.exec(http("CDM_020_060_Login")
				.options(BaseURL + "/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/pagination_metadata?state=TODO"))

			.exec(http("CDM_020_065_Login")
				.get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/work-basket-inputs")
				.headers(CommonHeader))

			.exec(http("CDM_020_070_Login")
				.get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases?view=WORKBASKET&state=TODO&page=1")
				.headers(CommonHeader))

			.exec(http("CDM_020_075_Login")
				.get(BaseURL + "/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/pagination_metadata?state=TODO")
				.headers(CommonHeader))
	}

		.pause(MinThinkTime seconds, MaxThinkTime seconds)

}