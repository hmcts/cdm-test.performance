package uk.gov.hmcts.ccd.corecasedata.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment

import scala.concurrent.duration._

object Validate {
  
  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime
  val CommonHeader = Environment.commonHeader	
  
 	val fileProviderRand = csv("listoffiles.csv").random
 	val RndShuffle = (1 to 10).toList
 	val _documentUpload = scala.util.Random.shuffle(RndShuffle).head
  	println(_documentUpload)
  
	val validateFirstPage = group("AT_Create") {
		exec(http("CDM_050_005_ValidateFirstPage")
			.options("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate"))

			.exec(http("CDM_050_010_ValidateFirstPage")
				.post("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
				.headers(CommonHeader)
				.body(StringBody("""{"data": {"TextField": "Performance Testing First Page Text ${FirstpageText}","NumberField": "${FirstpageNumberField}","YesOrNoField": "Yes","PhoneUKField": "02020002002","EmailField": "confirmation${FirstpageEmailRandNumber}@confirmation.com"},"event": {"id": "${PickCaseEventType}","summary": "","description": ""},"event_token": """"  + "${New_Case_event_token}" +   """","ignore_warning": false} """) ))

			.pause(MinThinkTime seconds, MaxThinkTime seconds)
	}
			
	val validateSecondPage = group("AT_Create") {
		exec(http("CDM_060_005_ValidateSecondPage")
			.options("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate"))

			.exec(http("CDM_060_010_ValidateSecondPage")
				.post("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
				.headers(CommonHeader)
				.body(StringBody("""{"data": {"MoneyGBPField": "${FirstpageMoneyField}","DateField": "2008-12-12","TextAreaField": "Performance Testing Second Page Text Area - ${SecondpageText}","FixedListField": "","MultiSelectListField": []},"event": {"id": "${PickCaseEventType}","summary": "","description": ""},"event_token": """"  + "${New_Case_event_token}" +   """","ignore_warning": false}""")))

			.pause(MinThinkTime seconds, MaxThinkTime seconds)
	}

 	val validateThirdPage = group("AT_Create") {
		feed(fileProviderRand)
			.exec(http("CDM_070_005_ValidateThirdPage")
				.post("/documents")
				.bodyPart(RawFileBodyPart("files", "${filename}")
					.fileName("${filename}")
					.transferEncoding("binary")
				).asMultipartForm
				.formParam("classification", "PUBLIC")
				.check(status.is(200))
				.check(regex("""http://(.+)/""").saveAs("DMURL"))
				.check(regex("""/documents/(.+)"""").saveAs("Document_ID")))

			.pause(MinThinkTime seconds, MaxThinkTime seconds)

			.doIf(session => session("Document_ID").as[String].isEmpty())
			{
				exec(http("CDM_070_010_ValidateThirdPageNo")
					.options("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate"))

					.exec(http("CDM_070_015_ValidateThirdPageNo")
						.post("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
						.headers(CommonHeader)
						.body(StringBody("""{"data": {"ComplexField": {"ComplexTextField": "Performance Testing Third page Text - Third page Text ${ThirdpageText}","ComplexFixedListField": "","ComplexNestedField": {"NestedNumberField": "${ThirdpageNestedNumberField}","NestedCollectionTextField": []}},"DocumentField": {"document_url": "http://${DMURL}/${Document_ID}","document_binary_url": "http://${DMURL}/${Document_ID}/binary","document_filename": "${filename}"}},"event": {"id": "${PickCaseEventType}","summary": "","description": ""},"event_token": """"  + "${New_Case_event_token}" +   """","ignore_warning": false}""")))

					.pause(MinThinkTime seconds, MaxThinkTime seconds)
			}

			.doIf(session => ! session("Document_ID").as[String].isEmpty())
			{
				exec(http("CDM_070_010_ValidateThirdPageYes")
					.options("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate"))

					.exec(http("CDM_070_015_ValidateThirdPageYes")
						.post("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
						.headers(CommonHeader)
						.body(StringBody("""{"data": {"ComplexField": {"ComplexTextField": "Performance Testing Third page Text - Third page Text ${ThirdpageText}","ComplexFixedListField": "","ComplexNestedField": {"NestedNumberField": "${ThirdpageNestedNumberField}","NestedCollectionTextField": []}}},"event": {"id": "${PickCaseEventType}","summary": "","description": ""},"event_token": """"  + "${New_Case_event_token}" +   """","ignore_warning": false}""")))

					.pause(MinThinkTime seconds, MaxThinkTime seconds)
			}
	}

}