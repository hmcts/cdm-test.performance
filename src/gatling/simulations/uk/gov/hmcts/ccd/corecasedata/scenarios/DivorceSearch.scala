package uk.gov.hmcts.ccd.corecasedata.scenarios

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment

object DivorceSearch {

  val BaseURL = Environment.baseURL
  val IdamURL = Environment.idamURL
  val CCDEnvurl = Environment.ccdEnvurl
  val CommonHeader = Environment.commonHeader
  val idam_header = Environment.idam_header
  val feedUserData = csv("DivorceUserData.csv").circular
  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val DivorceLogin = group("DIV_Login") {

    exec(http("CDM_020_005_Login")
      .post(IdamURL + "/login?response_type=code&client_id=ccd_gateway&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
      .disableFollowRedirect
      .headers(idam_header)
      .formParam("username", "${DivorceUserName}")
      .formParam("password", "${DivorceUserPassword}")
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
      .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/DIVORCE/case-types?access=read")
      .resources(http("CDM_020_045_Login")
        .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/DIVORCE/case-types?access=read")
        .headers(CommonHeader)))

    .exec(http("CDM_020_050_Login")
      .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/DIVORCE/case-types/DIVORCE/work-basket-inputs"))

    .exec(http("CDM_020_055_Login")
      .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/DIVORCE/case-types/DIVORCE/cases?view=WORKBASKET&state=Submitted&page=1"))

    .exec(http("CDM_020_060_Login")
      .options(BaseURL + "/data/caseworkers/:uid/jurisdictions/DIVORCE/case-types/DIVORCE/cases/pagination_metadata?state=Submitted"))

    .exec(http("CDM_020_065_Login")
      .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/DIVORCE/case-types/DIVORCE/work-basket-inputs")
      .headers(CommonHeader))

    .exec(http("CDM_020_070_Login")
      .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/DIVORCE/case-types/DIVORCE/cases?view=WORKBASKET&state=Submitted&page=1")
      .headers(CommonHeader))

    .exec(http("CDM_020_075_Login")
      .get(BaseURL + "/data/caseworkers/:uid/jurisdictions/DIVORCE/case-types/DIVORCE/cases/pagination_metadata?state=Submitted")
      .headers(CommonHeader))
  }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val SearchResult = group("DIV_SearchResults"){

    exec(http("DIV_SearchResults_005")
      .options("/aggregated/caseworkers/:uid/jurisdictions/DIVORCE/case-types/DIVORCE/cases?view=WORKBASKET&page=1")
      .headers(CommonHeader))

    .exec(http("DIV_SearchResults_010")
      .options("/data/caseworkers/:uid/jurisdictions/DIVORCE/case-types/DIVORCE/cases/pagination_metadata")
      .headers(CommonHeader))

    .exec(http("DIV_SearchResults_015")
      .get("/data/caseworkers/:uid/jurisdictions/DIVORCE/case-types/DIVORCE/cases/pagination_metadata")
      .headers(CommonHeader))

    .exec(http("DIV_SearchResults_020")
      .get("/aggregated/caseworkers/:uid/jurisdictions/DIVORCE/case-types/DIVORCE/cases?view=WORKBASKET&page=1")
      .headers(CommonHeader)
      //.check(jsonPath("$[*]").ofType[Map[String,Any]].findAll.saveAs("theArray"))
    )

  }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

}
