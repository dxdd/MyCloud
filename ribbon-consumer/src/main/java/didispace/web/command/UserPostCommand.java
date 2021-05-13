package didispace.web.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import didispace.web.User;
import org.springframework.web.client.RestTemplate;

/**
 * @Author: sean
 * @Date: 2021/5/11 21:09
 */
public class UserPostCommand extends HystrixCommand<User> {

    private RestTemplate restTemplate;

    private User user;

    @Override
    protected User run() {
        //写操作
        User r = restTemplate.postForObject("http://USER-SERVICE/users",user,User.class);
        //刷新缓存，清理缓存中失效的User
        UserGetCommand.flushCache(user.getId());
        return r;
    }

    public UserPostCommand(Setter setter, RestTemplate restTemplate, User user) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GetSetGet")));
        this.restTemplate = restTemplate;
        this.user = user;
    }
}
