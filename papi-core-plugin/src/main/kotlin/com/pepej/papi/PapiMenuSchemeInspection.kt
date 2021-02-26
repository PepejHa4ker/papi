package com.pepej.papi

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool
import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.wm.WindowManager
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiMethod
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.ui.awt.RelativePoint

class PapiMenuSchemeInspection : AbstractBaseJavaLocalInspectionTool() {

    override fun checkMethod(
        method: PsiMethod,
        manager: InspectionManager,
        isOnTheFly: Boolean,
    ): Array<ProblemDescriptor>? {
        val javaPsiFacade = JavaPsiFacade.getInstance(method.project)
        val projectScope = GlobalSearchScope.allScope(method.project)
        if (method.containingClass != null && method.containingClass!! == javaPsiFacade.findClass(SCHEME_CLASS, projectScope)) {
            if (method.name == "mask") {
                val statusBar = WindowManager.getInstance().getStatusBar(method.project)
                JBPopupFactory.getInstance()
                    .createHtmlTextBalloonBuilder(
                        "Java annotation processing has been enabled",
                        MessageType.INFO,
                        null
                    )
                    .setFadeoutTime(3000)
                    .createBalloon()
                    .show(RelativePoint.getNorthEastOf(statusBar.component), Balloon.Position.atRight)

            }
        }
        return super.checkMethod(method, manager, isOnTheFly)
    }
}