package uk.gov.hmcts.ccd.corecasedata.scenarios
import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment


object Logout {
 
   val IdamURL = Environment.idamURL
    val CCDEnvurl = Environment.ccdEnvurl
    val MinThinkTime = Environment.minThinkTime
    val MaxThinkTime = Environment.maxThinkTime
    
  val ccdLogout = exec(http("TX20_HMCTSReform_CCD_Logout")
			.options("/logout")
			.resources(http("TX20_HMCTSReform_CCD_Logout_getLogout")
			.get("/logout")
			.check(status.in(200,204,401)),
            http("TX20_HMCTSReform_CCD_Logout_getRedirectURL")
			.get(IdamURL + "/login?response_type=code&client_id=ccd_gateway&redirect_uri=" + CCDEnvurl + "%2Foauth2redirect")
			.check(status.in(200,401)),
            http("TX20_HMCTSReform_CCD_Logout_govuk-template.css")
			.get(IdamURL + "/public/stylesheets/govuk-template.css?0.23.0"),
            http("TX20_HMCTSReform_CCD_Logout_fonts.css")
			.get(IdamURL + "/public/stylesheets/fonts.css?0.23.0"),
            http("TX20_HMCTSReform_CCD_Logout_gov.uk_logotype_crown_invert_trans.png")
			.get(IdamURL + "/public/images/gov.uk_logotype_crown_invert_trans.png?0.23.0"),
            http("TX20_HMCTSReform_CCD_Logout_govuk-template.js")
			.get(IdamURL + "/public/javascripts/govuk-template.js?0.23.0"),
            http("TX20_HMCTSReform_CCD_Logout_govuk-template-print.css")
			.get(IdamURL + "/public/stylesheets/govuk-template-print.css?0.23.0"),
            http("TX20_HMCTSReform_CCD_Logout_govuk-crest-2x.png")
			.get(IdamURL + "/public/stylesheets/images/govuk-crest-2x.png?0.23.0"),
            http("TX20_HMCTSReform_CCD_Logout_open-government-licence_2x.png")
			.get(IdamURL + "/public/stylesheets/images/open-government-licence_2x.png?0.23.0"),
            http("TX20_HMCTSReform_CCD_Logout_gov.uk_logotype_crown.png")
			.get(IdamURL + "/public/stylesheets/images/gov.uk_logotype_crown.png?0.23.0")))
		  
			.pause(MinThinkTime seconds, MaxThinkTime seconds)
			
}