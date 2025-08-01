package com.kvartalica.repository

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

object FilesRepository {
    private val root: Path = Paths.get("static").toAbsolutePath().normalize()

    private fun resolve(path: String): Path {
        val resolved = root.resolve(path).normalize()
        require(resolved.startsWith(root)) { "Недопустимый путь: $path" }
        return resolved
    }

    fun listDirectories(): List<String> =
        Files.walk(root)
            .filter { Files.isDirectory(it) && it != root }
            .map { root.relativize(it).toString().replace("\\", "/") }
            .toList()

    fun listChildDirectories(parent: String): List<String> {
        val parentPath = resolve(parent)
        return Files.list(parentPath)
            .filter { Files.isDirectory(it) }
            .map { root.relativize(it).toString().replace("\\", "/") }
            .toList()
    }

    fun createDirectory(path: String): Boolean {
        val dir = resolve(path)
        return if (Files.notExists(dir)) {
            Files.createDirectories(dir)
            true
        } else {
            false
        }
    }

    fun deleteDirectory(path: String): Boolean {
        val dir = resolve(path)
        if (Files.exists(dir) && Files.isDirectory(dir)) {
            Files.walk(dir)
                .sorted(Comparator.reverseOrder())
                .forEach { Files.deleteIfExists(it) }
            return true
        }
        return false
    }

    fun renameDirectory(oldPath: String, newName: String): Boolean {
        val src = resolve(oldPath)
        val dst = src.parent.resolve(newName)
        return if (Files.exists(src) && Files.notExists(dst)) {
            Files.move(src, dst)
            true
        } else false
    }

    fun listFiles(dir: String): List<String> {
        val directory = resolve(dir)
        return Files.list(directory)
            .filter { Files.isRegularFile(it) }
            .map { root.relativize(it).toString().replace("\\", "/") }
            .toList()
    }

    fun getFile(path: String): File? {
        val file = resolve(path).toFile()
        return if (file.exists() && file.isFile) file else null
    }

    fun saveFile(dir: String, name: String, bytes: ByteArray): Boolean {
        val directory = resolve(dir)
        if (Files.notExists(directory)) {
            Files.createDirectories(directory)
        }
        val target = directory.resolve(name)
        Files.write(target, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
        return true
    }

    fun deleteFile(path: String): Boolean {
        val file = resolve(path)
        return Files.deleteIfExists(file)
    }

    fun renameFile(oldPath: String, newName: String): Boolean {
        val src = resolve(oldPath)
        val dst = src.parent.resolve(newName)
        return if (Files.exists(src) && Files.notExists(dst)) {
            Files.move(src, dst)
            true
        } else false
    }
}