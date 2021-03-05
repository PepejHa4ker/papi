package com.pepej.papi.spigot

import com.intellij.codeInsight.AnnotationUtil
import com.intellij.codeInsight.daemon.ImplicitUsageProvider
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiModifier
import com.intellij.psi.search.GlobalSearchScope
import com.pepej.papi.BUKKIT_CLASS
import com.pepej.papi.EVENT_HANDLER
import com.pepej.papi.LISTENER

class SpigotImplicitUsageProvider : ImplicitUsageProvider {
    override fun isImplicitUsage(element: PsiElement): Boolean {
        val javaPsiFacade = JavaPsiFacade.getInstance(element.project)
        val projectScope = GlobalSearchScope.allScope(element.project)
        return if (javaPsiFacade.findClass(BUKKIT_CLASS, projectScope) != null) {
            element is PsiMethod
                    && element.hasModifierProperty(PsiModifier.PUBLIC)
                    && AnnotationUtil.isAnnotated(element, EVENT_HANDLER, 0)
                    && element.containingClass != null
                    && element.containingClass!!.isInheritor(javaPsiFacade.findClass(LISTENER, projectScope)!!, false)
        } else {
            false
        }
    }

    override fun isImplicitRead(element: PsiElement): Boolean {
        return false
    }

    override fun isImplicitWrite(element: PsiElement): Boolean {
        return false
    }
}