package by.stub;

import by.stub.cli.ANSITerminal;
import by.stub.cli.CommandLineInterpreter;
import by.stub.client.StubbyClient;
import by.stub.utils.StringUtils;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

public class StubsTest {

   private static final String HEADER_APPLICATION_JSON = "application/json";
   private static StubbyClient stubbyClient;
   private static String stubsUrlAsString;
   private static String stubsSslUrlAsString;
   private static String contentAsString;
   private static HttpRequestFactory webClient;

   @BeforeClass
   public static void beforeClass() throws Exception {

      ANSITerminal.muteConsole(true);


      webClient = new NetHttpTransport().createRequestFactory(new HttpRequestInitializer() {
         @Override
         public void initialize(final HttpRequest request) {
            request.setThrowExceptionOnExecuteError(false);
            request.setReadTimeout(45000);
            request.setConnectTimeout(45000);
         }
      });

      final URL jsonContentUrl = StubsTest.class.getResource("/json/stub.response.body.json");
      Assert.assertNotNull(jsonContentUrl);
      contentAsString = StringUtils.inputStreamToString(jsonContentUrl.openStream());

      int clientPort = 5992;
      int sslPort = 5993;
      int adminPort = 5999;
      final URL url = StubsTest.class.getResource("/yaml/stubs.data.yaml");
      Assert.assertNotNull(url);

      CommandLineInterpreter.parseCommandLine(new String[]{"--data", url.getFile()});

      stubbyClient = new StubbyClient();
      stubbyClient.startJetty(clientPort, sslPort, adminPort, url.getFile());

      stubsUrlAsString = String.format("http://localhost:%s", clientPort);
      stubsSslUrlAsString = String.format("https://localhost:%s", sslPort);
   }

   @AfterClass
   public static void afterClass() throws Exception {
      stubbyClient.stopJetty();
   }

   @Before
   public void beforeEach() {

   }

   @Test
   public void should_MakeSuccesfulRequest_WhenQueryParamsAreAnArrayWithEscapedSingleQuoteElements() throws Exception {
      final String requestUrl = String.format("%s%s", stubsUrlAsString, "/entity.find.single.quote?type_name=user&client_id=id&client_secret=secret&attributes=[%27id%27,%27uuid%27,%27created%27,%27lastUpdated%27,%27displayName%27,%27email%27,%27givenName%27,%27familyName%27]");
      final HttpRequest request = constructHttpRequest("GET", requestUrl);

      final HttpHeaders httpHeaders = new HttpHeaders();
      request.setHeaders(httpHeaders);

      final HttpResponse response = request.execute();
      final String responseContent = response.parseAsString().trim();

      Assert.assertEquals(HttpStatus.OK_200, response.getStatusCode());
      Assert.assertEquals("{\"status\": \"hello world with single quote\"}", responseContent);
   }


   @Test
   public void should_MakeSuccesfulRequest_WhenQueryParamsAreAnArrayWithEscapedQuotedElements() throws Exception {
      final String requestUrl = String.format("%s%s", stubsUrlAsString, "/entity.find?type_name=user&client_id=id&client_secret=secret&attributes=[%22id%22,%22uuid%22,%22created%22,%22lastUpdated%22,%22displayName%22,%22email%22,%22givenName%22,%22familyName%22]");
      final HttpRequest request = constructHttpRequest("GET", requestUrl);

      final HttpHeaders httpHeaders = new HttpHeaders();
      request.setHeaders(httpHeaders);

      final HttpResponse response = request.execute();
      final String responseContent = response.parseAsString().trim();

      Assert.assertEquals(HttpStatus.OK_200, response.getStatusCode());
      Assert.assertEquals("{\"status\": \"hello world\"}", responseContent);
   }

   @Test
   public void should_MakeSuccesfulRequest_WhenQueryParamsAreAnArray() throws Exception {
      final String requestUrl = String.format("%s%s", stubsUrlAsString, "/entity.find.again?type_name=user&client_id=id&client_secret=secret&attributes=[id,uuid,created,lastUpdated,displayName,email,givenName,familyName]");
      final HttpRequest request = constructHttpRequest("GET", requestUrl);

      final HttpHeaders httpHeaders = new HttpHeaders();
      request.setHeaders(httpHeaders);

      final HttpResponse response = request.execute();
      final String responseContent = response.parseAsString().trim();

      Assert.assertEquals(HttpStatus.OK_200, response.getStatusCode());
      Assert.assertEquals("{\"status\": \"hello world\"}", responseContent);
   }

   @Test
   public void should_ReactToPostRequest_WithoutPost_AndPostNotSupplied() throws Exception {
      final String requestUrl = String.format("%s%s", stubsUrlAsString, "/invoice/new/no/post");
      final HttpRequest request = constructHttpRequest("POST", requestUrl);

      final HttpHeaders httpHeaders = new HttpHeaders();
      request.setHeaders(httpHeaders);

      final HttpResponse response = request.execute();

      Assert.assertEquals(HttpStatus.NO_CONTENT_204, response.getStatusCode());
   }

   @Test
   public void should_ReactToPostRequest_WithoutPost_AndPostSupplied() throws Exception {
      final String requestUrl = String.format("%s%s", stubsUrlAsString, "/invoice/new/no/post");
      final String content = "{\"name\": \"chocolate\", \"description\": \"full\", \"department\": \"savoury\"}";
      final HttpRequest request = constructHttpRequest("POST", requestUrl, content);

      final HttpHeaders httpHeaders = new HttpHeaders();
      request.setHeaders(httpHeaders);

      final HttpResponse response = request.execute();

      Assert.assertEquals(HttpStatus.NO_CONTENT_204, response.getStatusCode());
   }

   @Test
   public void should_ReturnPDF_WhenGetRequestMade() throws Exception {

      final String requestUrl = String.format("%s%s", stubsUrlAsString, "/pdf/hello-world");
      final HttpResponse response = constructHttpRequest("GET", requestUrl).execute();

      Assert.assertEquals(HttpStatus.OK_200, response.getStatusCode());
   }

   @Test
   public void should_ReturnAllProducts_WhenGetRequestMade() throws Exception {

      final String requestUrl = String.format("%s%s", stubsUrlAsString, "/invoice?status=active&type=full");
      final HttpResponse response = constructHttpRequest("GET", requestUrl).execute();

      final String contentTypeHeader = response.getContentType();
      final String responseContent = response.parseAsString().trim();

      Assert.assertEquals(HttpStatus.OK_200, response.getStatusCode());
      Assert.assertEquals(contentAsString, responseContent);
      Assert.assertTrue(contentTypeHeader.contains(HEADER_APPLICATION_JSON));
   }

   @Test
   public void should_FailToReturnAllProducts_WhenGetRequestMadeWithoutRequiredQueryString() throws Exception {

      final String requestUrl = String.format("%s%s", stubsUrlAsString, "/invoice?status=active");
      final HttpResponse response = constructHttpRequest("GET", requestUrl).execute();
      final String responseContentAsString = response.parseAsString().trim();

      Assert.assertEquals(HttpStatus.NOT_FOUND_404, response.getStatusCode());
      Assert.assertTrue(responseContentAsString.contains("No data found for GET request at URI /invoice?status=active"));
   }

   @Test
   public void should_ReturnAllProducts_WhenGetRequestMadeOverSsl() throws Exception {

      final String requestUrl = String.format("%s%s", stubsSslUrlAsString, "/invoice?status=active&type=full");
      final HttpResponse response = constructHttpRequest("GET", requestUrl).execute();

      final String contentTypeHeader = response.getContentType();

      Assert.assertEquals(HttpStatus.OK_200, response.getStatusCode());
      Assert.assertEquals(contentAsString, response.parseAsString().trim());
      Assert.assertTrue(contentTypeHeader.contains(HEADER_APPLICATION_JSON));
   }

   @Test
   public void should_FailToReturnAllProducts_WhenGetRequestMadeWithoutRequiredQueryStringOverSsl() throws Exception {

      final String requestUrl = String.format("%s%s", stubsSslUrlAsString, "/invoice?status=active");
      final HttpResponse response = constructHttpRequest("GET", requestUrl).execute();
      final String responseContentAsString = response.parseAsString().trim();

      Assert.assertEquals(HttpStatus.NOT_FOUND_404, response.getStatusCode());
      Assert.assertTrue(responseContentAsString.contains("No data found for GET request at URI /invoice?status=active"));

   }

   @Test
   public void should_UpdateProduct_WhenPutRequestMade() throws Exception {

      final String requestUrl = String.format("%s%s", stubsUrlAsString, "/invoice/123");
      final String content = "{\"name\": \"milk\", \"description\": \"full\", \"department\": \"savoury\"}";
      final HttpRequest request = constructHttpRequest("PUT", requestUrl, content);

      final HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setContentType(HEADER_APPLICATION_JSON);

      request.setHeaders(httpHeaders);

      final HttpResponse response = request.execute();
      final String contentTypeHeader = response.getContentType();

      Assert.assertEquals(HttpStatus.OK_200, response.getStatusCode());
      Assert.assertEquals("{\"id\": \"123\", \"status\": \"updated\"}", response.parseAsString().trim());
      Assert.assertTrue(contentTypeHeader.contains(HEADER_APPLICATION_JSON));
   }

   @Test
   public void should_UpdateProduct_WhenPutRequestMadeOverSsl() throws Exception {

      final String requestUrl = String.format("%s%s", stubsSslUrlAsString, "/invoice/123");
      final String content = "{\"name\": \"milk\", \"description\": \"full\", \"department\": \"savoury\"}";
      final HttpRequest request = constructHttpRequest("PUT", requestUrl, content);

      final HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setContentType(HEADER_APPLICATION_JSON);

      request.setHeaders(httpHeaders);

      final HttpResponse response = request.execute();
      final String contentTypeHeader = response.getContentType();

      Assert.assertEquals(HttpStatus.OK_200, response.getStatusCode());
      Assert.assertEquals("{\"id\": \"123\", \"status\": \"updated\"}", response.parseAsString().trim());
      Assert.assertTrue(contentTypeHeader.contains(HEADER_APPLICATION_JSON));
   }

   @Test
   public void should_UpdateProduct_WhenPutRequestMadeWithWrongPost() throws Exception {

      final String requestUrl = String.format("%s%s", stubsUrlAsString, "/invoice/123");
      final String content = "{\"wrong\": \"post\"}";
      final HttpRequest request = constructHttpRequest("PUT", requestUrl, content);

      final HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setContentType(HEADER_APPLICATION_JSON);

      request.setHeaders(httpHeaders);

      final HttpResponse response = request.execute();
      final String responseContentAsString = response.parseAsString().trim();

      Assert.assertEquals(HttpStatus.NOT_FOUND_404, response.getStatusCode());
      Assert.assertTrue(responseContentAsString.contains("No data found for PUT request at URI /invoice/123"));
   }

   @Test
   public void should_UpdateProduct_WhenPutRequestMadeWithWrongPostOverSsl() throws Exception {

      final String requestUrl = String.format("%s%s", stubsSslUrlAsString, "/invoice/123");
      final String content = "{\"wrong\": \"post\"}";
      final HttpRequest request = constructHttpRequest("PUT", requestUrl, content);

      final HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setContentType(HEADER_APPLICATION_JSON);

      request.setHeaders(httpHeaders);

      final HttpResponse response = request.execute();
      final String responseContentAsString = response.parseAsString().trim();

      Assert.assertEquals(HttpStatus.NOT_FOUND_404, response.getStatusCode());
      Assert.assertTrue(responseContentAsString.contains("No data found for PUT request at URI /invoice/123"));
   }

   @Test
   public void should_CreateNewProduct_WhenPostRequestMade() throws Exception {

      final String requestUrl = String.format("%s%s", stubsUrlAsString, "/invoice/new");
      final String content = "{\"name\": \"chocolate\", \"description\": \"full\", \"department\": \"savoury\"}";
      final HttpRequest request = constructHttpRequest("POST", requestUrl, content);

      final HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setContentType(HEADER_APPLICATION_JSON);

      request.setHeaders(httpHeaders);

      final HttpResponse response = request.execute();
      final String contentTypeHeader = response.getContentType();
      final String responseContentAsString = response.parseAsString().trim();

      Assert.assertEquals(HttpStatus.CREATED_201, response.getStatusCode());
      Assert.assertEquals("{\"id\": \"456\", \"status\": \"created\"}", responseContentAsString);
      Assert.assertTrue(contentTypeHeader.contains(HEADER_APPLICATION_JSON));
   }

   @Test
   public void should_CreateNewProduct_WhenPostRequestMadeOverSsl() throws Exception {

      final String requestUrl = String.format("%s%s", stubsSslUrlAsString, "/invoice/new");
      final String content = "{\"name\": \"chocolate\", \"description\": \"full\", \"department\": \"savoury\"}";
      final HttpRequest request = constructHttpRequest("POST", requestUrl, content);

      final HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setContentType(HEADER_APPLICATION_JSON);

      request.setHeaders(httpHeaders);

      final HttpResponse response = request.execute();
      final String contentTypeHeader = response.getContentType();
      final String responseContentAsString = response.parseAsString().trim();

      Assert.assertEquals(HttpStatus.CREATED_201, response.getStatusCode());
      Assert.assertEquals("{\"id\": \"456\", \"status\": \"created\"}", responseContentAsString);
      Assert.assertTrue(contentTypeHeader.contains(HEADER_APPLICATION_JSON));
   }

   @Test
   public void should_FailtToCreateNewProduct_WhenPostRequestMadeWhenWrongHeaderSet() throws Exception {

      final String requestUrl = String.format("%s%s", stubsUrlAsString, "/invoice/new");
      final String content = "{\"name\": \"chocolate\", \"description\": \"full\", \"department\": \"savoury\"}";
      final HttpRequest request = constructHttpRequest("POST", requestUrl, content);

      final HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setContentType("application/wrong");

      request.setHeaders(httpHeaders);

      final HttpResponse response = request.execute();
      final String responseContentAsString = response.parseAsString().trim();

      Assert.assertEquals(HttpStatus.NOT_FOUND_404, response.getStatusCode());
      Assert.assertTrue(responseContentAsString.contains("No data found for POST request at URI /invoice/new"));
   }

   @Test
   public void should_FailtToCreateNewProduct_WhenPostRequestMadeWhenWrongHeaderSetOverSsl() throws Exception {

      final String requestUrl = String.format("%s%s", stubsSslUrlAsString, "/invoice/new");
      final String content = "{\"name\": \"chocolate\", \"description\": \"full\", \"department\": \"savoury\"}";
      final HttpRequest request = constructHttpRequest("POST", requestUrl, content);

      final HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setContentType("application/wrong");

      request.setHeaders(httpHeaders);

      final HttpResponse response = request.execute();
      final String responseContentAsString = response.parseAsString().trim();

      Assert.assertEquals(HttpStatus.NOT_FOUND_404, response.getStatusCode());
      Assert.assertTrue(responseContentAsString.contains("No data found for POST request at URI /invoice/new"));
   }

   private HttpRequest constructHttpRequest(final String method, final String targetUrl) throws IOException {

      return webClient.buildRequest(method,
         new GenericUrl(targetUrl),
         null);
   }

   private HttpRequest constructHttpRequest(final String method, final String targetUrl, final String content) throws IOException {

      return webClient.buildRequest(method,
         new GenericUrl(targetUrl),
         new ByteArrayContent(null, content.getBytes(StringUtils.utf8Charset())));
   }
}
