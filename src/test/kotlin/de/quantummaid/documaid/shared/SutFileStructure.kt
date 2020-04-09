package de.quantummaid.documaid.shared

import java.nio.file.Path

class SutFileStructure internal constructor() {
    private val children = mutableListOf<SutFileObject>()
    private var basePath: Path? = null

    companion object {
        fun aFileStructureForDocuMaidToProcess(): SutFileStructure {
            return SutFileStructure()
        }
    }

    fun with(vararg children: SutFileObject): SutFileStructure {
        this.children.addAll(children.toList())
        return this
    }

    fun inDirectory(temporaryTestDirectory: TemporaryTestDirectory): SutFileStructure {
        this.basePath = temporaryTestDirectory.path.toAbsolutePath()
        return this
    }

    fun generateFileStructureForDocuMaidToProcess(): PhysicalFileSystemStructure {
        return generate(basePath!!, GeneratedTargetFileStructureType.INPUT_FOR_DOCUMAID)
    }

    fun generateExpectedFileStructureForGithub(): PhysicalFileSystemStructure {
        return generate(basePath!!, GeneratedTargetFileStructureType.EXPECTED_OUTPUT_FOR_GITHUB)
    }

    fun generateExpectedFileStructureForHugo(): PhysicalFileSystemStructure {
        return generate(basePath!!, GeneratedTargetFileStructureType.EXPECTED_OUTPUT_FOR_HUGO)
    }

    private fun generate(basePath: Path, generatedTargetFileStructureType: GeneratedTargetFileStructureType): PhysicalFileSystemStructure {

        val rootDirectory = SutDirectory.aDirectory(basePath.toFile().name)
            .with(children).generate(basePath.parent, generatedTargetFileStructureType)
        return PhysicalFileSystemStructure(rootDirectory)
    }

    fun cleanUp() {
        deleteDirectoryAndChildren(basePath!!)
    }

}

enum class GeneratedTargetFileStructureType {
    INPUT_FOR_DOCUMAID,
    EXPECTED_OUTPUT_FOR_GITHUB,
    EXPECTED_OUTPUT_FOR_HUGO,
}

interface SutFileObject {

    fun generate(parentPath: Path, generatedTargetFileStructureType: GeneratedTargetFileStructureType): PhysicalFileObject
}

class SutDirectory private constructor(private val name: String) : SutFileObject {

    private val children = ArrayList<SutFileObject>()

    companion object {
        fun aDirectory(name: String): SutDirectory {
            return SutDirectory(name)
        }

    }

    fun with(vararg children: SutFileObject): SutDirectory {
        this.children.addAll(children)
        return this
    }

    fun with(children: List<SutFileObject>): SutDirectory {
        this.children.addAll(children)
        return this
    }

    override fun generate(parentPath: Path, generatedTargetFileStructureType: GeneratedTargetFileStructureType): PhysicalDirectory {
        val physicalDirectoryBuilder = PhysicalDirectoryBuilder.aDirectory(name)
        val physicalDirectory = when(generatedTargetFileStructureType){
            GeneratedTargetFileStructureType.INPUT_FOR_DOCUMAID -> physicalDirectoryBuilder.create(parentPath)
            GeneratedTargetFileStructureType.EXPECTED_OUTPUT_FOR_GITHUB -> physicalDirectoryBuilder.construct(parentPath)
            GeneratedTargetFileStructureType.EXPECTED_OUTPUT_FOR_HUGO -> physicalDirectoryBuilder.construct(parentPath)
        }
        val physicalChildren = children.map { it.generate(physicalDirectory.path, generatedTargetFileStructureType) }
        physicalDirectory.add(physicalChildren)
        return physicalDirectory
    }
}

interface ProcessedFile : SutFileObject {
    fun originalFile(): PhysicalFileBuilder

    fun unprocessedFile(): PhysicalFileBuilder

    fun processedFile(): PhysicalFileBuilder

    fun processedFileInHugoFormat(): PhysicalFileBuilder
}

class ProcessedFileBuilder {
    private var originalFile: PhysicalFileBuilder? = null
    private var docuMaidedFile: PhysicalFileBuilder? = null

    private var docuMaidedFileInHugoFormat: PhysicalFileBuilder? = null

    companion object {
        fun anExpectedFile(): ProcessedFileBuilder {
            return ProcessedFileBuilder()
        }

    }

    fun withOriginalNameAndContent(name: String, content: String): ProcessedFileBuilder {
        originalFile = PhysicalFileBuilder.aFile(name)
            .withContent(content)
        return this
    }

    fun withProcessedNameAndContent(name: String, content: String): ProcessedFileBuilder {
        docuMaidedFile = PhysicalFileBuilder.aFile(name)
            .withContent(content)
        return this
    }

    fun withProcessedNameAndContentInHugoFormat(name: String, content: String): ProcessedFileBuilder {
        docuMaidedFileInHugoFormat = PhysicalFileBuilder.aFile(name)
            .withContent(content)
        return this
    }

    fun build(): ProcessedFile {
        return SimpleProcessedFile(originalFile!!, docuMaidedFile!!, docuMaidedFileInHugoFormat!!)
    }

}

open class SimpleProcessedFile(private val originalFile: PhysicalFileBuilder,
                               private val processedFile: PhysicalFileBuilder,
                               private val processedFileInHugoFormat: PhysicalFileBuilder) : ProcessedFile {

    override fun originalFile(): PhysicalFileBuilder {
        return originalFile
    }

    override fun unprocessedFile(): PhysicalFileBuilder {
        return originalFile
    }

    override fun processedFile(): PhysicalFileBuilder {
        return processedFile
    }

    override fun processedFileInHugoFormat(): PhysicalFileBuilder {
        return processedFileInHugoFormat
    }

    override fun generate(parentPath: Path, generatedTargetFileStructureType: GeneratedTargetFileStructureType): PhysicalFileObject {
        return when (generatedTargetFileStructureType) {
            GeneratedTargetFileStructureType.INPUT_FOR_DOCUMAID -> originalFile.create(parentPath)
            GeneratedTargetFileStructureType.EXPECTED_OUTPUT_FOR_GITHUB -> processedFile.construct(parentPath)
            GeneratedTargetFileStructureType.EXPECTED_OUTPUT_FOR_HUGO -> processedFileInHugoFormat.construct(parentPath)
        }
    }
}

open class NotProcessedSourceFile(notChangingFileBuilder: PhysicalFileBuilder): SimpleProcessedFile(notChangingFileBuilder, notChangingFileBuilder, notChangingFileBuilder)
