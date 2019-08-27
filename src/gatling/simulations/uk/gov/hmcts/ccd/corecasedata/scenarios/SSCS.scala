package uk.gov.hmcts.ccd.corecasedata.scenarios

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.ExecuteLogin.{MaxThinkTime, MinThinkTime}
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment

object SSCS {

  val BaseURL = Environment.baseURL
  val IdamURL = Environment.idamURL
  val CCDEnvurl = Environment.ccdEnvurl
  val CommonHeader = Environment.commonHeader
  val idam_header = Environment.idam_header
  //val feedUserData = csv("ProbateUserData.csv").circular
  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val headers_1 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val headers_2 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val headers_9 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val SSCSCreateCase = exec(http("request_0")
    .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
    .headers(CommonHeader))

    .exec(http("request_1")
      .get("/data/internal/case-types/Benefit/event-triggers/appealCreated?ignore-warning=false")
      .check(jsonPath("$.event_token").saveAs("New_Case_event_token"))
      .headers(headers_1))

    /*.exec(http("request_2")
      .post("/data/case-types/Benefit/validate?pageId=appealCreated2.0")
      .headers(headers_2)
      .body(StringBody("{\n  \"data\": {\n    \"caseReference\": null,\n    \"caseCreated\": \"2019-08-22\",\n    \"region\": \"London\",\n    \"appeal\": {\n      \"receivedVia\": \"Online\",\n      \"mrnDetails\": {\n        \"dwpIssuingOffice\": null,\n        \"mrnDate\": null,\n        \"mrnLateReason\": \"Test\",\n        \"mrnMissingReason\": null\n      },\n      \"appellant\": {\n        \"name\": {\n          \"title\": \"Mr\",\n          \"firstName\": \"Dan\",\n          \"middleName\": null,\n          \"lastName\": \"Gleeballs\"\n        },\n        \"identity\": {\n          \"dob\": \"1970-10-02\",\n          \"nino\": \"ab123456z\"\n        },\n        \"address\": {\n          \"line1\": \"5 test street\",\n          \"line2\": null,\n          \"line3\": null,\n          \"town\": \"london\",\n          \"county\": null,\n          \"postcode\": \"kt25bu\",\n          \"country\": \"uk\"\n        },\n        \"contact\": {\n          \"phone\": \"07123456789\",\n          \"mobile\": null,\n          \"email\": \"test@test.com\"\n        },\n        \"isAppointee\": null,\n        \"appointee\": {\n          \"name\": {\n            \"title\": null,\n            \"firstName\": null,\n            \"middleName\": null,\n            \"lastName\": null\n          },\n          \"identity\": {\n            \"dob\": null,\n            \"nino\": null\n          },\n          \"address\": {\n            \"line1\": null,\n            \"line2\": null,\n            \"line3\": null,\n            \"town\": null,\n            \"county\": null,\n            \"postcode\": null,\n            \"country\": null\n          },\n          \"contact\": {\n            \"phone\": null,\n            \"mobile\": null,\n            \"email\": null\n          }\n        },\n        \"isAddressSameAsAppointee\": null\n      },\n      \"benefitType\": {\n        \"code\": \"DWP\",\n        \"description\": null\n      },\n      \"hearingType\": \"cor\",\n      \"hearingOptions\": {\n        \"wantsToAttend\": \"No\",\n        \"languageInterpreter\": \"No\",\n        \"other\": null,\n        \"signLanguageType\": null\n      },\n      \"appealReasons\": {\n        \"reasons\": [],\n        \"otherReasons\": null\n      },\n      \"supporter\": {\n        \"name\": {\n          \"title\": null,\n          \"firstName\": null,\n          \"middleName\": null,\n          \"lastName\": null\n        },\n        \"contact\": {\n          \"phone\": null,\n          \"mobile\": null,\n          \"email\": null\n        }\n      },\n      \"rep\": {\n        \"hasRepresentative\": null\n      },\n      \"signer\": null\n    },\n    \"regionalProcessingCenter\": {\n      \"name\": null,\n      \"address1\": null,\n      \"address2\": null,\n      \"address3\": null,\n      \"address4\": null,\n      \"postcode\": null,\n      \"city\": null,\n      \"phoneNumber\": null,\n      \"faxNumber\": null\n    },\n    \"panel\": {\n      \"assignedTo\": null,\n      \"medicalMember\": null,\n      \"disabilityQualifiedMember\": null\n    }\n  },\n  \"event\": {\n    \"id\": \"appealCreated\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"caseReference\": null,\n    \"caseCreated\": \"2019-08-22\",\n    \"region\": \"London\",\n    \"appeal\": {\n      \"receivedVia\": \"Online\",\n      \"mrnDetails\": {\n        \"dwpIssuingOffice\": null,\n        \"mrnDate\": null,\n        \"mrnLateReason\": \"Test\",\n        \"mrnMissingReason\": null\n      },\n      \"appellant\": {\n        \"name\": {\n          \"title\": \"Mr\",\n          \"firstName\": \"Dan\",\n          \"middleName\": null,\n          \"lastName\": \"Gleeballs\"\n        },\n        \"identity\": {\n          \"dob\": \"1970-10-02\",\n          \"nino\": \"ab123456z\"\n        },\n        \"address\": {\n          \"line1\": \"5 test street\",\n          \"line2\": null,\n          \"line3\": null,\n          \"town\": \"london\",\n          \"county\": null,\n          \"postcode\": \"kt25bu\",\n          \"country\": \"uk\"\n        },\n        \"contact\": {\n          \"phone\": \"07123456789\",\n          \"mobile\": null,\n          \"email\": \"test@test.com\"\n        },\n        \"isAppointee\": null,\n        \"appointee\": {\n          \"name\": {\n            \"title\": null,\n            \"firstName\": null,\n            \"middleName\": null,\n            \"lastName\": null\n          },\n          \"identity\": {\n            \"dob\": null,\n            \"nino\": null\n          },\n          \"address\": {\n            \"line1\": null,\n            \"line2\": null,\n            \"line3\": null,\n            \"town\": null,\n            \"county\": null,\n            \"postcode\": null,\n            \"country\": null\n          },\n          \"contact\": {\n            \"phone\": null,\n            \"mobile\": null,\n            \"email\": null\n          }\n        },\n        \"isAddressSameAsAppointee\": null\n      },\n      \"benefitType\": {\n        \"code\": \"DWP\",\n        \"description\": null\n      },\n      \"hearingType\": \"cor\",\n      \"hearingOptions\": {\n        \"wantsToAttend\": \"No\",\n        \"languageInterpreter\": \"No\",\n        \"other\": null,\n        \"signLanguageType\": null\n      },\n      \"appealReasons\": {\n        \"reasons\": [],\n        \"otherReasons\": null\n      },\n      \"supporter\": {\n        \"name\": {\n          \"title\": null,\n          \"firstName\": null,\n          \"middleName\": null,\n          \"lastName\": null\n        },\n        \"contact\": {\n          \"phone\": null,\n          \"mobile\": null,\n          \"email\": null\n        }\n      },\n      \"rep\": {\n        \"hasRepresentative\": null\n      },\n      \"signer\": null\n    },\n    \"regionalProcessingCenter\": {\n      \"name\": null,\n      \"address1\": null,\n      \"address2\": null,\n      \"address3\": null,\n      \"address4\": null,\n      \"postcode\": null,\n      \"city\": null,\n      \"phoneNumber\": null,\n      \"faxNumber\": null\n    },\n    \"panel\": {\n      \"assignedTo\": null,\n      \"medicalMember\": null,\n      \"disabilityQualifiedMember\": null\n    }\n  }\n}")))
*/
    .exec(http("request_3")
      .post("/data/caseworkers/:uid/jurisdictions/SSCS/case-types/Benefit/cases?ignore-warning=false")
      .headers(CommonHeader)
      .body(StringBody("{\n  \"data\": {\n    \"caseReference\": null,\n    \"caseCreated\": \"2019-08-22\",\n    \"region\": \"London\",\n    \"appeal\": {\n      \"receivedVia\": \"Online\",\n      \"mrnDetails\": {\n        \"dwpIssuingOffice\": null,\n        \"mrnDate\": null,\n        \"mrnLateReason\": \"Test\",\n        \"mrnMissingReason\": null\n      },\n      \"appellant\": {\n        \"name\": {\n          \"title\": \"Mr\",\n          \"firstName\": \"Dan\",\n          \"middleName\": null,\n          \"lastName\": \"Gleeballs\"\n        },\n        \"identity\": {\n          \"dob\": \"1970-10-02\",\n          \"nino\": \"ab123456z\"\n        },\n        \"address\": {\n          \"line1\": \"5 test street\",\n          \"line2\": null,\n          \"line3\": null,\n          \"town\": \"london\",\n          \"county\": null,\n          \"postcode\": \"kt25bu\",\n          \"country\": \"uk\"\n        },\n        \"contact\": {\n          \"phone\": \"07123456789\",\n          \"mobile\": null,\n          \"email\": \"test@test.com\"\n        },\n        \"isAppointee\": null,\n        \"appointee\": {\n          \"name\": {\n            \"title\": null,\n            \"firstName\": null,\n            \"middleName\": null,\n            \"lastName\": null\n          },\n          \"identity\": {\n            \"dob\": null,\n            \"nino\": null\n          },\n          \"address\": {\n            \"line1\": null,\n            \"line2\": null,\n            \"line3\": null,\n            \"town\": null,\n            \"county\": null,\n            \"postcode\": null,\n            \"country\": null\n          },\n          \"contact\": {\n            \"phone\": null,\n            \"mobile\": null,\n            \"email\": null\n          }\n        },\n        \"isAddressSameAsAppointee\": null\n      },\n      \"benefitType\": {\n        \"code\": \"DWP\",\n        \"description\": null\n      },\n      \"hearingType\": \"cor\",\n      \"hearingOptions\": {\n        \"wantsToAttend\": \"No\",\n        \"languageInterpreter\": \"No\",\n        \"other\": null,\n        \"signLanguageType\": null\n      },\n      \"appealReasons\": {\n        \"reasons\": [],\n        \"otherReasons\": null\n      },\n      \"supporter\": {\n        \"name\": {\n          \"title\": null,\n          \"firstName\": null,\n          \"middleName\": null,\n          \"lastName\": null\n        },\n        \"contact\": {\n          \"phone\": null,\n          \"mobile\": null,\n          \"email\": null\n        }\n      },\n      \"rep\": {\n        \"hasRepresentative\": null\n      },\n      \"signer\": null\n    },\n    \"regionalProcessingCenter\": {\n      \"name\": null,\n      \"address1\": null,\n      \"address2\": null,\n      \"address3\": null,\n      \"address4\": null,\n      \"postcode\": null,\n      \"city\": null,\n      \"phoneNumber\": null,\n      \"faxNumber\": null\n    },\n    \"panel\": {\n      \"assignedTo\": null,\n      \"medicalMember\": null,\n      \"disabilityQualifiedMember\": null\n    }\n  },\n  \"event\": {\n    \"id\": \"appealCreated\",\n    \"summary\": \"test create case\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
      .check(jsonPath("$.id").saveAs("New_Case_Id")))


  val SSCSDocUpload = exec( http("request_9")
    .get("/data/internal/cases/${New_Case_Id}/event-triggers/uploadDocument?ignore-warning=false")
    .headers(headers_9))


}
