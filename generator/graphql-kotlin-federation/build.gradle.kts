description = "Federated GraphQL schema generator"

val junitVersion: String by project

dependencies {
    api(project(path = ":graphql-kotlin-schema-generator"))
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
}

tasks {
    jacocoTestCoverageVerification {
        violationRules {
            rule {
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = "0.96".toBigDecimal()
                }
                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = "0.93".toBigDecimal()
                }
            }
        }
    }
}
