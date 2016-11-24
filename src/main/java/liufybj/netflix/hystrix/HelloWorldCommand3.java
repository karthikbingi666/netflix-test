package liufybj.netflix.hystrix;

import com.netflix.hystrix.*;
import rx.functions.Action0;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HelloWorldCommand3 extends HystrixCommand<String> {
    private final String name;

    public HelloWorldCommand3(String name) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))
                        .andCommandKey(HystrixCommandKey.Factory.asKey("HelloWorldCommand3"))
                        .andCommandPropertiesDefaults(
                                HystrixCommandProperties.Setter()
                                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
                        )
                        .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                                .withCoreSize(3)
                                .withMaxQueueSize(6)
                                .withQueueSizeRejectionThreshold(6))
        );
        this.name = name;
    }

    @Override
    protected String run() throws Exception {
        Thread.currentThread().sleep(200L);
        return "Hello " + name + " thread:" + Thread.currentThread().getName();
    }

    public static void main(String[] args) throws Exception {

        ExecutorService executorService = Executors.newFixedThreadPool(6);
        for (int i = 0; i < 100; i++) {
            executorService.submit(new Task(i));
        }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

    }

//    @Override
//    protected String getFallback() {
//        return "excute Falled";
//    }

    static class Task implements Runnable {
        int i;

        public Task(int i) {
            this.i = i;
        }

        @Override
        public void run() {
            HelloWorldCommand3 command = new HelloWorldCommand3("test_" + i);
            String result = null;
            try {
                result = command.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("result=" + result + "| execute thread =" + Thread.currentThread().getName());
        }
    }
}


