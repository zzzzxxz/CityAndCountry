package com.example.demo.area.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.beans.Transient;
import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


/**
 * @author zx
 */
@Data
@TableName(value = "t_china")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class TChina implements Serializable {

    @JsonProperty("code")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @JsonIgnore
    @TableField(value = "type")
    private String type;

    @TableField(value = "name")
    private String name;

    @JsonIgnore
    @TableField(value = "parent")
    private Integer parent;

    @JsonIgnore
    @TableField(value = "code")
    private String code;

    @JsonIgnore
    @TableField(value = "province")
    private Integer province;

    @JsonIgnore
    @TableField(value = "city")
    private Integer city;

    @JsonIgnore
    @TableField(value = "district")
    private Integer district;

    @TableField(exist = false)
    private transient List<TChina> children;

    private static final long serialVersionUID = 1L;
}