package cn.haiyinlong.hspace.codegenerator.utils;

import cn.haiyinlong.hspace.codegenerator.domain.Column;
import cn.haiyinlong.hspace.codegenerator.domain.Table;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

public class GenUtils {
  public static List<String> getTemplates() {
    List<String> templates = new ArrayList<String>();
    templates.add("template/ddd/domain/Domain.java.vm");
    templates.add("template/ddd/domain/Repository.java.vm");
    templates.add("template/ddd/infrastructure/Dao.java.vm");
    templates.add("template/ddd/infrastructure/Entity.java.vm");
    templates.add("template/ddd/infrastructure/Mapper.xml.vm");
    templates.add("template/ddd/infrastructure/RepositoryImpl.java.vm");
    templates.add("template/ddd/infrastructure/RepositoryTransform.java.vm");

    templates.add("template/ddd/application/Assemble.java.vm");
    templates.add("template/ddd/application/Service.java.vm");
    templates.add("template/ddd/application/ServiceImpl.java.vm");

    templates.add("template/ddd/controller/Controller.java.vm");
    templates.add("template/ddd/controller/dto/Dto.java.vm");
    templates.add("template/ddd/controller/dto/ModifyDto.java.vm");
    templates.add("template/ddd/controller/vo/Vo.java.vm");
    return templates;
  }

  /** 生成代码 */
  public static void generatorCode(
      Map<String, String> table, List<Map<String, String>> columns, ZipOutputStream zip)
      throws Exception {
    // 配置信息
    Configuration config = getConfig();
    boolean hasBigDecimal = false;
    boolean hasDate = false;
    // 表信息
    Table tableEntity = new Table();
    tableEntity.setTableName(table.get("tableName"));
    tableEntity.setComments(table.get("tableComment"));
    // 表名转换成Java类名
    String className = tableToJava(tableEntity.getTableName(), config.getString("tablePrefix"));
    tableEntity.setClassName(className);
    tableEntity.setClassNameLowerCase(StringUtils.uncapitalize(className));

    // 列信息
    List<Column> columnsList = new ArrayList<>();
    for (Map<String, String> column : columns) {
      Column columnEntity = new Column();
      columnEntity.setColumnName(column.get("columnName"));
      columnEntity.setDataType(column.get("dataType"));
      columnEntity.setComments(column.get("columnComment"));
      columnEntity.setExtra(column.get("extra"));

      // 列名转换成Java属性名
      String attrName = columnToJava(columnEntity.getColumnName());
      columnEntity.setAttrName(attrName);
      columnEntity.setAttrNameLowerCase(StringUtils.uncapitalize(attrName));

      // 列的数据类型，转换成Java类型
      String attrType = config.getString(columnEntity.getDataType(), "unknowType");
      columnEntity.setAttrType(attrType);
      if (!hasBigDecimal && attrType.equals("BigDecimal")) {
        hasBigDecimal = true;
      }
      if (!hasDate && attrType.equalsIgnoreCase("LocalDateTime")) {
        hasDate = true;
      }
      // 是否主键
      if ("PRI".equalsIgnoreCase(column.get("columnKey")) && tableEntity.getPk() == null) {
        tableEntity.setPk(columnEntity);
      }

      columnsList.add(columnEntity);
    }
    tableEntity.setColumns(columnsList);

    // 没主键，则第一个字段为主键
    if (tableEntity.getPk() == null) {
      tableEntity.setPk(tableEntity.getColumns().get(0));
    }

    // 设置velocity资源加载器
    Properties prop = new Properties();
    prop.put(
        "file.resource.loader.class",
        "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    Velocity.init(prop);
    String mainPath = config.getString("mainPath");
    mainPath = StringUtils.isBlank(mainPath) ? "io.renren" : mainPath;
    // 封装模板数据
    Map<String, Object> map = new HashMap<>();
    map.put("tableName", tableEntity.getTableName());
    map.put("comments", tableEntity.getComments());
    map.put("pk", tableEntity.getPk());
    map.put("className", tableEntity.getClassName());
    map.put("classname", tableEntity.getClassNameLowerCase());
    map.put("pathName", tableEntity.getClassNameLowerCase().toLowerCase());
    map.put("columns", tableEntity.getColumns());
    map.put("hasBigDecimal", hasBigDecimal);
    map.put("hasDate", hasDate);
    map.put("mainPath", mainPath);
    map.put("package", config.getString("pkg"));
    map.put("moduleName", config.getString("moduleName"));
    map.put("author", config.getString("author"));
    map.put("email", config.getString("email"));
    map.put("datetime", format(new Date(), "yyyy-MM-dd HH:mm:ss"));
    VelocityContext context = new VelocityContext(map);

    // 获取模板列表
    List<String> templates = getTemplates();
    for (String template : templates) {
      // 渲染模板
      StringWriter sw = new StringWriter();
      Template tpl = Velocity.getTemplate(template, "UTF-8");
      tpl.merge(context, sw);

      try {
        // 添加到zip
        zip.putNextEntry(
            new ZipEntry(
                getFileName(template, tableEntity.getClassName(), map.get("package").toString())));
        IOUtils.write(sw.toString(), zip, "UTF-8");
        IOUtils.closeQuietly(sw);
        zip.closeEntry();
      } catch (IOException e) {
        throw new Exception("渲染模板失败，表名：" + tableEntity.getTableName(), e);
      }
    }
  }

  /** 列名转换成Java属性名 */
  public static String columnToJava(String columnName) {
    return WordUtils.capitalizeFully(columnName, new char[] {'_'}).replace("_", "");
  }

  /** 表名转换成Java类名 */
  public static String tableToJava(String tableName, String tablePrefix) {
    if (StringUtils.isNotBlank(tablePrefix)) {
      tableName = tableName.replaceFirst(tablePrefix, "");
    }
    return columnToJava(tableName);
  }

  /** 获取配置信息 */
  public static Configuration getConfig() throws Exception {
    try {
      return new PropertiesConfiguration("generator.properties");
    } catch (ConfigurationException e) {
      throw new Exception("获取配置文件失败，", e);
    }
  }

  /** 获取文件名 */
  public static String getFileName(String template, String className, String packageName) {

    String javaPackagePath = "main" + File.separator + "java" + File.separator;

    if (StringUtils.isNotBlank(packageName)) {
      javaPackagePath += packageName.replace(".", File.separator) + File.separator;
    }

    String resourcePackagePath = "main" + File.separator + "resources" + File.separator;

    if (template.contains("ddd/domain/Domain.java.vm")) {
      return javaPackagePath + "domain" + File.separator + className + ".java";
    } else if (template.contains("ddd/domain/Repository.java.vm")) {
      return javaPackagePath
          + "domain"
          + File.separator
          + "repository"
          + File.separator
          + "I"
          + className
          + "Repository.java";
    } else if (template.contains("ddd/infrastructure/RepositoryImpl.java.vm")) {
      return javaPackagePath
          + "infrastructure"
          + File.separator
          + className
          + "RepositoryImpl.java";
    } else if (template.contains("ddd/infrastructure/RepositoryTransform.java.vm")) {
      return javaPackagePath + "infrastructure" + File.separator + className + "Transform.java";
    } else if (template.contains("ddd/infrastructure/Dao.java.vm")) {
      return javaPackagePath
          + "infrastructure"
          + File.separator
          + "dao"
          + File.separator
          + className
          + "Dao.java";
    } else if (template.contains("ddd/infrastructure/Entity.java.vm")) {
      return javaPackagePath
          + "infrastructure"
          + File.separator
          + "dao"
          + File.separator
          + "entity"
          + File.separator
          + className
          + "Entity.java";
    } else if (template.contains("Mapper.xml.vm")) {
      return resourcePackagePath + "mapper" + File.separator + className + "Mapper.xml";
    } else if (template.contains("ddd/application/Assemble.java.vm")) {
      /////////////// application
      return javaPackagePath + "application" + File.separator + className + "Assemble.java";
    } else if (template.contains("ddd/application/Service.java.vm")) {
      return javaPackagePath + "application" + File.separator + className + "Service.java";
    } else if (template.contains("ddd/application/ServiceImpl.java.vm")) {
      return javaPackagePath + "application" + File.separator + className + "ServiceImpl.java";
    } else if (template.contains("ddd/controller/Controller.java.vm")) {
      return javaPackagePath + "controller" + File.separator + className + "Controller.java";
    } else if (template.contains("ddd/controller/dto/Dto.java.vm")) {
      return javaPackagePath
          + "controller"
          + File.separator
          + "dto"
          + File.separator
          + className
          + "Dto.java";
    } else if (template.contains("ddd/controller/dto/ModifyDto.java.vm")) {
      return javaPackagePath
          + "controller"
          + File.separator
          + "dto"
          + File.separator
          + "Modify"
          + className
          + "Dto.java";
    } else if (template.contains("ddd/controller/vo/Vo.java.vm")) {
      return javaPackagePath
          + "controller"
          + File.separator
          + "vo"
          + File.separator
          + className
          + "Vo.java";
    }

    return null;
  }

  public static String format(Date date, String pattern) {
    if (date != null) {
      SimpleDateFormat df = new SimpleDateFormat(pattern);
      return df.format(date);
    }
    return null;
  }
}
