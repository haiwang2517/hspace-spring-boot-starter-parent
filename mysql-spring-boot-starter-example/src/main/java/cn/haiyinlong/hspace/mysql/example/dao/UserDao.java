package cn.haiyinlong.hspace.mysql.example.dao;

import cn.haiyinlong.hspace.mysql.example.dao.entity.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UserDao extends BaseMapper<UserEntity> {}
