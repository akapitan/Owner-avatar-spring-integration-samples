rootProject.name = "SpringIntegrationSamples"

//include("basic")
//include("basic:helloworld")

// A helper function to include all buildable subprojects in a folder
fun includeSubprojectsUnder(parent: String) {
    val parentDir = file(parent)
    if (parentDir.isDirectory) {
        parentDir.listFiles { file ->
            file.isDirectory && File(file, "build.gradle.kts").exists()
        }?.forEach { subDir ->
            val modulePath = "$parent:${subDir.name}"
            include(modulePath.replace("/", ":"))
            project(":$modulePath".replace("/", ":")).projectDir = subDir
        }
    }
}

// Scan these folders for submodules
includeSubprojectsUnder("basic")
includeSubprojectsUnder("advanced")