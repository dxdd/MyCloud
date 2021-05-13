package didispace.web;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheKey;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheRemove;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @Author: sean
 * @Date: 2021/5/6 21:51
 */
public class UserServiceImpl implements UserService {

    @Autowired
    private RestTemplate restTemplate;

    @HystrixCommand(commandKey = "getUserById", groupKey = "UserGroup", threadPoolKey = "getUserByIdThread")
    @CacheResult(cacheKeyMethod = "getUserByIdCacheKey")
    public User getUserById(@CacheKey Long id) {
        return restTemplate.getForObject("http://USER-SERVICE/users/{1}", User.class, id);
    }

    @CacheRemove(commandKey = "getUserById")
    @HystrixCommand
    public User update(@CacheKey("id") User user) {
        return restTemplate.postForObject("http://USER-SERVICE/users/{1}", user, User.class);
    }

    public User defaultUser(String id, Throwable e) {
        //此处可能是另外一个网络请求来获取,所以也有可能失败
        assert "getUserById command failed".equals(e.getMessage());
        return new User();
    }


    public User defaultUserSec() {
        return new User("Second Fallback");
    }

    @Override
    @HystrixCollapser(batchMethod = "findAll", collapserProperties = {@HystrixProperty(name = "timerDelayInMilliseconds", value = "100")})
    public User find(Long id) {
        return restTemplate.getForObject("http://USER-SERVICE/users/{1}", User.class, id);
    }

    @Override
    @HystrixCommand
    public List<User> findAll(List<Long> ids) {
        return restTemplate.getForObject("http://USER-SERVICE/users/?ids={1}", List.class, StringUtils.join(ids, ","));
    }

}