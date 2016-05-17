package com.fyxridd.lib.per.manager;

import com.fyxridd.lib.per.PerPlugin;
import net.milkbowl.vault.permission.Permission;

import java.util.Collection;

/**
 * 与Vault交互
 */
public class VaultManager extends Permission {
    public static final String PER_NAME = "LibPer";

    @Override
    public String getName() {
        return PER_NAME;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return false;
    }

    @Override
    public boolean playerHas(String world, String player, String per) {
        return PerPlugin.instance.getPerManager().has(player, per);
    }

    @Override
    public boolean playerAdd(String world, String player, String per) {
        return PerPlugin.instance.getPerManager().add(player, per);
    }

    @Override
    public boolean playerRemove(String world, String player, String per) {
        return PerPlugin.instance.getPerManager().del(player, per);
    }

    @Override
    public boolean groupHas(String world, String group, String per) {
        return PerPlugin.instance.getPerManager().groupHas(group, per);
    }

    @Override
    public boolean groupAdd(String world, String group, String per) {
        return PerPlugin.instance.getPerManager().groupAddPer(group, per);
    }

    @Override
    public boolean groupRemove(String world, String group, String per) {
        return PerPlugin.instance.getPerManager().groupRemovePer(group, per);
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        return PerPlugin.instance.getPerManager().hasGroup(player, group, true);
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        return PerPlugin.instance.getPerManager().addGroup(player, group);
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        return PerPlugin.instance.getPerManager().delGroup(player, group);
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        Collection<String> c = PerPlugin.instance.getPerManager().getPlayerGroups(player);
        return c.toArray(new String[c.size()]);
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        return PerManager.DEFAULT_GROUP;
    }

    @Override
    public String[] getGroups() {
        Collection<String> c = PerPlugin.instance.getPerManager().getGroups();
        return c.toArray(new String[c.size()]);
    }

    @Override
    public boolean hasGroupSupport() {
        return true;
    }
}
