// package uk.gov.hmcts.ccd.corecasedata.scenarios.utils

// import com.typesafe.config.ConfigFactory
// import com.warrenstrange.googleauth.GoogleAuthenticator
// import io.restassured.RestAssured
// import io.restassured.config.EncoderConfig
// import io.restassured.http.ContentType

// object CcdTokenGenerator {

//     private val config = ConfigFactory.load()

//     val TOKEN_LEASE_URL = config.getString("s2sUrl")
//     val USERTOKEN_SidAM_URL = config.getString("idam_api_url")

//     val RD_URL = config.getString("baseUrl")

//     def generateS2SToken() : String = {

//     val authenticator: GoogleAuthenticator = new GoogleAuthenticator()
//     val password = authenticator.getTotpPassword(config.getString("aat_service.pass"))
//     val jsonPayload: String = """{"microservice":"""" + config.getString("aat_service.name") + """","oneTimePassword":"""" + password + """"}"""

//     val s2sRequest = RestAssured.given
//                     .contentType("application/json")
//                     .accept("application/json")
//                     .proxy("proxyout.reform.hmcts.net", 8080)
//                     .body(jsonPayload)
//                     .post(TOKEN_LEASE_URL +"/lease")
//                     .then()
//                     .statusCode(200)
//                     .extract()
//                     .response()

//     val token = s2sRequest.asString()

//     token
//   }

//   //=======================================

// def generateSIDAMUserTokenInternal() : String = {
//     return generateSIDAMUserTokenInternal("ccdloadtest1@gmail.com") //mallikarjun.puttana@hmcts.net
// }

// def generateSIDAMUserTokenInternal(userName : String) : String = {

//     val authCodeRequest = RestAssured.given().config(RestAssured.config()
//                         .encoderConfig(EncoderConfig.encoderConfig()
//                         .encodeContentTypeAs("x-www-form-urlencoded",
//                         ContentType.URLENC)))
//                         .contentType("application/x-www-form-urlencoded; charset=UTF-8")
//                         .proxy("proxyout.reform.hmcts.net", 8080)
//                         .formParam("username", userName)
//                         .formParam("password", "Testing1234") 
//                         .formParam("client_id", "rd-professional-api")
//                         .formParam("client_secret", "cc5f2a6-9690-11e9-bc42-526af7764f64")
//                         .formParam("redirect_uri", RD_URL + "/oauth2redirect")
//                         .formParam("grant_type", "password")
//                         .formParam("scope", "openid profile roles create-user manage-user search-user")
//                         .request()


//     val response = authCodeRequest.post(USERTOKEN_SidAM_URL + ":443/o/token")
//     val statusCode = response.getStatusCode()
//     val tokenStr = response.asString()
//     val tokenIndexStart = tokenStr.indexOf(":")
//     val tokenIndexEnd = tokenStr.indexOf(",")
//     val token =  tokenStr.substring(tokenIndexStart+2,tokenIndexEnd -1 )

//     "Bearer " + token
//     }

//     def generateSIDAMUserTokenExternal() : String = {
//             return generateSIDAMUserTokenExternal("")
//     }

//     def generateSIDAMUserTokenExternal(userName : String) : String = {

//     val authCodeRequest = RestAssured.given().config(RestAssured.config()
//                         .encoderConfig(EncoderConfig.encoderConfig()
//                         .encodeContentTypeAs("x-www-form-urlencoded",
//                         ContentType.URLENC)))
//                         .contentType("application/x-www-form-urlencoded; charset=UTF-8")
//                         .proxy("proxyout.reform.hmcts.net", 8080)
//                         .formParam("username", userName)
//                         .formParam("password", "Password12")
//                         .formParam("client_id", "rd-professional-api")
//                         .formParam("client_secret", "cc5f2a6-9690-11e9-bc42-526af7764f64")
//                         .formParam("redirect_uri", RD_URL + "/oauth2redirect")
//                         .formParam("grant_type", "password")
//                         .formParam("scope", "openid profile roles create-user manage-user search-user")
//                         .request();

//     val response = authCodeRequest.post(USERTOKEN_SidAM_URL + ":443/o/token")
//     val statusCode = response.getStatusCode()
//     val tokenStr = response.asString()
//     val tokenIndexStart = tokenStr.indexOf(":")
//     val tokenIndexEnd = tokenStr.indexOf(",")
//     val token =  tokenStr.substring(tokenIndexStart+2,tokenIndexEnd -1 )

//     "Bearer " + "Bearer" + token
//   }

// }
