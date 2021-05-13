package didispace.web.command;

import com.netflix.hystrix.HystrixCommand;
import didispace.web.User;
import didispace.web.UserService;

import java.util.List;

import static com.netflix.hystrix.HystrixCommandGroupKey.Factory.asKey;

/**
 * 为请求合并的实现准备一个批量请求命令的实现
 *
 * @Author: sean
 * @Date: 2021/5/12 17:05
 */
public class UserBatchCommand extends HystrixCommand<List<User>> {
    UserService userService;
    List<Long> userIds;

    public UserBatchCommand(UserService userService, List<Long> userIds) {
        super(Setter.withGroupKey(asKey("userServiceCommand")));

        this.userService = userService;
        this.userIds = userIds;
    }

    @Override
    protected List<User> run() {
        return userService.findAll(userIds);
    }
}
