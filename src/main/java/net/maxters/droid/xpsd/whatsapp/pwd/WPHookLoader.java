package net.maxters.droid.xpsd.whatsapp.pwd;

import net.maxters.droid.xposed.hook.AppHookFactory;
import net.maxters.droid.xposed.hook.XPosedHookLoadPackage;

/**
 * Created by nizam on 6/26/14.
 */
public class WPHookLoader extends XPosedHookLoadPackage {
    public WPHookLoader() {
        addSupportPackage("com.whatsapp");
    }

    private static final WPFactory factory = new WPFactory();

    @Override
    protected AppHookFactory getHooksFactory() {
        return factory;
    }
}
