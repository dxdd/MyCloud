package didispace.web.command;

import com.netflix.hystrix.*;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategyDefault;
import didispace.web.User;
import org.springframework.web.client.RestTemplate;

/**
 * @Author: sean
 * @Date: 2021/5/6 21:43
 */
public class UserGetCommand extends HystrixCommand<User> {

    private static final HystrixCommandKey GETTER_KEY = HystrixCommandKey.Factory.asKey("CommandKey");

    private RestTemplate restTemplate;

    private Long id;

    public UserGetCommand(RestTemplate restTemplate, Long id) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GetSetGet"))
                .andCommandKey(GETTER_KEY));
        this.restTemplate=restTemplate;
        this.id= id;
    }

    @Override
    protected User run() throws Exception {
        return restTemplate.getForObject("http://HELLO-SERVICE/users/{1}", User.class, id);
    }

    @Override
    protected User getFallback() {
        return new User();
    }

    @Override
    protected String getCacheKey() {
        //根据id置入缓存
        return String.valueOf(id);
    }

    public static void flushCache(Long id) {
        //刷新缓存，根据id进行清理
        HystrixRequestCache.getInstance(GETTER_KEY,
                HystrixConcurrencyStrategyDefault.getInstance())
                .clear(String.valueOf(id));
    }
}
