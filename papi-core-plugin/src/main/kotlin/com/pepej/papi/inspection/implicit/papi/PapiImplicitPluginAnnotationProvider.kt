package com.pepej.papi.inspection.implicit.papi

import com.intellij.codeInsight.AnnotationUtil
import com.intellij.codeInsight.daemon.ImplicitUsageProvider
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.pepej.papi.APPLICATION_CLASS
import com.pepej.papi.PLUGIN_ANNOTATION_CLASS

class PapiImplicitPluginAnnotationProvider : ImplicitUsageProvider {
    override fun isImplicitUsage(element: PsiElement): Boolean {
        val javaPsiFacade = JavaPsiFacade.getInstance(element.project)
        val projectScope = GlobalSearchScope.allScope(element.project)
        return if (javaPsiFacade.findClass(APPLICATION_CLASS, projectScope) != null) {
            element is PsiClass && AnnotationUtil.isAnnotated(element, PLUGIN_ANNOTATION_CLASS, 0)
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