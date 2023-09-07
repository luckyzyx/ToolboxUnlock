package com.luckyzyx.toolboxunlock

import android.app.Dialog
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.type.android.DialogClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit

@InjectYukiHookWithXposed(isUsingResourcesHook = false)
object MainHook : IYukiHookXposedInit {
    override fun onInit() = configs {
        debugLog {
            tag = "ToolboxUnlock"
            isEnable = true
            isRecord = true
            elements(TAG, PRIORITY, PACKAGE_NAME, USER_ID)
        }
        isDebug = false
    }

    override fun onHook() = encase {
        //Toolbox
        loadApp("io.mrarm.mctoolbox") {
            //Source Dialog -> unlock_premium
            DialogClass.hook {
                injectMember {
                    constructor { paramCount = 2 }
                    afterHook {
                        if (instanceClass == DialogClass) return@afterHook
                        else if (instanceClass.hasMethod { emptyParam();returnType = IntType }) {
                            instance<Dialog>().setOnShowListener { d -> d?.dismiss() }
//                            loggerD(msg = instance.javaClass.name)
                            instance.javaClass.name.let { loadHooker(HookPremiumDialog(it)) }
                        }
                    }
                }
            }
        }
    }

    private class HookPremiumDialog(val cls: String) : YukiBaseHooker() {
        override fun onHook() {
            //Source Dialog -> super -> remaining_time / premium_ticket
            cls.toClassOrNull()?.hook {
                injectMember {
                    constructor { paramCount = 5 }
                    afterHook {
                        method { emptyParam();returnType = IntType }.giveAll()
                            .forEach { its -> its.invoke(null) }
                        instance<Dialog>().setOnShowListener { d -> d?.dismiss() }
                    }
                }
            }
        }
    }
}