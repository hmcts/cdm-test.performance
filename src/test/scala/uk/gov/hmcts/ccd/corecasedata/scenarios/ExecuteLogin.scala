package uk.gov.hmcts.ccd.corecasedata.scenarios

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment

object ExecuteLogin {

    val IdamURL = Environment.idamURL
    val CCDEnvurl = Environment.ccdEnvurl
    val MinThinkTime = Environment.minThinkTime
    val MaxThinkTime = Environment.maxThinkTime
    val constantThinkTime = Environment.constantthinkTime
    val MinWaitForNextIteration = Environment.minWaitForNextIteration
    val MaxWaitForNextIteration = Environment.maxWaitForNextIteration
    
    val feedUserData = csv("CCDUserData.csv").circular
    val CCDCreateCaseFeeder = csv("CCD_CreateCase_TestData.csv").circular
    
    val CommonHeader = Environment.commonHeader
    
  val submitLogin = exec(http("TX02_HMCTSReform_CCD_submitLogin")
			.post(IdamURL + "/login?response_type=code&client_id=ccd_gateway&redirect_uri=" + CCDEnvurl + "%2Foauth2redirect").disableFollowRedirect
			.formParam("username", "${CCDUserName}")  
			.formParam("password", "${CCDUserPassword}")    
			.formParam("continue", CCDEnvurl+"/oauth2redirect")
			.formParam("upliftToken", "")
			.formParam("response_type", "code")
			.formParam("_csrf", "${csrftoken}")
			.formParam("redirect_uri", CCDEnvurl+"/oauth2redirect")
			.formParam("client_id", "ccd_gateway")
			.formParam("scope", "")
			.formParam("state", "")
			.check(headerRegex("Location", "(?<=code=)(.*)").saveAs("authCode"))
		  .check(status.in(200,302)))
      .exec(http("TX02_HMCTSReform_CCD_submitLogin_get_config")
			.get(CCDEnvurl + "/config")
			.headers(CommonHeader))
       .exec(http("TX02_HMCTSReform_CCD_submitLogin_option_oauth2redirect")
			.options("/oauth2?code=${authCode}&redirect_uri="+CCDEnvurl+"/oauth2redirect"))
       .exec(http("TX02_HMCTSReform_CCD_submitLogin_get_oauth2redirect")
			.get("/oauth2?code=${authCode}&redirect_uri="+CCDEnvurl+"/oauth2redirect")
			.headers(CommonHeader))
       .exec(http("TX02_HMCTSReform_CCD_submitLogin_get_config")
			.get(CCDEnvurl + "/config")
			.headers(CommonHeader))
       .exec(http("TX02_HMCTSReform_CCD_submitLogin_option_profile")
			.options("/data/caseworkers/:uid/profile"))
       .exec(http("TX02_HMCTSReform_CCD_submitLogin_get_profile")
			.get("/data/caseworkers/:uid/profile")
			.headers(CommonHeader))
		.pause(constantThinkTime seconds)
		
		.exec(http("TX02_HMCTSReform_CCD_submitLogin_option_case-types_access_read")
			.options("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types?access=read")
			.resources(http("TX02_HMCTSReform_CCD_submitLogin_get_case-types_access_read")
			.get("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types?access=read")
			.headers(CommonHeader),
            http("TX02_HMCTSReform_CCD_submitLogin_options_work-basket-inputs")
			.options("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/work-basket-inputs"),
            http("TX02_HMCTSReform_CCD_submitLogin_options_casesview_WORKBASKET&state=TODO&page=1")
			.options("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases?view=WORKBASKET&state=TODO&page=1"),
            http("TX02_HMCTSReform_CCD_submitLogin_options_pagination_metadata?state=TODO")
			.options("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/pagination_metadata?state=TODO"),
            http("TX02_HMCTSReform_CCD_submitLogin_get_work-basket-inputs")
			.get("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/work-basket-inputs")
			.headers(CommonHeader),
            http("TX02_HMCTSReform_CCD_submitLogin_get_casesview_WORKBASKET&state=TODO&page=1")
			.get("/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases?view=WORKBASKET&state=TODO&page=1")
			.headers(CommonHeader),
            http("TX02_HMCTSReform_CCD_submitLogin_pagination_metadata?state=TODO")
			.get("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/pagination_metadata?state=TODO")
			.headers(CommonHeader)))
		.pause(MinThinkTime seconds, MaxThinkTime seconds)

}