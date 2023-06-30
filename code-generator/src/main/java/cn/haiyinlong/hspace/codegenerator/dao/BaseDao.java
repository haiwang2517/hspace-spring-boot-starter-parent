package cn.haiyinlong.hspace.codegenerator.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface BaseDao extends BaseMapper {
  Map<String, String> queryTable(String tableName, String tableSchema);

  List<Map<String, String>> queryColumns(String tableName, String tableSchema);
}
