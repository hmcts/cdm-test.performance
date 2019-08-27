package uk.gov.hmcts.ccd.corecasedata.scenarios

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment

object DVExcep {

  val BaseURL = Environment.baseURL
  val IdamURL = Environment.idamURL
  val CCDEnvurl = Environment.ccdEnvurl
  val CommonHeader = Environment.commonHeader
  val idam_header = Environment.idam_header
  val feedUserData = csv("ProbateUserData.csv").circular
  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val headers_1 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val headers_2 = Map(
    "Accept" -> "",
    "Content-Type" -> "multipart/form-data; boundary=----WebKitFormBoundarySKz4PKRfoQa1wnPT",
    "Sec-Fetch-Mode" -> "cors")

  val headers_3 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  //login as divorce case worker
  //create exception case
  //upload doc during case create
  //doc view - maybe remove this if doing doc view later?
  //do search
  //open a case
  //doc view, although check above?
  //logout

  val DVCreateCase = exec(http("DVExc_030_005_CreateCase")
    .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
    .headers(CommonHeader))

    .exec(http("DVExc_030_010_CreateCase")
      .get("/data/internal/case-types/DIVORCE_ExceptionRecord/event-triggers/createException?ignore-warning=false")
      .headers(headers_1)
      .check(jsonPath("$.event_token").saveAs("New_Case_event_token")))

    .exec(http("DVExc_030_015_CreateCase")
      .post(BaseURL + "/documents")
      .headers(headers_2)
      .bodyPart(RawFileBodyPart("files", "1MB.pdf")
        .fileName("1MB.pdf")
        .transferEncoding("binary"))
      .asMultipartForm
      .formParam("classification", "PUBLIC")
      .check(status.is(200))
      .check(regex("""http://(.+)/""").saveAs("DMURL"))
      .check(regex("""/documents/(.+)"""").saveAs("Document_ID")))

    .exec(http("DVExc_030_020_CreateCase")
      .post("/data/case-types/DIVORCE_ExceptionRecord/validate?pageId=createException1")
      .headers(headers_3)
      .body(StringBody("{\n  \"data\": {\n    \"journeyClassification\": null,\n    \"poBox\": null,\n    \"poBoxJurisdiction\": null,\n    \"deliveryDate\": \"2019-02-01T11:00:00.000\",\n    \"openingDate\": \"2018-04-02T12:00:00.000\",\n    \"scannedDocuments\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"type\": \"cherished\",\n          \"subtype\": null,\n          \"controlNumber\": null,\n          \"fileName\": \"test doc\",\n          \"scannedDate\": \"2019-01-02T10:00:00.000\",\n          \"url\": {\n            \"document_url\": \"http://${DMURL}/${Document_ID}\",\n            \"document_binary_url\": \"http://${DMURL}/${Document_ID}/binary\",\n            \"document_filename\": \"1MB.pdf\"\n          }\n        }\n      }\n    ],\n    \"scanOCRData\": []\n  },\n  \"event\": {\n    \"id\": \"createException\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"journeyClassification\": null,\n    \"poBox\": null,\n    \"poBoxJurisdiction\": null,\n    \"deliveryDate\": \"2019-02-01T11:00:00.000\",\n    \"openingDate\": \"2018-04-02T12:00:00.000\",\n    \"scannedDocuments\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"type\": \"cherished\",\n          \"subtype\": null,\n          \"controlNumber\": null,\n          \"fileName\": \"test doc\",\n          \"scannedDate\": \"2019-01-02T10:00:00.000\",\n          \"url\": {\n            \"document_url\": \"http://${DMURL}/${Document_ID}\",\n            \"document_binary_url\": \"http://${DMURL}/${Document_ID}/binary\",\n            \"document_filename\": \"1MB.pdf\"\n          }\n        }\n      }\n    ],\n    \"scanOCRData\": []\n  }\n}")))



}
