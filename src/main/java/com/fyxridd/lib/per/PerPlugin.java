package com.fyxridd.lib.per;

import com.fyxridd.lib.core.api.SqlApi;
import com.fyxridd.lib.core.api.plugin.SimplePlugin;
import com.fyxridd.lib.per.manager.DaoManager;
import com.fyxridd.lib.per.manager.PerManager;
import com.fyxridd.lib.per.manager.VaultManager;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

import java.io.File;

public class PerPlugin extends SimplePlugin{
    public static PerPlugin instance;

    private DaoManager daoManager;
    private PerManager perManager;

    @Override
    public void onLoad() {
        super.onLoad();
        //注册Permission服务
        Bukkit.getServicesManager().register(Permission.class, new VaultManager(), this, ServicePriority.Highest);
    }

    @Override
    public void onEnable() {
        instance = this;

        //注册Mapper文件
        SqlApi.registerMapperXml(new File(dataPath, "PerGroupMapper.xml"));
        SqlApi.registerMapperXml(new File(dataPath, "PerUserMapper.xml"));

        daoManager = new DaoManager();
        perManager = new PerManager();

        super.onEnable();
    }

    public PerManager getPerManager() {
        return perManager;
    }

    public DaoManager getDaoManager() {
        return daoManager;
    }
}