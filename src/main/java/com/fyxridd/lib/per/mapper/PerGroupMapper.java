package com.fyxridd.lib.per.mapper;

import com.fyxridd.lib.per.model.PerGroup;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

public interface PerGroupMapper {
    Collection<PerGroup> selectPerGroups();

    boolean exist(@Param("name") String name);

    void insert(@Param("perGroup") PerGroup user);

    void update(@Param("perGroup") PerGroup user);

    void delete(@Param("name") String name);
}
