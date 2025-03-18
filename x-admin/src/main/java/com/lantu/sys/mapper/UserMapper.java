package com.lantu.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lantu.sys.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author laocai
 * @since 2023-02-07
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 根据用户ID查询角色名称列表
     * 使用索引优化查询性能
     */
    @Select("SELECT DISTINCT r.role_name FROM x_role r " +
            "INNER JOIN x_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.deleted = 0")
    List<String> getRoleNameByUserId(@Param("userId") Integer userId);
    
    /**
     * 批量查询用户信息
     * 使用IN查询优化性能
     */
    @Select("<script>" +
            "SELECT * FROM x_user WHERE id IN " +
            "<foreach collection='userIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            " AND deleted = 0" +
            "</script>")
    List<User> batchGetUsers(@Param("userIds") List<Integer> userIds);
    
    /**
     * 根据用户名模糊查询用户列表
     * 使用索引优化模糊查询
     */
    @Select("SELECT * FROM x_user WHERE username LIKE CONCAT('%', #{username}, '%') " +
            "AND deleted = 0 LIMIT #{limit}")
    List<User> searchUsersByUsername(@Param("username") String username, @Param("limit") Integer limit);
}
