package didispace.web.command;

import com.netflix.hystrix.HystrixCollapser;
import com.netflix.hystrix.HystrixCollapserKey;
import com.netflix.hystrix.HystrixCollapserProperties;
import com.netflix.hystrix.HystrixCommand;
import didispace.web.User;
import didispace.web.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: sean
 * @Date: 2021/5/12 17:11
 */
public class UserCollapseCommand extends HystrixCollapser<List<User>, User, Long> {
    private UserService userService;
    private Long userId;

    public UserCollapseCommand(Setter setter, UserService userService, Long userId) {
        super(Setter.withCollapserKey(
                HystrixCollapserKey.Factory.asKey("userCollapseCommand"))
                .andCollapserPropertiesDefaults(HystrixCollapserProperties.Setter().withTimerDelayInMilliseconds(100)));
        this.userService = userService;
        this.userId = userId;
    }

    @Override
    public Long getRequestArgument() {
        return userId;
    }

    /**
     * collapsedRequests参数中保存了延迟时间窗中收集到的所有获取单个User的请求。
     * 通过获取这些请求的参数来组织上面我们准备的批量请求命令UserBatchCommand实例。
     */
    @Override
    protected HystrixCommand<List<User>> createCommand(Collection<CollapsedRequest<User, Long>> collapsedRequests) {
        List<Long> userIds = new ArrayList<>(collapsedRequests.size());
        userIds.addAll(collapsedRequests.stream().map(CollapsedRequest::getArgument).collect(Collectors.toList()));

        return new UserBatchCommand(userService, userIds);
    }

    /**
     * 在批量请求命令UserBatchCommand实例被触发执行完成之后，该方法开始执行，
     * 其中 batchResponse参数保存了createCommand中组织的批量请求命令的返回结果，
     * 而 collapsedRequests参数则代表了每个被合并的请求。
     * 在这里我们通过遍历批量结果batchResponse对象，
     * 为collapsedRequests 中每个合并前的单个请求设置返回结果，
     *  以此完成批量结果到单个请求结果的转换。
     */
    @Override
    protected void mapResponseToRequests(List<User> batchResponse, Collection<CollapsedRequest<User, Long>> collapsedRequests) {
        int count = 0;
        for (CollapsedRequest<User, Long> collapsedRequest : collapsedRequests) {
            User user = batchResponse.get(count++);
            collapsedRequest.setResponse(user);
        }
    }
}
