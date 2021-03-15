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

val submitLogin = group("Login") {

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
      .options(BaseURL + "/data/caseworkers/:uid/profile"))
    //.exitHereIfFailed

    .exec(http("CCD_020_035_Login")
      .get(BaseURL + "/data/caseworkers/:uid/profile")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("CCD_020_040_Login")
      .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/PROBATE/case-types?access=read"))

    .exec(http("CCD_020_045_Login")
      .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/PROBATE/case-types?access=read")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("CCD_020_050_Login")
      .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/work-basket-inputs"))
    //.exitHereIfFailed

    .exec(http("CCD_020_055_Login")
      .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases?view=WORKBASKET&state=BOCaseStopped&page=1"))
    //.exitHereIfFailed

    .exec(http("CCD_020_060_Login")
      .options(BaseURL + "/data/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/pagination_metadata?state=BOCaseStopped"))
    //.exitHereIfFailed

    .exec(http("CCD_020_065_Login")
      .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/work-basket-inputs")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("CCD_020_070_Login")
      .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases?view=WORKBASKET&state=BOCaseStopped&page=1")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("CCD_020_075_Login")
      .get(BaseURL + "/data/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/pagination_metadata?state=BOCaseStopped")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .pause(MinThinkTime seconds, MaxThinkTime seconds)
  }

  val CaseActivityList =

    feed(caseActivityListFeeder)

    .exec(http("CCD_CaseActivityListOptions")
      .options(BaseURL + "/activity/cases/${caseList}/activity")
      .headers(headers_0)
      .check(responseTimeInMillis.saveAs("listResponseTimeOptions")))

    .exec(http("CCD_CaseActivityListGet")
      .get(BaseURL + "/activity/cases/${caseList}/activity")
      .headers(headers_1)
      .check(responseTimeInMillis.saveAs("listResponseTimeGet")))

    .exec{ session =>
      val responseListTimeOptions = session("listResponseTimeOptions").as[Int]
      val responseListTimeGet = session("listResponseTimeGet").as[Int]
      val totalListThinktime = Environment.ccdCaseActivityPause * 1000 - responseListTimeOptions - responseListTimeGet
      session.set("listthinktime", totalListThinktime)
    }

  .pause(session => session("listthinktime").validate[Int].map(i => i milliseconds))


  val CaseActivitySingle = 

    feed(caseActivityFeeder)

    .exec(http("CCD_CaseActivity_Options")
      .options(BaseURL + "/activity/cases/${caseRef}/activity")
      .headers(headers_0)
      .check(responseTimeInMillis.saveAs("responseTimeOptions")))

    .exec(http("CCD_CaseActivity_Post")
      .post(BaseURL + "/activity/cases/${caseRef}/activity")
      .headers(headers_1)
      .body(StringBody("{\n  \"activity\": \"view\"\n}"))
      .check(responseTimeInMillis.saveAs("responseTimePost")))

    .exec{ session =>
      val responseTimePost = session("responseTimePost").as[Int]
      val responseTimeOptions = session("responseTimeOptions").as[Int]
      val totalThinktime = Environment.ccdCaseActivityPause * 1000 - responseTimePost - responseTimeOptions
      session.set("thinktime1", totalThinktime)
    }

    .pause(session => session("thinktime1").validate[Int].map(i => i milliseconds))

    .exec(http("CCD_CaseActivity_Options")
      .options(BaseURL + "/activity/cases/${caseRef}/activity")
      .headers(headers_0)
      .check(responseTimeInMillis.saveAs("responseTimeOptions")))

    .exec(http("CCD_CaseActivity_Get")
      .get(BaseURL + "/activity/cases/${caseRef}/activity")
      .headers(headers_1)
      .check(responseTimeInMillis.saveAs("responseTimeGet")))

    .exec{ session =>
      val responseTimeGet = session("responseTimeGet").as[Int]
      val responseTimeOptions = session("responseTimeOptions").as[Int]
      val totalThinktime = Environment.ccdCaseActivityPause * 1000 - responseTimeGet - responseTimeOptions
      session.set("thinktime2", totalThinktime)
    }

    .pause(session => session("thinktime2").validate[Int].map(i => i milliseconds))
}