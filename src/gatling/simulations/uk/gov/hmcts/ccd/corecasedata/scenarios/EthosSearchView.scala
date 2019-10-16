package uk.gov.hmcts.ccd.corecasedata.scenarios

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils._

object EthosSearchView {

  val BaseURL = Environment.baseURL
  val IdamURL = Environment.idamURL
  val CCDEnvurl = Environment.ccdEnvurl
  val CommonHeader = Environment.commonHeader
  val idam_header = Environment.idam_header
  //val feedUserData = csv("EthosUserData.csv").circular
  //val feedEthosSearchData = csv("EthosSearchData.csv").circular
  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val headers_0 = Map(
    "Access-Control-Request-Headers" -> "content-type",
    "Access-Control-Request-Method" -> "GET",
    "Origin" -> "https://ccd-case-management-web-perftest.service.core-compute-perftest.internal",
    "Sec-Fetch-Mode" -> "no-cors")

  val headers_2 = Map(
    "Accept" -> "application/json",
    "Content-Type" -> "application/json",
    "Origin" -> "https://ccd-case-management-web-perftest.service.core-compute-perftest.internal",
    "Sec-Fetch-Mode" -> "cors")

  val headers_6 = Map(
    "Access-Control-Request-Headers" -> "content-type,experimental",
    "Access-Control-Request-Method" -> "GET",
    "Origin" -> "https://ccd-case-management-web-perftest.service.core-compute-perftest.internal",
    "Sec-Fetch-Mode" -> "no-cors")

  val headers_7 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json",
    "Content-Type" -> "application/json",
    "Origin" -> "https://ccd-case-management-web-perftest.service.core-compute-perftest.internal",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val headers_19 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Sec-Fetch-Mode" -> "navigate",
    "Sec-Fetch-Site" -> "none",
    "Upgrade-Insecure-Requests" -> "1")

  val submitLogin = group("CDM_Login") {

    exec(http("CDM_020_005_Login")
      .post(IdamURL + "/login?response_type=code&client_id=ccd_gateway&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
      .disableFollowRedirect
      .headers(idam_header)
      .formParam("username", "${EthosUserName}")
      .formParam("password", "${EthosUserPassword}")
      .formParam("save", "Sign in")
      .formParam("selfRegistrationEnabled", "false")
      .formParam("_csrf", "${csrf}")
      .check(headerRegex("Location", "(?<=code=)(.*)&scope").saveAs("authCode"))
      .check(status.in(200, 302)))
      //.exitHereIfFailed

      .exec(http("CDM_020_010_Login")
      .get(CCDEnvurl + "/config")
      .headers(CommonHeader))
      //.exitHereIfFailed

      .exec(http("CDM_020_015_Login")
      .options(BaseURL + "/oauth2?code=${authCode}&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
      .headers(CommonHeader))
      //.exitHereIfFailed

      .exec(http("CDM_020_020_Login")
      .get(BaseURL + "/oauth2?code=${authCode}&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
      .headers(CommonHeader))
      //.exitHereIfFailed

      .exec(http("CDM_020_025_Login")
      .get(CCDEnvurl + "/config")
      .headers(CommonHeader))
      //.exitHereIfFailed

      .exec(http("CDM_020_030_Login")
      .options(BaseURL + "/data/caseworkers/:uid/profile"))
      //.exitHereIfFailed

      .exec(http("CDM_020_035_Login")
      .get(BaseURL + "/data/caseworkers/:uid/profile")
      .headers(CommonHeader))
      //.exitHereIfFailed

      .exec(http("CDM_020_040_Login")
      .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${EthosJurisdiction}/case-types?access=read")
      .resources(http("CDM_020_045_Login")
        .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${EthosJurisdiction}/case-types?access=read")
        .headers(CommonHeader)))
      //.exitHereIfFailed

      .exec(http("CDM_020_050_Login")
      .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${EthosJurisdiction}/case-types/${EthosCaseType}/work-basket-inputs"))
      //.exitHereIfFailed

      .exec(http("CDM_020_055_Login")
      .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${EthosJurisdiction}/case-types/${EthosCaseType}/cases?view=WORKBASKET&state=TODO&page=1"))
      //.exitHereIfFailed

      .exec(http("CDM_020_060_Login")
      .options(BaseURL + "/data/caseworkers/:uid/jurisdictions/${EthosJurisdiction}/case-types/${EthosCaseType}/cases/pagination_metadata?state=TODO"))
      //.exitHereIfFailed

      .exec(http("CDM_020_065_Login")
      .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${EthosJurisdiction}/case-types/${EthosCaseType}/work-basket-inputs")
      .headers(CommonHeader))
      //.exitHereIfFailed

      .exec(http("CDM_020_070_Login")
      .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${EthosJurisdiction}/case-types/${EthosCaseType}/cases?view=WORKBASKET&state=TODO&page=1")
      .headers(CommonHeader))
      //.exitHereIfFailed

      .exec(http("CDM_020_075_Login")
      .get(BaseURL + "/data/caseworkers/:uid/jurisdictions/${EthosJurisdiction}/case-types/${EthosCaseType}/cases/pagination_metadata?state=TODO")
      .headers(CommonHeader))
      //.exitHereIfFailed

      .pause(MinThinkTime seconds, MaxThinkTime seconds)
  }

  val Search = group("Ethos_View") {

    exec(http("ET_030_005_SearchCases")
      .options("/aggregated/caseworkers/:uid/jurisdictions/EMPLOYMENT/case-types/${EthosCaseType}/cases?view=WORKBASKET&page=1")
      .headers(headers_0))

      .exec(http("ET_030_010_SearchCases")
        .options("/data/caseworkers/:uid/jurisdictions/EMPLOYMENT/case-types/${EthosCaseType}/cases/pagination_metadata")
        .headers(headers_0))

      .exec(http("ET_030_015_SearchCases")
        .get("/data/caseworkers/:uid/jurisdictions/EMPLOYMENT/case-types/${EthosCaseType}/cases/pagination_metadata")
        .headers(headers_2))

      .exec(http("ET_030_020_SearchCases")
        .get("/aggregated/caseworkers/:uid/jurisdictions/EMPLOYMENT/case-types/${EthosCaseType}/cases?view=WORKBASKET&page=1")
        .headers(headers_2)
        //.check(jsonPath("$.results[*].case_id").saveAs("SearchParam_Case_Id"))
      )
  }

  val OpenCase = group("Ethos_View") {

    exec {
      session =>
        println(session("EthosCaseType").as[String])
        println(session("EthosCaseRef").as[String])
        session
    }

    .exec(http("ET_040_005_OpenCase")
      .options("/data/internal/cases/${EthosCaseRef}")
      .headers(headers_6))

    .exec(http("ET_040_010_OpenCase")
      .get("/data/internal/cases/${EthosCaseRef}")
      .headers(headers_7)
      .check(regex("/documents/(.+)\",\"document_filename\"").saveAs("Document_ID")))
      .exitHereIfFailed

      .exec(http("ET_040_010_OpenDocument")
      .get("/documents/${Document_ID}/binary")
      .headers(headers_19))
  }
}

