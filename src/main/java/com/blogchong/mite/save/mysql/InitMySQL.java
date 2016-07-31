package com.blogchong.mite.save.mysql;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Author: blogchong
 * Time:   2016/7/30
 * Email:  blogchong@qq.com
 * WeChat: mute88
 * Des:    初始化
 */
public class InitMySQL {

    JdbcTemplate jdbcTemplate = null;

    public InitMySQL() {
        try {
            jdbcTemplate = JDBCHelper.createMysqlTemplate("data-mite",
                    "jdbc:mysql://localhost:3307/tmp?useUnicode=true&characterEncoding=utf-8",
                    "root", "adf@910.`a322", 5, 30);

        } catch (Exception ex) {
            jdbcTemplate = null;
            System.out.println("mysql未开启或JDBCHelper.createMysqlTemplate中参数配置不正确!");
        }
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

}
