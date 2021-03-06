package com.revature.application.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import javax.ws.rs.QueryParam;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.glassfish.jersey.client.ClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.docusign.esign.api.AuthenticationApi;
import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.client.Configuration;
import com.docusign.esign.client.auth.Authentication;
import com.docusign.esign.client.auth.OAuth;
import com.docusign.esign.model.Envelope;
import com.docusign.esign.model.EnvelopesInformation;
import com.docusign.esign.model.LoginAccount;
import com.docusign.esign.model.LoginInformation;
import com.docusign.esign.model.Recipients;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.revature.application.model.Associate;
import com.revature.application.model.MrSingletonState;
import com.revature.application.service.ProfileCompositeService;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import io.swagger.annotations.ApiParam;


@Controller
@RequestMapping("profilecomposite")
public class ProfileCompositeController {

	@Autowired
	ProfileCompositeService profileCService;
	
	public static final String service_url = "http://ec2-107-23-6-219.compute-1.amazonaws.com";

	/* IMPORTANT INFORMATION FOR FUTURE ITERATIONS!
	 *
	 * There are a few things that you will need to know about how things work that are not covered in below notes.
	 * For starters, there currently is not a connection to this API from the front end. This endpoint is ready to
	 * use in a testing environment but is not ready for use on an enterprise application scale. There are numerous
	 * things that will prevent this from working on an enterprise scale.
	 * - One of the main things that you will notice when trying to develop this for enterprise level launching is that
	 * the integration key is for sandbox only. This means that it will only work for us when using test data that we set up.
	 * This will not work with real data from the Revature HR team yet.
	 * - Another thing is that you need to go get your own Integrator key. Using my key will be hard without the information
	 * for getting into the account. You may find that in here, but it will be far better for you to make your own sandbox.
	 * - Now, during the process for getting your own integrationkey you will go by a lot of things that I have left out of
	 * the notes below because the information needed to understand how things work was gained through the process of setting up
	 * my personal integration key. Because of this, I urge you to set up your own integrator key for your iteration of this
	 * Revature Housing project.
	 * - Lastly, I already know of a bug that you will face when gtrying to set up for enterprise level launch so I will make
	 * sure you are aware to look out for this right away. Revature uses DocuSign to manage numerous different contracts with
	 * Associates. There is the housing agreements, Revature hiring contracts, and other things. The call to this endpoint
	 * currently does not differentiate between completed housing agreements and other DocuSign 'Envelopes' (Documents as we
	 * would call them but Document is another type of DocuSign object so the actual object we know these as is called an
	 * Envelope). This means that a person may have accepted the Revature contract through DocuSign but not the housing agreement.
	 * In that instance, a completed Envelope of ANY type, even if not the housing agreement, will cause the database to say that
	 * the housing agreement has been signed. A check to make sure the database is only being updated when the completed document
	 * is a housing agreement needs to be put in place. That will be how simple it is but you need to talk to HR in order to get
	 * some sample data to know how to differentiate between the different completed forms.
	 *
	 * That wraps up the important things you need to know about this that isn't covered below so I hope you enjoy your time
	 * continuing the integration of DocuSign into the application and that it is nice and easy for you.
	 * Good luck (You will need it)
	 *
	 * Signed,
	 * Nora Duckett*/
	
	@GetMapping("{id}")
	public ResponseEntity<Object> getProfileInfo(@PathVariable("id") String id) {
	/*	JsonObject compositeObj = getJsonFromService(service_url + ":8090/associates/" +id);		
		JsonObject unitJson = getJsonFromService(service_url + ":8093/unit/" +compositeObj.get("unitId").getAsString());
		JsonObject complexJson = getJsonFromService(service_url + ":8093/complex/" +compositeObj.get("complexId").getAsString());*/	
		
		JsonObject obj1 = jsonReturned("associates", id);
		JsonObject unitJson = jsonReturned("unit", obj1.get("unitId").getAsString());
		JsonObject complexJson = jsonReturned("complex", obj1.get("complexId").getAsString());
		
		//profile needs to know complex name
//		compositeObj.add("complexName", complexJson.get("name")); 
		obj1.add("complexName", complexJson.get("name")); 
		
		// profile needs to know room number
//		compositeObj.add("unitName", unitJson.get("unitNumber"));
		obj1.add("unitName", unitJson.get("unitNumber"));

		// profile needs to know id for routing
//		compositeObj.add("unitId", unitJson.get("unitId"));
		obj1.add("unitId", unitJson.get("unitId"));

		return ResponseEntity.ok(obj1.toString());
	}
	/*
	 * so they moved sun jersey to glassfish jersey, check dependencies and mvnrepository.com
	 */
	private JsonObject getJsonFromService(String url) {
		//for consuming a rest service
		ClientConfig config = new ClientConfig();
		javax.ws.rs.client.Client client = ClientBuilder.newClient(config);
		WebTarget target = client.target(getRestServiceURI());
		String associate = target.path(url).request().accept(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
		return new JsonParser().parse(associate).getAsJsonObject();
	}

	
	
	private JsonObject jsonReturned(String endpoint1, String endpoint2) {
		//for consuming a rest service
		ClientConfig config = new ClientConfig();
		javax.ws.rs.client.Client client = ClientBuilder.newClient(config);
		WebTarget target = client.target(getRestServiceURI());
		String associate = target.path(endpoint1).path(endpoint2).request().accept(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
		return new JsonParser().parse(associate).getAsJsonObject();
	}

	private static URI getRestServiceURI() {

		String loc = service_url + ":8085";
		String site = "/api";//idk if this is right..?

		return UriBuilder.fromUri(loc+site).build();
	}

	// Important stuff! Do not delete.... Feel free to edit this URI to make it more professional though
		private final String RedirectionUri = "docusignRedirection";

		// The date must be formatted as YYYY-MM-DD
		@GetMapping("docusign")//TODO: change the mapping to something more appropriate now that testing is done
		public String updateDocusignTry3(@QueryParam("date") String date) {
			/*
			 * Most of this entire method is taken from the DocuSign example page:
			 * https://www.docusign.com/developer-center/api-overview
			 * Actually, it was taken from the test found there but more information on that in the other method
			 * riiiiight below here that gets called after the user is redirected.
			 */
 
			MrSingletonState.setDate(date);
			
			
			String OAuthBaseUrl = "account-d.docusign.com";
			// To get your own integrator key, see the DocuSign developer website:
			// https://www.docusign.com/developer-center
			String IntegratorKey = "6b512969-dc6a-4a85-b522-3094833f0373";
			String ClientSecret = "40342a64-f8d8-44ca-b57a-43804ffd8248";// set up with integrator key setup
			String BaseUrl = "https://demo.docusign.net/restapi"; // Only for sandbox/ demo use.
			String fullRedirectUri = service_url + ":8105/profilecomposite/" + RedirectionUri;

			try {
				ApiClient apiClient = new ApiClient("https://" + OAuthBaseUrl, "docusignAccessCode", IntegratorKey,
						ClientSecret);
				apiClient.setBasePath(BaseUrl);
				// make sure to pass the redirect uri
				apiClient.configureAuthorizationFlow(IntegratorKey, ClientSecret, fullRedirectUri);
				Configuration.setDefaultApiClient(apiClient);

				// get DocuSign OAuth authorization url
				String oauthLoginUrl;
				oauthLoginUrl = apiClient.getAuthorizationUri();
				// open DocuSign OAuth login in the browser
				return "redirect:" + oauthLoginUrl;

			} catch (OAuthSystemException e) {
				// You get this exception if something goes wrong with oauthLoginUrl =
				// apiClient.getAuthorizationUri();
				e.printStackTrace();
			}

			return null;
		}

		// The date must be formatted as YYYY-MM-DD
		@GetMapping(RedirectionUri)
		public String updateDocusignTry3Part2(@ApiParam("code") String code) {
			// once redirected here, we will have the code (denoted as the code path
			// variable that we can
			// then exchange for the actual oauth token that we need to use for sending REST
			// requests

			String OAuthBaseUrl = "account-d.docusign.com";
			// To get your own integrator key, see the DocuSign developer website:
			// https://www.docusign.com/developer-center
			String IntegratorKey = "6b512969-dc6a-4a85-b522-3094833f0373";
			String ClientSecret = "40342a64-f8d8-44ca-b57a-43804ffd8248";// set up with integrator key setup
			String BaseUrl = "https://demo.docusign.net/restapi"; // Only for sandbox/ demo use.

			/*
			 * Much of this following code was adapted from the docusign java client libraries on GitHub:
			 * https://github.com/docusign/docusign-java-client/blob/master/src/test/java/SdkUnitTests.java
			 * The specific test I used as the base of this was the OauthLoginTest.
			 * 
			 * For future integration with Docusign, please refer to this specific GitHub
			 * repository as it is far more helpful to use compared to the terrible standard
			 * REST API documentation provided by DocuSign. Trust me, the normal
			 * documentation is terrible and is basically only good for getting yourself
			 * confused and understanding some of the structure of the DocuSign models.
			 * 
			 * Base DocuSign Java GitHub repository:
			 * https://github.com/docusign/docusign-java-client DocuSign developer center
			 * (if you should need to look at it): https://www.docusign.com/developer-center
			 */

			try {
				ApiClient apiClient = new ApiClient("https://" + OAuthBaseUrl, "docusignAccessCode", IntegratorKey,
						ClientSecret);
				apiClient.setBasePath(BaseUrl);
				// make sure to pass the redirect uri
				apiClient.configureAuthorizationFlow(IntegratorKey, ClientSecret, RedirectionUri);
				Configuration.setDefaultApiClient(apiClient);

				// assign it to the token endpoint
				apiClient.getTokenEndPoint().setCode(code);

				// ask to exchange the auth code with an access code
				apiClient.updateAccessToken();

				
				/*
				 * References for things used:
				 * https://docs.docusign.com/esign/restapi/Envelopes/Envelopes/listStatusChanges/
				 * https://docs.docusign.com/esign/guide/authentication/oa2_auth_code.html
				 * https://docs.docusign.com/esign/guide/authentication/auth_server.html#using-the-state-parameter
				 */

				// now that the API client has an OAuth token, let's use in all
				// DocuSign APIs
				AuthenticationApi authApi = new AuthenticationApi(apiClient);
				AuthenticationApi.LoginOptions loginOps = authApi.new LoginOptions();
				loginOps.setApiPassword("true");
				loginOps.setIncludeAccountIdGuid("true");
				LoginInformation loginInfo;
				loginInfo = authApi.login(loginOps);

				List<LoginAccount> loginAccounts = loginInfo.getLoginAccounts();

				System.out.println("LoginInformation: " + loginInfo);

				// parse first account's baseUrl
				LoginAccount defaultAccount = loginInfo.getLoginAccounts().get(0);
				String[] accountDomain = defaultAccount.getBaseUrl().split("/v2");
				BaseUrl = defaultAccount.getBaseUrl();

				// below code required for production, no effect in demo (same domain)
				apiClient.setBasePath(accountDomain[0]);
				Configuration.setDefaultApiClient(apiClient);
				
				
				/****** Use the service of GET /restapi/v2/accounts/{accountID}/envelopes?from_date={date}
				 * See the documentation at https://docs.docusign.com/esign/restapi/Envelopes/Envelopes/listStatusChanges/ *******/
				
				
				//Now we can use the EnvelopesApi to call the method we need to use to get certain envelopes
				EnvelopesApi envelopesApi = new EnvelopesApi();
				
				//set the options up for filtering out envelopes we do not want
				EnvelopesApi.ListStatusChangesOptions options = envelopesApi.new ListStatusChangesOptions();
				options.setFromDate(MrSingletonState.getDate());
				options.setFromToStatus("Completed");
				
				EnvelopesInformation eInfo = envelopesApi.listStatusChanges(defaultAccount.getAccountId(), options);
				//System.out.println(eInfo);
				for(Envelope e : eInfo.getEnvelopes()) {
					Envelope e2 = envelopesApi.getEnvelope(defaultAccount.getAccountId(), e.getEnvelopeId());
					Recipients recipients = e2.getRecipients();//Why is this always null?

					//System.out.println(e);
					//System.out.println(e2);
					//System.out.println("NEXT!\n\n");
					
					if (recipients == null) {
						String changedDate = e.getStatusChangedDateTime();
						LocalDateTime time = LocalDateTime.parse(changedDate.split("Z")[0]);
						
						
						Recipients r = CallUriPersonally(BaseUrl + e.getRecipientsUri(), Recipients.class);
						String email = r.getSigners().get(0).getEmail();
						Associate a = CallUriPersonally(service_url + ":8085/api/associates/" + email + "/email", Associate.class);
						a.setHousingAgreed(time);//TODO: change to changedDate once up to date with dev
						updateAssociate(a);
					}
					//Redirecting to the recipients uri gives me nothing, oddly enough.
					//It very well may actually be empty for some odd reason
					 
				}
				
				
				
				return code.toString();

			} catch (ApiException e) {
				e.printStackTrace();
			}

			return null;
		}
		
		private void updateAssociate(Associate a) {
			Client client = Client.create();
			WebResource webResource =client.resource(service_url + ":8085/api/associates/createOrUpdate");


			try {
				// Turn a into a JSON object
				HttpPost post = new HttpPost(service_url + ":8085/api/associates/createOrUpdate");
				post.setHeader("Content-Type", "application/json");
				Gson gson = new Gson();
				StringEntity entity = new StringEntity(gson.toJson(a));
				post.setEntity(entity);
				HttpClientBuilder.create().build().execute(post);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		private <T> T CallUriPersonally(String uri, Object returnType) {
			Client client = Client.create();
			WebResource webResource =client.resource(uri);

			MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
			//queryParams.add("json", js); //set parametes for request
			
			Map<String, Authentication> auths = Configuration.getDefaultApiClient().getAuthentications();
			OAuth oAuth = (OAuth) auths.get("docusignAccessCode");
			String appKey = oAuth.getAccessToken();
			
			System.out.println("I got the Oauth code and here it is! It's right here! " + appKey);
			//System.out.println(code.equals(appKey));

			appKey = "Bearer " + appKey; // appKey is unique number
			
			//Get response from RESTful Server get(ClientResponse.class);
			T response = null;
			response = (T) webResource.queryParams(queryParams)
			                        .header("Content-Type", "application/json;charset=UTF-8")
			    .header("Authorization", appKey)
			    .get((Class<T>) returnType);

			//String jsonStr = response.getEntity(String.class);
			return response;
	}
	
}
