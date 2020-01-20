[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.quantummaid/documaid/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.quantummaid/documaid)

# DocuMaid

To generate the the docu:

```
mvn de.quantummaid:documaid:0.9.7:generate
mvn de.quantummaid:documaid:0.9.7:generate -DskipPaths=docs/Usage.md


```

To validate, if the current docu is correct and does not require any changes:

```
mvn de.quantummaid:documaid:0.9.7:validate
```


## Configuration

#### Minimal
Takes the `README.md` in the root directory
<!---[Plugin](groupId artifactId version goal=generate phase=verify )-->
```xml
<plugin>
    <groupId>de.quantummaid</groupId>
    <artifactId>documaid</artifactId>
    <version>0.9.7</version>
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

More information can be found [here](./docs/Usage.md)