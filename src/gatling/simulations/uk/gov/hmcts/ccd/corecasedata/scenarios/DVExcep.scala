package uk.gov.hmcts.ccd.corecasedata.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment
import scala.concurrent.duration._

object DVExcep {

  val BaseURL = Environment.baseURL
  val IdamURL = Environment.idamURL
  val CCDEnvurl = Environment.ccdEnvurl
  val CommonHeader = Environment.commonHeader
  val idam_header = Environment.idam_header
  val feedUserData = csv("DivorceUserData.csv").circular
  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val headers_0 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-workbasket-input-details.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
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

  val headers_5 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val headers_6 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json",
    "Content-Type" -> "application/json",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val headers_7 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Sec-Fetch-Mode" -> "navigate",
    "Sec-Fetch-Site" -> "none",
    "Upgrade-Insecure-Requests" -> "1")

  //login as divorce case worker
  //create exception case
  //upload doc during case create
  //doc view - maybe remove this if doing doc view later?
  //do search
  //open a case
  //doc view, although check above?
  //logout

  val DVCreateCase = group ("DIV_Create") {

    exec(http("DIV_030_005_CreateCase")
      .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
      .headers(CommonHeader))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

      .exec(http("DIV_030_010_CreateCase")
        .get("/data/internal/case-types/DIVORCE_ExceptionRecord/event-triggers/createException?ignore-warning=false")
        .headers(headers_1)
        .check(jsonPath("$.event_token").saveAs("New_Case_event_token")))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

      .exec(http("DIV_030_015_CreateCase")
        .post("/data/caseworkers/:uid/jurisdictions/DIVORCE/case-types/DIVORCE_ExceptionRecord/cases?ignore-warning=false")
        .headers(CommonHeader)
        .body(StringBody("{\n  \"data\": {\n    \"journeyClassification\": \"Exception\",\n    \"poBox\": \"PO1234\",\n    \"poBoxJurisdiction\": \"Divorce\",\n    \"deliveryDate\": \"2019-09-01T12:00:00.000\",\n    \"openingDate\": null,\n    \"scannedDocuments\": [],\n    \"scanOCRData\": []\n  },\n  \"event\": {\n    \"id\": \"createException\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
        .check(jsonPath("$.id").saveAs("New_Case_Id")))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)
  }

  val DVDocUpload = group("DIV_DocUpload") {

    exec(http("DIV_040_005_DocumentUpload")
      .get("/data/internal/cases/${New_Case_Id}/event-triggers/attachToExistingCase?ignore-warning=false")
      .headers(headers_5)
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token")))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

      .exec(http("DIV_040_010_DocumentUpload")
        .post(BaseURL + "/documents")
        .bodyPart(RawFileBodyPart("files", "1MB.pdf")
          .fileName("1MB.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
        .formParam("classification", "PUBLIC")
        .check(status.is(200))
        .check(regex("""http://(.+)/""").saveAs("DMURL"))
        .check(regex("""/documents/(.+)"""").saveAs("Document_ID")))

      .exec(http("DIV_040_015_DocumentUpload")
        .post("/data/caseworkers/:uid/jurisdictions/DIVORCE/case-types/DIVORCE_ExceptionRecord/cases/${New_Case_Id}/events")
        .headers(CommonHeader)
        .body(StringBody("{\n  \"data\": {\n    \"attachToCaseReference\": \"${New_Case_Id}\",\n    \"scannedDocuments\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"type\": \"other\",\n          \"subtype\": null,\n          \"controlNumber\": null,\n          \"fileName\": null,\n          \"scannedDate\": \"2019-09-02T12:00:00.000\",\n          \"url\": {\n            \"document_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal:443/documents/${Document_ID}\",\n            \"document_binary_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal:443/documents/${Document_ID}/binary\",\n            \"document_filename\": \"1MB.pdf\"\n          }\n        }\n      }\n    ]\n  },\n  \"event\": {\n    \"id\": \"attachToExistingCase\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}")))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)
  }

  val DVSearchAndView = group ("DIV_View") {

    exec(http("DIV_050_005_SearchPage")
      .get("/data/internal/case-types/DIVORCE_ExceptionRecord/work-basket-inputs")
      .headers(headers_0))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

      .exec(http("DIV_050_010_SearchAndView")
        .get("/aggregated/caseworkers/:uid/jurisdictions/DIVORCE/case-types/DIVORCE_ExceptionRecord/cases?view=WORKBASKET&page=1")
        .headers(CommonHeader))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

      .exec(http("DIV_050_015_SearchAndView")
        .get("/data/internal/cases/${New_Case_Id}")
        .headers(headers_6))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

      .exec(http("DIV_050_020_SearchAndOpenDoc")
        .get("/documents/${Document_ID}/binary")
        .headers(headers_7))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)
  }
}
