package uk.gov.hmcts.ccd.corecasedata.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment

import scala.concurrent.duration._

object ProbateSearch {

  val BaseURL = Environment.baseURL
  val IdamURL = Environment.idamURL
  val CCDEnvurl = Environment.ccdEnvurl
  val CommonHeader = Environment.commonHeader
  val idam_header = Environment.idam_header
  val feedUserData = csv("ProbateUserData.csv").circular
  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val ProbateLogin = group("PB_Login") {

    exec(http("CDM_020_005_Login")
      .post(IdamURL + "/login?response_type=code&client_id=ccd_gateway&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
      .disableFollowRedirect
      .headers(idam_header)
      .formParam("username", "${ProbateUserName}")
      .formParam("password", "${ProbateUserPassword}")
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
      .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/PROBATE/case-types?access=read")
      .resources(http("CDM_020_045_Login")
        .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/PROBATE/case-types?access=read")
        .headers(CommonHeader)))

    .exec(http("CDM_020_050_Login")
      .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/PROBATE/case-types/Caveat/work-basket-inputs"))

    .exec(http("CDM_020_055_Login")
      .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/PROBATE/case-types/Caveat/cases?view=WORKBASKET&state=TODO&page=1"))

    .exec(http("CDM_020_060_Login")
      .options(BaseURL + "/data/caseworkers/:uid/jurisdictions/PROBATE/case-types/Caveat/cases/pagination_metadata?state=TODO"))

    .exec(http("CDM_020_065_Login")
      .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/PROBATE/case-types/Caveat/work-basket-inputs")
      .headers(CommonHeader))

    .exec(http("CDM_020_070_Login")
      .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/PROBATE/case-types/Caveat/cases?view=WORKBASKET&state=TODO&page=1")
      .headers(CommonHeader))

    .exec(http("CDM_020_075_Login")
      .get(BaseURL + "/data/caseworkers/:uid/jurisdictions/PROBATE/case-types/Caveat/cases/pagination_metadata?state=TODO")
      .headers(CommonHeader))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)
  }

  val SearchResult = group("PB_SearchResults") {

    exec(http("PB_SearchResults_005")
      .options("/aggregated/caseworkers/:uid/jurisdictions/PROBATE/case-types/Caveat/cases?view=WORKBASKET&page=1")
      .headers(CommonHeader))

    .exec(http("PB_SearchResults_010")
      .options("/data/caseworkers/:uid/jurisdictions/PROBATE/case-types/Caveat/cases/pagination_metadata")
      .headers(CommonHeader))

    .exec(http("PB_SearchResults_015")
      .get("/data/caseworkers/:uid/jurisdictions/PROBATE/case-types/Caveat/cases/pagination_metadata")
      .headers(CommonHeader))

    .exec(http("PB_SearchResults_020")
      .get("/aggregated/caseworkers/:uid/jurisdictions/PROBATE/case-types/Caveat/cases?view=WORKBASKET&page=1")
      .headers(CommonHeader)
      //.check(jsonPath("$[*]").ofType[Map[String, Any]].findAll.saveAs("theArray"))
      )
      .pause(MinThinkTime seconds, MaxThinkTime seconds)
  }

}
