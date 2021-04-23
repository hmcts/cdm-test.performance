package uk.gov.hmcts.ccd.corecasedata.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils._
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
  val divCaseActivityRepeat = 1

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

  val headers_4 = Map(
    "Accept" -> "application/json",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Cache-Control" -> "no-cache",
    "Connection" -> "keep-alive",
    "Content-Type" -> "application/json",
    "Origin" -> CCDEnvurl,
    "Sec-Fetch-Dest" -> "empty",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "same-site"
  )

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

  val headers_8 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
    "Origin" -> CCDEnvurl,
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site",
    "experimental" -> "true")

  val headers_9 = Map(
		"Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8",
		"Origin" -> CCDEnvurl,
		"Pragma" -> "no-cache",
		"Sec-Fetch-Dest" -> "empty",
		"Sec-Fetch-Mode" -> "cors",
		"Sec-Fetch-Site" -> "same-site",
		"experimental" -> "true")

  val submitLogin = group("DIV_Login") {

    exec(http("DIV_020_005_Login")
      .post(IdamURL + "/login?response_type=code&client_id=ccd_gateway&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
      .disableFollowRedirect
      .headers(idam_header)
      .formParam("username", "${DivorceUserName}")
      .formParam("password", "${DivorceUserPassword}")
      .formParam("save", "Sign in")
      .formParam("selfRegistrationEnabled", "false")
      .formParam("_csrf", "${csrf}")
      .check(headerRegex("Location", "(?<=code=)(.*)&client").saveAs("authCode"))
      .check(status.in(200, 302)))
      //.exitHereIfFailed

    .exec(http("DIV_020_010_Login")
      .get(CCDEnvurl + "/config")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("DIV_020_015_Login")
      .options(BaseURL + "/oauth2?code=${authCode}&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("DIV_020_020_Login")
      .get(BaseURL + "/oauth2?code=${authCode}&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("DIV_020_025_Login")
      .get(CCDEnvurl + "/config")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("DIV_020_030_Login")
      .options(BaseURL + "/data/caseworkers/:uid/profile"))
    //.exitHereIfFailed

    .exec(http("DIV_020_035_Login")
      .get(BaseURL + "/data/caseworkers/:uid/profile")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("DIV_020_040_Login")
      .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${DVJurisdiction}/case-types?access=read"))

    .exec(http("DIV_020_045_Login")
      .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${DVJurisdiction}/case-types?access=read")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("DIV_020_050_Login")
      .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${DVJurisdiction}/case-types/${DVCaseType}/work-basket-inputs"))
    //.exitHereIfFailed

    .exec(http("DIV_020_055_Login")
      .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${DVJurisdiction}/case-types/${DVCaseType}/cases?view=WORKBASKET&state=TODO&page=1"))
    //.exitHereIfFailed

    .exec(http("DIV_020_060_Login")
      .options(BaseURL + "/data/caseworkers/:uid/jurisdictions/${DVJurisdiction}/case-types/${DVCaseType}/cases/pagination_metadata?state=TODO"))
    //.exitHereIfFailed

    .exec(http("DIV_020_065_Login")
      .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${DVJurisdiction}/case-types/${DVCaseType}/work-basket-inputs")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("DIV_020_070_Login")
      .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${DVJurisdiction}/case-types/${DVCaseType}/cases?view=WORKBASKET&state=TODO&page=1")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .exec(http("DIV_020_075_Login")
      .get(BaseURL + "/data/caseworkers/:uid/jurisdictions/${DVJurisdiction}/case-types/${DVCaseType}/cases/pagination_metadata?state=TODO")
      .headers(CommonHeader))
    //.exitHereIfFailed

    .pause(MinThinkTime seconds, MaxThinkTime seconds)
  }

  val DVCreateCase =

    exec(http("DIV_030_005_CreateCaseStartPage")
      .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
      .headers(CommonHeader))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    // .exec(http("DIV_030_010_CreateCasePage1")
		// 	.options("/data/internal/case-types/${DVCaseType}/event-triggers/hwfCreate?ignore-warning=false")
		// 	.headers(DivorceHeader.headers_0))

    .exec(http("DIV_030_010_CreateCasePage1")
			.get("/data/internal/case-types/${DVCaseType}/event-triggers/hwfCreate?ignore-warning=false")
			.headers(DivorceHeader.headers_1)
      .check(jsonPath("$.event_token").saveAs("New_Case_event_token")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

		// .exec(http("DIV_030_020_CreateCasePage2")
		// 	.options("/data/case-types/${DVCaseType}/validate?pageId=hwfCreate1")
		// 	.headers(DivorceHeader.headers_2))

    .exec(http("DIV_030_015_CreateCasePage2")
			.post("/data/case-types/${DVCaseType}/validate?pageId=hwfCreate1")
			.headers(DivorceHeader.headers_3)
			.body(StringBody("{\n  \"data\": {\n    \"LanguagePreferenceWelsh\": \"No\"\n  },\n  \"event\": {\n    \"id\": \"hwfCreate\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"LanguagePreferenceWelsh\": \"No\"\n  }\n}")))

		.pause(MinThinkTime seconds, MaxThinkTime seconds)

		// .exec(http("request_4")
		// 	.options("/data/case-types/${DVCaseType}/cases?ignore-warning=false")
		// 	.headers(DivorceHeader.headers_2))

    .exec(http("DIV_030_020_CreateCaseSubmit")
			.post("/data/case-types/${DVCaseType}/cases?ignore-warning=false")
			.headers(DivorceHeader.headers_5)
			.body(StringBody("{\n  \"data\": {\n    \"LanguagePreferenceWelsh\": \"No\"\n  },\n  \"event\": {\n    \"id\": \"hwfCreate\",\n    \"summary\": \"Perf Testing Case 2021\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
      .check(jsonPath("$.id").saveAs("New_Case_Id")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val DVDocUpload =

    exec(http("DIV_040_005_DocumentUploadPage")
      .get("/data/internal/cases/${New_Case_Id}/event-triggers/uploadDocument?ignore-warning=false")
      .headers(headers_5)
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

     .exec(session => {
      session.set("FileName1", "3MB.pdf")
    })

    .exec(http("DIV_040_010_DocumentUploadToDM")
      .post(BaseURL + "/documents")
      .bodyPart(RawFileBodyPart("files", "${FileName1}")
        .fileName("${FileName1}")
        .transferEncoding("binary"))
      .asMultipartForm
      .formParam("classification", "PUBLIC")
      .check(status.is(200))
      .check(regex("""http://(.+)/""").saveAs("DMURL"))
      .check(regex("""documents/(.+?)/binary""").saveAs("Document_ID")))

    .exec(http("DIV_040_015_DocumentUploadSubmit")
      .post("/data/caseworkers/:uid/jurisdictions/${DVJurisdiction}/case-types/${DVCaseType}/cases/${New_Case_Id}/events")
      .headers(CommonHeader)
      .body(StringBody("{\n  \"data\": {\n    \"D8DocumentsUploaded\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"DocumentType\": \"other\",\n          \"DocumentEmailContent\": null,\n          \"DocumentDateAdded\": null,\n          \"DocumentComment\": null,\n          \"DocumentFileName\": null,\n          \"DocumentLink\": {\n            \"document_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal/documents/${Document_ID}\",\n            \"document_binary_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal/documents/${Document_ID}/binary\",\n            \"document_filename\": \"${FileName1}\"\n          }\n        }\n      }\n    ]\n  },\n  \"event\": {\n    \"id\": \"uploadDocument\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val DVUpdateContactDetails =

    exec(http("DIV_050_005_UpdateContactDetailsPage")
			.get("/data/internal/cases/${New_Case_Id}/event-triggers/updateContactDetails?ignore-warning=false")
			.headers(DivorceHeader.headers_10)
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("DIV_050_010_EnterDetails")
			.post("/data/case-types/DIVORCE_XUI/validate?pageId=updateContactDetailsupdateContactDetails")
			.headers(DivorceHeader.headers_51)
			.body(StringBody("{\n  \"data\": {\n    \"D8PetitionerFirstName\": \"John\",\n    \"D8PetitionerLastName\": \"Smith\",\n    \"D8InferredPetitionerGender\": \"male\",\n    \"D8PetitionerContactDetailsConfidential\": \"keep\",\n    \"D8DerivedPetitionerHomeAddress\": \"12 Divorce Street, London, KT25BU\",\n    \"D8DerivedPetitionerCorrespondenceAddr\": null,\n    \"D8PetitionerEmail\": \"john@smith.com\",\n    \"D8PetitionerPhoneNumber\": null,\n    \"D8RespondentFirstName\": \"Jane\",\n    \"D8RespondentLastName\": \"Smith\",\n    \"D8InferredRespondentGender\": \"female\",\n    \"RespondentContactDetailsConfidential\": \"keep\",\n    \"D8DerivedRespondentHomeAddress\": \"12 Divorce Street, London, KT25BU\",\n    \"D8DerivedRespondentCorrespondenceAddr\": \"\",\n    \"RespEmailAddress\": \"jane@smith.com\",\n    \"RespPhoneNumber\": null,\n    \"D8MarriagePetitionerName\": null,\n    \"D8MarriageRespondentName\": null,\n    \"D8PetitionerConsent\": \"Yes\",\n    \"D8SolicitorReference\": \"11222333\",\n    \"PetitionerSolicitorName\": null,\n    \"PetitionerSolicitorFirm\": null,\n    \"PetitionerSolicitorAddress\": {\n      \"AddressLine1\": null,\n      \"AddressLine2\": null,\n      \"AddressLine3\": null,\n      \"PostCode\": null,\n      \"PostTown\": null,\n      \"County\": null,\n      \"Country\": null\n    },\n    \"PetitionerSolicitorPhone\": null,\n    \"PetitionerSolicitorEmail\": null,\n    \"SolicitorAgreeToReceiveEmails\": null,\n    \"respondentSolicitorReference\": null,\n    \"RespSolLinkedEmail\": null,\n    \"respondentSolicitorRepresented\": null,\n    \"D8RespondentSolicitorName\": null,\n    \"D8RespondentSolicitorCompany\": null,\n    \"D8RespondentSolicitorAddress\": {\n      \"AddressLine1\": null,\n      \"AddressLine2\": null,\n      \"AddressLine3\": null,\n      \"PostCode\": null,\n      \"PostTown\": null,\n      \"County\": null,\n      \"Country\": null\n    },\n    \"D8RespondentSolicitorPhone\": null,\n    \"D8RespondentSolicitorEmail\": null,\n    \"D8DerivedRespondentSolicitorAddr\": null,\n    \"D8ReasonForDivorce\": null\n  },\n  \"event\": {\n    \"id\": \"updateContactDetails\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"D8PetitionerFirstName\": \"John\",\n    \"D8PetitionerLastName\": \"Smith\",\n    \"D8InferredPetitionerGender\": \"male\",\n    \"D8PetitionerContactDetailsConfidential\": \"keep\",\n    \"D8DerivedPetitionerHomeAddress\": \"12 Divorce Street, London, KT25BU\",\n    \"D8DerivedPetitionerCorrespondenceAddr\": null,\n    \"D8PetitionerEmail\": \"john@smith.com\",\n    \"D8PetitionerPhoneNumber\": null,\n    \"D8RespondentFirstName\": \"Jane\",\n    \"D8RespondentLastName\": \"Smith\",\n    \"D8InferredRespondentGender\": \"female\",\n    \"RespondentContactDetailsConfidential\": \"keep\",\n    \"D8DerivedRespondentHomeAddress\": \"12 Divorce Street, London, KT25BU\",\n    \"D8DerivedRespondentCorrespondenceAddr\": \"\",\n    \"RespEmailAddress\": \"jane@smith.com\",\n    \"RespPhoneNumber\": null,\n    \"D8MarriagePetitionerName\": null,\n    \"D8MarriageRespondentName\": null,\n    \"D8PetitionerConsent\": \"Yes\",\n    \"D8SolicitorReference\": \"11222333\",\n    \"PetitionerSolicitorName\": null,\n    \"PetitionerSolicitorFirm\": null,\n    \"PetitionerSolicitorAddress\": {\n      \"AddressLine1\": null,\n      \"AddressLine2\": null,\n      \"AddressLine3\": null,\n      \"PostCode\": null,\n      \"PostTown\": null,\n      \"County\": null,\n      \"Country\": null\n    },\n    \"PetitionerSolicitorPhone\": null,\n    \"PetitionerSolicitorEmail\": null,\n    \"SolicitorAgreeToReceiveEmails\": null,\n    \"respondentSolicitorReference\": null,\n    \"RespSolLinkedEmail\": null,\n    \"respondentSolicitorRepresented\": null,\n    \"D8RespondentSolicitorName\": null,\n    \"D8RespondentSolicitorCompany\": null,\n    \"D8RespondentSolicitorAddress\": {\n      \"AddressLine1\": null,\n      \"AddressLine2\": null,\n      \"AddressLine3\": null,\n      \"PostCode\": null,\n      \"PostTown\": null,\n      \"County\": null,\n      \"Country\": null\n    },\n    \"D8RespondentSolicitorPhone\": null,\n    \"D8RespondentSolicitorEmail\": null,\n    \"D8DerivedRespondentSolicitorAddr\": null,\n    \"D8ReasonForDivorce\": null\n  },\n  \"case_reference\": \"${New_Case_Id}\"\n}")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  .exec(http("DIV_050_015_UpdateContactDetailsSubmit")
			.post("/data/cases/${New_Case_Id}/events")
			.headers(DivorceHeader.headers_57)
			.body(StringBody("{\n  \"data\": {\n    \"D8PetitionerFirstName\": \"John\",\n    \"D8PetitionerLastName\": \"Smith\",\n    \"D8InferredPetitionerGender\": \"male\",\n    \"D8PetitionerContactDetailsConfidential\": \"keep\",\n    \"D8DerivedPetitionerHomeAddress\": \"12 Divorce Street, London, KT25BU\",\n    \"D8DerivedPetitionerCorrespondenceAddr\": null,\n    \"D8PetitionerEmail\": \"john@smith.com\",\n    \"D8PetitionerPhoneNumber\": null,\n    \"D8RespondentFirstName\": \"Jane\",\n    \"D8RespondentLastName\": \"Smith\",\n    \"D8InferredRespondentGender\": \"female\",\n    \"RespondentContactDetailsConfidential\": \"keep\",\n    \"D8DerivedRespondentHomeAddress\": \"12 Divorce Street, London, KT25BU\",\n    \"D8DerivedRespondentCorrespondenceAddr\": \"\",\n    \"RespEmailAddress\": \"jane@smith.com\",\n    \"RespPhoneNumber\": null,\n    \"D8MarriagePetitionerName\": null,\n    \"D8MarriageRespondentName\": null,\n    \"D8PetitionerConsent\": \"Yes\",\n    \"D8SolicitorReference\": \"11222333\",\n    \"PetitionerSolicitorName\": null,\n    \"PetitionerSolicitorFirm\": null,\n    \"PetitionerSolicitorAddress\": {\n      \"AddressLine1\": null,\n      \"AddressLine2\": null,\n      \"AddressLine3\": null,\n      \"PostCode\": null,\n      \"PostTown\": null,\n      \"County\": null,\n      \"Country\": null\n    },\n    \"PetitionerSolicitorPhone\": null,\n    \"PetitionerSolicitorEmail\": null,\n    \"SolicitorAgreeToReceiveEmails\": null,\n    \"respondentSolicitorReference\": null,\n    \"RespSolLinkedEmail\": null,\n    \"respondentSolicitorRepresented\": null,\n    \"D8RespondentSolicitorName\": null,\n    \"D8RespondentSolicitorCompany\": null,\n    \"D8RespondentSolicitorAddress\": {\n      \"AddressLine1\": null,\n      \"AddressLine2\": null,\n      \"AddressLine3\": null,\n      \"PostCode\": null,\n      \"PostTown\": null,\n      \"County\": null,\n      \"Country\": null\n    },\n    \"D8RespondentSolicitorPhone\": null,\n    \"D8RespondentSolicitorEmail\": null,\n    \"D8DerivedRespondentSolicitorAddr\": null,\n    \"D8ReasonForDivorce\": null\n  },\n  \"event\": {\n    \"id\": \"updateContactDetails\",\n    \"summary\": \"Update Contact Details\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}")))

  .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val DVSearch =

    exec(http("DIV_060_005_SearchPage")
      .get("/data/internal/case-types/${DVCaseType}/work-basket-inputs")
      .headers(headers_0))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("DIV_060_010_SearchForCase")
      .get("/aggregated/caseworkers/:uid/jurisdictions/${DVJurisdiction}/case-types/${DVCaseType}/cases?view=WORKBASKET&page=1")
      .headers(headers_4))

    .exec(http("DIV_060_015_SearchForCase")
      .get("/data/caseworkers/:uid/jurisdictions/${DVJurisdiction}/case-types/${DVCaseType}/cases/pagination_metadata")
      .headers(CommonHeader))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    val DVView =

    exec(http("DIV_070_005_OpenCase")
      .get("/data/internal/cases/${New_Case_Id}")
      .headers(headers_6))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)  

    .exec(http("DIV_070_010_OpenDocument")
      .get("/documents/${Document_ID}/binary")
      .headers(headers_7))

}