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

  val headers_11 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")


  val CMCCreateCase = group("CMC_Create") {

    exec(http("CMC_030_005_CreateCasePage")
      .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions?access=create")
      .headers(CommonHeader))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("CMC_030_010_CreateCaseDetails")
      .get(BaseURL + "/data/internal/case-types/MoneyClaimCase/event-triggers/SubmitPrePayment?ignore-warning=false")
      .headers(headers_1)
      .check(jsonPath("$.event_token").saveAs("New_Case_event_token")))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("CMC_030_015_CreateCase")
      //.post(BaseURL + "/data/caseworkers/:uid/jurisdictions/CMC/case-types/MoneyClaimCase/cases?ignore-warning=false")
      .post(BaseURL + "/data/case-types/MoneyClaimCase/validate?pageId=SubmitPrePayment1")
      .headers(headers_3)
      .body(StringBody("{\n  \"data\": {\n    \"externalId\": \"cmc-id\"\n  },\n  \"event\": {\n    \"id\": \"SubmitPrePayment\",\n    \"summary\": \"cmc-create-case\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}")))

    .exec(http("CMC_030_020_CreateCase")
      .post("/data/caseworkers/:uid/jurisdictions/CMC/case-types/MoneyClaimCase/cases?ignore-warning=false")
      .headers(CommonHeader)
      .body(StringBody("{\n  \"data\": {\n    \"externalId\": null\n  },\n  \"event\": {\n    \"id\": \"SubmitPrePayment\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
      .check(jsonPath("$.id").saveAs("New_Case_Id")))
  }

  .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val PrintCaseID = exec {
    session =>
      println(session("New_Case_Id").as[String])
      session
  }

  val CMCSubmitPayment = group("CMC_SubmitPayment")
  {
    exec(http("CMC_040_005_SubmitPaymentPage")
      .get(BaseURL + "/data/internal/cases/${New_Case_Id}/event-triggers/SubmitPostPayment?ignore-warning=false")
      .headers(headers_11)
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token")))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

    /*.exec(http("CMC_040_010_SubmitPayment")
      .post(BaseURL + "/data/caseworkers/:uid/jurisdictions/CMC/case-types/MoneyClaimCase/cases/${New_Case_Id}/events")
      .headers(CommonHeader)
      .body(StringBody("{\n  \"data\": {\n    \"interestDateType\": \"CUSTOM\",\n    \"interestType\": \"BREAKDOWN\",\n    \"subjectType\": \"CLAIMANT\",\n    \"amountType\": \"NOT_KNOWN\",\n    \"interestEndDateType\": \"SUBMISSION\",\n    \"evidence\": [],\n    \"defendants\": [],\n    \"claimants\": [],\n    \"timeline\": [],\n    \"feeCode\": null,\n    \"interestRate\": null,\n    \"interestReason\": null,\n    \"interestBreakDownAmount\": null,\n    \"interestBreakDownExplanation\": null,\n    \"interestSpecificDailyAmount\": null,\n    \"interestClaimStartDate\": \"2019-08-01\",\n    \"interestStartDateReason\": null,\n    \"housingDisrepairOtherDamages\": null,\n    \"housingDisrepairCostOfRepairDamages\": null,\n    \"personalInjuryGeneralDamages\": null,\n    \"issuedOn\": \"2019-08-30\",\n    \"submittedOn\": \"2019-08-30T12:00:00.000\",\n    \"submitterEmail\": null,\n    \"id\": null,\n    \"features\": null,\n    \"subjectName\": null,\n    \"paymentId\": null,\n    \"paymentAmount\": null,\n    \"paymentReference\": null,\n    \"paymentStatus\": null,\n    \"submitterId\": null,\n    \"paymentDateCreated\": \"2019-08-30\",\n    \"externalId\": \"cmc-id\",\n    \"referenceNumber\": null,\n    \"feeAmountInPennies\": null,\n    \"reason\": null,\n    \"sotSignerName\": null,\n    \"sotSignerRole\": null,\n    \"feeAccountNumber\": null,\n    \"externalReferenceNumber\": null,\n    \"preferredCourt\": null\n  },\n  \"event\": {\n    \"id\": \"SubmitPostPayment\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}")))
*/
    .exec(http("CMC_040_010_SubmitPaymentDetails")
      .post("/data/case-types/MoneyClaimCase/validate?pageId=SubmitPostPayment1")
      .headers(headers_3)
      .body(StringBody("{\n  \"data\": {\n    \"interestDateType\": null,\n    \"interestType\": null,\n    \"subjectType\": null,\n    \"amountType\": null,\n    \"interestEndDateType\": null,\n    \"evidence\": [],\n    \"defendants\": [],\n    \"claimants\": [],\n    \"timeline\": [],\n    \"feeCode\": null,\n    \"interestRate\": null,\n    \"interestReason\": null,\n    \"interestBreakDownAmount\": null,\n    \"interestBreakDownExplanation\": null,\n    \"interestSpecificDailyAmount\": null,\n    \"interestClaimStartDate\": null,\n    \"interestStartDateReason\": null,\n    \"housingDisrepairOtherDamages\": null,\n    \"housingDisrepairCostOfRepairDamages\": null,\n    \"personalInjuryGeneralDamages\": null,\n    \"issuedOn\": null,\n    \"submittedOn\": null,\n    \"submitterEmail\": null,\n    \"id\": null,\n    \"features\": null,\n    \"subjectName\": null,\n    \"paymentId\": null,\n    \"paymentAmount\": null,\n    \"paymentReference\": null,\n    \"paymentStatus\": null,\n    \"submitterId\": null,\n    \"paymentDateCreated\": null,\n    \"externalId\": null,\n    \"referenceNumber\": null,\n    \"feeAmountInPennies\": null,\n    \"reason\": null,\n    \"sotSignerName\": null,\n    \"sotSignerRole\": null,\n    \"feeAccountNumber\": null,\n    \"externalReferenceNumber\": null,\n    \"preferredCourt\": null\n  },\n  \"event\": {\n    \"id\": \"SubmitPostPayment\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"interestDateType\": null,\n    \"interestType\": null,\n    \"subjectType\": null,\n    \"amountType\": null,\n    \"interestEndDateType\": null,\n    \"evidence\": [],\n    \"defendants\": [],\n    \"claimants\": [],\n    \"timeline\": [],\n    \"feeCode\": null,\n    \"interestRate\": null,\n    \"interestReason\": null,\n    \"interestBreakDownAmount\": null,\n    \"interestBreakDownExplanation\": null,\n    \"interestSpecificDailyAmount\": null,\n    \"interestClaimStartDate\": null,\n    \"interestStartDateReason\": null,\n    \"housingDisrepairOtherDamages\": null,\n    \"housingDisrepairCostOfRepairDamages\": null,\n    \"personalInjuryGeneralDamages\": null,\n    \"issuedOn\": null,\n    \"submittedOn\": null,\n    \"submitterEmail\": null,\n    \"id\": null,\n    \"features\": null,\n    \"subjectName\": null,\n    \"paymentId\": null,\n    \"paymentAmount\": null,\n    \"paymentReference\": null,\n    \"paymentStatus\": null,\n    \"submitterId\": null,\n    \"paymentDateCreated\": null,\n    \"externalId\": null,\n    \"referenceNumber\": null,\n    \"feeAmountInPennies\": null,\n    \"reason\": null,\n    \"sotSignerName\": null,\n    \"sotSignerRole\": null,\n    \"feeAccountNumber\": null,\n    \"externalReferenceNumber\": null,\n    \"preferredCourt\": null\n  },\n  \"case_reference\": \"${New_Case_Id}\"\n}")))

    .exec(http("CMC_040_015_SubmitPayment")
      .post("/data/caseworkers/:uid/jurisdictions/CMC/case-types/MoneyClaimCase/cases/${New_Case_Id}/events")
      .headers(CommonHeader)
      .body(StringBody("{\n  \"data\": {\n    \"interestDateType\": null,\n    \"interestType\": null,\n    \"subjectType\": null,\n    \"amountType\": null,\n    \"interestEndDateType\": null,\n    \"evidence\": [],\n    \"defendants\": [],\n    \"claimants\": [],\n    \"timeline\": [],\n    \"feeCode\": null,\n    \"interestRate\": null,\n    \"interestReason\": null,\n    \"interestBreakDownAmount\": null,\n    \"interestBreakDownExplanation\": null,\n    \"interestSpecificDailyAmount\": null,\n    \"interestClaimStartDate\": null,\n    \"interestStartDateReason\": null,\n    \"housingDisrepairOtherDamages\": null,\n    \"housingDisrepairCostOfRepairDamages\": null,\n    \"personalInjuryGeneralDamages\": null,\n    \"issuedOn\": null,\n    \"submittedOn\": null,\n    \"submitterEmail\": null,\n    \"id\": null,\n    \"features\": null,\n    \"subjectName\": null,\n    \"paymentId\": null,\n    \"paymentAmount\": null,\n    \"paymentReference\": null,\n    \"paymentStatus\": null,\n    \"submitterId\": null,\n    \"paymentDateCreated\": null,\n    \"externalId\": null,\n    \"referenceNumber\": null,\n    \"feeAmountInPennies\": null,\n    \"reason\": null,\n    \"sotSignerName\": null,\n    \"sotSignerRole\": null,\n    \"feeAccountNumber\": null,\n    \"externalReferenceNumber\": null,\n    \"preferredCourt\": null\n  },\n  \"event\": {\n    \"id\": \"SubmitPostPayment\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}")))
  }

  .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val CMCSearchAndView = group("CMC_View") {
    exec(http("CMC_050_005_SearchPage")
      .get("/data/internal/case-types/MoneyClaimCase/work-basket-inputs")
      .headers(headers_0))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("CMC_050_010_SearchForCase")
      .get("/aggregated/caseworkers/:uid/jurisdictions/CMC/case-types/MoneyClaimCase/cases?view=WORKBASKET&page=1&case_reference=${New_Case_Id}")
      .headers(CommonHeader))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("CMC_050_015_OpenCase")
      .get("/data/caseworkers/:uid/jurisdictions/CMC/case-types/MoneyClaimCase/cases/pagination_metadata?case_reference=${New_Case_Id}")
      .headers(CommonHeader))
  }

  .pause(MinThinkTime seconds, MaxThinkTime seconds)
}
