package architecture

import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.core.domain.JavaAnnotation
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import utils.classification.AcceptanceTest
import utils.classification.IntegrationTest
import utils.classification.UnitTest

@UnitTest
internal class ArchitectureTest {

    val classes = ClassFileImporter().importClasspath()!!

    @ValueSource(strings = [
        "library.enrichment.correlation",
        "library.enrichment.gateways",
        "library.enrichment.messaging",
        "library.enrichment.metrics",
        "library.enrichment.security"
    ])
    @ParameterizedTest fun `core module classes are not allowed to access technical modules`(packageName: String) {
        noClasses().that()
                .resideInAPackage("library.enrichment.core..")
                .should().accessClassesThat()
                .resideInAPackage("$packageName..")
                .check(classes)
    }

    @Test fun `test classes must be classified`() = checkThat {
        classes().that()
                .haveNameMatching(".*Test(s)?")
                .and()
                .areAssignableTo(Any::class.java)
                .should()
                .beAnnotatedWith(AnyTestClassification)
    }

    fun checkThat(ruleSupplier: () -> ArchRule) {
        ruleSupplier().check(classes)
    }

    object AnyTestClassification : DescribedPredicate<JavaAnnotation>("Test Classification") {

        override fun apply(input: JavaAnnotation): Boolean {
            val javaClass = input.type
            return when {
                javaClass.isEquivalentTo(UnitTest::class.java) -> true
                javaClass.isEquivalentTo(IntegrationTest::class.java) -> true
                javaClass.isEquivalentTo(AcceptanceTest::class.java) -> true
                else -> false
            }
        }

    }

}