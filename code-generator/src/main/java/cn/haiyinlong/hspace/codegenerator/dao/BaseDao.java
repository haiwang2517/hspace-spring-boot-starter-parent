package cn.haiyinlong.hspace.codegenerator.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface BaseDao extends BaseMapper {
    Map<String, String> queryTable(String tableName);

    List<Map<String, String>> queryColumns(String tableName);
}
