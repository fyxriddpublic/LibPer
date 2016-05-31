package com.fyxridd.lib.per.mapper;

import com.fyxridd.lib.per.model.PerGroup;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

public interface PerGroupMapper {
    Collection<PerGroup> selectPerGroups();

    boolean exist(@Param("name") String name);

    void insert(PerGroup user);

    void update(PerGroup user);

    void delete(@Param("name") String name);
}
