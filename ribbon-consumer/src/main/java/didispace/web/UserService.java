package didispace.web;

import java.util.List;

/**
 * @Author: sean
 * @Date: 2021/5/12 17:02
 */
public interface UserService {
    User find(Long id);

    List<User> findAll(List<Long> ids);
}
