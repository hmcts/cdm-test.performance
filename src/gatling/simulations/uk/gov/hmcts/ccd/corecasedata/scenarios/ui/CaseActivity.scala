package uk.gov.hmcts.ccd.corecasedata.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils._

object CaseActivity {

val BaseURL = Environment.baseURL
val IdamURL = Environment.idamURL
val CCDEnvurl = Environment.ccdEnvurl
val idam_header = Environment.idam_header
val feedXUIUserData = csv("XUISearchUsers.csv").circular
val feedXUISearchData = csv("XUISearchData.csv").circular
val caseActivityFeeder = csv("XUICaseActivityData.csv").random
val caseActivityListFeeder = csv("CaseActivityListData.csv").random
val MinThinkTime = Environment.minThinkTime
val MaxThinkTime = Environment.maxThinkTime
val CommonHeader = Environment.commonHeader

val headers_0 = Map(
		"Access-Control-Request-Headers" -> "content-type",
		"Access-Control-Request-Method" -> "GET",
		"Origin" -> "https://www-ccd.perftest.platform.hmcts.net",
		"Pragma" -> "no-cache",
		"Sec-Fetch-Dest" -> "empty",
		"Sec-Fetch-Mode" -> "cors",
		"Sec-Fetch-Site" -> "same-site")

	val headers_1 = Map(
		"Accept" -> "application/json",
		"Content-Type" -> "application/json",
		"DNT" -> "1",
		"Origin" -> "https://www-ccd.perftest.platform.hmcts.net",
		"Pragma" -> "no-cache",
		"Sec-Fetch-Dest" -> "empty",
		"Sec-Fetch-Mode" -> "cors",
		"Sec-Fetch-Site" -> "same-site",
		"sec-ch-ua" -> """Chromium";v="88", "Google Chrome";v="88", ";Not A Brand";v="99""",
		"sec-ch-ua-mobile" -> "?0")

val submitLogin = group("CCD_Login") {

    feed(feedXUIUserData)

    .exec(http("CCD_020_005_Login")
      .post(IdamURL + "/login?response_type=code&client_id=ccd_gateway&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
      .disableFollowRedirect
      .headers(idam_header)
      .formParam("username", "${email}") //${CCDUserName}
      .formParam("password", "Password12")
      .formParam("save", "Sign in")
      .formParam("selfRegistrationEnabled", "false")
      .formParam("_csrf", "${csrf}")
      .check(headerRegex("Location", "(?<=code=)(.*)&client").saveAs("authCode"))
      .check(status.in(200, 302)))
      .exitHereIfFailed

    .exec(http("CCD_020_010_Login")
      .get(CCDEnvurl + "/config")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("CCD_020_015_Login")
      .options(BaseURL + "/oauth2?code=${authCode}&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("CCD_020_020_Login")
      .get(BaseURL + "/oauth2?code=${authCode}&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("CCD_020_025_Login")
      .get(CCDEnvurl + "/config")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("CCD_020_030_Login")
      // .options(BaseURL + "/data/caseworkers/:uid/profile"))
      .options(BaseURL + "/data/internal/profile"))
    //.exitHereIfFailed

    .exec(http("CCD_020_031_Login")
      .options(BaseURL + "/activity/cases/0/activity")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("CCD_020_035_Login")
      // .get(BaseURL + "/data/caseworkers/:uid/profile")
      .get(BaseURL + "/data/internal/profile")
      .headers(LoginHeader.new_headers_14))
    //.exitHereIfFailed

    .exec(http("CCD_020_036_Login")
      .get(BaseURL + "/activity/cases/0/activity")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("CCD_020_037_Login")
      .options(BaseURL + "/data/internal/jurisdiction-ui-configs/?ids=PROBATE&ids=AUTOTEST1&ids=DIVORCE&ids=SSCS")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("CCD_020_038_Login")
      .get(BaseURL + "/data/internal/jurisdiction-ui-configs/?ids=PROBATE&ids=AUTOTEST1&ids=DIVORCE&ids=SSCS")
      .headers(LoginHeader.new_headers_16))
    //.exitHereIfFailed

    .exec(http("CCD_020_039_Login")
      .options(BaseURL + "/data/internal/jurisdiction-ui-configs/?ids=PROBATE&ids=AUTOTEST1&ids=DIVORCE&ids=SSCS")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("CCD_020_040_Login")
      .options(BaseURL + "/data/internal/banners/?ids=PROBATE&ids=AUTOTEST1&ids=DIVORCE&ids=SSCS")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("CCD_020_041_Login")
      .options(BaseURL + "/data/internal/jurisdiction-ui-configs/?ids=PROBATE&ids=AUTOTEST1&ids=DIVORCE&ids=SSCS")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("CCD_020_042_Login")
      .get(BaseURL + "/data/internal/jurisdiction-ui-configs/?ids=PROBATE&ids=AUTOTEST1&ids=DIVORCE&ids=SSCS")
      .headers(LoginHeader.new_headers_16))
    //.exitHereIfFailed

    .exec(http("CCD_020_043_Login")
      .get(BaseURL + "/data/internal/banners/?ids=PROBATE&ids=AUTOTEST1&ids=DIVORCE&ids=SSCS")
      .headers(LoginHeader.new_headers_20))
    //.exitHereIfFailed

    .exec(http("CCD_020_044_Login")
      .get(BaseURL + "/data/internal/jurisdiction-ui-configs/?ids=PROBATE&ids=AUTOTEST1&ids=DIVORCE&ids=SSCS")
      .headers(LoginHeader.new_headers_16))
    //.exitHereIfFailed

    .exec(http("CCD_020_045_Login")
      .options(BaseURL + "/data/internal/case-types/Legacy/work-basket-inputs")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("CCD_020_046_Login")
      .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/PROBATE/case-typesLegacy/cases?view=WORKBASKET&state=ReadOnly&page=1"))

    .exec(http("CCD_020_047_Login")
      .options(BaseURL + "/data/caseworkers/:uid/jurisdictions/PROBATE/case-types/Legacy/cases/pagination_metadata?state=ReadOnly")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("CCD_020_048_Login")
      .get(BaseURL + "/data/internal/case-types/Legacy/work-basket-inputs")
      .headers(LoginHeader.new_headers_27))
    //.exitHereIfFailed

    .exec(http("CCD_020_049_Login")
      .get(BaseURL + "/data/caseworkers/:uid/jurisdictions/PROBATE/case-types/Legacy/cases/pagination_metadata?state=ReadOnly")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("CCD_020_050_Login")
      .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/PROBATE/case-types/Legacy/cases?view=WORKBASKET&state=ReadOnly&page=1")
      .headers(LoginHeader.new_headers_9))


    

    // .exec(http("CCD_020_038_Login")
    //   .get(BaseURL + "/data/internal/jurisdiction-ui-configs/?ids=PROBATE&ids=AUTOTEST1&ids=DIVORCE&ids=SSCS")
    //   .headers(CommonHeader))
    // //.exitHereIfFailed

    // .exec(http("CCD_020_040_Login")
    //   .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/PROBATE/case-types?access=read"))

    // .exec(http("CCD_020_045_Login")
    //   .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/PROBATE/case-types?access=read")
    //   .headers(CommonHeader))
    // //.exitHereIfFailed

    // .exec(http("CCD_020_050_Login")
    //   .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/work-basket-inputs"))
    // //.exitHereIfFailed

    // .exec(http("CCD_020_055_Login")
    //   .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases?view=WORKBASKET&state=BOCaseStopped&page=1"))
    // //.exitHereIfFailed

    // .exec(http("CCD_020_060_Login")
    //   .options(BaseURL + "/data/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/pagination_metadata?state=BOCaseStopped"))
    // //.exitHereIfFailed

    // .exec(http("CCD_020_065_Login")
    //   .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/work-basket-inputs")
    //   .headers(CommonHeader))
    // //.exitHereIfFailed

    // .exec(http("CCD_020_070_Login")
    //   .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases?view=WORKBASKET&state=BOCaseStopped&page=1")
    //   .headers(CommonHeader))
    // //.exitHereIfFailed

    // .exec(http("CCD_020_075_Login")
    //   .get(BaseURL + "/data/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/pagination_metadata?state=BOCaseStopped")
    //   .headers(CommonHeader))
    // //.exitHereIfFailed

    .pause(MinThinkTime seconds, MaxThinkTime seconds)
  }

  val CaseActivityList =

    feed(caseActivityListFeeder)

    .group("CCD_001_CaseActivityListOptions") {
      exec(http("CCD_CaseActivityListOptions")
        .options(BaseURL + "/activity/cases/${caseList}/activity")
        .headers(headers_0)
        .check(responseTimeInMillis.saveAs("listResponseTimeOptions")))
    }

    .group("CCD_002_CaseActivityListGet") {
      exec(http("CCD_CaseActivityListGet")
        .get(BaseURL + "/activity/cases/${caseList}/activity")
        .headers(headers_1)
        .check(responseTimeInMillis.saveAs("listResponseTimeGet")))
    }

    .exec{ session =>
      val responseListTimeOptions = session("listResponseTimeOptions").as[Int]
      val responseListTimeGet = session("listResponseTimeGet").as[Int]
      val totalListThinktime = Environment.ccdCaseActivityPause * 1000 - responseListTimeOptions - responseListTimeGet
      session.set("listthinktime", totalListThinktime)
    }

  .pause(session => session("listthinktime").validate[Int].map(i => i milliseconds))


  val CaseActivitySingle = 

    feed(caseActivityFeeder)

    .group("CCD_003_CaseActivityOptions") {
      exec(http("CCD_CaseActivityOptions")
        .options(BaseURL + "/activity/cases/${caseRef}/activity")
        .headers(headers_0)
        .check(responseTimeInMillis.saveAs("responseTimeOptions")))
    }

    .group("CCD_004_CaseActivityPost") {
      exec(http("CCD_CaseActivityPost")
        .post(BaseURL + "/activity/cases/${caseRef}/activity")
        .headers(headers_1)
        .body(StringBody("{\n  \"activity\": \"view\"\n}"))
        .check(responseTimeInMillis.saveAs("responseTimePost")))
    }

    .exec{ session =>
      val responseTimePost = session("responseTimePost").as[Int]
      val responseTimeOptions = session("responseTimeOptions").as[Int]
      val totalThinktime = Environment.ccdCaseActivityPause * 1000 - responseTimePost - responseTimeOptions
      session.set("thinktime1", totalThinktime)
    }

    .pause(session => session("thinktime1").validate[Int].map(i => i milliseconds))

    .group("CCD_005_CaseActivityOptions") {
      exec(http("CCD_CaseActivityOptions")
        .options(BaseURL + "/activity/cases/${caseRef}/activity")
        .headers(headers_0)
        .check(responseTimeInMillis.saveAs("responseTimeOptions")))
    }

    .group("CCD_006_CaseActivityGet") {
      exec(http("CCD_CaseActivityGet")
        .get(BaseURL + "/activity/cases/${caseRef}/activity")
        .headers(headers_1)
        .check(responseTimeInMillis.saveAs("responseTimeGet")))
    }

    .exec{ session =>
      val responseTimeGet = session("responseTimeGet").as[Int]
      val responseTimeOptions = session("responseTimeOptions").as[Int]
      val totalThinktime = Environment.ccdCaseActivityPause * 1000 - responseTimeGet - responseTimeOptions
      session.set("thinktime2", totalThinktime)
    }

    .pause(session => session("thinktime2").validate[Int].map(i => i milliseconds))
}