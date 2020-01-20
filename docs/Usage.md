## Features
The following features a supported in markdown

#### Links:
Links to existing files can be automatically checked and inserted.
If the target file is missing, an exception is raised.
```
<!---[Link](path/to/a/file.java name)-->

<!---[Link](path/to/a/file.java "link name with whitespaces and %")-->
```

#### CodeSnippet:
Parts or complete files can be imported into Markdown. 
Cited parts begin with `//Showcase start id` and end with `//Showcase end id`.

The following snippet
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
results in 
```java
final LinkedList<Object> list = new LinkedList<Object>();
list.add("5");
```
as code block in Markdown.

Full files can be imported with the `file=` option:

```
<!---[CodeSnippet](file=src/main/java/Main.java)-->
```

In case of XML use the `<!-- Showcase start example -->` and `<!-- Showcase end example -->`

#### Depdendency
Dependency markdown code can be automatically generated.
The following elements are available: `groupId`, `artifactId`, `version` and `scope`.

If the element is defined without a value, the projects maven coordinates are inserted.
The following dependency tag for a project sampleCompany:sampleProject:1.2.3
```
<!---[Dependency](groupId artifactId version)-->
```
would result in
``` 
<dependency>
    <groupId>sampleCompany</groupId>
    <artifactId>sampleProject</artifactId>
    <version>1.2.3</version>
</dependency>
```

Each option can be overridden. The `scope` option must always be set. 
Omitting an option omits the the respective coordinate in the generated markdown.
```
<!---[Dependency](groupId="manualSet" artifactId="manualSet version="1.0.0")-->

<!---[Dependency](groupId artifactId="overriden" version scope="test")-->

<!---[Dependency](groupId artifactId)-->
```

#### Plugin
Plugin configurations can also be generated.

The following
```
<!---[Plugin](groupId artifactId version goal="mygoal" phase="myphase")-->
```
results in 
```
<plugin>
    <groupId>sampleCompany</groupId>
    <artifactId>sampleProject</artifactId>
    <version>1.2.3</version>
    <executions>
        <execution>
            <goals>
                <goal>mygoal</goal>
            </goals>
            <phase>myphase</phase>
        </execution>
    </executions>
</plugin>
```
The `goal` and `phase` part have to be set explicitly.
All other options can be overridden or omitted similar to the `dependency` tag.

```
<!---[Plugin](groupId artifactId version)-->

<!---[Plugin](artifactId="overriden" version goal="mygoal" )-->

```

#### Navigation and Table of Contents
Documaid can generate a table of contents and navigation links between the files.

Each file included in the Toc must follow the naming pattern `[index]_[name].md`.

Assume the following directory structure:
```
1_Introduction.md
2_Docs/1_FirstDocs.md
2_Docs/2_SecondDocs.md
3_Summary.md
```

Using the following `table of contents` tag

```
<!---[TOC](.)--> 
```

would generate the following markdown:
```
<!---[TOC](.)-->
1. [Introduction](1_Introduction.md)
2. Docs
    1. [First docs](2_docs/1_FirstDocs.md)
    2. [Second docs](2_docs/2_SecondDocs.md)
3. [Summary](3_Summary.md)
<!---EndOfToc-->
```
In case a different directory should be used as root directory for the TOC, use:
```
<!---[TOC](./docs)--> 
```

Files index by the table of contents can be linked via a navigation. 
The navigation is optional. But either none index file uses it or all. 
Partial navigation is not supported.
Navigation can be used with:

```
<!---[Nav]-->
```

This results in 
``` 
[<-](1_predecessorFile.md)   [Overview](../FileWithToc.md)   [->](3_successorFile.md)
```

