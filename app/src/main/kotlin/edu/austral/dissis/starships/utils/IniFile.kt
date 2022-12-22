package edu.austral.dissis.starships.utils

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

class IniFile(path: String) {
    private val _section = Pattern.compile("\\s*\\[([^]]*)\\]\\s*")
    private val _keyValue = Pattern.compile("\\s*([^=]*)=(.*)")
    private val _entries: MutableMap<String, MutableMap<String, String>> = HashMap()

    init {
        try {
            load(path)
        } catch (e: IOException) {
            println("No file founded.")
        }
    }

    @Throws(IOException::class)
    fun load(path: String) {
        BufferedReader(InputStreamReader(FileLoader().loadFromResources(path), StandardCharsets.UTF_8)).use { br ->
            var line: String?
            var section: String? = null
            while (br.readLine().also { line = it } != null) {
                var m = _section.matcher(line!!)
                if (m.matches()) {
                    section = m.group(1).trim { it <= ' ' }
                } else if (section != null) {
                    m = _keyValue.matcher(line!!)
                    if (m.matches()) {
                        val key = m.group(1).trim { it <= ' ' }
                        val value = m.group(2).trim { it <= ' ' }
                        var kv = _entries[section]
                        if (kv == null) {
                            _entries[section] = HashMap<String, String>().also { kv = it }
                        }
                        kv!![key] = value
                    }
                }
            }
        }
    }

    fun getString(section: String, key: String, defaultvalue: String): String {
        val kv = _entries[section] ?: return defaultvalue
        return if (kv[key] == null) {
            defaultvalue
        } else kv[key]!!
    }

    fun getInt(section: String, key: String, defaultvalue: Int): Int {
        val kv = _entries[section] ?: return defaultvalue
        return if (kv[key] == null) {
            defaultvalue
        } else kv[key]!!.toInt()
    }

    fun getFloat(section: String, key: String, defaultvalue: Float): Float {
        val kv = _entries[section] ?: return defaultvalue
        return kv[key]!!.toFloat()
    }

    fun getDouble(section: String, key: String, defaultvalue: Double): Double {
        val kv = _entries[section] ?: return defaultvalue
        return kv[key]!!.toDouble()
    }
}