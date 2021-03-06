package by.stub.yaml.stubs;

import by.stub.cli.CommandLineInterpreter;
import by.stub.utils.StringUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Alexander Zagniotov
 * @since 10/24/12, 10:49 AM
 */
public class StubResponseTest {
   @BeforeClass
   public static void Setup() throws Exception {
      CommandLineInterpreter.parseCommandLine(new String[]{});
   }

   @Test
   public void getResponseBody_ShouldReturnBody_WhenFileCannotBeFound() throws Exception {

      final StubResponse stubResponse = new StubResponse();
      stubResponse.setBody("this is some body");

      Assert.assertEquals("this is some body", StringUtils.utf8String(stubResponse.getResponseBody()));
   }

   @Test
   public void getResponseBody_ShouldReturnBody_WhenFileIsNull() throws Exception {

      final StubResponse stubResponse = new StubResponse();
      stubResponse.setFile(null);
      stubResponse.setBody("this is some body");

      Assert.assertEquals("this is some body", StringUtils.utf8String(stubResponse.getResponseBody()));
   }

   @Test
   public void getResponseBody_ShouldReturnBody_WhenFileIsEmpty() throws Exception {

      final StubResponse stubResponse = new StubResponse();
      stubResponse.setBody("this is some body");

      Assert.assertEquals("this is some body", StringUtils.utf8String(stubResponse.getResponseBody()));
   }

   @Test
   public void getResponseBody_ShouldReturnEmptyBody_WhenFileAndBodyAreNull() throws Exception {

      final StubResponse stubResponse = new StubResponse();
      stubResponse.setFile(null);
      stubResponse.setBody(null);

      Assert.assertEquals("", StringUtils.utf8String(stubResponse.getResponseBody()));
   }

   @Test
   public void getResponseBody_ShouldReturnEmptyBody_WhenBodyIsEmpty() throws Exception {

      final StubResponse stubResponse = new StubResponse();
      stubResponse.setFile(null);
      stubResponse.setBody("");

      Assert.assertEquals("", StringUtils.utf8String(stubResponse.getResponseBody()));
   }
}
