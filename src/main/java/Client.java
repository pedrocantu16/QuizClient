import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class Client implements Runnable {

  private static final int REQUEST = 100;
  private String url;
  private HttpClient client;
  CountDownLatch completed;
  Counter count;

  public Client(String url, CountDownLatch completed, Counter count) {
    this.url = url;
    this.client = new HttpClient();
    this.completed = completed;
    this.count = count;
  }

  private void executeGetRequest() {
    // generate random odd number
    int n = ThreadLocalRandom.current().nextInt(1,10000) | 1;

    String completedURL = url + String.valueOf(n);

    // instantiate GET method
    GetMethod method = new GetMethod(completedURL);
    // Provide custom retry handler is necessary
    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
        new DefaultHttpMethodRetryHandler(0, false));

    try {
      // Execute the method.
      int getStatus = client.executeMethod(method);

      if (getStatus == HttpStatus.SC_OK) {
        this.count.increasePrimeCount();
      }
    } catch (HttpException e) {
      System.err.println("Fatal protocol violation: " + e.getMessage());
      e.printStackTrace();
    } catch (IOException e) {
      System.err.println("Fatal transport error: " + e.getMessage());
      e.printStackTrace();
    } finally {
      // Release the connection.
      method.releaseConnection();
    }
  }

  /**
   * When an object implementing interface <code>Runnable</code> is used to create a thread,
   * starting the thread causes the object's
   * <code>run</code> method to be called in that separately executing
   * thread.
   * <p>
   * The general contract of the method <code>run</code> is that it may take any action whatsoever.
   *
   * @see Thread#run()
   */
  @Override
  public void run() {
    for(int i = 0; i < REQUEST; i++) {
      executeGetRequest();
    }
    completed.countDown();
  }

}
