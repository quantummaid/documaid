[![Last Commit](https://img.shields.io/github/last-commit/quantummaid/documaid)](https://github.com/quantummaid/documaid)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.quantummaid/documaid/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.quantummaid/documaid)
[![Code Size](https://img.shields.io/github/languages/code-size/quantummaid/documaid)](https://github.com/quantummaid/documaid)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Slack](https://img.shields.io/badge/chat%20on-Slack-brightgreen)](https://join.slack.com/t/quantummaid/shared_invite/zt-cx5qd605-vG10I~WazfgH9WOnXMzl3Q)
[![Gitter](https://img.shields.io/badge/chat%20on-Gitter-brightgreen)](https://gitter.im/quantum-maid-framework/community)
[![Twitter](https://img.shields.io/twitter/follow/quantummaid)](https://twitter.com/quantummaid)


<img src="documaid_logo.png" align="left"/>

# DocuMaid

DocuMaid keeps your documentation in sync with your codebase.

## Usage
To generate the the documentation:

```
mvn de.quantummaid:documaid:0.9.22:generate
mvn de.quantummaid:documaid:0.9.22:generate -DskipPaths=docs/Usage.md

```

To validate, if the current documentation is correct and does not require any changes:

```
mvn de.quantummaid:documaid:0.9.22:validate
```


## Maven Configuration
<!---[Plugin](groupId artifactId version goal=generate phase=verify )-->
```xml
<plugin>
    <groupId>de.quantummaid</groupId>
    <artifactId>documaid</artifactId>
    <version>0.9.22</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
            <phase>verify</phase>
        </execution>
    </executions>
</plugin>
```

More information can be found [here](documentation/Usage.md).