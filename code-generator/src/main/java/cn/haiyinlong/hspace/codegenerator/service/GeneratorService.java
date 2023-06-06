package cn.haiyinlong.hspace.codegenerator.service;

import cn.haiyinlong.hspace.codegenerator.dao.BaseDao;
import cn.haiyinlong.hspace.codegenerator.utils.GenUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class GeneratorService {
  final BaseDao baseDao;

  public byte[] generatorCode(List<String> tables) throws Exception {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ZipOutputStream zip = new ZipOutputStream(outputStream);
    for (String tableName : tables) {
      // 查询表信息
      Map<String, String> table = baseDao.queryTable(tableName);
      // 查询列信息
      List<Map<String, String>> columns = baseDao.queryColumns(tableName);
      // 生成代码
      GenUtils.generatorCode(table, columns, zip);
    }
    IOUtils.closeQuietly(zip);
    return outputStream.toByteArray();
  }
}
