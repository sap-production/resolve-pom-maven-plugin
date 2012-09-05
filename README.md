resolve-pom-maven-plugin
========================

This Maven plugin creates in the initialization phase a copy of the original pom where all properties get resolved by its actual values. This substituted POM is used in the former processing. Especially it will be used when an upload to the local or remote repository takes place.