package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.experimental.DiscouragedCommentLocationRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint-website](https://ktlint.github.io#rule-spacing) for documentation.
 */
@AutoCorrectable(since = "1.20.0")
class DiscouragedCommentLocation(config: Config) : FormattingRule(config) {

    override val wrapping = DiscouragedCommentLocationRule()
    override val issue = issueFor("Detect discouraged comment locations.")
}
