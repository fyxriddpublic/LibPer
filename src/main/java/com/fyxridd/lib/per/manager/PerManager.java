package com.fyxridd.lib.per.manager;

import com.fyxridd.lib.core.api.PlayerApi;
import com.fyxridd.lib.per.PerPlugin;
import com.fyxridd.lib.per.model.PerGroup;
import com.fyxridd.lib.per.model.PerUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.EventExecutor;

import java.util.*;

public class PerManager {
    //默认权限组名
    public static final String DEFAULT_GROUP = "default";

    //默认权限组,所有玩家都有的
    private PerGroup defaultGroup;
    //权限组名 权限组
    //不包括默认权限组
    private Map<String, PerGroup> groupHash;

    //优化策略
    //玩家名 权限用户
    //动态读取
    private Map<String, PerUser> userHash = new HashMap<>();
    //优化策略
    //玩家名 拥有的权限列表
    //动态读取
    private Map<String, Set<String>> persHash = new HashMap<>();
    //优化策略
    //需要保存的玩家列表
    private Set<PerUser> needUpdateList = new HashSet<>();

    public PerManager() {
        //读取数据
        loadData();
        //注册事件
        {
            //插件停止事件
            Bukkit.getPluginManager().registerEvent(PluginDisableEvent.class, PerPlugin.instance, EventPriority.NORMAL, new EventExecutor() {
                @Override
                public void execute(Listener listener, Event e) throws EventException {
                    if (e instanceof PluginDisableEvent) {
                        PluginDisableEvent event = (PluginDisableEvent) e;
                        if (event.getPlugin().getName().equals(PerPlugin.instance.pn)) saveAll();
                    }
                }
            }, PerPlugin.instance);
            //玩家进服事件
            Bukkit.getPluginManager().registerEvent(PlayerJoinEvent.class, PerPlugin.instance, EventPriority.LOWEST, new EventExecutor() {
                @Override
                public void execute(Listener listener, Event e) throws EventException {
                    if (e instanceof PlayerJoinEvent) {
                        PlayerJoinEvent event = (PlayerJoinEvent) e;
                        checkInit(event.getPlayer().getName());
                    }
                }
            }, PerPlugin.instance);
        }
        //计时器: 保存
        Bukkit.getScheduler().scheduleSyncRepeatingTask(PerPlugin.instance, new Runnable() {
            @Override
            public void run() {
                saveAll();
            }
        }, 319, 319);
    }

    public boolean has(Player p, String per) {
        if (p == null) return false;

        return has(p.getName(), per);
    }

    public boolean has(String name, String per) {
        if (name == null) return false;
        if (per == null || per.isEmpty()) return true;

        //玩家存在性检测
        name = PlayerApi.getRealName(null, name);
        if (name == null) return false;

        //检测
        Set<String> set = persHash.get(name);
        if (set == null) set = updatePers(name);
        return set.contains(per);
    }

    public boolean add(Player p, String per) {
        if (p == null) return false;

        return add(p.getName(), per);
    }

    public boolean add(String name, String per) {
        if (name == null || per == null || per.isEmpty()) return false;
        //玩家存在性检测
        name = PlayerApi.getRealName(null, name);
        if (name == null) return false;
        //读取
        PerUser pu = checkInit(name);
        if (pu.getPers().contains(per)) return false;
        pu.getPers().add(per);
        //优化策略
        //更新persHash
        updatePers(name);
        //保存
        needUpdateList.add(pu);
        return true;
    }

    public boolean del(Player p, String per) {
        if (p == null) return false;

        return del(p.getName(), per);
    }

    public boolean del(String name, String per) {
        if (name == null || per == null) return false;
        //玩家存在性检测
        name = PlayerApi.getRealName(null, name);
        if (name == null) return false;
        //读取
        PerUser pu = checkInit(name);
        if (!pu.getPers().contains(per)) return false;
        pu.getPers().remove(per);
        //优化策略
        //更新persHash
        updatePers(name);
        //保存
        needUpdateList.add(pu);
        return true;
    }

    public boolean hasGroup(Player p,String groupName, boolean loop) {
        if (p == null) return false;

        return hasGroup(p.getName(), groupName, loop);
    }

    public boolean hasGroup(String name, String groupName, boolean loop) {
        if (name == null || groupName == null) return false;
        //玩家存在性检测
        name = PlayerApi.getRealName(null, name);
        if (name == null) return false;
        //权限组不存在
        PerGroup group = groupHash.get(groupName);
        if (group == null) return false;
        //检测
        PerUser pu = checkInit(name);
        if (loop) {
            if (pu.getGroups().contains(groupName)) return true;
            for (String s:pu.getGroups()) {
                if (checkHasGroup(s, groupName)) return true;
            }
            return false;
        }else return pu.getGroups().contains(groupName);
    }

    public boolean addGroup(Player p,String groupName) {
        if (p == null) return false;

        return addGroup(p.getName(), groupName);
    }

    public boolean addGroup(String name, String groupName) {
        if (name == null || groupName == null) return false;

        //玩家存在性检测
        name = PlayerApi.getRealName(null, name);
        if (name == null) return false;
        //权限组不存在
        PerGroup group = groupHash.get(groupName);
        if (group == null) return false;
        //已经有此权限组
        PerUser pu = checkInit(name);
        if (pu.getGroups().contains(groupName)) return false;
        //添加权限组成功
        pu.getGroups().add(groupName);
        //优化策略
        //更新persHash
        updatePers(name);
        //保存
        needUpdateList.add(pu);
        return true;
    }

    public boolean delGroup(Player p,String groupName) {
        if (p == null) return false;

        return delGroup(p.getName(), groupName);
    }

    public boolean delGroup(String name, String groupName) {
        if (name == null || groupName == null) return false;

        //玩家存在性检测
        name = PlayerApi.getRealName(null, name);
        if (name == null) return false;
        //权限组不存在
        PerGroup group = groupHash.get(groupName);
        if (group == null) return false;
        //没有此权限组
        PerUser pu = checkInit(name);
        if (!pu.getGroups().contains(groupName)) return false;
        //删除权限组成功
        pu.getGroups().remove(groupName);
        //优化策略
        //更新persHash
        updatePers(name);
        //保存
        needUpdateList.add(pu);
        return true;
    }

    public boolean checkHasGroup(String tar, String groupName) {
        if (tar == null || groupName == null) return false;

        PerGroup group = groupHash.get(tar);
        if (group == null) return false;
        if (group.getInherits().contains(groupName)) return true;
        for (String s:group.getInherits()) {
            if (checkHasGroup(s, groupName)) return true;
        }
        return false;
    }

    public boolean createGroup(String group) {
        //group不能为null,不能与默认组名相同
        if (group == null || group.equalsIgnoreCase(DEFAULT_GROUP)) return false;

        //组已经存在
        PerGroup perGroup = groupHash.get(group);
        if (perGroup != null) return false;

        //新建成功,保存数据
        perGroup = new PerGroup(group, new HashSet<String>(), new HashSet<String>());
        groupHash.put(group, perGroup);
        PerPlugin.instance.getDaoManager().saveOrUpdatePerGroup(perGroup);
        //清空用户缓存
        clearUsers();
        return true;
    }

    public boolean delGroup(String group) {
        //group不能为null,不能与默认组名相同
        if (group == null || group.equalsIgnoreCase(DEFAULT_GROUP)) return false;

        //组不存在
        PerGroup perGroup = groupHash.get(group);
        if (perGroup == null) return false;

        //删除成功,保存数据
        groupHash.remove(group);
        PerPlugin.instance.getDaoManager().deletePerGroup(perGroup);
        //清空用户缓存
        clearUsers();
        return true;
    }

    public boolean groupAddPer(String group, String per) {
        if (group == null || per == null) return false;

        //权限组不存在(允许使用默认权限组)
        PerGroup perGroup = group.equalsIgnoreCase(DEFAULT_GROUP)?defaultGroup:groupHash.get(group);
        if (perGroup == null) return false;
        //已经包含此权限
        if (!perGroup.getPers().add(per)) return false;
        //添加成功,保存数据库
        PerPlugin.instance.getDaoManager().saveOrUpdatePerGroup(perGroup);
        //清空用户缓存
        clearUsers();
        return true;
    }

    public boolean groupRemovePer(String group, String per) {
        if (group == null || per == null) return false;

        //权限组不存在(允许使用默认权限组)
        PerGroup perGroup = group.equalsIgnoreCase(DEFAULT_GROUP)?defaultGroup:groupHash.get(group);
        if (perGroup == null) return false;
        //不包含此权限
        if (!perGroup.getPers().remove(per)) return false;
        //删除成功,保存数据库
        PerPlugin.instance.getDaoManager().saveOrUpdatePerGroup(perGroup);
        //清空用户缓存
        clearUsers();
        return true;
    }

    public boolean groupAddInherit(String group, String inherit) {
        if (group == null || inherit == null) return false;

        //权限组不存在
        PerGroup perGroup = groupHash.get(group);
        if (perGroup == null) return false;
        if (!groupHash.containsKey(inherit)) return false;
        //已经包含此继承
        if (!perGroup.getInherits().add(inherit)) return false;
        //添加成功,保存数据库
        PerPlugin.instance.getDaoManager().saveOrUpdatePerGroup(perGroup);
        //清空用户缓存
        clearUsers();
        return true;
    }

    public boolean groupRemoveInherit(String group, String inherit) {
        if (group == null || inherit == null) return false;

        //权限组不存在
        PerGroup perGroup = groupHash.get(group);
        if (perGroup == null) return false;
        //不包含此继承
        if (!perGroup.getInherits().remove(inherit)) return false;
        //删除成功,保存数据库
        PerPlugin.instance.getDaoManager().saveOrUpdatePerGroup(perGroup);
        //清空用户缓存
        clearUsers();
        return true;
    }

    public boolean groupHas(String group, String per) {
        return groupHas(group, per, true);
    }

    public boolean groupHas(String group, String per, boolean loop) {
        //权限组不存在
        PerGroup perGroup = groupHash.get(group);
        if (perGroup == null) return false;
        //检测当前组
        if (perGroup.getPers().contains(per)) return true;
        //检测继承
        if (loop && perGroup.getInherits() != null) {
            for (String inherit:perGroup.getInherits()) {
                if (groupHas(inherit, per, true)) return true;
            }
        }
        return false;
    }

    /**
     * 获取玩家的权限组列表(玩家直接拥有的列表,权限组的继承不会添加进来)
     */
    public Collection<String> getPlayerGroups(String player) {
        PerUser user = userHash.get(player);
        if (user == null) return new HashSet<>();
        return user.getGroups();
    }

    /**
     * 获取所有的权限组名(包括默认权限组)
     */
    public Collection<String> getGroups() {
        Collection<String> c = groupHash.keySet();
        c.add(DEFAULT_GROUP);
        return c;
    }

    /**
     * 优化策略<br>
     * (根据默认权限组与PerUser)更新玩家的权限信息<br>
     *     更新过后persHash中必然包含玩家
     * @param name 玩家名,不为null
     */
    private Set<String> updatePers(String name) {
        Set<String> set = new HashSet<>();

        //默认权限组
        checkAdd(set, DEFAULT_GROUP);
        //本身权限
        PerUser pu = checkInit(name);
        for (String per:pu.getPers()) set.add(per);
        //本身权限组
        for (String group:pu.getGroups()) checkAdd(set, group);

        //设置
        persHash.put(name, set);
        //返回
        return set;
    }

    /**
     * 将权限组内的所有权限加入集合
     * @param set 集合,不为null
     * @param groupName 权限组名,不为null
     */
    private void checkAdd(Set<String> set, String groupName) {
        PerGroup group;
        if (groupName.equals(DEFAULT_GROUP)) group = defaultGroup;
        else group = groupHash.get(groupName);
        if (group != null) {
            //权限组内权限
            for (String per:group.getPers()) set.add(per);
            //权限组内继承的权限组列表
            for (String inherit:group.getInherits()) checkAdd(set, inherit);
        }
    }

    /**
     * 检测初始化玩家的权限信息<br>
     *     如果不存在会新建
     * @param name 玩家名,不为null
     * @return 不为null
     */
    private PerUser checkInit(String name) {
        //先从缓存中读取
        PerUser user = userHash.get(name);

        //再从数据库中读取
        if (user == null) {
            user = PerPlugin.instance.getDaoManager().getPerUser(name);
            //新建
            if (user == null) {
                user = new PerUser(name, new HashSet<String>(), new HashSet<String>());
                needUpdateList.add(user);
            }
            //添加缓存
            userHash.put(name, user);
        }

        return user;
    }

    /**
     * 保存所有缓存的未保存的信息
     */
    private void saveAll() {
        if (!needUpdateList.isEmpty()) {
            PerPlugin.instance.getDaoManager().saveOrUpdatePerUsers(needUpdateList);
            needUpdateList.clear();
        }
    }

    /**
     * 清空用户缓存
     */
    private void clearUsers() {
        saveAll();
        userHash.clear();
        persHash.clear();
    }

    private void loadData() {
        //清空缓存
        clearUsers();

        //读取所有权限组
        groupHash = new HashMap<>();
        for (PerGroup group:PerPlugin.instance.getDaoManager().getPerGroups()) {
            if (group.getName().equalsIgnoreCase(DEFAULT_GROUP)) {
                defaultGroup = group;
                //检测清空默认权限组的继承列表
                if (!defaultGroup.getInherits().isEmpty()) {
                    defaultGroup.getInherits().clear();
                    PerPlugin.instance.getDaoManager().saveOrUpdatePerGroup(defaultGroup);
                }
            }else groupHash.put(group.getName(), group);
        }

        //未生成默认权限组
        if (defaultGroup == null) {
            defaultGroup = new PerGroup(DEFAULT_GROUP, new HashSet<String>(), new HashSet<String>());
            PerPlugin.instance.getDaoManager().saveOrUpdatePerGroup(defaultGroup);
        }
    }
}
