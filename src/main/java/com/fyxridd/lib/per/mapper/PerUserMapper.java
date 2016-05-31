package com.fyxridd.lib.per.mapper;

import com.fyxridd.lib.per.model.PerUser;
import org.apache.ibatis.annotations.Param;

public interface PerUserMapper {
    boolean exist(@Param("name") String name);

    PerUser selectPerUser(@Param("name") String name);

    void insert(PerUser user);

    void update(PerUser user);

}
