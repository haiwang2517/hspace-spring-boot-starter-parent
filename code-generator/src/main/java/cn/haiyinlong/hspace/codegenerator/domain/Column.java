package cn.haiyinlong.hspace.codegenerator.domain;

import java.io.Serializable;
import lombok.Data;

@Data
public class Column implements Serializable {
  // 列名
  private String columnName;
  // 列名类型
  private String dataType;
  // 列名备注
  private String comments;
  // 属性名称(第一个字母大写)，如：user_name => UserName
  private String attrName;
  // 属性名称(第一个字母小写)，如：user_name => userName
  private String attrNameLowerCase;
  // 属性类型
  private String attrType;
  // auto_increment
  private String extra;
}
