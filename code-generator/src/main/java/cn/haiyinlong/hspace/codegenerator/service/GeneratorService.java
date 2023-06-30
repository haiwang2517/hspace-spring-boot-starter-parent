package cn.haiyinlong.hspace.codegenerator.service;

import cn.haiyinlong.hspace.codegenerator.dao.BaseDao;
import cn.haiyinlong.hspace.codegenerator.utils.GenUtils;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeneratorService {
  final BaseDao baseDao;

  public byte[] generatorCode(List<String> tables, String tableSchema) throws Exception {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ZipOutputStream zip = new ZipOutputStream(outputStream);
    for (String tableName : tables) {
      // 查询表信息
      Map<String, String> table = baseDao.queryTable(tableName, tableSchema);
      // 查询列信息
      List<Map<String, String>> columns = baseDao.queryColumns(tableName, tableSchema);
      // 生成代码
      GenUtils.generatorCode(table, columns, zip);
    }
    IOUtils.closeQuietly(zip);
    return outputStream.toByteArray();
  }
}
