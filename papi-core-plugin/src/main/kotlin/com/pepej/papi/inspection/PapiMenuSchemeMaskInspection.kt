package com.pepej.papi.inspection

import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.search.GlobalSearchScope
import com.pepej.papi.MASKS_METHOD_NAME
import com.pepej.papi.MASK_METHOD_NAME
import com.pepej.papi.SCHEME_CLASS
import com.siyeh.ig.BaseInspection
import com.siyeh.ig.BaseInspectionVisitor


class PapiMenuSchemeMaskInspection : BaseInspection() {
    override fun buildVisitor(): BaseInspectionVisitor {
        return object : BaseInspectionVisitor() {

            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                val method = expression.resolveMethod()
                if (method != null) {
                    val javaPsiFacade = JavaPsiFacade.getInstance(expression.project)
                    val projectScope = GlobalSearchScope.allScope(expression.project)
                    if (method.containingClass == javaPsiFacade.findClass(SCHEME_CLASS, projectScope)) {
                        if (MASK_METHOD_NAME == method.name) {

                            if (expression.argumentList.expressions[0].text.length != 11 || !expression.argumentList.expressions[0].text.replace("\"\"", "").matches("(0)(1)*".toRegex())) {
                                registerMethodCallError(expression, expression)
                            }
                        } else if (MASKS_METHOD_NAME == method.name) {
                            if (expression.argumentList.expressions.any { it.text.length != 11 }) {
                                registerMethodCallError(expression, null)
                            }
                        }

                    }


                }
            }
        }
    }


    override fun getStaticDescription() = "Invalid mask"

    override fun buildErrorString(vararg infos: Any?): String {
        return "Invalid mask"
    }
}

