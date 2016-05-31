package com.fyxridd.lib.per.model;

import java.util.Set;

/**
 * 权限组
 */
public class PerGroup{
    //组名
    private String name;
    //可为空不为null
    private Set<String> inherits;
    //可为空不为null
    private Set<String> pers;

    public PerGroup() {
    }

    public PerGroup(String name, Set<String> inherits, Set<String> pers) {
        this.name = name;
        this.inherits = inherits;
        this.pers = pers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getInherits() {
        return inherits;
    }

    public void setInherits(Set<String> inherits) {
        this.inherits = inherits;
    }

    public Set<String> getPers() {
        return pers;
    }

    public void setPers(Set<String> pers) {
        this.pers = pers;
    }
}
