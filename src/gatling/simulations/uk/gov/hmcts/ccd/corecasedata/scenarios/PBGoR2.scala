package uk.gov.hmcts.ccd.corecasedata.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment
import scala.concurrent.duration._

object PBGoR2 {

  val BaseURL = Environment.baseURL
  val IdamURL = Environment.idamURL
  val CCDEnvurl = Environment.ccdEnvurl
  val CommonHeader = Environment.commonHeader
  val idam_header = Environment.idam_header
  val feedUserData = csv("ProbateUserData.csv").circular
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

  val headers_11 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
    "Origin" -> "https://ccd-case-management-web-perftest.service.core-compute-perftest.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site",
    "experimental" -> "true")

  val headers_23 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-case.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
    "Origin" -> "https://ccd-case-management-web-perftest.service.core-compute-perftest.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site",
    "experimental" -> "true")

  val headers_25 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json",
    "Content-Type" -> "application/json",
    "Origin" -> "https://ccd-case-management-web-perftest.service.core-compute-perftest.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site",
    "experimental" -> "true")

  val headers_26 = Map(
    "Accept" -> "application/json, text/plain, */*",
    "Origin" -> "https://ccd-case-management-web-perftest.service.core-compute-perftest.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site")

  val headers_33 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
    "Origin" -> "https://ccd-case-management-web-perftest.service.core-compute-perftest.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site",
    "experimental" -> "true")

  val headers_55 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
    "Origin" -> "https://ccd-case-management-web-perftest.service.core-compute-perftest.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site",
    "experimental" -> "true")

  val PBCreateCase = group("PB_Create") {
    exec(http("PBGoR_030_005_CreateCase")
      .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions?access=create")
      .headers(CommonHeader))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

      .exec(http("PBGoR_030_010_CreateCase")
        .get(BaseURL + "/data/internal/case-types/GrantOfRepresentation/event-triggers/applyforGrantPaperApplication?ignore-warning=false")
        .headers(headers_3)
        .check(jsonPath("$.event_token").saveAs("New_Case_event_token")))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

      .exec(http("PBGoR_030_015_CreateCase")
        .post("/data/case-types/GrantOfRepresentation/validate?pageId=applyforGrantPaperApplicationapplyforGrantPaperApplicationPage1")
        .headers(headers_5)
        .body(StringBody("{\n  \"data\": {\n    \"registryLocation\": \"Winchester\",\n    \"applicationType\": \"Personal\",\n    \"applicationSubmittedDate\": \"2020-02-01\",\n    \"caseType\": \"gop\",\n    \"extraCopiesOfGrant\": null,\n    \"outsideUKGrantCopies\": null,\n    \"applicationFeePaperForm\": null,\n    \"feeForCopiesPaperForm\": null,\n    \"totalFeePaperForm\": null,\n    \"paperPaymentMethod\": null,\n    \"languagePreferenceWelsh\": \"No\"\n  },\n  \"event\": {\n    \"id\": \"applyforGrantPaperApplication\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"registryLocation\": \"Winchester\",\n    \"applicationType\": \"Personal\",\n    \"applicationSubmittedDate\": \"2020-02-01\",\n    \"caseType\": \"gop\",\n    \"extraCopiesOfGrant\": null,\n    \"outsideUKGrantCopies\": null,\n    \"applicationFeePaperForm\": null,\n    \"feeForCopiesPaperForm\": null,\n    \"totalFeePaperForm\": null,\n    \"paperPaymentMethod\": null,\n    \"languagePreferenceWelsh\": \"No\"\n  }\n}"))
        )

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

      .exec(http("PBGoR_030_020_CreateCase")
        .post("/data/case-types/GrantOfRepresentation/validate?pageId=applyforGrantPaperApplicationapplyforGrantPaperApplicationPage2")
        .headers(headers_5)
        .body(StringBody("{\n  \"data\": {\n    \"primaryApplicantForenames\": \"tess\",\n    \"primaryApplicantSurname\": \"tickles\",\n    \"primaryApplicantPhoneNumber\": null,\n    \"primaryApplicantSecondPhoneNumber\": null,\n    \"primaryApplicantEmailAddress\": null,\n    \"primaryApplicantAddress\": {\n      \"AddressLine1\": \"01 test street\",\n      \"AddressLine2\": \"test lane\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"test\",\n      \"County\": \"london\",\n      \"PostCode\": \"kt25bu\",\n      \"Country\": \"united kingdom\"\n    },\n    \"primaryApplicantRelationshipToDeceased\": null,\n    \"primaryApplicantHasAlias\": \"No\",\n    \"primaryApplicantIsApplying\": \"Yes\"\n  },\n  \"event\": {\n    \"id\": \"applyforGrantPaperApplication\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"registryLocation\": \"Winchester\",\n    \"applicationType\": \"Personal\",\n    \"applicationSubmittedDate\": \"2020-02-01\",\n    \"caseType\": \"gop\",\n    \"extraCopiesOfGrant\": null,\n    \"outsideUKGrantCopies\": null,\n    \"applicationFeePaperForm\": null,\n    \"feeForCopiesPaperForm\": null,\n    \"totalFeePaperForm\": null,\n    \"paperPaymentMethod\": null,\n    \"languagePreferenceWelsh\": \"No\",\n    \"primaryApplicantForenames\": \"tess\",\n    \"primaryApplicantSurname\": \"tickles\",\n    \"primaryApplicantPhoneNumber\": null,\n    \"primaryApplicantSecondPhoneNumber\": null,\n    \"primaryApplicantEmailAddress\": null,\n    \"primaryApplicantAddress\": {\n      \"AddressLine1\": \"01 test street\",\n      \"AddressLine2\": \"test lane\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"test \",\n      \"County\": \"london\",\n      \"PostCode\": \"kt25bu\",\n      \"Country\": \"united kingdom\"\n    },\n    \"primaryApplicantRelationshipToDeceased\": null,\n    \"primaryApplicantHasAlias\": \"No\",\n    \"primaryApplicantIsApplying\": \"Yes\"\n  }\n}"))
      )

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

      .exec(http("PBGoR_030_025_CreateCase")
        .post("/data/case-types/GrantOfRepresentation/validate?pageId=applyforGrantPaperApplicationapplyforGrantPaperApplicationPage3")
        .headers(headers_5)
        .body(StringBody("{\n  \"data\": {\n    \"otherExecutorExists\": \"No\",\n    \"notifiedApplicants\": null,\n    \"adopted\": null\n  },\n  \"event\": {\n    \"id\": \"applyforGrantPaperApplication\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"registryLocation\": \"Winchester\",\n    \"applicationType\": \"Personal\",\n    \"applicationSubmittedDate\": \"2020-02-01\",\n    \"caseType\": \"gop\",\n    \"extraCopiesOfGrant\": null,\n    \"outsideUKGrantCopies\": null,\n    \"applicationFeePaperForm\": null,\n    \"feeForCopiesPaperForm\": null,\n    \"totalFeePaperForm\": null,\n    \"paperPaymentMethod\": null,\n    \"languagePreferenceWelsh\": \"No\",\n    \"primaryApplicantForenames\": \"tess\",\n    \"primaryApplicantSurname\": \"tickles\",\n    \"primaryApplicantPhoneNumber\": null,\n    \"primaryApplicantSecondPhoneNumber\": null,\n    \"primaryApplicantEmailAddress\": null,\n    \"primaryApplicantAddress\": {\n      \"AddressLine1\": \"01 test street\",\n      \"AddressLine2\": \"test lane\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"test\",\n      \"County\": \"london\",\n      \"PostCode\": \"kt25bu\",\n      \"Country\": \"united kingdom\"\n    },\n    \"primaryApplicantRelationshipToDeceased\": null,\n    \"primaryApplicantHasAlias\": \"No\",\n    \"primaryApplicantIsApplying\": \"Yes\",\n    \"otherExecutorExists\": \"No\",\n    \"notifiedApplicants\": null,\n    \"adopted\": null\n  }\n}"))
      )

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

      .exec(http("PBGoR_030_030_CreateCase")
        .post("/data/case-types/GrantOfRepresentation/validate?pageId=applyforGrantPaperApplicationapplyforGrantPaperApplicationPage4")
        .headers(headers_5)
        .body(StringBody("{\n  \"data\": {\n    \"boDeceasedTitle\": null,\n    \"deceasedForenames\": \"john\",\n    \"deceasedSurname\": \"smith\",\n    \"boDeceasedHonours\": null,\n    \"deceasedAddress\": {\n      \"AddressLine1\": \"12 test street\",\n      \"AddressLine2\": \"test lane\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"test\",\n      \"County\": \"london\",\n      \"PostCode\": \"kt25bu\",\n      \"Country\": \"\"\n    },\n    \"deceasedDateOfBirth\": \"1960-08-01\",\n    \"dateOfDeathType\": \"diedOn\",\n    \"deceasedDateOfDeath\": \"2020-01-01\",\n    \"deceasedAnyOtherNames\": \"No\",\n    \"deceasedMaritalStatus\": null,\n    \"foreignAsset\": null\n  },\n  \"event\": {\n    \"id\": \"applyforGrantPaperApplication\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"registryLocation\": \"Winchester\",\n    \"applicationType\": \"Personal\",\n    \"applicationSubmittedDate\": \"2020-02-01\",\n    \"caseType\": \"gop\",\n    \"extraCopiesOfGrant\": null,\n    \"outsideUKGrantCopies\": null,\n    \"applicationFeePaperForm\": null,\n    \"feeForCopiesPaperForm\": null,\n    \"totalFeePaperForm\": null,\n    \"paperPaymentMethod\": null,\n    \"languagePreferenceWelsh\": \"No\",\n    \"primaryApplicantForenames\": \"tess\",\n    \"primaryApplicantSurname\": \"tickles\",\n    \"primaryApplicantPhoneNumber\": null,\n    \"primaryApplicantSecondPhoneNumber\": null,\n    \"primaryApplicantEmailAddress\": null,\n    \"primaryApplicantAddress\": {\n      \"AddressLine1\": \"01 test street\",\n      \"AddressLine2\": \"test lane\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"test\",\n      \"County\": \"london\",\n      \"PostCode\": \"kt25bu\",\n      \"Country\": \"united kingdom\"\n    },\n    \"primaryApplicantRelationshipToDeceased\": null,\n    \"primaryApplicantHasAlias\": \"No\",\n    \"primaryApplicantIsApplying\": \"Yes\",\n    \"otherExecutorExists\": \"No\",\n    \"notifiedApplicants\": null,\n    \"adopted\": null,\n    \"boDeceasedTitle\": null,\n    \"deceasedForenames\": \"john\",\n    \"deceasedSurname\": \"smith\",\n    \"boDeceasedHonours\": null,\n    \"deceasedAddress\": {\n      \"AddressLine1\": \"12 test street\",\n      \"AddressLine2\": \"test lane\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"test\",\n      \"County\": \"london\",\n      \"PostCode\": \"kt25bu\",\n      \"Country\": \"\"\n    },\n    \"deceasedDateOfBirth\": \"1960-08-01\",\n    \"dateOfDeathType\": \"diedOn\",\n    \"deceasedDateOfDeath\": \"2020-01-01\",\n    \"deceasedAnyOtherNames\": \"No\",\n    \"deceasedMaritalStatus\": null,\n    \"foreignAsset\": null\n  }\n}"))
      )

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

      .exec(http("PBGoR_030_035_CreateCase")
        .post("/data/case-types/GrantOfRepresentation/validate?pageId=applyforGrantPaperApplicationapplyforGrantPaperApplicationPage5")
        .headers(headers_5)
        .body(StringBody("{\n  \"data\": {\n    \"willExists\": \"No\",\n    \"deceasedEnterMarriageOrCP\": null,\n    \"dateOfDivorcedCPJudicially\": null,\n    \"courtOfDecree\": null,\n    \"willGiftUnderEighteen\": null\n  },\n  \"event\": {\n    \"id\": \"applyforGrantPaperApplication\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"registryLocation\": \"Winchester\",\n    \"applicationType\": \"Personal\",\n    \"applicationSubmittedDate\": \"2020-02-01\",\n    \"caseType\": \"gop\",\n    \"extraCopiesOfGrant\": null,\n    \"outsideUKGrantCopies\": null,\n    \"applicationFeePaperForm\": null,\n    \"feeForCopiesPaperForm\": null,\n    \"totalFeePaperForm\": null,\n    \"paperPaymentMethod\": null,\n    \"languagePreferenceWelsh\": \"No\",\n    \"primaryApplicantForenames\": \"tess\",\n    \"primaryApplicantSurname\": \"tickles\",\n    \"primaryApplicantPhoneNumber\": null,\n    \"primaryApplicantSecondPhoneNumber\": null,\n    \"primaryApplicantEmailAddress\": null,\n    \"primaryApplicantAddress\": {\n      \"AddressLine1\": \"01 test street\",\n      \"AddressLine2\": \"test lane\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"test\",\n      \"County\": \"london\",\n      \"PostCode\": \"kt25bu\",\n      \"Country\": \"united kingdom\"\n    },\n    \"primaryApplicantRelationshipToDeceased\": null,\n    \"primaryApplicantHasAlias\": \"No\",\n    \"primaryApplicantIsApplying\": \"Yes\",\n    \"otherExecutorExists\": \"No\",\n    \"notifiedApplicants\": null,\n    \"adopted\": null,\n    \"boDeceasedTitle\": null,\n    \"deceasedForenames\": \"john\",\n    \"deceasedSurname\": \"smith\",\n    \"boDeceasedHonours\": null,\n    \"deceasedAddress\": {\n      \"AddressLine1\": \"12 test street\",\n      \"AddressLine2\": \"test lane\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"test\",\n      \"County\": \"london\",\n      \"PostCode\": \"kt25bu\",\n      \"Country\": \"\"\n    },\n    \"deceasedDateOfBirth\": \"1960-08-01\",\n    \"dateOfDeathType\": \"diedOn\",\n    \"deceasedDateOfDeath\": \"2020-01-01\",\n    \"deceasedAnyOtherNames\": \"No\",\n    \"deceasedMaritalStatus\": null,\n    \"foreignAsset\": null,\n    \"willExists\": \"No\",\n    \"deceasedEnterMarriageOrCP\": null,\n    \"dateOfDivorcedCPJudicially\": null,\n    \"courtOfDecree\": null,\n    \"willGiftUnderEighteen\": null\n  }\n}"))
      )

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

      .exec(http("PBGoR_030_040_CreateCase")
        .post("/data/case-types/GrantOfRepresentation/validate?pageId=applyforGrantPaperApplicationapplyforGrantPaperApplicationPage6")
        .headers(headers_5)
        .body(StringBody("{\n  \"data\": {\n    \"spouseOrPartner\": null\n  },\n  \"event\": {\n    \"id\": \"applyforGrantPaperApplication\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"registryLocation\": \"Winchester\",\n    \"applicationType\": \"Personal\",\n    \"applicationSubmittedDate\": \"2020-02-01\",\n    \"caseType\": \"gop\",\n    \"extraCopiesOfGrant\": null,\n    \"outsideUKGrantCopies\": null,\n    \"applicationFeePaperForm\": null,\n    \"feeForCopiesPaperForm\": null,\n    \"totalFeePaperForm\": null,\n    \"paperPaymentMethod\": null,\n    \"languagePreferenceWelsh\": \"No\",\n    \"primaryApplicantForenames\": \"tess\",\n    \"primaryApplicantSurname\": \"tickles\",\n    \"primaryApplicantPhoneNumber\": null,\n    \"primaryApplicantSecondPhoneNumber\": null,\n    \"primaryApplicantEmailAddress\": null,\n    \"primaryApplicantAddress\": {\n      \"AddressLine1\": \"01 test street\",\n      \"AddressLine2\": \"test lane\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"test\",\n      \"County\": \"london\",\n      \"PostCode\": \"kt25bu\",\n      \"Country\": \"united kingdom\"\n    },\n    \"primaryApplicantRelationshipToDeceased\": null,\n    \"primaryApplicantHasAlias\": \"No\",\n    \"primaryApplicantIsApplying\": \"Yes\",\n    \"otherExecutorExists\": \"No\",\n    \"notifiedApplicants\": null,\n    \"adopted\": null,\n    \"boDeceasedTitle\": null,\n    \"deceasedForenames\": \"john\",\n    \"deceasedSurname\": \"smith\",\n    \"boDeceasedHonours\": null,\n    \"deceasedAddress\": {\n      \"AddressLine1\": \"12 test street\",\n      \"AddressLine2\": \"test lane\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"test\",\n      \"County\": \"london\",\n      \"PostCode\": \"kt25bu\",\n      \"Country\": \"\"\n    },\n    \"deceasedDateOfBirth\": \"1960-08-01\",\n    \"dateOfDeathType\": \"diedOn\",\n    \"deceasedDateOfDeath\": \"2020-01-01\",\n    \"deceasedAnyOtherNames\": \"No\",\n    \"deceasedMaritalStatus\": null,\n    \"foreignAsset\": null,\n    \"willExists\": \"No\",\n    \"deceasedEnterMarriageOrCP\": null,\n    \"dateOfDivorcedCPJudicially\": null,\n    \"courtOfDecree\": null,\n    \"willGiftUnderEighteen\": null,\n    \"spouseOrPartner\": null\n  }\n}"))
      )

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

      .exec(http("PBGoR_030_045_CreateCase")
        .post("/data/case-types/GrantOfRepresentation/validate?pageId=applyforGrantPaperApplicationapplyforGrantPaperApplicationPage7")
        .headers(headers_5)
        .body(StringBody("{\n  \"data\": {\n    \"applyingAsAnAttorney\": null\n  },\n  \"event\": {\n    \"id\": \"applyforGrantPaperApplication\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"registryLocation\": \"Winchester\",\n    \"applicationType\": \"Personal\",\n    \"applicationSubmittedDate\": \"2020-02-01\",\n    \"caseType\": \"gop\",\n    \"extraCopiesOfGrant\": null,\n    \"outsideUKGrantCopies\": null,\n    \"applicationFeePaperForm\": null,\n    \"feeForCopiesPaperForm\": null,\n    \"totalFeePaperForm\": null,\n    \"paperPaymentMethod\": null,\n    \"languagePreferenceWelsh\": \"No\",\n    \"primaryApplicantForenames\": \"tess\",\n    \"primaryApplicantSurname\": \"tickles\",\n    \"primaryApplicantPhoneNumber\": null,\n    \"primaryApplicantSecondPhoneNumber\": null,\n    \"primaryApplicantEmailAddress\": null,\n    \"primaryApplicantAddress\": {\n      \"AddressLine1\": \"01 test street\",\n      \"AddressLine2\": \"test lane\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"test\",\n      \"County\": \"london\",\n      \"PostCode\": \"kt25bu\",\n      \"Country\": \"united kingdom\"\n    },\n    \"primaryApplicantRelationshipToDeceased\": null,\n    \"primaryApplicantHasAlias\": \"No\",\n    \"primaryApplicantIsApplying\": \"Yes\",\n    \"otherExecutorExists\": \"No\",\n    \"notifiedApplicants\": null,\n    \"adopted\": null,\n    \"boDeceasedTitle\": null,\n    \"deceasedForenames\": \"john\",\n    \"deceasedSurname\": \"smith\",\n    \"boDeceasedHonours\": null,\n    \"deceasedAddress\": {\n      \"AddressLine1\": \"12 test street\",\n      \"AddressLine2\": \"test lane\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"test\",\n      \"County\": \"london\",\n      \"PostCode\": \"kt25bu\",\n      \"Country\": \"\"\n    },\n    \"deceasedDateOfBirth\": \"1960-08-01\",\n    \"dateOfDeathType\": \"diedOn\",\n    \"deceasedDateOfDeath\": \"2020-01-01\",\n    \"deceasedAnyOtherNames\": \"No\",\n    \"deceasedMaritalStatus\": null,\n    \"foreignAsset\": null,\n    \"willExists\": \"No\",\n    \"deceasedEnterMarriageOrCP\": null,\n    \"dateOfDivorcedCPJudicially\": null,\n    \"courtOfDecree\": null,\n    \"willGiftUnderEighteen\": null,\n    \"spouseOrPartner\": null,\n    \"applyingAsAnAttorney\": null\n  }\n}"))
      )

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

      .exec(http("PBGoR_030_050_CreateCase")
        .post("/data/case-types/GrantOfRepresentation/validate?pageId=applyforGrantPaperApplicationapplyforGrantPaperApplicationPage8")
        .headers(headers_5)
        .body(StringBody("{\n  \"data\": {\n    \"deceasedDomicileInEngWales\": null,\n    \"domicilityCountry\": null,\n    \"ukEstate\": [],\n    \"domicilityIHTCert\": null\n  },\n  \"event\": {\n    \"id\": \"applyforGrantPaperApplication\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"registryLocation\": \"Winchester\",\n    \"applicationType\": \"Personal\",\n    \"applicationSubmittedDate\": \"2020-02-01\",\n    \"caseType\": \"gop\",\n    \"extraCopiesOfGrant\": null,\n    \"outsideUKGrantCopies\": null,\n    \"applicationFeePaperForm\": null,\n    \"feeForCopiesPaperForm\": null,\n    \"totalFeePaperForm\": null,\n    \"paperPaymentMethod\": null,\n    \"languagePreferenceWelsh\": \"No\",\n    \"primaryApplicantForenames\": \"tess\",\n    \"primaryApplicantSurname\": \"tickles\",\n    \"primaryApplicantPhoneNumber\": null,\n    \"primaryApplicantSecondPhoneNumber\": null,\n    \"primaryApplicantEmailAddress\": null,\n    \"primaryApplicantAddress\": {\n      \"AddressLine1\": \"01 test street\",\n      \"AddressLine2\": \"test lane\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"test\",\n      \"County\": \"london\",\n      \"PostCode\": \"kt25bu\",\n      \"Country\": \"united kingdom\"\n    },\n    \"primaryApplicantRelationshipToDeceased\": null,\n    \"primaryApplicantHasAlias\": \"No\",\n    \"primaryApplicantIsApplying\": \"Yes\",\n    \"otherExecutorExists\": \"No\",\n    \"notifiedApplicants\": null,\n    \"adopted\": null,\n    \"boDeceasedTitle\": null,\n    \"deceasedForenames\": \"john\",\n    \"deceasedSurname\": \"smith\",\n    \"boDeceasedHonours\": null,\n    \"deceasedAddress\": {\n      \"AddressLine1\": \"12 test street\",\n      \"AddressLine2\": \"test lane\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"test\",\n      \"County\": \"london\",\n      \"PostCode\": \"kt25bu\",\n      \"Country\": \"\"\n    },\n    \"deceasedDateOfBirth\": \"1960-08-01\",\n    \"dateOfDeathType\": \"diedOn\",\n    \"deceasedDateOfDeath\": \"2020-01-01\",\n    \"deceasedAnyOtherNames\": \"No\",\n    \"deceasedMaritalStatus\": null,\n    \"foreignAsset\": null,\n    \"willExists\": \"No\",\n    \"deceasedEnterMarriageOrCP\": null,\n    \"dateOfDivorcedCPJudicially\": null,\n    \"courtOfDecree\": null,\n    \"willGiftUnderEighteen\": null,\n    \"spouseOrPartner\": null,\n    \"applyingAsAnAttorney\": null,\n    \"deceasedDomicileInEngWales\": null,\n    \"domicilityCountry\": null,\n    \"ukEstate\": [],\n    \"domicilityIHTCert\": null\n  }\n}"))
      )

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

      .exec(http("PBGoR_030_055_CreateCase")
        .post("/data/case-types/GrantOfRepresentation/validate?pageId=applyforGrantPaperApplicationapplyforGrantPaperApplicationPage9")
        .headers(headers_5)
        .body(StringBody("{\n  \"data\": {\n    \"ihtFormCompletedOnline\": \"No\",\n    \"ihtFormId\": \"IHT205\",\n    \"ihtGrossValue\": \"300000\",\n    \"ihtNetValue\": \"350000\"\n  },\n  \"event\": {\n    \"id\": \"applyforGrantPaperApplication\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"registryLocation\": \"Winchester\",\n    \"applicationType\": \"Personal\",\n    \"applicationSubmittedDate\": \"2020-02-01\",\n    \"caseType\": \"gop\",\n    \"extraCopiesOfGrant\": null,\n    \"outsideUKGrantCopies\": null,\n    \"applicationFeePaperForm\": null,\n    \"feeForCopiesPaperForm\": null,\n    \"totalFeePaperForm\": null,\n    \"paperPaymentMethod\": null,\n    \"languagePreferenceWelsh\": \"No\",\n    \"primaryApplicantForenames\": \"tess\",\n    \"primaryApplicantSurname\": \"tickles\",\n    \"primaryApplicantPhoneNumber\": null,\n    \"primaryApplicantSecondPhoneNumber\": null,\n    \"primaryApplicantEmailAddress\": null,\n    \"primaryApplicantAddress\": {\n      \"AddressLine1\": \"01 test street\",\n      \"AddressLine2\": \"test lane\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"test\",\n      \"County\": \"london\",\n      \"PostCode\": \"kt25bu\",\n      \"Country\": \"united kingdom\"\n    },\n    \"primaryApplicantRelationshipToDeceased\": null,\n    \"primaryApplicantHasAlias\": \"No\",\n    \"primaryApplicantIsApplying\": \"Yes\",\n    \"otherExecutorExists\": \"No\",\n    \"notifiedApplicants\": null,\n    \"adopted\": null,\n    \"boDeceasedTitle\": null,\n    \"deceasedForenames\": \"john\",\n    \"deceasedSurname\": \"smith\",\n    \"boDeceasedHonours\": null,\n    \"deceasedAddress\": {\n      \"AddressLine1\": \"12 test street\",\n      \"AddressLine2\": \"test lane\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"test\",\n      \"County\": \"london\",\n      \"PostCode\": \"kt25bu\",\n      \"Country\": \"\"\n    },\n    \"deceasedDateOfBirth\": \"1960-08-01\",\n    \"dateOfDeathType\": \"diedOn\",\n    \"deceasedDateOfDeath\": \"2020-01-01\",\n    \"deceasedAnyOtherNames\": \"No\",\n    \"deceasedMaritalStatus\": null,\n    \"foreignAsset\": null,\n    \"willExists\": \"No\",\n    \"deceasedEnterMarriageOrCP\": null,\n    \"dateOfDivorcedCPJudicially\": null,\n    \"courtOfDecree\": null,\n    \"willGiftUnderEighteen\": null,\n    \"spouseOrPartner\": null,\n    \"applyingAsAnAttorney\": null,\n    \"deceasedDomicileInEngWales\": null,\n    \"domicilityCountry\": null,\n    \"ukEstate\": [],\n    \"domicilityIHTCert\": null,\n    \"ihtFormCompletedOnline\": \"No\",\n    \"ihtFormId\": \"IHT205\",\n    \"ihtGrossValue\": \"300000\",\n    \"ihtNetValue\": \"350000\"\n  }\n}"))
      )

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

      .exec(http("PBGoR_030_060_CreateCase")
        .post("/data/case-types/GrantOfRepresentation/cases?ignore-warning=false")
        .headers(headers_23)
        .body(StringBody("{\n  \"data\": {\n    \"registryLocation\": \"Winchester\",\n    \"applicationType\": \"Personal\",\n    \"applicationSubmittedDate\": \"2020-02-01\",\n    \"caseType\": \"gop\",\n    \"extraCopiesOfGrant\": null,\n    \"outsideUKGrantCopies\": null,\n    \"applicationFeePaperForm\": null,\n    \"feeForCopiesPaperForm\": null,\n    \"totalFeePaperForm\": null,\n    \"paperPaymentMethod\": null,\n    \"languagePreferenceWelsh\": \"No\",\n    \"primaryApplicantForenames\": \"tess\",\n    \"primaryApplicantSurname\": \"tickles\",\n    \"primaryApplicantPhoneNumber\": null,\n    \"primaryApplicantSecondPhoneNumber\": null,\n    \"primaryApplicantEmailAddress\": null,\n    \"primaryApplicantAddress\": {\n      \"AddressLine1\": \"01 test street\",\n      \"AddressLine2\": \"test lane\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"test\",\n      \"County\": \"london\",\n      \"PostCode\": \"kt25bu\",\n      \"Country\": \"united kingdom\"\n    },\n    \"primaryApplicantRelationshipToDeceased\": null,\n    \"primaryApplicantHasAlias\": \"No\",\n    \"primaryApplicantIsApplying\": \"Yes\",\n    \"otherExecutorExists\": \"No\",\n    \"notifiedApplicants\": null,\n    \"adopted\": null,\n    \"boDeceasedTitle\": null,\n    \"deceasedForenames\": \"john\",\n    \"deceasedSurname\": \"smith\",\n    \"boDeceasedHonours\": null,\n    \"deceasedAddress\": {\n      \"AddressLine1\": \"12 test street\",\n      \"AddressLine2\": \"test lane\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"test\",\n      \"County\": \"london\",\n      \"PostCode\": \"kt25bu\",\n      \"Country\": \"\"\n    },\n    \"deceasedDateOfBirth\": \"1960-08-01\",\n    \"dateOfDeathType\": \"diedOn\",\n    \"deceasedDateOfDeath\": \"2020-01-01\",\n    \"deceasedAnyOtherNames\": \"No\",\n    \"deceasedMaritalStatus\": null,\n    \"foreignAsset\": null,\n    \"willExists\": \"No\",\n    \"deceasedEnterMarriageOrCP\": null,\n    \"dateOfDivorcedCPJudicially\": null,\n    \"courtOfDecree\": null,\n    \"willGiftUnderEighteen\": null,\n    \"spouseOrPartner\": null,\n    \"applyingAsAnAttorney\": null,\n    \"deceasedDomicileInEngWales\": null,\n    \"domicilityCountry\": null,\n    \"ukEstate\": [],\n    \"domicilityIHTCert\": null,\n    \"ihtFormCompletedOnline\": \"No\",\n    \"ihtFormId\": \"IHT205\",\n    \"ihtGrossValue\": \"300000\",\n    \"ihtNetValue\": \"350000\"\n  },\n  \"event\": {\n    \"id\": \"applyforGrantPaperApplication\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${New_Case_event_token}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
        .check(jsonPath("$.id").saveAs("New_Case_Id")))
  }
  val PBPrintCase = group("PB_Print") {
    exec(http("PBGoR_040_005_PrintCase")
      .get(BaseURL + "/data/internal/cases/${New_Case_Id}/event-triggers/boPrintCase?ignore-warning=false")
      .headers(headers_11)
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token")))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

      .exec(http("PBGoR_040_010_PrintCase")
        .post("/data/case-types/GrantOfRepresentation/validate?pageId=boPrintCasecasePrintedPage1")
        .headers(headers_33)
        .body(StringBody("{\n  \"data\": {\n    \"casePrinted\": \"Yes\"\n  },\n  \"event\": {\n    \"id\": \"boPrintCase\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"casePrinted\": \"Yes\"\n  },\n  \"case_reference\": \"${New_Case_Id}\"\n}")))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

      .exec(http("PBGoR_040_015_PrintCase")
        .post("/data/cases/${New_Case_Id}/events")
        .headers(headers_55)
        .body(StringBody("{\n  \"data\": {\n    \"casePrinted\": \"Yes\"\n  },\n  \"event\": {\n    \"id\": \"boPrintCase\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}")))

  }

}