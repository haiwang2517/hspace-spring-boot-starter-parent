package cn.haiyinlong.hspace.mysql.example.dao.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("user")
@Data
public class UserEntity {
  @TableId("id")
  private Integer id;

  private String name;
}
