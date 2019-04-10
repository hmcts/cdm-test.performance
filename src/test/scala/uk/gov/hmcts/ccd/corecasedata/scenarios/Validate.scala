package uk.gov.hmcts.ccd.corecasedata.scenarios

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment
import io.gatling.core.feeder.RecordSeqFeederBuilder
import scala.util.Random

object Validate {
  
  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime
  val CommonHeader = Environment.commonHeader	
  
   val fileProviderRand: RecordSeqFeederBuilder[String] = csv("listoffiles.csv").random
   val RndShuffle = (1 to 10).toList 
   val _documentUpload = scala.util.Random.shuffle(RndShuffle).head 
  println(_documentUpload) 
  
  	val validateFirstPage = exec(http("TX05_HMCTSReform_CCD_CreateCase_Validate_validateFirstPage_options_validate")
			   .options("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
      		  .resources(http("TX05_HMCTSReform_CCD_CreateCase_Validate_validateFirstPage_post_validate")
      			.post("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
      			.headers(CommonHeader)
      			.body(StringBody("""{"data": {"TextField": "Performance Testing First Page Text ${FirstpageText}","NumberField": "${FirstpageNumberField}","YesOrNoField": "Yes","PhoneUKField": "02020002002","EmailField": "confirmation${FirstpageEmailRandNumber}@confirmation.com"},"event": {"id": "${PickCaseEventType}","summary": "","description": ""},"event_token": """"  + "${New_Case_event_token}" +   """","ignore_warning": false} """) )))
      			.pause(MinThinkTime seconds, MaxThinkTime seconds)
  	
			
 val validateSecondPage = exec(http("TX06_HMCTSReform_CCD_CreateCase_Validate_validateSecondPage_options_validate")
			.options("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
			.resources(http("TX06_HMCTSReform_CCD_CreateCase_Validate_validateSecondPage_post_validate")
			.post("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
			.headers(CommonHeader)
			.body(StringBody("""{"data": {"MoneyGBPField": "${FirstpageMoneyField}","DateField": "2008-12-12","TextAreaField": "Performance Testing Second Page Text Area - ${SecondpageText}","FixedListField": "","MultiSelectListField": []},"event": {"id": "${PickCaseEventType}","summary": "","description": ""},"event_token": """"  + "${New_Case_event_token}" +   """","ignore_warning": false}"""))))
	    .pause(MinThinkTime seconds, MaxThinkTime seconds)
 

	    
 val validateThirdPage = feed(fileProviderRand)
        .exec(http("TX07.1_HMCTSReform_CCD_CreateCase_UploadDocument_${filename}")
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
        .pause(MinThinkTime seconds, MaxThinkTime seconds)
        
 
        
        .doIfOrElse(session => !session("Document_ID").equals(Nil)) { // this doesn't work 
         exec(http("TX07_HMCTSReform_CCD_CreateCase_Validate_validateThirdPage_options_validate")
			.options("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
			.resources(http("TX07_HMCTSReform_CCD_CreateCase_Validate_validateThirdPage_post_validate")
			.post("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
			.headers(CommonHeader)
		  .body(StringBody("""{"data": {"ComplexField": {"ComplexTextField": "Performance Testing Third page Text - Third page Text ${ThirdpageText}","ComplexFixedListField": "","ComplexNestedField": {"NestedNumberField": "${ThirdpageNestedNumberField}","NestedCollectionTextField": []}},"DocumentField": {"document_url": "http://${DMURL}/${Document_ID}","document_binary_url": "http://${DMURL}/${Document_ID}/binary","document_filename": "${filename}"}},"event": {"id": "${PickCaseEventType}","summary": "","description": ""},"event_token": """"  + "${New_Case_event_token}" +   """","ignore_warning": false}"""))))
			.pause(MinThinkTime seconds, MaxThinkTime seconds)
      }
      {
        exec(http("TX07_HMCTSReform_CCD_CreateCase_Validate_validateThirdPage_options_validate")
			.options("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
			.resources(http("TX07_HMCTSReform_CCD_CreateCase_Validate_validateThirdPage_post_validate")
			.post("/data/caseworkers/:uid/jurisdictions/${Jurisdiction}/case-types/${CaseType}/validate")
			.headers(CommonHeader)
		  .body(StringBody("""{"data": {"ComplexField": {"ComplexTextField": "Performance Testing Third page Text - Third page Text ${ThirdpageText}","ComplexFixedListField": "","ComplexNestedField": {"NestedNumberField": "${ThirdpageNestedNumberField}","NestedCollectionTextField": []}}},"event": {"id": "${PickCaseEventType}","summary": "","description": ""},"event_token": """"  + "${New_Case_event_token}" +   """","ignore_warning": false}"""))))
			.pause(MinThinkTime seconds, MaxThinkTime seconds)
      }
 
}