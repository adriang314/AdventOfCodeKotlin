package year2022

import common.BaseSolution

fun main() = println(SolutionDay07().result())

class SolutionDay07 : BaseSolution() {
    override val day = 7
    
    override fun task1(): String {
        val dirsSize = allDirs.values.filter { it.size() < 100000 }.sumOf { it.size() }
        return dirsSize.toString()
    }

    override fun task2(): String {
        val totalDiskSpace = 70000000L
        val requiredDiskSpace = 30000000L
        val usedDiskSpace = root.size()
        val freeDiskSpace = totalDiskSpace - usedDiskSpace
        val neededDiskSpace = requiredDiskSpace - freeDiskSpace
        val dirSize = allDirs.values.filter { it.size() > neededDiskSpace }.minByOrNull { it.size() }!!.size()
        return dirSize.toString()
    }

    private var root: Directory
    private var allDirs: MutableMap<Pair<String, Directory?>, Directory>

    init {
        val lines = input().split("\r\n").map { Line(it) }
        root = Directory("/")
        allDirs = mutableMapOf(Pair(root.name, root.parentDirectory) to root)
        var currDir: Directory = root
        lines.forEach {
            if (it.changeDir != null) {
                currDir = when (it.changeDir.dir) {
                    ".." -> currDir.parentDirectory!!
                    "/" -> root
                    else -> currDir.getDir(it.changeDir.dir)!!
                }
            } else if (it.file != null) {
                currDir.addFile(it.file)
            } else if (it.dirName != null) {
                val newDir = allDirs.getOrPut(Pair(it.dirName, currDir)) { Directory(it.dirName, currDir) }
                currDir.addDir(newDir)
            }
        }
    }

    data class Line(val text: String) {
        private val isChangeDirOperation = text.startsWith("$ cd ")
        private val isListDirOperation = text.startsWith("$ ls")
        private val isDir = text.startsWith("dir")
        private val isFile = !isChangeDirOperation && !isListDirOperation && !isDir
        val dirName = if (isDir) text.split(" ").last() else null
        val file = if (isFile) {
            val split = text.split(" ")
            File(split.last(), split.first().toLong())
        } else null
        val changeDir = if (isChangeDirOperation) ChangeDir(text.substring(5)) else null
    }

    data class ChangeDir(val dir: String)

    data class Directory(val name: String, val parentDirectory: Directory? = null) {
        private val files: MutableMap<String, File> = mutableMapOf()
        private val dirs: MutableMap<String, Directory> = mutableMapOf()
        private var size = 0L

        fun size() = size

        fun getDir(name: String) = dirs[name]

        fun addDir(d: Directory) = dirs.putIfAbsent(d.name, d)

        fun addFile(f: File) {
            files[f.name] = f
            var currDir: Directory? = this
            while (currDir != null) {
                currDir.size += f.size
                currDir = currDir.parentDirectory
            }
        }
    }

    data class File(val name: String, val size: Long)
}