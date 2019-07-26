package com.yukong.chapter5.repository;

import com.yukong.chapter5.config.DynamicDataSourceContextHolder;
import com.yukong.chapter5.entity.User;
import com.yukong.chapter5.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Auther: yukong
 * @Date: 2018/8/14 16:34
 * @Description:
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class UserMapperTest {
    /**
     * 由于近期项目要对数据库进行划分成多个数据库，从当一的数据库，根据业务进行划分多个数据库，
     * 所以使用动态路由+aop来实现数据库的动态切换，当是其中有一个重要的问题，就是事务要怎么解决，
     * 比如在一个service中对其他业务数据库进行查询，修改，添加，会出现数据源不会进行切换的问题，
     * 这个问题就是事务导致的，只要你在service中开启事务，service中的对其他业务的数据库操作只会使用开启事务时的数据源，
     * 因为开启事务之后数据源会被缓存下来，service主体会从缓存中拿数据源，所以导致其他业务数据源切换失败。

     解决办法：

     因为这个问题是事务导致，所以要从事务中下手，spring事务注解中的传播级别可以解决这个问题，
     spring默认的传播级别是required只会使用当前事务，所以在service中你要对其他service进行修改、添加时，
     需要在另外的一个service中开启另一个新事务使用required_new创建一个新的事务，就会切换数据源，
     当你要对另外一个service进行查询时开启另一个事务使用not_supported把主service中的事务挂起来，这样就会切换数据源了。

     spring的事务传播级别：

      REQUIRED：支持当前事务，如果当前没有事务，就新建一个事务。这是最常见的选择。 

      SUPPORTS：支持当前事务，如果当前没有事务，就以非事务方式执行。 

      MANDATORY：支持当前事务，如果当前没有事务，就抛出异常。 

      REQUIRES_NEW：新建事务，如果当前存在事务，把当前事务挂起。 

      NOT_SUPPORTED：以非事务方式执行操作，如果当前存在事务，就把当前事务挂起。 

      NEVER：以非事务方式执行，如果当前存在事务，则抛出异常。 

      NESTED：支持当前事务，如果当前事务存在，则执行一个嵌套事务，如果当前没有事务，就新建一个事务
     */

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Test
    public void save() {
        DynamicDataSourceContextHolder.setDataSourceRouterKey("salve1");
        User user = new User();
        user.setUsername("master");
        user.setPassword("master");
        user.setSex(1);
        user.setAge(18);
        Assert.assertEquals(1,userMapper.save(user));
    }

    @Test
    public void update() {
        User user = new User();
        user.setId(8L);
        user.setPassword("newpassword");
        // 返回插入的记录数 ，期望是1条 如果实际不是一条则抛出异常
        Assert.assertEquals(1,userMapper.update(user));
    }

    @Test
    public void selectById() {
        DynamicDataSourceContextHolder.setDataSourceRouterKey("slave2");
        User user = userMapper.selectById(2L);
        System.out.println("id:" + user.getId());
        System.out.println("name:" + user.getUsername());
        System.out.println("password:" + user.getPassword());
    }

    @Test
    public void deleteById() {
        Assert.assertEquals(1,userMapper.deleteById(1L));
    }

    @Test
    public void selectAll() {
        List<User> users= userMapper.selectAll();
        users.forEach(user -> {
            System.out.println("id:" + user.getId());
            System.out.println("name:" + user.getUsername());
            System.out.println("password:" + user.getPassword());
        });
    }

    @Test
    public void testTransactional() {
       userService.testTransactional();
    }


}