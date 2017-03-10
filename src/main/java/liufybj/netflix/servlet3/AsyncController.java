package liufybj.netflix.servlet3;

import org.apache.catalina.Executor;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by liufengyu on 2017/3/9.
 */
@RestController()
public class AsyncController {
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private CloseableHttpAsyncClient httpAsyncClient = HttpAsyncClients.createDefault();

    @PostConstruct
    private void init() {
        httpAsyncClient.start();
    }

    @ResponseBody
    @RequestMapping(path = "/test1")
    public String test1(String parm1) {
        return parm1;
    }

    @RequestMapping(value="/test2", method = RequestMethod.GET)
    public DeferredResult<String> test2(final String parm1){
        final DeferredResult<String> deferredResult = new DeferredResult<String>();

//        scheduler.schedule(new Runnable() {
//            @Override
//            public void run() {
//
//                deferredResult.setResult(parm1);
//            }
//        }, 0, TimeUnit.SECONDS);

        HttpUriRequest request = new HttpGet("https://www.baidu.com");
        System.out.println(Thread.currentThread().getName());

        httpAsyncClient.execute(request, new FutureCallback<HttpResponse>() {

            @Override
            public void completed(HttpResponse httpResponse) {
                System.out.println(Thread.currentThread().getName());
                deferredResult.setResult(httpResponse.toString());
            }

            @Override
            public void failed(Exception e) {

            }

            @Override
            public void cancelled() {

            }
        });
        return deferredResult;
    }
}
