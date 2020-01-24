package uk.gov.hmcts.ccd.corecasedata.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment
import scala.concurrent.duration._

object IAC {

  val BaseURL = Environment.baseURL
  val IdamURL = Environment.idamURL
  val CCDEnvurl = Environment.ccdEnvurl
  val CommonHeader = Environment.commonHeader
  val idam_header = Environment.idam_header
  val feedUserData = csv("IACUserData.csv").circular
  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val IALogin = group("IAC_Login") {

    exec(http("IAC_020_005_Login")
      .post(IdamURL + "/login?response_type=code&client_id=ccd_gateway&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
      .disableFollowRedirect
      .headers(idam_header)
      .formParam("username", "${IACUserName}")
      .formParam("password", "${IACUserPassword}")
      .formParam("save", "Sign in")
      .formParam("selfRegistrationEnabled", "false")
      .formParam("_csrf", "${csrf}")
      .check(headerRegex("Location", "(?<=code=)(.*)&scope").saveAs("authCode"))
      .check(status.in(200, 302))

      .resources(http("IAC_020_010_Login")
        .get(CCDEnvurl + "/config")
        .headers(CommonHeader)))

      .exec(http("IAC_020_015_Login")
        .options(BaseURL + "/oauth2?code=${authCode}&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
        .headers(CommonHeader))
      .exec(http("IAC_020_020_Login")
        .get(BaseURL + "/oauth2?code=${authCode}&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
        .headers(CommonHeader))
      .exec(http("IAC_020_025_Login")
        .get(CCDEnvurl + "/config")
        .headers(CommonHeader))

      .exec(http("IAC_020_030_Login")
        .options(BaseURL + "/data/caseworkers/:uid/profile"))

      .exec(http("IAC_020_035_Login")
        .get(BaseURL + "/data/caseworkers/:uid/profile")
        .headers(CommonHeader))

      .exec(http("IAC_020_040_Login")
        .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/SSCS/case-types?access=read")
        .resources(http("IAC_020_045_Login")
          .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/SSCS/case-types?access=read")
          .headers(CommonHeader)))

      .exec(http("IAC_020_050_Login")
        .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/SSCS/case-types/Benefit/work-basket-inputs"))

      .exec(http("IAC_020_055_Login")
        .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/SSCS/case-types/Benefit/cases?view=WORKBASKET&state=TODO&page=1"))

      .exec(http("IAC_020_060_Login")
        .options(BaseURL + "/data/caseworkers/:uid/jurisdictions/SSCS/case-types/Benefit/cases/pagination_metadata?state=TODO"))

      .exec(http("IAC_020_065_Login")
        .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/SSCS/case-types/Benefit/work-basket-inputs")
        .headers(CommonHeader))

      .exec(http("IAC_020_070_Login")
        .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/SSCS/case-types/Benefit/cases?view=WORKBASKET&state=TODO&page=1")
        .headers(CommonHeader))

      .exec(http("IAC_020_075_Login")
        .get(BaseURL + "/data/caseworkers/:uid/jurisdictions/SSCS/case-types/Benefit/cases/pagination_metadata?state=TODO")
        .headers(CommonHeader))
  }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)


}
