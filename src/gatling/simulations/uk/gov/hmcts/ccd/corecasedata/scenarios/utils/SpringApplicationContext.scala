//package uk.gov.hmcts.ccd.corecasedata.scenarios.utils
//
//import org.springframework.boot.SpringApplication
//import uk.gov.hmcts.ccd.corecasedata.scenarios.config.CCDPerformanceTestApplication
//
//trait SpringApplicationContext {
//
//  def S2SAuthUrl(): String
//
//  //  val applicationContext = SpringApplication.run(classOf[CCDPerformanceTestsApplication], s"--idam.s2s-auth.url=${s2sAuthUrl()}", "--logging.level.org.springframework=DEBUG");
//  //val applicationContext = SpringApplication.run(classOf[PerformanceTestsApplication], s"--idam.s2s-auth.url=$S2SAuthUrl")
//  val applicationContext = SpringApplication.run(classOf[CCDPerformanceTestApplication], "--idam.s2s-auth.url=$S2SAuthUrl")
//
//}