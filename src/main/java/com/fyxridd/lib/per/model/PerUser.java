package com.fyxridd.lib.per.model;

import java.util.Set;

/**
 * 权限用户
 */
public class PerUser {
    //用户名
    private String name;
    //可为空不为null
    private Set<String> groups;
    //可为空不为null
    private Set<String> pers;

    public PerUser() {}

    public PerUser(String name, Set<String> groups, Set<String> pers) {
        this.name = name;
        this.groups = groups;
        this.pers = pers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getGroups() {
        return groups;
    }

    public void setGroups(Set<String> groups) {
        this.groups = groups;
    }

    public Set<String> getPers() {
        return pers;
    }

    public void setPers(Set<String> pers) {
        this.pers = pers;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return ((PerUser)obj).name.equals(name);
    }
}
