package uk.gov.hmcts.ccd.corecasedata.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.SSCS._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment

import scala.concurrent.duration._

object LargeFileUpload {

  val BaseURL = Environment.baseURL
  val IdamURL = Environment.idamURL
  val CCDEnvurl = Environment.ccdEnvurl
  val CommonHeader = Environment.commonHeader
  val idam_header = Environment.idam_header
  val feedUserData = csv("LargeUserData.csv").circular
  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val headers_1 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val headers_3 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
    "Origin" -> "https://ccd-case-management-web-perftest.service.core-compute-perftest.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site",
    "experimental" -> "true")

  val headers_9 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val LFULogin = group("LFU_Login") {

    exec(http("LFU_020_005_Login")
      .post(IdamURL + "/login?response_type=code&client_id=ccd_gateway&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
      .disableFollowRedirect
      .headers(idam_header)
      .formParam("username", "${LFUUserName}")
      .formParam("password", "${LFUUserPassword}")
      .formParam("save", "Sign in")
      .formParam("selfRegistrationEnabled", "false")
      .formParam("_csrf", "${csrf}")
      .check(headerRegex("Location", "(?<=code=)(.*)&scope").saveAs("authCode"))
      .check(status.in(200, 302))

      .resources(http("LFU_020_010_Login")
        .get(CCDEnvurl + "/config")
        .headers(CommonHeader)))

      .exec(http("LFU_020_015_Login")
        .options(BaseURL + "/oauth2?code=${authCode}&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
        .headers(CommonHeader))
      .exec(http("LFU_020_020_Login")
        .get(BaseURL + "/oauth2?code=${authCode}&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
        .headers(CommonHeader))
      .exec(http("LFU_020_025_Login")
        .get(CCDEnvurl + "/config")
        .headers(CommonHeader))

      .exec(http("LFU_020_030_Login")
        .options(BaseURL + "/data/caseworkers/:uid/profile"))

      .exec(http("LFU_020_035_Login")
        .get(BaseURL + "/data/caseworkers/:uid/profile")
        .headers(CommonHeader))

      .exec(http("LFU_020_040_Login")
        .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/SSCS/case-types?access=read")
        .resources(http("LFU_020_045_Login")
          .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/SSCS/case-types?access=read")
          .headers(CommonHeader)))

      .exec(http("LFU_020_050_Login")
        .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/LFU/case-types/Benefit/work-basket-inputs"))

      .exec(http("LFU_020_055_Login")
        .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/SSCS/case-types/Benefit/cases?view=WORKBASKET&state=TODO&page=1"))

      .exec(http("LFU_020_060_Login")
        .options(BaseURL + "/data/caseworkers/:uid/jurisdictions/SSCS/case-types/Benefit/cases/pagination_metadata?state=TODO"))

      .exec(http("LFU_020_065_Login")
        .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/SSCS/case-types/Benefit/work-basket-inputs")
        .headers(CommonHeader))

      .exec(http("LFU_020_070_Login")
        .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/SSCS/case-types/Benefit/cases?view=WORKBASKET&state=TODO&page=1")
        .headers(CommonHeader))

      .exec(http("LFU_020_075_Login")
        .get(BaseURL + "/data/caseworkers/:uid/jurisdictions/SSCS/case-types/Benefit/cases/pagination_metadata?state=TODO")
        .headers(CommonHeader))
  }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val LFU_CreateCase = exec(http("LFU_030_005_CreateCasePage")
    .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
    .headers(CommonHeader))

    .exec(http("LFU_030_010_CreateCaseDetailsPage")
      .get("/data/internal/case-types/Benefit/event-triggers/appealCreated?ignore-warning=false")
      .check(jsonPath("$.event_token").saveAs("New_Case_event_token"))
      .headers(headers_1))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("LFU_030_015_CreateCaseConfirmDetails")
      .post("/data/case-types/Benefit/validate?pageId=appealCreated2.0")
      .headers(headers_3)
      .body(StringBody("{\n  \"data\": {\n    \"caseReference\": null,\n    \"caseCreated\": \"2019-11-22\",\n    \"region\": null,\n    \"appeal\": {\n      \"receivedVia\": \"Online\",\n      \"mrnDetails\": {\n        \"dwpIssuingOffice\": \"DWP\",\n        \"mrnDate\": \"2019-11-22\",\n        \"mrnLateReason\": null,\n        \"mrnMissingReason\": null\n      },\n      \"appellant\": {\n        \"name\": {\n          \"title\": \"Mr\",\n          \"firstName\": \"Daniel\",\n          \"middleName\": null,\n          \"lastName\": \"Gleeballs\"\n        },\n        \"identity\": {\n          \"dob\": \"2000-03-01\",\n          \"nino\": \"AB1234567Z\"\n        },\n        \"address\": {\n          \"line1\": \"24 Test Street\",\n          \"line2\": null,\n          \"line3\": null,\n          \"town\": \"London\",\n          \"county\": null,\n          \"postcode\": \"KT2 5BU\",\n          \"country\": \"UK\"\n        },\n        \"contact\": {\n          \"phone\": \"07123456789\",\n          \"mobile\": null,\n          \"email\": null\n        },\n        \"isAppointee\": \"No\",\n        \"appointee\": {\n          \"name\": {\n            \"title\": null,\n            \"firstName\": null,\n            \"middleName\": null,\n            \"lastName\": null\n          },\n          \"identity\": {\n            \"dob\": null,\n            \"nino\": null\n          },\n          \"address\": {\n            \"line1\": null,\n            \"line2\": null,\n            \"line3\": null,\n            \"town\": null,\n            \"county\": null,\n            \"postcode\": null,\n            \"country\": null\n          },\n          \"contact\": {\n            \"phone\": null,\n            \"mobile\": null,\n            \"email\": null\n          }\n        },\n        \"isAddressSameAsAppointee\": null\n      },\n      \"benefitType\": {\n        \"code\": null,\n        \"description\": null\n      },\n      \"hearingType\": null,\n      \"hearingOptions\": {\n        \"wantsToAttend\": null,\n        \"languageInterpreter\": null,\n        \"other\": null,\n        \"signLanguageType\": null\n      },\n      \"appealReasons\": {\n        \"reasons\": [],\n        \"otherReasons\": null\n      },\n      \"supporter\": {\n        \"name\": {\n          \"title\": null,\n          \"firstName\": null,\n          \"middleName\": null,\n          \"lastName\": null\n        },\n        \"contact\": {\n          \"phone\": null,\n          \"mobile\": null,\n          \"email\": null\n        }\n      },\n      \"rep\": {\n        \"hasRepresentative\": null\n      },\n      \"signer\": null\n    },\n    \"regionalProcessingCenter\": {\n      \"name\": null,\n      \"address1\": null,\n      \"address2\": null,\n      \"address3\": null,\n      \"address4\": null,\n      \"postcode\": null,\n      \"city\": null,\n      \"phoneNumber\": null,\n      \"faxNumber\": null,\n      \"email\": null\n    },\n    \"panel\": {\n      \"assignedTo\": null,\n      \"medicalMember\": null,\n      \"disabilityQualifiedMember\": null\n    }\n  },\n  \"event\": {\n    \"id\": \"appealCreated\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"caseReference\": null,\n    \"caseCreated\": \"2019-11-22\",\n    \"region\": null,\n    \"appeal\": {\n      \"receivedVia\": \"Online\",\n      \"mrnDetails\": {\n        \"dwpIssuingOffice\": \"DWP\",\n        \"mrnDate\": \"2019-11-22\",\n        \"mrnLateReason\": null,\n        \"mrnMissingReason\": null\n      },\n      \"appellant\": {\n        \"name\": {\n          \"title\": \"Mr\",\n          \"firstName\": \"Daniel\",\n          \"middleName\": null,\n          \"lastName\": \"Gleeballs\"\n        },\n        \"identity\": {\n          \"dob\": \"2000-03-01\",\n          \"nino\": \"AB1234567Z\"\n        },\n        \"address\": {\n          \"line1\": \"24 Test Street\",\n          \"line2\": null,\n          \"line3\": null,\n          \"town\": \"London\",\n          \"county\": null,\n          \"postcode\": \"KT2 5BU\",\n          \"country\": \"UK\"\n        },\n        \"contact\": {\n          \"phone\": \"07123456789\",\n          \"mobile\": null,\n          \"email\": null\n        },\n        \"isAppointee\": \"No\",\n        \"appointee\": {\n          \"name\": {\n            \"title\": null,\n            \"firstName\": null,\n            \"middleName\": null,\n            \"lastName\": null\n          },\n          \"identity\": {\n            \"dob\": null,\n            \"nino\": null\n          },\n          \"address\": {\n            \"line1\": null,\n            \"line2\": null,\n            \"line3\": null,\n            \"town\": null,\n            \"county\": null,\n            \"postcode\": null,\n            \"country\": null\n          },\n          \"contact\": {\n            \"phone\": null,\n            \"mobile\": null,\n            \"email\": null\n          }\n        },\n        \"isAddressSameAsAppointee\": null\n      },\n      \"benefitType\": {\n        \"code\": null,\n        \"description\": null\n      },\n      \"hearingType\": null,\n      \"hearingOptions\": {\n        \"wantsToAttend\": null,\n        \"languageInterpreter\": null,\n        \"other\": null,\n        \"signLanguageType\": null\n      },\n      \"appealReasons\": {\n        \"reasons\": [],\n        \"otherReasons\": null\n      },\n      \"supporter\": {\n        \"name\": {\n          \"title\": null,\n          \"firstName\": null,\n          \"middleName\": null,\n          \"lastName\": null\n        },\n        \"contact\": {\n          \"phone\": null,\n          \"mobile\": null,\n          \"email\": null\n        }\n      },\n      \"rep\": {\n        \"hasRepresentative\": null\n      },\n      \"signer\": null\n    },\n    \"regionalProcessingCenter\": {\n      \"name\": null,\n      \"address1\": null,\n      \"address2\": null,\n      \"address3\": null,\n      \"address4\": null,\n      \"postcode\": null,\n      \"city\": null,\n      \"phoneNumber\": null,\n      \"faxNumber\": null,\n      \"email\": null\n    },\n    \"panel\": {\n      \"assignedTo\": null,\n      \"medicalMember\": null,\n      \"disabilityQualifiedMember\": null\n    }\n  }\n}")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("LFU_030_020_CreateCaseSubmit")
      .post("/data/caseworkers/:uid/jurisdictions/SSCS/case-types/Benefit/cases?ignore-warning=false")
      .headers(CommonHeader)
      .body(StringBody("{\n  \"data\": {\n    \"caseReference\": null,\n    \"caseCreated\": \"2019-11-22\",\n    \"region\": null,\n    \"appeal\": {\n      \"receivedVia\": \"Online\",\n      \"mrnDetails\": {\n        \"dwpIssuingOffice\": \"DWP\",\n        \"mrnDate\": \"2019-11-22\",\n        \"mrnLateReason\": null,\n        \"mrnMissingReason\": null\n      },\n      \"appellant\": {\n        \"name\": {\n          \"title\": \"Mr\",\n          \"firstName\": \"Daniel\",\n          \"middleName\": null,\n          \"lastName\": \"Gleeballs\"\n        },\n        \"identity\": {\n          \"dob\": \"2000-03-01\",\n          \"nino\": \"AB1234567Z\"\n        },\n        \"address\": {\n          \"line1\": \"24 Test Street\",\n          \"line2\": null,\n          \"line3\": null,\n          \"town\": \"London\",\n          \"county\": null,\n          \"postcode\": \"KT2 5BU\",\n          \"country\": \"UK\"\n        },\n        \"contact\": {\n          \"phone\": \"07123456789\",\n          \"mobile\": null,\n          \"email\": null\n        },\n        \"isAppointee\": \"No\",\n        \"appointee\": {\n          \"name\": {\n            \"title\": null,\n            \"firstName\": null,\n            \"middleName\": null,\n            \"lastName\": null\n          },\n          \"identity\": {\n            \"dob\": null,\n            \"nino\": null\n          },\n          \"address\": {\n            \"line1\": null,\n            \"line2\": null,\n            \"line3\": null,\n            \"town\": null,\n            \"county\": null,\n            \"postcode\": null,\n            \"country\": null\n          },\n          \"contact\": {\n            \"phone\": null,\n            \"mobile\": null,\n            \"email\": null\n          }\n        },\n        \"isAddressSameAsAppointee\": null\n      },\n      \"benefitType\": {\n        \"code\": null,\n        \"description\": null\n      },\n      \"hearingType\": null,\n      \"hearingOptions\": {\n        \"wantsToAttend\": null,\n        \"languageInterpreter\": null,\n        \"other\": null,\n        \"signLanguageType\": null\n      },\n      \"appealReasons\": {\n        \"reasons\": [],\n        \"otherReasons\": null\n      },\n      \"supporter\": {\n        \"name\": {\n          \"title\": null,\n          \"firstName\": null,\n          \"middleName\": null,\n          \"lastName\": null\n        },\n        \"contact\": {\n          \"phone\": null,\n          \"mobile\": null,\n          \"email\": null\n        }\n      },\n      \"rep\": {\n        \"hasRepresentative\": null\n      },\n      \"signer\": null\n    },\n    \"regionalProcessingCenter\": {\n      \"name\": null,\n      \"address1\": null,\n      \"address2\": null,\n      \"address3\": null,\n      \"address4\": null,\n      \"postcode\": null,\n      \"city\": null,\n      \"phoneNumber\": null,\n      \"faxNumber\": null,\n      \"email\": null\n    },\n    \"panel\": {\n      \"assignedTo\": null,\n      \"medicalMember\": null,\n      \"disabilityQualifiedMember\": null\n    }\n  },\n  \"event\": {\n    \"id\": \"appealCreated\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
      .check(jsonPath("$.id").saveAs("New_Case_Id")))

  .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val LFUDocUpload = exec(http("LFU_040_005_DocumentUploadPage1")
    .get("/data/internal/cases/${New_Case_Id}/event-triggers/uploadDocument?ignore-warning=false")
    .headers(headers_9)
    .check(jsonPath("$.event_token").saveAs("existing_case_event_token")))

  //.pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(session => {
      session.set("FileName1", "460mbaudio.mp3")
    })

    .exec(http("LFU_040_010_DocumentUploadToDM1")
      .post(BaseURL + "/documents")
      .bodyPart(RawFileBodyPart("files", "${FileName1}")
      .fileName("${FileName1}")
      .transferEncoding("binary"))
      .asMultipartForm
      .formParam("classification", "PUBLIC")
      .check(status.is(200))
      .check(regex("""http://(.+)/""").saveAs("DMURL"))
      .check(regex("""/documents/(.+)"""").saveAs("Document_ID")))

    .exec(http("LFU_040_015_DocumentUploadProcess")
      .post("/data/caseworkers/:uid/jurisdictions/SSCS/case-types/Benefit/cases/${New_Case_Id}/events")
      .headers(CommonHeader)
      .body(StringBody("{\n  \"data\": {\n    \"sscsDocument\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"documentType\": \"Other evidence\",\n          \"documentEmailContent\": null,\n          \"documentDateAdded\": \"2019-11-12\",\n          \"documentComment\": \"${FileName1} upload\",\n          \"documentFileName\": null,\n          \"documentLink\": {\n            \"document_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal:443/documents/${Document_ID}\",\n            \"document_binary_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal:443/documents/${Document_ID}/binary\",\n            \"document_filename\": \"${FileName1}\"\n          }\n        }\n      },\n      {\n        \"id\": null,\n        \"value\": {\n          \"documentType\": null,\n          \"documentEmailContent\": null,\n          \"documentDateAdded\": null,\n          \"documentComment\": null,\n          \"documentFileName\": null\n        }\n      }\n    ]\n  },\n  \"event\": {\n    \"id\": \"uploadDocument\",\n    \"summary\": \"${FileName1} upload doc\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val LFUSearchAndView = exec(http("LFU_050_005_SearchPage")
      .get("/data/internal/case-types/Benefit/work-basket-inputs")
      .headers(headers_0))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("LFU_050_010_SearchForCase")
      .get("/aggregated/caseworkers/:uid/jurisdictions/SSCS/case-types/Benefit/cases?view=WORKBASKET&page=1&case_reference=${New_Case_Id}")
      .headers(CommonHeader))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    /*.exec(http("LFU_050_015_SearchAndView")
      .get("/data/caseworkers/:uid/jurisdictions/SSCS/case-types/Benefit/cases/pagination_metadata?case_reference=${New_Case_Id}")
      .headers(CommonHeader))*/

    .exec(http("LFU_050_025_OpenCase")
    .get("/data/internal/cases/${New_Case_Id}")
    .headers(headers_8))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("LFU_050_020_OpenDocument")
      .get("/documents/${Document_ID}/binary")
      .headers(headers_15))

}
