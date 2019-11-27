package uk.gov.hmcts.ccd.corecasedata.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment

import scala.concurrent.duration._

object CMC {

  val BaseURL = Environment.baseURL
  val IdamURL = Environment.idamURL
  val CCDEnvurl = Environment.ccdEnvurl
  val CommonHeader = Environment.commonHeader
  val idam_header = Environment.idam_header
  val feedUserData = csv("CMCUserData.csv").circular
  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val headers_0 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-workbasket-input-details.v2+json;charset=UTF-8",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val headers_1 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val headers_2 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val headers_3 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
    "Origin" -> "https://ccd-case-management-web-perftest.service.core-compute-perftest.internal",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val headers_8 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json",
    "Content-Type" -> "application/json",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val headers_9 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
    "Origin" -> "https://ccd-case-management-web-perftest.service.core-compute-perftest.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site",
    "experimental" -> "true")

  val headers_11 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val CMCLogin = group("CMC_Login") {

    exec(http("CMC_020_005_Login")
      .post(IdamURL + "/login?response_type=code&client_id=ccd_gateway&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
      .disableFollowRedirect
      .headers(idam_header)
      .formParam("username", "${CMCUserName}")
      .formParam("password", "${CMCUserPassword}")
      .formParam("save", "Sign in")
      .formParam("selfRegistrationEnabled", "false")
      .formParam("_csrf", "${csrf}")
      .check(headerRegex("Location", "(?<=code=)(.*)&scope").saveAs("authCode"))
      .check(status.in(200, 302))

      .resources(http("CMC_020_010_Login")
        .get(CCDEnvurl + "/config")
        .headers(CommonHeader)))

      .exec(http("CMC_020_015_Login")
        .options(BaseURL + "/oauth2?code=${authCode}&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
        .headers(CommonHeader))
      .exec(http("CMC_020_020_Login")
        .get(BaseURL + "/oauth2?code=${authCode}&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
        .headers(CommonHeader))
      .exec(http("CMC_020_025_Login")
        .get(CCDEnvurl + "/config")
        .headers(CommonHeader))

      .exec(http("CMC_020_030_Login")
        .options(BaseURL + "/data/caseworkers/:uid/profile"))

      .exec(http("CMC_020_035_Login")
        .get(BaseURL + "/data/caseworkers/:uid/profile")
        .headers(CommonHeader))

      .exec(http("CMC_020_040_Login")
        .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/CMC/case-types?access=read")
        .resources(http("CMC_020_045_Login")
          .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/CMC/case-types?access=read")
          .headers(CommonHeader)))

      .exec(http("CMC_020_050_Login")
        .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/CMC/case-types/MoneyClaimCase/work-basket-inputs"))

      .exec(http("CMC_020_055_Login")
        .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/CMC/case-types/MoneyClaimCase/cases?view=WORKBASKET&state=TODO&page=1"))

      .exec(http("CMC_020_060_Login")
        .options(BaseURL + "/data/caseworkers/:uid/jurisdictions/CMC/case-types/MoneyClaimCase/cases/pagination_metadata?state=TODO"))

      .exec(http("CMC_020_065_Login")
        .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/CMC/case-types/MoneyClaimCase/work-basket-inputs")
        .headers(CommonHeader))

      .exec(http("CMC_020_070_Login")
        .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/CMC/case-types/MoneyClaimCase/cases?view=WORKBASKET&state=TODO&page=1")
        .headers(CommonHeader))

      .exec(http("CMC_020_075_Login")
        .get(BaseURL + "/data/caseworkers/:uid/jurisdictions/CMC/case-types/MoneyClaimCase/cases/pagination_metadata?state=TODO")
        .headers(CommonHeader))
  }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val CMCCreateCase = exec(http("CMC_030_005_CreateCasePage")
      .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions?access=create")
      .headers(CommonHeader))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("CMC_030_010_CreateCaseDetails")
      .get(BaseURL + "/data/internal/case-types/MoneyClaimCase/event-triggers/CreateClaim?ignore-warning=false")
      .headers(headers_1)
      .check(jsonPath("$.event_token").saveAs("New_Case_event_token")))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("CMC_030_015_CreateCaseSubmit")
      .post(BaseURL + "/data/caseworkers/:uid/jurisdictions/CMC/case-types/MoneyClaimCase/cases?ignore-warning=false")
      //.post(BaseURL + "/data/case-types/MoneyClaimCase/validate?pageId=SubmitPrePayment1")
      .headers(CommonHeader)
      .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"CreateClaim\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
      .check(jsonPath("$.id").saveAs("New_Case_Id")))

  .pause(MinThinkTime seconds, MaxThinkTime seconds)
//
//  .exec { session =>
//    val s1 = new File("src/gatling/resources/CMCData.csv")
//    val writer = new PrintWriter(new FileOutputStream(new File("/src/gatling/resources/CMCData.csv"), true))
//    writer.write(session("New_Case_Id").as[String].trim)
//    writer.write(",")
//    writer.write(session("New_Case_Id").as[String].trim)
//    writer.close()
//    session
//  }

  val CMCStayCase = exec(http("CMC_040_005_StayClaim")
      .get("/data/internal/cases/${New_Case_Id}/event-triggers/StayClaim?ignore-warning=false")
      .headers(headers_11)
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("CMC_040_005_StayClaimSubmit")
      .post("/data/cases/${New_Case_Id}/events")
      .headers(headers_9)
      .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"StayClaim\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val CMCWaitingTransfer = exec(http("CMC_050_005_WaitingTransfer")
    .get("/data/internal/cases/${New_Case_Id}/event-triggers/WaitingTransfer?ignore-warning=false")
    .headers(headers_11)
    .check(jsonPath("$.event_token").saveAs("existing_case_event_token")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("CMC_050_005_WaitingTransferSubmit")
      .post("/data/cases/${New_Case_Id}/events")
      .headers(headers_9)
      .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"WaitingTransfer\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val CMCTransfer = exec(http("CMC_060_005_Transfer")
    .get("/data/internal/cases/${New_Case_Id}/event-triggers/Transfer?ignore-warning=false")
    .headers(headers_11)
    .check(jsonPath("$.event_token").saveAs("existing_case_event_token")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("CMC_060_005_TransferSubmit")
      .post("/data/cases/${New_Case_Id}/events")
      .headers(headers_9)
      .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"Transfer\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val CMCAttachScannedDocs = exec(http("CMC_070_005_AttachScannedDocs")
    .get("/data/internal/cases/${New_Case_Id}/event-triggers/attachScannedDocs?ignore-warning=false")
    .headers(headers_11)
    .check(jsonPath("$.event_token").saveAs("existing_case_event_token")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("CMC_070_005_AttachScannedDocsSubmit")
      .post("/data/cases/${New_Case_Id}/events")
      .headers(headers_9)
      .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"attachScannedDocs\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val CMCSupportUpdate = exec(http("CMC_080_005_SupportUpdate")
    .get("/data/internal/cases/${New_Case_Id}/event-triggers/SupportUpdate?ignore-warning=false")
    .headers(headers_11)
    .check(jsonPath("$.event_token").saveAs("existing_case_event_token")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("CMC_080_005_SupportUpdateSubmit")
      .post("/data/cases/${New_Case_Id}/events")
      .headers(headers_9)
      .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"SupportUpdate\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val CMCSearchAndView = exec(http("CMC_090_005_SearchPage")
      .get("/data/internal/case-types/MoneyClaimCase/work-basket-inputs")
      .headers(headers_0))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("CMC_090_010_SearchForCase")
      .get("/aggregated/caseworkers/:uid/jurisdictions/CMC/case-types/MoneyClaimCase/cases?view=WORKBASKET&page=1&case_reference=${New_Case_Id}")
      .headers(CommonHeader))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("CMC_090_015_OpenCase")
      .get("/data/caseworkers/:uid/jurisdictions/CMC/case-types/MoneyClaimCase/cases/pagination_metadata?case_reference=${New_Case_Id}")
      .headers(CommonHeader))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)
}
