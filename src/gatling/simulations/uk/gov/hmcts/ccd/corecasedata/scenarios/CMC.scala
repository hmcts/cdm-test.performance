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

  val headers_8 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json",
    "Content-Type" -> "application/json",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val headers_11 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val setJurisdiction = exec(session => {
    session.set("Jurisdiction", "CMC")
  })
    .exec(session => {
      println(session)
      session
    })

  val setCaseType = exec(session => {
    session.set("CaseType", "MoneyClaimCase")
  })
    .exec(session => {
      println(session)
      session
    })

  val CMCLogin = group("CMC_Login") {

    exec(http("CDM_020_005_Login")
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

      .resources(http("CDM_020_010_Login")
        .get(CCDEnvurl + "/config")
        .headers(CommonHeader)))

      .exec(http("CDM_020_015_Login")
        .options(BaseURL + "/oauth2?code=${authCode}&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
        .headers(CommonHeader))
      .exec(http("CDM_020_020_Login")
        .get(BaseURL + "/oauth2?code=${authCode}&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
        .headers(CommonHeader))
      .exec(http("CDM_020_025_Login")
        .get(CCDEnvurl + "/config")
        .headers(CommonHeader))

      .exec(http("CDM_020_030_Login")
        .options(BaseURL + "/data/caseworkers/:uid/profile"))

      .exec(http("CDM_020_035_Login")
        .get(BaseURL + "/data/caseworkers/:uid/profile")
        .headers(CommonHeader))

      .exec(http("CDM_020_040_Login")
        .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types?access=read")
        .resources(http("CDM_020_045_Login")
          .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types?access=read")
          .headers(CommonHeader)))

      .exec(http("CDM_020_050_Login")
        .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/work-basket-inputs"))

      .exec(http("CDM_020_055_Login")
        .options(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases?view=WORKBASKET&state=TODO&page=1"))

      .exec(http("CDM_020_060_Login")
        .options(BaseURL + "/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/pagination_metadata?state=TODO"))

      .exec(http("CDM_020_065_Login")
        .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/work-basket-inputs")
        .headers(CommonHeader))

      .exec(http("CDM_020_070_Login")
        .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases?view=WORKBASKET&state=TODO&page=1")
        .headers(CommonHeader))

      .exec(http("CDM_020_075_Login")
        .get(BaseURL + "/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/cases/pagination_metadata?state=TODO")
        .headers(CommonHeader))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)
  }

  val CMCCreateCase = group("CMC_Create") {

    exec(http("CMC_030_005_CreateCase")
      .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions?access=create")
      .headers(CommonHeader))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("CMC_030_010_CreateCase")
      .get(BaseURL + "/data/internal/case-types/MoneyClaimCase/event-triggers/SubmitPrePayment?ignore-warning=false")
      .headers(headers_1)
      .check(jsonPath("$.event_token").saveAs("New_Case_event_token")))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("CMC_030_015_CreateCase")
      .post(BaseURL + "/data/caseworkers/:uid/jurisdictions/CMC/case-types/MoneyClaimCase/cases?ignore-warning=false")
      .headers(CommonHeader)
      .body(StringBody("{\n  \"data\": {\n    \"externalId\": \"cmc-id\"\n  },\n  \"event\": {\n    \"id\": \"SubmitPrePayment\",\n    \"summary\": \"cmc-create-case\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
      .check(jsonPath("$.id").saveAs("New_Case_Id")))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)
  }

  val PrintCaseID = exec {
    session =>
      println(session("New_Case_Id").as[String])
      session
  }

  val CMCSubmitPayment = group("CMC_SubmitPayment")
  {
    exec(http("CMC_040_005_SubmitPayment")
      .get(BaseURL + "/data/internal/cases/${New_Case_Id}/event-triggers/SubmitPostPayment?ignore-warning=false")
      .headers(headers_11)
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token")))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("CMC_040_010_SubmitPayment")
      .post(BaseURL + "/data/caseworkers/:uid/jurisdictions/CMC/case-types/MoneyClaimCase/cases/${New_Case_Id}/events")
      .headers(CommonHeader)
      .body(StringBody("{\n  \"data\": {\n    \"interestDateType\": \"CUSTOM\",\n    \"interestType\": \"BREAKDOWN\",\n    \"subjectType\": \"CLAIMANT\",\n    \"amountType\": \"NOT_KNOWN\",\n    \"interestEndDateType\": \"SUBMISSION\",\n    \"evidence\": [],\n    \"defendants\": [],\n    \"claimants\": [],\n    \"timeline\": [],\n    \"feeCode\": null,\n    \"interestRate\": null,\n    \"interestReason\": null,\n    \"interestBreakDownAmount\": null,\n    \"interestBreakDownExplanation\": null,\n    \"interestSpecificDailyAmount\": null,\n    \"interestClaimStartDate\": \"2019-08-01\",\n    \"interestStartDateReason\": null,\n    \"housingDisrepairOtherDamages\": null,\n    \"housingDisrepairCostOfRepairDamages\": null,\n    \"personalInjuryGeneralDamages\": null,\n    \"issuedOn\": \"2019-08-30\",\n    \"submittedOn\": \"2019-08-30T12:00:00.000\",\n    \"submitterEmail\": null,\n    \"id\": null,\n    \"features\": null,\n    \"subjectName\": null,\n    \"paymentId\": null,\n    \"paymentAmount\": null,\n    \"paymentReference\": null,\n    \"paymentStatus\": null,\n    \"submitterId\": null,\n    \"paymentDateCreated\": \"2019-08-30\",\n    \"externalId\": \"cmc-id\",\n    \"referenceNumber\": null,\n    \"feeAmountInPennies\": null,\n    \"reason\": null,\n    \"sotSignerName\": null,\n    \"sotSignerRole\": null,\n    \"feeAccountNumber\": null,\n    \"externalReferenceNumber\": null,\n    \"preferredCourt\": null\n  },\n  \"event\": {\n    \"id\": \"SubmitPostPayment\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}")))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)
  }

  val CMCSearchAndView = group("CMC_View") {
    exec(http("CMC_050_005_SearchPage")
      .get("/data/internal/case-types/MoneyClaimCase/work-basket-inputs")
      .headers(headers_0))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

      .exec(http("CMC_050_010_SearchAndView")
        .get("/aggregated/caseworkers/:uid/jurisdictions/CMC/case-types/MoneyClaimCase/cases?view=WORKBASKET&page=1&case_reference=${New_Case_Id}")
        .headers(CommonHeader))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

      .exec(http("CMC_050_015_SearchAndView")
        .get("/data/caseworkers/:uid/jurisdictions/CMC/case-types/MoneyClaimCase/cases/pagination_metadata?case_reference=${New_Case_Id}")
        .headers(CommonHeader))

      .exec(http("CMC_050_020_SearchAndView")
        .get("/data/internal/cases/${New_Case_Id}")
        .headers(headers_8))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)
  }
}
