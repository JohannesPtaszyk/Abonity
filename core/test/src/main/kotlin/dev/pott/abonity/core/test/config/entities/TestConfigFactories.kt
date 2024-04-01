package dev.pott.abonity.core.test.config.entities

import dev.pott.abonity.core.entity.config.Config
import dev.pott.abonity.core.entity.config.LegalConfig

const val TEST_PRIVACY_POLICY_URL = "https://example.com/privacy"
const val TEST_IMPRINT_URL = "https://example.com/imprint"

fun createTestConfig(legal: LegalConfig = createTestLegalConfig()): Config = Config(legal = legal)

fun createTestLegalConfig(
    privacyPolicyUrl: String = TEST_PRIVACY_POLICY_URL,
    imprintUrl: String = TEST_IMPRINT_URL,
): LegalConfig =
    LegalConfig(
        privacyPolicyUrl = privacyPolicyUrl,
        imprintUrl = imprintUrl,
    )
