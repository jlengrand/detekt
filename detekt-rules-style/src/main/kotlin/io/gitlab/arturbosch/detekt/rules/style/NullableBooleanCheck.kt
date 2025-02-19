package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.resolve.calls.util.getType
import org.jetbrains.kotlin.types.typeUtil.isBooleanOrNullableBoolean

/**
 * Detects nullable boolean checks which use an elvis expression `?:` rather than equals `==`.
 *
 * Per the [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html#nullable-boolean-values-in-conditions)
 * converting a nullable boolean property to non-null should be done via `!= false` or `== true`
 * rather than `?: true` or `?: false` (respectively).
 *
 * <noncompliant>
 * value ?: true
 * value ?: false
 * </noncompliant>
 *
 * <compliant>
 * value != false
 * value == true
 * </compliant>
 */
@RequiresTypeResolution
class NullableBooleanCheck(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Nullable boolean check should use `==` rather than `?:`",
        Debt.FIVE_MINS,
    )

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        if (expression.operationToken == KtTokens.ELVIS &&
            expression.right?.isBooleanConstant() == true &&
            expression.left?.getType(bindingContext)?.isBooleanOrNullableBoolean() == true
        ) {
            if (expression.right?.text == "true") {
                report(
                    CodeSmell(
                        issue,
                        Entity.from(expression),
                        "The nullable boolean check `${expression.text}` should use `!= false` rather than `?: true`",
                    )
                )
            } else {
                report(
                    CodeSmell(
                        issue,
                        Entity.from(expression),
                        "The nullable boolean check `${expression.text}` should use `== true` rather than `?: false`",
                    )
                )
            }
        }

        super.visitBinaryExpression(expression)
    }

    private fun PsiElement.isBooleanConstant() = node.elementType == KtNodeTypes.BOOLEAN_CONSTANT
}
