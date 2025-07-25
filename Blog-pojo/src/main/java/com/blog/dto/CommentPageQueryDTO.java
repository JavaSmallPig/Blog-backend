package com.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Diamond
 * @create 2025-01-27
 */
@Data
@Schema(description = "评论分页查询DTO")
public class CommentPageQueryDTO implements Serializable {
    
    @Schema(description = "文章ID")
    private Integer articleId;
    
    @Schema(description = "评论用户名")
    private String username;
    
    @Schema(description = "评论内容")
    private String content;
    
    @Schema(description = "页码")
    private Integer page;
    
    @Schema(description = "每页记录数")
    private Integer pageSize;
}