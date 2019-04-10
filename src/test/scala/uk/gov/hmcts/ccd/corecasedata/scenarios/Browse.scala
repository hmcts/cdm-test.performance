package uk.gov.hmcts.ccd.corecasedata.scenarios
import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.checks.{CsrfCheck, CurrentPageCheck}
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment

object Browse {
  
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
		
    val loginPage = exec(http("TX01_HMCTSReform_CCD_Login")
			.get(CCDEnvurl + "/")
			.resources(
            http("TX01_HMCTSReform_CCD_Login_get_config")
			.get(CCDEnvurl + "/config")
			.headers(CommonHeader),
            http("TX01_HMCTSReform_CCD_Login_option_profile")
			.options("/data/caseworkers/:uid/profile"),
            http("TX01_HMCTSReform_CCD_Login_get_profile")
			.get("/data/caseworkers/:uid/profile")
			.headers(CommonHeader)
			.check(status.is(401)),
            http("TX01_HMCTSReform_CCD_Login_redirectURL")
			.get(IdamURL + "/login?response_type=code&client_id=ccd_gateway&redirect_uri=" + CCDEnvurl + "%2Foauth2redirect")
			.check(CsrfCheck.save),
            http("TX01_HMCTSReform_CCD_Login_govuk-template.css")
			.get(IdamURL + "/public/stylesheets/govuk-template.css?0.23.0"),
            http("TX01_HMCTSReform_CCD_Login_fonts.css")
			.get(IdamURL + "/public/stylesheets/fonts.css?0.23.0"),
            http("TX01_HMCTSReform_CCD_Login_gov.uk_logotype_crown_invert_trans.png?")
			.get(IdamURL + "/public/images/gov.uk_logotype_crown_invert_trans.png?0.23.0"),
            http("TX01_HMCTSReform_CCD_Login_govuk-template.js")
			.get(IdamURL + "/public/javascripts/govuk-template.js?0.23.0"),
            http("TX01_HMCTSReform_CCD_Login_govuk-template-print.css")
			.get(IdamURL + "/public/stylesheets/govuk-template-print.css?0.23.0"),
            http("TX01_HMCTSReform_CCD_Login_govuk-crest-2x.png")
			.get(IdamURL + "/public/stylesheets/images/govuk-crest-2x.png?0.23.0"),
            http("TX01_HMCTSReform_CCD_Login_open-government-licence_2x.png")
			.get(IdamURL + "/public/stylesheets/images/open-government-licence_2x.png?0.23.0"),
            http("TX01_HMCTSReform_CCD_Login_gov.uk_logotype_crown.png")
			.get(IdamURL + "/public/stylesheets/images/gov.uk_logotype_crown.png?0.23.0")
			))
		.pause(MinThinkTime seconds, MaxThinkTime seconds)
		.feed(CCDCreateCaseFeeder)
		.feed(feedUserData)
		
}