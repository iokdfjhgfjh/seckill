package com.southwind.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author admin
 * @since 2021-11-22
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    public class User implements Serializable {

    private static final long serialVersionUID=1L;

      /**
     * 主键
     */
        @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      /**
     * 登录名
     */
      private String loginName;

      /**
     * 用户名
     */
      private String userName;

      /**
     * 密码
     */
      private String password;

      /**
     * 性别(1:男 0：女)
     */
      private Integer gender;

      /**
     * 邮箱
     */
      private String email;

      /**
     * 手机
     */
      private String mobile;

      private String fileName;

      @JsonDeserialize(using = LocalDateTimeDeserializer.class)
      @JsonSerialize(using = LocalDateTimeSerializer.class)
      @TableField(fill = FieldFill.INSERT)
      private LocalDateTime createTime;

      @JsonDeserialize(using = LocalDateTimeDeserializer.class)
      @JsonSerialize(using = LocalDateTimeSerializer.class)
      @TableField(fill = FieldFill.INSERT_UPDATE)
      private LocalDateTime updateTime;


}
