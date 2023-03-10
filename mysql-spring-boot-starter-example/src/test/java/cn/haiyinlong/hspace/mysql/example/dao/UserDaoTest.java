package cn.haiyinlong.hspace.mysql.example.dao;

import cn.haiyinlong.hspace.mysql.example.dao.entity.UserEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserDaoTest {

  @Autowired UserDao userDao;

  @Test
  public void testGet() {
    UserEntity userEntity = userDao.selectById(100L);
    System.out.println(userEntity);
  }

  @Test
  public void testPage() {
    Page<UserEntity> userEntityPage =
        userDao.selectPage(
            new Page<UserEntity>(1, 1),
            new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getId, 100L));
    System.out.println(userEntityPage);
  }
}
