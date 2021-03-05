package com.pepej.papi

import com.intellij.compiler.CompilerConfiguration
import com.intellij.compiler.CompilerConfigurationImpl
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.wm.WindowManager
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.ui.awt.RelativePoint
import javax.swing.event.HyperlinkEvent

class CheckAnnotationProcessorsStartupActivity : StartupActivity {
    override fun runActivity(project: Project) {
        DumbService.getInstance(project).runWhenSmart {
            val javaPsiFacade = JavaPsiFacade.getInstance(project)
            val projectScope = GlobalSearchScope.allScope(project)
            if (javaPsiFacade.findClass(APPLICATION_CLASS, projectScope) != null) {
                val compilerConfiguration = getCompilerConfiguration(project)
                if (!compilerConfiguration.defaultProcessorProfile.isEnabled) {
                    suggestEnableAnnotations(project)
                }


            }

        }
    }

    private fun getCompilerConfiguration(project: Project) =
        CompilerConfiguration.getInstance(project) as CompilerConfigurationImpl

    private fun suggestEnableAnnotations(project: Project) {
        val statusBar = WindowManager.getInstance().getStatusBar(project)

        JBPopupFactory.getInstance()
            .createHtmlTextBalloonBuilder(
                "Do you want to enable annotation processors for Papi compilation? <a href=\"enable\">Enable</a>",
                MessageType.WARNING,
            )

            {
                if (it.eventType == HyperlinkEvent.EventType.ACTIVATED) {
                    enableAnnotations(project)
                }
            }
            .setFadeoutTime(12000)
            .setHideOnLinkClick(true)
            .createBalloon()
            .show(RelativePoint.getCenterOf(statusBar.component), Balloon.Position.atRight)
    }

    private fun enableAnnotations(project: Project) {
        getCompilerConfiguration(project).defaultProcessorProfile.isEnabled = true

        val statusBar = WindowManager.getInstance().getStatusBar(project)
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