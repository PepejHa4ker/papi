package com.pepej.papi.inspection

import com.intellij.lang.jvm.annotation.JvmAnnotationClassValue
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiClass
import com.intellij.psi.util.PsiTreeUtil
import com.pepej.papi.IMPLEMENTOR_ANNOTATION_CLASS
import com.pepej.papi.IMPLEMENTOR_ANNOTATION_CLASS_VALUE
import com.siyeh.ig.BaseInspection
import com.siyeh.ig.BaseInspectionVisitor

class PapiImplementorAnnotationInstanceCheckInspection : BaseInspection() {
    override fun buildVisitor(): BaseInspectionVisitor {
        return object : BaseInspectionVisitor() {

            override fun visitAnnotation(annotation: PsiAnnotation) {
                super.visitAnnotation(annotation)
                val annotationQualifiedName = annotation.qualifiedName
                if (annotationQualifiedName == IMPLEMENTOR_ANNOTATION_CLASS) {
                    val value = annotation.attributes
                        .find { a -> a.attributeName == IMPLEMENTOR_ANNOTATION_CLASS_VALUE }
                        .let { it?.attributeValue as JvmAnnotationClassValue}
                        .clazz
                        as PsiClass

                    val owner = PsiTreeUtil.getParentOfType(annotation, PsiClass::class.java) ?: return
                    if (!value.isInheritor(owner, false)) {
                        registerError(annotation, owner, value)
                    }

                }
            }
        }
    }


    override fun getStaticDescription() = "Check implementor class inheritance"

    override fun buildErrorString(vararg infos: Any?): String {
        val owner = infos[0] as PsiClass
        val targetClass = infos[1] as PsiClass

        return "${targetClass.name} doesn't implements ${owner.name}"
    }
}