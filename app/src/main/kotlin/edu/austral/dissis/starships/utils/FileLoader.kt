package edu.austral.dissis.starships.utils

import java.io.*

class FileLoader {
    @Throws(IOException::class)
    fun loadFromResources(path: String): InputStream {
        val resource = FileLoader::class.java.classLoader.getResource(path) ?: throw FileNotFoundException(path)
        return resource.openStream()
    }

    @Throws(IOException::class)
    fun loadFromFileSystem(path: String): InputStream {
        val file = File(path)
        return FileInputStream(file)
    }
}