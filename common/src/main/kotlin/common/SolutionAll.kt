package common

import java.io.File

class SolutionAll(private val packageName: String) {

    private val classNameRegex = Regex("""SolutionDay(\d+)""")
    private val solutions = solutions()

    fun run() {
        solutions.forEach { solution ->
            val instance = solution.getDeclaredConstructor().newInstance()
            println(instance.result())
        }
    }

    private fun solutions(): List<Class<out BaseSolution>> {
        val packagePath = packageName.replace('.', '/')
        val classLoader = this::class.java.classLoader
        val classNames = mutableListOf<String>()
        val resources = classLoader.getResources(packagePath)

        while (resources.hasMoreElements()) {
            val url = resources.nextElement()

            if (url.protocol == "file") {
                val directory = File(url.toURI())

                directory.walkTopDown()
                    .filter { it.isFile && it.extension == "class" }
                    .forEach { file ->
                        val relativeName = file.relativeTo(directory)
                            .path
                            .removeSuffix(".class")
                            .replace(File.separatorChar, '.')

                        if (
                            !relativeName.endsWith("module-info") &&
                            !relativeName.endsWith("package-info")
                        ) {
                            classNames.add("$packageName.$relativeName")
                        }
                    }
            }
        }

        return classNames.distinct().mapNotNull { name ->
            val clazz = Class.forName(name, false, classLoader)
            if (BaseSolution::class.java.isAssignableFrom(clazz) && clazz != BaseSolution::class.java) {
                clazz.asSubclass(BaseSolution::class.java)
            } else {
                null
            }
        }.sortedBy { clazz ->
            classNameRegex.find(clazz.simpleName)!!.groupValues[1].toInt()
        }
    }
}