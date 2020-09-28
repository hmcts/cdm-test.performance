package uk.gov.hmcts.ccd.corecasedata.scenarios

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment

object LoginAndSearch {

val BaseURL = Environment.baseURL
val IdamURL = Environment.idamURL
val CCDEnvurl = Environment.ccdEnvurl
val CommonHeader = Environment.commonHeader
val idam_header = Environment.idam_header
val feedSearchData = csv("searchData.csv").circular
val MinThinkTime = Environment.minThinkTime
val MaxThinkTime = Environment.maxThinkTime

val headers_2 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8",
    "Origin" -> CCDEnvurl,
    "Pragma" -> "no-cache",
    "Sec-Fetch-Dest" -> "empty",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "same-site",
    "experimental" -> "true")

val headers_7 = Map(
    "Accept" -> "application/json",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Cache-Control" -> "no-cache",
    "Connection" -> "keep-alive",
    "Content-Type" -> "application/json",
    "Origin" -> CCDEnvurl,
    "Sec-Fetch-Dest" -> "empty",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "same-site")

val Login = 

    feed(feedSearchData)

    .exec(http("Search_020_005_Login")
      .post(IdamURL + "/login?response_type=code&client_id=ccd_gateway&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
      .disableFollowRedirect
      .headers(idam_header)
      .formParam("username", "ccdloadtest1@gmail.com") //${CCDUserName}
      .formParam("password", "Password12")
      .formParam("save", "Sign in")
      .formParam("selfRegistrationEnabled", "false")
      .formParam("_csrf", "${csrf}")
      .check(headerRegex("Location", "(?<=code=)(.*)&client").saveAs("authCode"))
      .check(status.in(200, 302)))
      //.exitHereIfFailed

    .exec(http("Search_020_010_Login")
      .get(CCDEnvurl + "/config")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("Search_020_015_Login")
      .options(BaseURL + "/oauth2?code=${authCode}&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("Search_020_020_Login")
      .get(BaseURL + "/oauth2?code=${authCode}&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("Search_020_025_Login")
      .get(CCDEnvurl + "/config")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("Search_020_030_Login")
      .options(BaseURL + "/data/caseworkers/:uid/profile"))
    //.exitHereIfFailed

    .exec(http("Search_020_035_Login")
      .get(BaseURL + "/data/caseworkers/:uid/profile")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("Search_020_040_Login")
      .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${jurisdiction}/case-types?access=read"))

    .exec(http("Search_020_045_Login")
      .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${jurisdiction}/case-types?access=read")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("Search_020_050_Login")
      .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${jurisdiction}/case-types/${caseType}/work-basket-inputs"))
    //.exitHereIfFailed

    .exec(http("Search_020_055_Login")
      .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${jurisdiction}/case-types/${caseType}/cases?view=WORKBASKET&state=TODO&page=1"))
    //.exitHereIfFailed

    .exec(http("Search_020_060_Login")
      .options(BaseURL + "/data/caseworkers/:uid/jurisdictions/${jurisdiction}/case-types/${caseType}/cases/pagination_metadata?state=TODO"))
    //.exitHereIfFailed

    .exec(http("Search_020_065_Login")
      .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${jurisdiction}/case-types/${caseType}/work-basket-inputs")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("Search_020_070_Login")
      .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${jurisdiction}/case-types/${caseType}/cases?view=WORKBASKET&state=TODO&page=1")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("Search_020_075_Login")
      .get(BaseURL + "/data/caseworkers/:uid/jurisdictions/${jurisdiction}/case-types/${caseType}/cases/pagination_metadata?state=TODO")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("Search_020_080_Login")
      .get("/activity/cases/1552650446279756,1574715851299047,1574771353085053,1574771425565793,1574775065167620,1574775076679514,1574775081771140,1574775085031665,1574775090059446,1574775116202087,1574775125129875,1574775125356445,1574775164890403,1574775167970699,1574775170224035,1574775201506996,1574775205680128,1574775230602188,1574775232314675,1574775247646285,1574775263929649,1574775275516038,1574775282732867,1574775283695253,1574775292722858/activity")
      .headers(headers_2))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)
  
  val Search = 

    exec(http("Search_030__005_SearchForCase")
      .get("/data/caseworkers/:uid/jurisdictions/${jurisdiction}/case-types/${caseType}/cases/pagination_metadata")
      .headers(CommonHeader))

    .exec(http("Search_030__010_SearchForCase")
      .get("/aggregated/caseworkers/:uid/jurisdictions/${jurisdiction}/case-types/${caseType}/cases?view=WORKBASKET&page=1")
      .headers(headers_7))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)
}