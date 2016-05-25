package com.fyxridd.lib.per.manager;

import com.fyxridd.lib.core.api.SqlApi;
import com.fyxridd.lib.per.mapper.PerGroupMapper;
import com.fyxridd.lib.per.mapper.PerUserMapper;
import com.fyxridd.lib.per.model.PerGroup;
import com.fyxridd.lib.per.model.PerUser;
import org.apache.ibatis.session.SqlSession;

import java.util.*;

/**
 * 与数据库交互
 */
public class DaoManager {
    public Collection<PerGroup> getPerGroups() {
        SqlSession session = SqlApi.getSqlSessionFactory().openSession();
        try {
            PerGroupMapper mapper = session.getMapper(PerGroupMapper.class);
            return mapper.selectPerGroups();
        } finally {
            session.close();
        }
    }

    public void saveOrUpdatePerGroup(PerGroup group) {
        List<PerGroup> list = new ArrayList<>();
        list.add(group);
        saveOrUpdatePerGroups(list);
    }

    public void saveOrUpdatePerGroups(Collection<PerGroup> c) {
        SqlSession session = SqlApi.getSqlSessionFactory().openSession();
        try {
            PerGroupMapper mapper = session.getMapper(PerGroupMapper.class);
            for (PerGroup group:c) {
                if (!mapper.exist(group.getName())) mapper.insert(group);
                else mapper.update(group);
            }
            session.commit();
        } finally {
            session.close();
        }
    }

    public void deletePerGroup(PerGroup group) {
        List<PerGroup> list = new ArrayList<>();
        list.add(group);
        deletePerGroups(list);
    }

    public void deletePerGroups(Collection<PerGroup> c) {
        SqlSession session = SqlApi.getSqlSessionFactory().openSession();
        try {
            PerGroupMapper mapper = session.getMapper(PerGroupMapper.class);
            for (PerGroup group:c) mapper.delete(group.getName());
            session.commit();
        } finally {
            session.close();
        }
    }

    public PerUser getPerUser(String name) {
        SqlSession session = SqlApi.getSqlSessionFactory().openSession();
        try {
            PerUserMapper mapper = session.getMapper(PerUserMapper.class);
            return mapper.selectPerUser(name);
        } finally {
            session.close();
        }
    }

    public void saveOrUpdatePerUsers(Collection<PerUser> c) {
        SqlSession session = SqlApi.getSqlSessionFactory().openSession();
        try {
            PerUserMapper mapper = session.getMapper(PerUserMapper.class);
            for (PerUser user:c) {
                if (!mapper.exist(user.getName())) mapper.insert(user);
                else mapper.update(user);
            }
            session.commit();
        } finally {
            session.close();
        }
    }
}
