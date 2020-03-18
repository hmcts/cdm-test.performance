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

  val headers_8 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
    "Origin" -> CCDEnvurl,
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site",
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
  }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val DVCreateCase = group ("DIV_Create") {

    exec(http("DIV_030_005_CreateCaseStartPage")
      .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
      .headers(CommonHeader))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("DIV_030_010_CreateCaseUsingSolicitor")
      .get("/data/internal/case-types/${DVCaseType}/event-triggers/solicitorCreate?ignore-warning=false")
      .headers(headers_1)
      .check(jsonPath("$.event_token").saveAs("New_Case_event_token")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("DIV_030_015_CreateCaseAboutTheSolicitor")
      .post("/data/case-types/${DVCaseType}/validate?pageId=solicitorCreateSolAboutTheSolicitor")
      .headers(headers_8)
      .body(StringBody("{\n  \"data\": {\n    \"PetitionerSolicitorName\": \"john smith\",\n    \"PetitionerSolicitorFirm\": \"solicitor ltd\",\n    \"DerivedPetitionerSolicitorAddr\": \"12 test street, solicitor lane, kt25bu\",\n    \"D8SolicitorReference\": \"0123456\",\n    \"PetitionerSolicitorPhone\": \"07123456789\",\n    \"PetitionerSolicitorEmail\": \"johnsmith@solicitorltd.com\",\n    \"SolicitorAgreeToReceiveEmails\": \"Yes\"\n  },\n  \"event\": {\n    \"id\": \"solicitorCreate\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"PetitionerSolicitorName\": \"john smith\",\n    \"PetitionerSolicitorFirm\": \"solicitor ltd\",\n    \"DerivedPetitionerSolicitorAddr\": \"12 test street, solicitor lane, kt25bu\",\n    \"D8SolicitorReference\": \"0123456\",\n    \"PetitionerSolicitorPhone\": \"07123456789\",\n    \"PetitionerSolicitorEmail\": \"johnsmith@solicitorltd.com\",\n    \"SolicitorAgreeToReceiveEmails\": \"Yes\"\n  }\n}")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("DIV_030_020_CreateCaseAboutThePetitioner")
      .post("/data/case-types/${DVCaseType}/validate?pageId=solicitorCreateSolAboutThePetitioner")
      .headers(headers_8)
      .body(StringBody("{\n  \"data\": {\n    \"D8PetitionerFirstName\": \"tessa\",\n    \"D8PetitionerLastName\": \"tickles\",\n    \"D8PetitionerNameDifferentToMarriageCert\": \"No\",\n    \"D8DivorceWho\": \"husband\",\n    \"D8InferredPetitionerGender\": \"female\",\n    \"D8MarriageIsSameSexCouple\": \"No\",\n    \"D8DerivedPetitionerHomeAddress\": \"14 divorce street, london, kt25bu\",\n    \"D8PetitionerPhoneNumber\": null,\n    \"D8PetitionerEmail\": null,\n    \"D8PetitionerContactDetailsConfidential\": \"keep\"\n  },\n  \"event\": {\n    \"id\": \"solicitorCreate\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"PetitionerSolicitorName\": \"john smith\",\n    \"PetitionerSolicitorFirm\": \"solicitor ltd\",\n    \"DerivedPetitionerSolicitorAddr\": \"12 test street, solicitor lane, kt25bu\",\n    \"D8SolicitorReference\": \"0123456\",\n    \"PetitionerSolicitorPhone\": \"07123456789\",\n    \"PetitionerSolicitorEmail\": \"johnsmith@solicitorltd.com\",\n    \"SolicitorAgreeToReceiveEmails\": \"Yes\",\n    \"D8PetitionerFirstName\": \"tessa\",\n    \"D8PetitionerLastName\": \"tickles\",\n    \"D8PetitionerNameDifferentToMarriageCert\": \"No\",\n    \"D8DivorceWho\": \"husband\",\n    \"D8InferredPetitionerGender\": \"female\",\n    \"D8MarriageIsSameSexCouple\": \"No\",\n    \"D8DerivedPetitionerHomeAddress\": \"14 divorce street, london, kt25bu\",\n    \"D8PetitionerPhoneNumber\": null,\n    \"D8PetitionerEmail\": null,\n    \"D8PetitionerContactDetailsConfidential\": \"keep\"\n  }\n}")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("DIV_030_025_CreateCaseAboutTheRespondent")
      .post("/data/case-types/${DVCaseType}/validate?pageId=solicitorCreateSolAboutTheRespondent")
      .headers(headers_8)
      .body(StringBody("{\n  \"data\": {\n    \"D8RespondentFirstName\": \"daniel\",\n    \"D8RespondentLastName\": \"gleeballs\",\n    \"D8RespondentNameAsOnMarriageCertificate\": \"No\",\n    \"D8InferredRespondentGender\": \"male\",\n    \"D8DerivedRespondentHomeAddress\": null,\n    \"D8RespondentCorrespondenceSendToSol\": \"No\",\n    \"D8DerivedRespondentCorrespondenceAddr\": \"14 divorcee street, london, kt25bu\"\n  },\n  \"event\": {\n    \"id\": \"solicitorCreate\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"PetitionerSolicitorName\": \"john smith\",\n    \"PetitionerSolicitorFirm\": \"solicitor ltd\",\n    \"DerivedPetitionerSolicitorAddr\": \"12 test street, solicitor lane, kt25bu\",\n    \"D8SolicitorReference\": \"0123456\",\n    \"PetitionerSolicitorPhone\": \"07123456789\",\n    \"PetitionerSolicitorEmail\": \"johnsmith@solicitorltd.com\",\n    \"SolicitorAgreeToReceiveEmails\": \"Yes\",\n    \"D8PetitionerFirstName\": \"tessa\",\n    \"D8PetitionerLastName\": \"tickles\",\n    \"D8PetitionerNameDifferentToMarriageCert\": \"No\",\n    \"D8DivorceWho\": \"husband\",\n    \"D8InferredPetitionerGender\": \"female\",\n    \"D8MarriageIsSameSexCouple\": \"No\",\n    \"D8DerivedPetitionerHomeAddress\": \"14 divorce street, london, kt25bu\",\n    \"D8PetitionerPhoneNumber\": null,\n    \"D8PetitionerEmail\": null,\n    \"D8PetitionerContactDetailsConfidential\": \"keep\",\n    \"D8RespondentFirstName\": \"daniel\",\n    \"D8RespondentLastName\": \"gleeballs\",\n    \"D8RespondentNameAsOnMarriageCertificate\": \"No\",\n    \"D8InferredRespondentGender\": \"male\",\n    \"D8DerivedRespondentHomeAddress\": null,\n    \"D8RespondentCorrespondenceSendToSol\": \"No\",\n    \"D8DerivedRespondentCorrespondenceAddr\": \"14 divorcee street, london, kt25bu\"\n  }\n}")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("DIV_030_030_CreateCaseMarriageCertificate")
      .post("/data/case-types/${DVCaseType}/validate?pageId=solicitorCreateSolMarriageCertificate")
      .headers(headers_8)
      .body(StringBody("{\n  \"data\": {\n    \"D8MarriageDate\": \"2000-10-02\",\n    \"D8MarriagePetitionerName\": \"tessa tickles\",\n    \"D8MarriageRespondentName\": \"daniel gleeballs\",\n    \"D8MarriedInUk\": \"Yes\"\n  },\n  \"event\": {\n    \"id\": \"solicitorCreate\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"PetitionerSolicitorName\": \"john smith\",\n    \"PetitionerSolicitorFirm\": \"solicitor ltd\",\n    \"DerivedPetitionerSolicitorAddr\": \"12 test street, solicitor lane, kt25bu\",\n    \"D8SolicitorReference\": \"0123456\",\n    \"PetitionerSolicitorPhone\": \"07123456789\",\n    \"PetitionerSolicitorEmail\": \"johnsmith@solicitorltd.com\",\n    \"SolicitorAgreeToReceiveEmails\": \"Yes\",\n    \"D8PetitionerFirstName\": \"tessa\",\n    \"D8PetitionerLastName\": \"tickles\",\n    \"D8PetitionerNameDifferentToMarriageCert\": \"No\",\n    \"D8DivorceWho\": \"husband\",\n    \"D8InferredPetitionerGender\": \"female\",\n    \"D8MarriageIsSameSexCouple\": \"No\",\n    \"D8DerivedPetitionerHomeAddress\": \"14 divorce street, london, kt25bu\",\n    \"D8PetitionerPhoneNumber\": null,\n    \"D8PetitionerEmail\": null,\n    \"D8PetitionerContactDetailsConfidential\": \"keep\",\n    \"D8RespondentFirstName\": \"daniel\",\n    \"D8RespondentLastName\": \"gleeballs\",\n    \"D8RespondentNameAsOnMarriageCertificate\": \"No\",\n    \"D8InferredRespondentGender\": \"male\",\n    \"D8DerivedRespondentHomeAddress\": null,\n    \"D8RespondentCorrespondenceSendToSol\": \"No\",\n    \"D8DerivedRespondentCorrespondenceAddr\": \"14 divorcee street, london, kt25bu\",\n    \"D8MarriageDate\": \"2000-10-02\",\n    \"D8MarriagePetitionerName\": \"tessa tickles\",\n    \"D8MarriageRespondentName\": \"daniel gleeballs\",\n    \"D8MarriedInUk\": \"Yes\"\n  }\n}")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("DIV_030_035_CreateCaseSolicitorJurisdiction")
      .post("/data/case-types/${DVCaseType}/validate?pageId=solicitorCreateSolJurisdiction")
      .headers(headers_8)
      .body(StringBody("{\n  \"data\": {\n    \"D8JurisdictionConnection\": [\n      \"G\"\n    ]\n  },\n  \"event\": {\n    \"id\": \"solicitorCreate\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"PetitionerSolicitorName\": \"john smith\",\n    \"PetitionerSolicitorFirm\": \"solicitor ltd\",\n    \"DerivedPetitionerSolicitorAddr\": \"12 test street, solicitor lane, kt25bu\",\n    \"D8SolicitorReference\": \"0123456\",\n    \"PetitionerSolicitorPhone\": \"07123456789\",\n    \"PetitionerSolicitorEmail\": \"johnsmith@solicitorltd.com\",\n    \"SolicitorAgreeToReceiveEmails\": \"Yes\",\n    \"D8PetitionerFirstName\": \"tessa\",\n    \"D8PetitionerLastName\": \"tickles\",\n    \"D8PetitionerNameDifferentToMarriageCert\": \"No\",\n    \"D8DivorceWho\": \"husband\",\n    \"D8InferredPetitionerGender\": \"female\",\n    \"D8MarriageIsSameSexCouple\": \"No\",\n    \"D8DerivedPetitionerHomeAddress\": \"14 divorce street, london, kt25bu\",\n    \"D8PetitionerPhoneNumber\": null,\n    \"D8PetitionerEmail\": null,\n    \"D8PetitionerContactDetailsConfidential\": \"keep\",\n    \"D8RespondentFirstName\": \"daniel\",\n    \"D8RespondentLastName\": \"gleeballs\",\n    \"D8RespondentNameAsOnMarriageCertificate\": \"No\",\n    \"D8InferredRespondentGender\": \"male\",\n    \"D8DerivedRespondentHomeAddress\": null,\n    \"D8RespondentCorrespondenceSendToSol\": \"No\",\n    \"D8DerivedRespondentCorrespondenceAddr\": \"14 divorcee street, london, kt25bu\",\n    \"D8MarriageDate\": \"2000-10-02\",\n    \"D8MarriagePetitionerName\": \"tessa tickles\",\n    \"D8MarriageRespondentName\": \"daniel gleeballs\",\n    \"D8MarriedInUk\": \"Yes\",\n    \"D8JurisdictionConnection\": [\n      \"G\"\n    ]\n  }\n}")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("DIV_030_040_CreateCase${DVCaseType}Reason")
      .post("/data/case-types/${DVCaseType}/validate?pageId=solicitorCreateSolReasonForDivorce")
      .headers(headers_8)
      .body(StringBody("{\n  \"data\": {\n    \"D8ReasonForDivorce\": \"unreasonable-behaviour\"\n  },\n  \"event\": {\n    \"id\": \"solicitorCreate\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"PetitionerSolicitorName\": \"john smith\",\n    \"PetitionerSolicitorFirm\": \"solicitor ltd\",\n    \"DerivedPetitionerSolicitorAddr\": \"12 test street, solicitor lane, kt25bu\",\n    \"D8SolicitorReference\": \"0123456\",\n    \"PetitionerSolicitorPhone\": \"07123456789\",\n    \"PetitionerSolicitorEmail\": \"johnsmith@solicitorltd.com\",\n    \"SolicitorAgreeToReceiveEmails\": \"Yes\",\n    \"D8PetitionerFirstName\": \"tessa\",\n    \"D8PetitionerLastName\": \"tickles\",\n    \"D8PetitionerNameDifferentToMarriageCert\": \"No\",\n    \"D8DivorceWho\": \"husband\",\n    \"D8InferredPetitionerGender\": \"female\",\n    \"D8MarriageIsSameSexCouple\": \"No\",\n    \"D8DerivedPetitionerHomeAddress\": \"14 divorce street, london, kt25bu\",\n    \"D8PetitionerPhoneNumber\": null,\n    \"D8PetitionerEmail\": null,\n    \"D8PetitionerContactDetailsConfidential\": \"keep\",\n    \"D8RespondentFirstName\": \"daniel\",\n    \"D8RespondentLastName\": \"gleeballs\",\n    \"D8RespondentNameAsOnMarriageCertificate\": \"No\",\n    \"D8InferredRespondentGender\": \"male\",\n    \"D8DerivedRespondentHomeAddress\": null,\n    \"D8RespondentCorrespondenceSendToSol\": \"No\",\n    \"D8DerivedRespondentCorrespondenceAddr\": \"14 divorcee street, london, kt25bu\",\n    \"D8MarriageDate\": \"2000-10-02\",\n    \"D8MarriagePetitionerName\": \"tessa tickles\",\n    \"D8MarriageRespondentName\": \"daniel gleeballs\",\n    \"D8MarriedInUk\": \"Yes\",\n    \"D8JurisdictionConnection\": [\n      \"G\"\n    ],\n    \"D8ReasonForDivorce\": \"unreasonable-behaviour\"\n  }\n}")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("DIV_030_045_CreateCaseBehaviourDetails")
      .post("/data/case-types/${DVCaseType}/validate?pageId=solicitorCreateSolSOCBehaviour1")
      .headers(headers_8)
      .body(StringBody("{\n  \"data\": {\n    \"D8ReasonForDivorceBehaviourDetails\": \"bad behaviour\"\n  },\n  \"event\": {\n    \"id\": \"solicitorCreate\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"PetitionerSolicitorName\": \"john smith\",\n    \"PetitionerSolicitorFirm\": \"solicitor ltd\",\n    \"DerivedPetitionerSolicitorAddr\": \"12 test street, solicitor lane, kt25bu\",\n    \"D8SolicitorReference\": \"0123456\",\n    \"PetitionerSolicitorPhone\": \"07123456789\",\n    \"PetitionerSolicitorEmail\": \"johnsmith@solicitorltd.com\",\n    \"SolicitorAgreeToReceiveEmails\": \"Yes\",\n    \"D8PetitionerFirstName\": \"tessa\",\n    \"D8PetitionerLastName\": \"tickles\",\n    \"D8PetitionerNameDifferentToMarriageCert\": \"No\",\n    \"D8DivorceWho\": \"husband\",\n    \"D8InferredPetitionerGender\": \"female\",\n    \"D8MarriageIsSameSexCouple\": \"No\",\n    \"D8DerivedPetitionerHomeAddress\": \"14 divorce street, london, kt25bu\",\n    \"D8PetitionerPhoneNumber\": null,\n    \"D8PetitionerEmail\": null,\n    \"D8PetitionerContactDetailsConfidential\": \"keep\",\n    \"D8RespondentFirstName\": \"daniel\",\n    \"D8RespondentLastName\": \"gleeballs\",\n    \"D8RespondentNameAsOnMarriageCertificate\": \"No\",\n    \"D8InferredRespondentGender\": \"male\",\n    \"D8DerivedRespondentHomeAddress\": null,\n    \"D8RespondentCorrespondenceSendToSol\": \"No\",\n    \"D8DerivedRespondentCorrespondenceAddr\": \"14 divorcee street, london, kt25bu\",\n    \"D8MarriageDate\": \"2000-10-02\",\n    \"D8MarriagePetitionerName\": \"tessa tickles\",\n    \"D8MarriageRespondentName\": \"daniel gleeballs\",\n    \"D8MarriedInUk\": \"Yes\",\n    \"D8JurisdictionConnection\": [\n      \"G\"\n    ],\n    \"D8ReasonForDivorce\": \"unreasonable-behaviour\",\n    \"D8ReasonForDivorceBehaviourDetails\": \"bad behaviour\"\n  }\n}")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("DIV_030_050_CreateCaseExistingCourtCase")
      .post("/data/case-types/${DVCaseType}/validate?pageId=solicitorCreateSolExistingCourtCases")
      .headers(headers_8)
      .body(StringBody("{\n  \"data\": {\n    \"D8LegalProceedings\": \"No\"\n  },\n  \"event\": {\n    \"id\": \"solicitorCreate\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"PetitionerSolicitorName\": \"john smith\",\n    \"PetitionerSolicitorFirm\": \"solicitor ltd\",\n    \"DerivedPetitionerSolicitorAddr\": \"12 test street, solicitor lane, kt25bu\",\n    \"D8SolicitorReference\": \"0123456\",\n    \"PetitionerSolicitorPhone\": \"07123456789\",\n    \"PetitionerSolicitorEmail\": \"johnsmith@solicitorltd.com\",\n    \"SolicitorAgreeToReceiveEmails\": \"Yes\",\n    \"D8PetitionerFirstName\": \"tessa\",\n    \"D8PetitionerLastName\": \"tickles\",\n    \"D8PetitionerNameDifferentToMarriageCert\": \"No\",\n    \"D8DivorceWho\": \"husband\",\n    \"D8InferredPetitionerGender\": \"female\",\n    \"D8MarriageIsSameSexCouple\": \"No\",\n    \"D8DerivedPetitionerHomeAddress\": \"14 divorce street, london, kt25bu\",\n    \"D8PetitionerPhoneNumber\": null,\n    \"D8PetitionerEmail\": null,\n    \"D8PetitionerContactDetailsConfidential\": \"keep\",\n    \"D8RespondentFirstName\": \"daniel\",\n    \"D8RespondentLastName\": \"gleeballs\",\n    \"D8RespondentNameAsOnMarriageCertificate\": \"No\",\n    \"D8InferredRespondentGender\": \"male\",\n    \"D8DerivedRespondentHomeAddress\": null,\n    \"D8RespondentCorrespondenceSendToSol\": \"No\",\n    \"D8DerivedRespondentCorrespondenceAddr\": \"14 divorcee street, london, kt25bu\",\n    \"D8MarriageDate\": \"2000-10-02\",\n    \"D8MarriagePetitionerName\": \"tessa tickles\",\n    \"D8MarriageRespondentName\": \"daniel gleeballs\",\n    \"D8MarriedInUk\": \"Yes\",\n    \"D8JurisdictionConnection\": [\n      \"G\"\n    ],\n    \"D8ReasonForDivorce\": \"unreasonable-behaviour\",\n    \"D8ReasonForDivorceBehaviourDetails\": \"bad behaviour\",\n    \"D8LegalProceedings\": \"No\"\n  }\n}")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("DIV_030_055_CreateCaseDividingAssets")
      .post("/data/case-types/${DVCaseType}/validate?pageId=solicitorCreateSolDividingMoneyAndProperty")
      .headers(headers_8)
      .body(StringBody("{\n  \"data\": {\n    \"D8FinancialOrder\": \"No\"\n  },\n  \"event\": {\n    \"id\": \"solicitorCreate\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"PetitionerSolicitorName\": \"john smith\",\n    \"PetitionerSolicitorFirm\": \"solicitor ltd\",\n    \"DerivedPetitionerSolicitorAddr\": \"12 test street, solicitor lane, kt25bu\",\n    \"D8SolicitorReference\": \"0123456\",\n    \"PetitionerSolicitorPhone\": \"07123456789\",\n    \"PetitionerSolicitorEmail\": \"johnsmith@solicitorltd.com\",\n    \"SolicitorAgreeToReceiveEmails\": \"Yes\",\n    \"D8PetitionerFirstName\": \"tessa\",\n    \"D8PetitionerLastName\": \"tickles\",\n    \"D8PetitionerNameDifferentToMarriageCert\": \"No\",\n    \"D8DivorceWho\": \"husband\",\n    \"D8InferredPetitionerGender\": \"female\",\n    \"D8MarriageIsSameSexCouple\": \"No\",\n    \"D8DerivedPetitionerHomeAddress\": \"14 divorce street, london, kt25bu\",\n    \"D8PetitionerPhoneNumber\": null,\n    \"D8PetitionerEmail\": null,\n    \"D8PetitionerContactDetailsConfidential\": \"keep\",\n    \"D8RespondentFirstName\": \"daniel\",\n    \"D8RespondentLastName\": \"gleeballs\",\n    \"D8RespondentNameAsOnMarriageCertificate\": \"No\",\n    \"D8InferredRespondentGender\": \"male\",\n    \"D8DerivedRespondentHomeAddress\": null,\n    \"D8RespondentCorrespondenceSendToSol\": \"No\",\n    \"D8DerivedRespondentCorrespondenceAddr\": \"14 divorcee street, london, kt25bu\",\n    \"D8MarriageDate\": \"2000-10-02\",\n    \"D8MarriagePetitionerName\": \"tessa tickles\",\n    \"D8MarriageRespondentName\": \"daniel gleeballs\",\n    \"D8MarriedInUk\": \"Yes\",\n    \"D8JurisdictionConnection\": [\n      \"G\"\n    ],\n    \"D8ReasonForDivorce\": \"unreasonable-behaviour\",\n    \"D8ReasonForDivorceBehaviourDetails\": \"bad behaviour\",\n    \"D8LegalProceedings\": \"No\",\n    \"D8FinancialOrder\": \"No\"\n  }\n}")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("DIV_030_060_CreateCaseClaimCosts")
      .post("/data/case-types/${DVCaseType}/validate?pageId=solicitorCreateSolApplyToClaimCosts")
      .headers(headers_8)
      .body(StringBody("{\n  \"data\": {\n    \"D8DivorceCostsClaim\": \"No\"\n  },\n  \"event\": {\n    \"id\": \"solicitorCreate\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"PetitionerSolicitorName\": \"john smith\",\n    \"PetitionerSolicitorFirm\": \"solicitor ltd\",\n    \"DerivedPetitionerSolicitorAddr\": \"12 test street, solicitor lane, kt25bu\",\n    \"D8SolicitorReference\": \"0123456\",\n    \"PetitionerSolicitorPhone\": \"07123456789\",\n    \"PetitionerSolicitorEmail\": \"johnsmith@solicitorltd.com\",\n    \"SolicitorAgreeToReceiveEmails\": \"Yes\",\n    \"D8PetitionerFirstName\": \"tessa\",\n    \"D8PetitionerLastName\": \"tickles\",\n    \"D8PetitionerNameDifferentToMarriageCert\": \"No\",\n    \"D8DivorceWho\": \"husband\",\n    \"D8InferredPetitionerGender\": \"female\",\n    \"D8MarriageIsSameSexCouple\": \"No\",\n    \"D8DerivedPetitionerHomeAddress\": \"14 divorce street, london, kt25bu\",\n    \"D8PetitionerPhoneNumber\": null,\n    \"D8PetitionerEmail\": null,\n    \"D8PetitionerContactDetailsConfidential\": \"keep\",\n    \"D8RespondentFirstName\": \"daniel\",\n    \"D8RespondentLastName\": \"gleeballs\",\n    \"D8RespondentNameAsOnMarriageCertificate\": \"No\",\n    \"D8InferredRespondentGender\": \"male\",\n    \"D8DerivedRespondentHomeAddress\": null,\n    \"D8RespondentCorrespondenceSendToSol\": \"No\",\n    \"D8DerivedRespondentCorrespondenceAddr\": \"14 divorcee street, london, kt25bu\",\n    \"D8MarriageDate\": \"2000-10-02\",\n    \"D8MarriagePetitionerName\": \"tessa tickles\",\n    \"D8MarriageRespondentName\": \"daniel gleeballs\",\n    \"D8MarriedInUk\": \"Yes\",\n    \"D8JurisdictionConnection\": [\n      \"G\"\n    ],\n    \"D8ReasonForDivorce\": \"unreasonable-behaviour\",\n    \"D8ReasonForDivorceBehaviourDetails\": \"bad behaviour\",\n    \"D8LegalProceedings\": \"No\",\n    \"D8FinancialOrder\": \"No\",\n    \"D8DivorceCostsClaim\": \"No\"\n  }\n}")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("DIV_030_065_CreateCaseDocUploadNo")
      .post("/data/case-types/${DVCaseType}/validate?pageId=solicitorCreateSolUploadDocs")
      .headers(headers_8)
      .body(StringBody("{\n  \"data\": {\n    \"D8DocumentsUploaded\": []\n  },\n  \"event\": {\n    \"id\": \"solicitorCreate\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"PetitionerSolicitorName\": \"john smith\",\n    \"PetitionerSolicitorFirm\": \"solicitor ltd\",\n    \"DerivedPetitionerSolicitorAddr\": \"12 test street, solicitor lane, kt25bu\",\n    \"D8SolicitorReference\": \"0123456\",\n    \"PetitionerSolicitorPhone\": \"07123456789\",\n    \"PetitionerSolicitorEmail\": \"johnsmith@solicitorltd.com\",\n    \"SolicitorAgreeToReceiveEmails\": \"Yes\",\n    \"D8PetitionerFirstName\": \"tessa\",\n    \"D8PetitionerLastName\": \"tickles\",\n    \"D8PetitionerNameDifferentToMarriageCert\": \"No\",\n    \"D8DivorceWho\": \"husband\",\n    \"D8InferredPetitionerGender\": \"female\",\n    \"D8MarriageIsSameSexCouple\": \"No\",\n    \"D8DerivedPetitionerHomeAddress\": \"14 divorce street, london, kt25bu\",\n    \"D8PetitionerPhoneNumber\": null,\n    \"D8PetitionerEmail\": null,\n    \"D8PetitionerContactDetailsConfidential\": \"keep\",\n    \"D8RespondentFirstName\": \"daniel\",\n    \"D8RespondentLastName\": \"gleeballs\",\n    \"D8RespondentNameAsOnMarriageCertificate\": \"No\",\n    \"D8InferredRespondentGender\": \"male\",\n    \"D8DerivedRespondentHomeAddress\": null,\n    \"D8RespondentCorrespondenceSendToSol\": \"No\",\n    \"D8DerivedRespondentCorrespondenceAddr\": \"14 divorcee street, london, kt25bu\",\n    \"D8MarriageDate\": \"2000-10-02\",\n    \"D8MarriagePetitionerName\": \"tessa tickles\",\n    \"D8MarriageRespondentName\": \"daniel gleeballs\",\n    \"D8MarriedInUk\": \"Yes\",\n    \"D8JurisdictionConnection\": [\n      \"G\"\n    ],\n    \"D8ReasonForDivorce\": \"unreasonable-behaviour\",\n    \"D8ReasonForDivorceBehaviourDetails\": \"bad behaviour\",\n    \"D8LegalProceedings\": \"No\",\n    \"D8FinancialOrder\": \"No\",\n    \"D8DivorceCostsClaim\": \"No\",\n    \"D8DocumentsUploaded\": []\n  }\n}")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("DIV_030_070_CreateCaseSubmit")
      .post("/data/caseworkers/:uid/jurisdictions/${DVJurisdiction}/case-types/${DVCaseType}/cases?ignore-warning=false")
      .headers(CommonHeader)
      .body(StringBody("{\n  \"data\": {\n    \"PetitionerSolicitorName\": \"john smith\",\n    \"PetitionerSolicitorFirm\": \"solicitor ltd\",\n    \"DerivedPetitionerSolicitorAddr\": \"12 test street, solicitor lane, kt25bu\",\n    \"D8SolicitorReference\": \"0123456\",\n    \"PetitionerSolicitorPhone\": \"07123456789\",\n    \"PetitionerSolicitorEmail\": \"johnsmith@solicitorltd.com\",\n    \"SolicitorAgreeToReceiveEmails\": \"Yes\",\n    \"D8PetitionerFirstName\": \"tessa\",\n    \"D8PetitionerLastName\": \"tickles\",\n    \"D8PetitionerNameDifferentToMarriageCert\": \"No\",\n    \"D8DivorceWho\": \"husband\",\n    \"D8InferredPetitionerGender\": \"female\",\n    \"D8MarriageIsSameSexCouple\": \"No\",\n    \"D8DerivedPetitionerHomeAddress\": \"14 divorce street, london, kt25bu\",\n    \"D8PetitionerPhoneNumber\": null,\n    \"D8PetitionerEmail\": null,\n    \"D8PetitionerContactDetailsConfidential\": \"keep\",\n    \"D8RespondentFirstName\": \"daniel\",\n    \"D8RespondentLastName\": \"gleeballs\",\n    \"D8RespondentNameAsOnMarriageCertificate\": \"No\",\n    \"D8InferredRespondentGender\": \"male\",\n    \"D8DerivedRespondentHomeAddress\": null,\n    \"D8RespondentCorrespondenceSendToSol\": \"No\",\n    \"D8DerivedRespondentCorrespondenceAddr\": \"14 divorcee street, london, kt25bu\",\n    \"D8MarriageDate\": \"2000-10-02\",\n    \"D8MarriagePetitionerName\": \"tessa tickles\",\n    \"D8MarriageRespondentName\": \"daniel gleeballs\",\n    \"D8MarriedInUk\": \"Yes\",\n    \"D8JurisdictionConnection\": [\n      \"G\"\n    ],\n    \"D8ReasonForDivorce\": \"unreasonable-behaviour\",\n    \"D8ReasonForDivorceBehaviourDetails\": \"bad behaviour\",\n    \"D8LegalProceedings\": \"No\",\n    \"D8FinancialOrder\": \"No\",\n    \"D8DivorceCostsClaim\": \"No\",\n    \"D8DocumentsUploaded\": []\n  },\n  \"event\": {\n    \"id\": \"solicitorCreate\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
      .check(jsonPath("$.id").saveAs("New_Case_Id")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)
  }

  val DVDocUpload = group("DIV_DocUpload") {

    exec(http("DIV_040_005_DocumentUploadPage")
      .get("/data/internal/cases/${New_Case_Id}/event-triggers/uploadDocument?ignore-warning=false")
      .headers(headers_5)
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("DIV_040_010_DocumentUploadToDM")
      .post(BaseURL + "/documents")
      .bodyPart(RawFileBodyPart("files", "1MB.pdf")
        .fileName("1MB.pdf")
        .transferEncoding("binary"))
      .asMultipartForm
      .formParam("classification", "PUBLIC")
      .check(status.is(200))
      .check(regex("""http://(.+)/""").saveAs("DMURL"))
      .check(regex("""/documents/(.+)"""").saveAs("Document_ID")))

    .exec(http("DIV_040_015_DocumentUploadSubmit")
      .post("/data/caseworkers/:uid/jurisdictions/${DVJurisdiction}/case-types/${DVCaseType}/cases/${New_Case_Id}/events")
      .headers(CommonHeader)
      .body(StringBody("{\n  \"data\": {\n    \"D8DocumentsUploaded\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"DocumentType\": \"other\",\n          \"DocumentEmailContent\": null,\n          \"DocumentDateAdded\": null,\n          \"DocumentComment\": null,\n          \"DocumentFileName\": null,\n          \"DocumentLink\": {\n            \"document_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal/documents/${Document_ID}\",\n            \"document_binary_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal/documents/${Document_ID}/binary\",\n            \"document_filename\": \"1MB.pdf\"\n          }\n        }\n      }\n    ]\n  },\n  \"event\": {\n    \"id\": \"uploadDocument\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}")))
  }

  .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val DVSearchAndView = group ("DIV_View") {

    exec(http("DIV_050_005_SearchPage")
      .get("/data/internal/case-types/${DVCaseType}/work-basket-inputs")
      .headers(headers_0))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("DIV_050_010_SearchForCase")
      .get("/aggregated/caseworkers/:uid/jurisdictions/${DVJurisdiction}/case-types/${DVCaseType}/cases?view=WORKBASKET&page=1")
      .headers(CommonHeader))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("DIV_050_015_OpenCase")
      .get("/data/internal/cases/${New_Case_Id}")
      .headers(headers_6))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("DIV_050_020_OpenDocument")
      .get("/documents/${Document_ID}/binary")
      .headers(headers_7))
  }
}
