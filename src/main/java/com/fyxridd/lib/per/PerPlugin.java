package com.fyxridd.lib.per;

import com.fyxridd.lib.core.api.plugin.SimplePlugin;
import com.fyxridd.lib.per.manager.DaoManager;
import com.fyxridd.lib.per.manager.PerManager;
import com.fyxridd.lib.per.manager.VaultManager;
import com.fyxridd.lib.sql.api.SqlApi;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

import java.io.File;

public class PerPlugin extends SimplePlugin{
    public static PerPlugin instance;

    private PerManager perManager;
    private DaoManager daoManager;

    @Override
    public void onEnable() {
        instance = this;

        //注册Mapper文件
        SqlApi.registerMapperXml(new File(dataPath, "PerGroupMapper.xml"));
        SqlApi.registerMapperXml(new File(dataPath, "PerUserMapper.xml"));

        perManager = new PerManager();
        daoManager = new DaoManager();
        //注册Permission服务
        Bukkit.getServicesManager().register(Permission.class, new VaultManager(), this, ServicePriority.Highest);

        super.onEnable();
    }

    public PerManager getPerManager() {
        return perManager;
    }

    public DaoManager getDaoManager() {
        return daoManager;
    }
}