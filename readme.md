# DocuMaid

To generate the the docu:

```
mvn de.quantummaid:documaid:1.2:generate

```

To validate, if the current docu is correct and does not require any changes:

```
mvn de.quantummaid:documaid:1.2:validate
```

## Features
Try something of the following:

#### Links:
```
<!---[Link](path/to/a/file.java name)-->

<!---[Link](path/to/a/file.java "link name with whitespaces and %")-->
```

#### CodeSnippet:
```
<!---[CodeSnippet](firstSnippet)-->
```
with a File
```
class Main {
    public static main(String[] args){
        //Showcase start firstSnippet 
        final LinkedList<Object> list = new LinkedList<Object>();
        list.add("5");
        //Showcase end firstSnippet
    }
}
```

For full file:

```
<!---[CodeSnippet](file=src/main/java/Main.java)-->
```

#### Depdendency

TBD
```
<!---[Dependency]-->
```

## Configuration

#### Minimal
Takes the `readme.md` in the root directory
```
<plugins>
    <plugin>
        <groupId>de.quantummaid</groupId>
        <artifactId>documaid</artifactId>
        <version>1.2</version>
        <executions>
            <execution>
                <goals>
                    <goal>generate</goal>
                </goals>
                <phase>verify</phase>
            </execution>
        </executions>
    </plugin>
</plugins>
```
