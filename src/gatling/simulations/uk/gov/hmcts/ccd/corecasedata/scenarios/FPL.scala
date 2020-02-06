package uk.gov.hmcts.ccd.corecasedata.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment
import scala.concurrent.duration._

object FPL {

  val BaseURL = Environment.baseURL
  val IdamURL = Environment.idamURL
  val CCDEnvurl = Environment.ccdEnvurl
  val CommonHeader = Environment.commonHeader
  val idam_header = Environment.idam_header
  val feedUserData = csv("FPLUserData.csv").circular
  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime


  val headers_0 = Map(
    "Access-Control-Request-Headers" -> "content-type",
    "Access-Control-Request-Method" -> "GET",
    "Origin" -> "https://ccd-case-management-web-perftest.service.core-compute-perftest.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site")

  val headers_1 = Map(
    "Accept" -> "application/json",
    "Content-Type" -> "application/json",
    "Origin" -> "https://ccd-case-management-web-perftest.service.core-compute-perftest.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site")

  val headers_2 = Map(
    "Access-Control-Request-Headers" -> "content-type,experimental",
    "Access-Control-Request-Method" -> "GET",
    "Origin" -> "https://ccd-case-management-web-perftest.service.core-compute-perftest.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site")

  val headers_3 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
    "Origin" -> "https://ccd-case-management-web-perftest.service.core-compute-perftest.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site",
    "experimental" -> "true")

  val headers_4 = Map(
    "Access-Control-Request-Headers" -> "content-type,experimental",
    "Access-Control-Request-Method" -> "POST",
    "Origin" -> "https://ccd-case-management-web-perftest.service.core-compute-perftest.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site")

  val headers_5 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
    "Origin" -> "https://ccd-case-management-web-perftest.service.core-compute-perftest.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site",
    "experimental" -> "true")

  val headers_7 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-case.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
    "Origin" -> "https://ccd-case-management-web-perftest.service.core-compute-perftest.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site",
    "experimental" -> "true")

  val headers_9 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json",
    "Content-Type" -> "application/json",
    "Origin" -> "https://ccd-case-management-web-perftest.service.core-compute-perftest.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site",
    "experimental" -> "true")

  val headers_11 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
    "Origin" -> "https://ccd-case-management-web-perftest.service.core-compute-perftest.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site",
    "experimental" -> "true")

  val headers_12 = Map(
    "Accept" -> "",
    "Content-Type" -> "multipart/form-data; boundary=----WebKitFormBoundaryuGwfXGKrIBpkZBbx",
    "Origin" -> "https://ccd-case-management-web-perftest.service.core-compute-perftest.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site")

  val headers_16 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
    "Origin" -> "https://ccd-case-management-web-perftest.service.core-compute-perftest.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site",
    "experimental" -> "true")


  val FPLLogin = group("FPL_Login") {

    exec(http("FPL_020_005_Login")
      .post(IdamURL + "/login?response_type=code&client_id=ccd_gateway&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
      .disableFollowRedirect
      .headers(idam_header)
      .formParam("username", "${FPLUserName}")
      .formParam("password", "${FPLUserPassword}")
      .formParam("save", "Sign in")
      .formParam("selfRegistrationEnabled", "false")
      .formParam("_csrf", "${csrf}")
      .check(headerRegex("Location", "(?<=code=)(.*)&scope").saveAs("authCode"))
      .check(status.in(200, 302))

      .resources(http("FPL_020_010_Login")
        .get(CCDEnvurl + "/config")
        .headers(CommonHeader)))

      .exec(http("FPL_020_015_Login")
        .options(BaseURL + "/oauth2?code=${authCode}&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
        .headers(CommonHeader))
      .exec(http("FPL_020_020_Login")
        .get(BaseURL + "/oauth2?code=${authCode}&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
        .headers(CommonHeader))
      .exec(http("FPL_020_025_Login")
        .get(CCDEnvurl + "/config")
        .headers(CommonHeader))

      .exec(http("FPL_020_030_Login")
        .options(BaseURL + "/data/caseworkers/:uid/profile"))

      .exec(http("FPL_020_035_Login")
        .get(BaseURL + "/data/caseworkers/:uid/profile")
        .headers(CommonHeader))

      .exec(http("FPL_020_040_Login")
        .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/PUBLICLAW/case-types?access=read")
        .resources(http("FPL_020_045_Login")
          .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/PUBLICLAW/case-types?access=read")
          .headers(CommonHeader)))

      .exec(http("FPL_020_050_Login")
        .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/PUBLICLAW/case-types/CARE_SUPERVISION_EPO/work-basket-inputs"))

      .exec(http("FPL_020_055_Login")
        .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/PUBLICLAW/case-types/CARE_SUPERVISION_EPO/cases?view=WORKBASKET&state=TODO&page=1"))

      .exec(http("FPL_020_060_Login")
        .options(BaseURL + "/data/caseworkers/:uid/jurisdictions/PUBLICLAW/case-types/CARE_SUPERVISION_EPO/cases/pagination_metadata?state=TODO"))

      .exec(http("FPL_020_065_Login")
        .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/PUBLICLAW/case-types/CARE_SUPERVISION_EPO/work-basket-inputs")
        .headers(CommonHeader))

      .exec(http("FPL_020_070_Login")
        .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/PUBLICLAW/case-types/CARE_SUPERVISION_EPO/cases?view=WORKBASKET&state=TODO&page=1")
        .headers(CommonHeader))

      .exec(http("FPL_020_075_Login")
        .get(BaseURL + "/data/caseworkers/:uid/jurisdictions/PUBLICLAW/case-types/CARE_SUPERVISION_EPO/cases/pagination_metadata?state=TODO")
        .headers(CommonHeader))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)
  }

  val FPLCreateCase = exec(http("FPL_030_005_CreateCasePage")
      .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
      .headers(headers_1))

    .exec(http("FPL_030_010_CreateCasePage")
      .get("/data/internal/case-types/CARE_SUPERVISION_EPO/event-triggers/openCase?ignore-warning=false")
      .headers(headers_3)
      .check(jsonPath("$.event_token").saveAs("New_Case_event_token")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    //    .exec(http("FPL_030_015_CreateCasePageDetails")
//      .get("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=openCase1")
//      .headers(headers_5)
//      .body(StringBody("{\n  \"data\": {\n    \"caseName\": \"test123\"\n  },\n  \"event\": {\n    \"id\": \"openCase\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"caseName\": \"test123\"\n  }\n}")))

    .exec(http("FPL_030_015_CreateCasePageSubmit")
      .post("/data/case-types/CARE_SUPERVISION_EPO/cases?ignore-warning=false")
      .headers(headers_7)
      .body(StringBody("{\n  \"data\": {\n    \"caseName\": \"test12345\"\n  },\n  \"event\": {\n    \"id\": \"openCase\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
      .check(jsonPath("$.id").saveAs("New_Case_Id")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val FPLDocumentUpload = exec(http("FPL_040_005_DocumentUpload")
    .get("/data/internal/cases/${New_Case_Id}/event-triggers/uploadDocuments?ignore-warning=false")
    .headers(headers_11)
    .check(jsonPath("$.event_token").saveAs("existing_case_event_token")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(session => {
      session.set("FileName1", "2MB.pdf")
    })

    .exec(http("FPL_040_010_DocumentUploadToDM1")
      .post(BaseURL + "/documents")
      .bodyPart(RawFileBodyPart("files", "${FileName1}") //1MB.pdf
        .fileName("${FileName1}") //1MB.pdf
        .transferEncoding("binary"))
      .asMultipartForm
      .formParam("classification", "PUBLIC")
      .check(status.is(200))
      .check(regex("""http://(.+)/""").saveAs("DMURL"))
      .check(regex("""/documents/(.+)"""").saveAs("Document_ID")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("FPL_040_015_DocumentUploadSubmit")
      .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=uploadDocuments1")
      .headers(headers_5)
      .body(StringBody("{\n  \"data\": {\n    \"documents_socialWorkChronology_document\": {\n      \"documentStatus\": null,\n      \"typeOfDocument\": {\n        \"document_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal/documents/${Document_ID}\",\n        \"document_binary_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal/documents/${Document_ID}/binary\",\n        \"document_filename\": \"${FileName1}\"\n      }\n    },\n    \"documents_socialWorkStatement_document\": {\n      \"documentStatus\": null\n    },\n    \"documents_socialWorkAssessment_document\": {\n      \"documentStatus\": null\n    },\n    \"documents_socialWorkCarePlan_document\": {\n      \"documentStatus\": null\n    },\n    \"documents_socialWorkEvidenceTemplate_document\": {\n      \"documentStatus\": null\n    },\n    \"documents_threshold_document\": {\n      \"documentStatus\": null\n    },\n    \"documents_checklist_document\": {\n      \"documentStatus\": null\n    },\n    \"documents_socialWorkOther\": []\n  },\n  \"event\": {\n    \"id\": \"uploadDocuments\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"documents_socialWorkChronology_document\": {\n      \"documentStatus\": null,\n      \"typeOfDocument\": {\n        \"document_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal/documents/${Document_ID}\",\n        \"document_binary_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal/documents/${Document_ID}/binary\",\n        \"document_filename\": \"${FileName1}\"\n      }\n    },\n    \"documents_socialWorkStatement_document\": {\n      \"documentStatus\": null\n    },\n    \"documents_socialWorkAssessment_document\": {\n      \"documentStatus\": null\n    },\n    \"documents_socialWorkCarePlan_document\": {\n      \"documentStatus\": null\n    },\n    \"documents_socialWorkEvidenceTemplate_document\": {\n      \"documentStatus\": null\n    },\n    \"documents_threshold_document\": {\n      \"documentStatus\": null\n    },\n    \"documents_checklist_document\": {\n      \"documentStatus\": null\n    },\n    \"documents_socialWorkOther\": []\n  },\n  \"case_reference\": \"${New_Case_Id}\"\n}")))

    .exec(http("FPL_040_020_DocumentUploadSubmit2")
      .post("/data/cases/${New_Case_Id}/events")
      .headers(headers_16)
      .body(StringBody("{\n  \"data\": {\n    \"documents_socialWorkChronology_document\": {\n      \"documentStatus\": null,\n      \"typeOfDocument\": {\n        \"document_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal/documents/${Document_ID}\",\n        \"document_binary_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal/documents/${Document_ID}/binary\",\n        \"document_filename\": \"${FileName1}\"\n      }\n    },\n    \"documents_socialWorkStatement_document\": {\n      \"documentStatus\": null\n    },\n    \"documents_socialWorkAssessment_document\": {\n      \"documentStatus\": null\n    },\n    \"documents_socialWorkCarePlan_document\": {\n      \"documentStatus\": null\n    },\n    \"documents_socialWorkEvidenceTemplate_document\": {\n      \"documentStatus\": null\n    },\n    \"documents_threshold_document\": {\n      \"documentStatus\": null\n    },\n    \"documents_checklist_document\": {\n      \"documentStatus\": null\n    },\n    \"documents_socialWorkOther\": []\n  },\n  \"event\": {\n    \"id\": \"uploadDocuments\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

}