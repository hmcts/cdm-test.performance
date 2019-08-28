package uk.gov.hmcts.ccd.corecasedata.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment
import scala.concurrent.duration._

object PBGoR {

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
    "Content-Type" -> "application/json",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val headers_0 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val headers_3 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json",
    "Content-Type" -> "application/json",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val headers_4 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val headers_8 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json",
    "Content-Type" -> "application/json",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val headers_15 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Sec-Fetch-Mode" -> "navigate",
    "Sec-Fetch-Site" -> "none",
    "Upgrade-Insecure-Requests" -> "1")


  val PBCreateCase = group("PB_Create") {
    exec(http("PBGoR_030_005_CreateCase")
      .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions?access=create")
      .headers(CommonHeader))

      .exec(http("PBGoR_030_010_CreateCase")
        .get(BaseURL + "/data/internal/case-types/GrantOfRepresentation/event-triggers/applyForGrant?ignore-warning=false")
        .headers(headers_1)
        .check(jsonPath("$.event_token").saveAs("New_Case_event_token")))

      .exec(http("PBGoR_030_015_CreateCase")
        .post("/data/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases?ignore-warning=false")
        .headers(CommonHeader)
        .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"applyForGrant\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
        .check(jsonPath("$.id").saveAs("New_Case_Id")))

      .exec(http("PBGoR_030_020_CreateCase")
        .get("/data/internal/cases/${New_Case_Id}")
        .headers(headers_3))

      .exec(http("PBGoR_030_025_CreateCase")
        .get("/activity/cases/${New_Case_Id}/activity")
        .headers(CommonHeader))

      .exec(http("PBGoR_030_030_CreateCase")
        .get("/payments/cases/${New_Case_Id}/payments")
        .headers(CommonHeader)
        .check(status.is(403)))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)
  }

  val PBPaymentSuccessful = group("PB_Payment") {
    exec(http("PBGoR_040_005_PaymentSuccessful")
      .get("/data/internal/cases/${New_Case_Id}/event-triggers/paymentSuccessApp?ignore-warning=false")
      .headers(headers_0)
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token")))

      .exec(http("PBGoR_040_010_PaymentSuccessful")
        .post("/data/case-types/GrantOfRepresentation/validate?pageId=paymentSuccessAppboPaymentSuccessfulAppPage1")
        .headers(headers_4)
        .body(StringBody("{\n  \"data\": {\n    \"applicationSubmittedDate\": \"2019-03-01\"\n  },\n  \"event\": {\n    \"id\": \"paymentSuccessApp\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"applicationSubmittedDate\": \"2019-03-01\"\n  },\n  \"case_reference\": \"${New_Case_Id}\"\n}")))

      .exec(http("PBGoR_040_015_PaymentSuccessful")
        .post("/data/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/${New_Case_Id}/events")
        .headers(CommonHeader)
        .body(StringBody("{\n  \"data\": {\n    \"applicationSubmittedDate\": \"2019-03-01\"\n  },\n  \"event\": {\n    \"id\": \"paymentSuccessApp\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}")))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)
  }

  val PBDocUpload = group("PB_DocUpload") {
    exec(http("PGBoR_050_005_DocumentUpload")
      .get("/data/internal/cases/${New_Case_Id}/event-triggers/boUploadDocumentsForCaseCreated?ignore-warning=false")
      .headers(headers_0)
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token")))

      .exec(http("PGBoR_050_010_DocumentUpload")
        .post(BaseURL + "/documents")
        .bodyPart(RawFileBodyPart("files", "1MB.pdf")
          .fileName("1MB.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
        .formParam("classification", "PUBLIC")
        .check(status.is(200))
        .check(regex("""http://(.+)/""").saveAs("DMURL"))
        .check(regex("""/documents/(.+)"""").saveAs("Document_ID")))

      .exec(http("PGBoR_050_015_DocumentUpload")
        .post("/data/case-types/GrantOfRepresentation/validate?pageId=boUploadDocumentsForCaseCreatedboUploadDocumentPage1")
        .headers(headers_4)
        .body(StringBody("{\n  \"data\": {\n    \"boDocumentsUploaded\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"DocumentType\": \"deathCertificate\",\n          \"Comment\": \"test 1mb file\",\n          \"DocumentLink\": {\n            \"document_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal:443/documents/${Document_ID}\",\n            \"document_binary_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal:443/documents/${Document_ID}/binary\",\n            \"document_filename\": \"1MB.pdf\"\n          }\n        }\n      }\n    ]\n  },\n  \"event\": {\n    \"id\": \"boUploadDocumentsForCaseCreated\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"boDocumentsUploaded\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"DocumentType\": \"deathCertificate\",\n          \"Comment\": \"test 1mb file\",\n          \"DocumentLink\": {\n            \"document_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal:443/documents/${Document_ID}\",\n            \"document_binary_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal:443/documents/${Document_ID}/binary\",\n            \"document_filename\": \"1MB.pdf\"\n          }\n        }\n      }\n    ]\n  },\n  \"case_reference\": \"${New_Case_Id}\"\n}")))

      .exec(http("PGBoR_050_020_DocumentUpload")
        .post("/data/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/${New_Case_Id}/events")
        .headers(CommonHeader)
        .body(StringBody("{\n  \"data\": {\n    \"boDocumentsUploaded\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"DocumentType\": \"deathCertificate\",\n          \"Comment\": \"test 1mb file\",\n          \"DocumentLink\": {\n            \"document_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal:443/documents/${Document_ID}\",\n            \"document_binary_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal:443/documents/${Document_ID}/binary\",\n            \"document_filename\": \"1MB.pdf\"\n          }\n        }\n      }\n    ]\n  },\n  \"event\": {\n    \"id\": \"boUploadDocumentsForCaseCreated\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}")))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)
  }

  val PBSearchAndView = group("PB_View") {
    exec(http("PBGoR_060_005_SearchAndView")
      .get("/data/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/pagination_metadata")//?case_reference=1566214443240990")
      .headers(CommonHeader))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

      .exec(http("PBGoR_060_010_SearchAndView")
        .get("/data/internal/cases/${New_Case_Id}")
        .headers(headers_8))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

      .exec(http("PBGoR_060_015_SearchAndView")
        .get("/documents/${Document_ID}/binary")
        .headers(headers_15))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)
  }

  val PrintCaseID = exec{
    session =>
      println(session("New_Case_Id").as[String])
      session
  }
}