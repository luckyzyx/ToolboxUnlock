package com.luckyzyx.toolboxunlock

import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
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
            //<string name="premium_dialog_header">解锁高级功能</string>
            //dialog layout unlock_premium
            DialogClass.hook {
                injectMember {
                    constructor { paramCount = 2 }
                    afterHook {
                        if (instanceClass == DialogClass) return@afterHook
                        instanceClass.canonicalName?.let {
                            findClass(it).hook {
                                injectMember {
                                    constructor { paramCount = 5 }
                                    afterHook {
                                        method { emptyParam();returnType = IntType }.giveAll()
                                            .forEach { its -> its.invoke(null) }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}