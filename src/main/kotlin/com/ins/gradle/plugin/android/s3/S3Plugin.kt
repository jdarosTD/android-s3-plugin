package com.ins.gradle.plugin.android.s3

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.github.mgk.gradle.S3Extension
import com.github.mgk.gradle.S3Upload
import com.ins.gradle.plugin.android.s3.task.UploadVariant
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logging


/**
 * Created by Jonathan DA ROS on 23/01/2019.
 * Gradle plugin based on com.github.mgk.gradle plugins and offering uploadTasks of generated apk to S3
 */

class S3Plugin : Plugin<Project> {


    val _log = Logging.getLogger(S3Plugin::class.java.simpleName)
    override fun apply(project: Project) {


        project.plugins.withType(AppPlugin::class.java){
            applyInternal(project)
        }

    }

    private fun  applyInternal(project: Project) {


        val extension = project.extensions.create("uploadS3", S3PluginExtension::class.java)
        var s3Extension = project.extensions.findByName("s3") as S3Extension?

        if(s3Extension == null) s3Extension = project.extensions.create("s3", S3Extension::class.java)

        s3Extension?.bucket  =  extension.bucket
        s3Extension?.profile =  extension.profile
        s3Extension?.region  =  extension.region

        val android = project.extensions.findByName("android") as AppExtension


        val uploadAllVariant = project.tasks.create("uploadAllApks") // all apk upload task
        uploadAllVariant.doFirst {
            _log.quiet("Uploading all apk variants")
        }.doLast{
            _log.quiet("All apks variants uploaded")
        }

        // for each variant of the app creation of upload task for all its apks
        android.applicationVariants.whenObjectAdded { appVariant ->
            val variantName = appVariant.name.capitalize()
            _log.info("Variant added : $variantName")

            val uploadVariant = project.tasks.create("upload${variantName}Apks", UploadVariant::class.java)
            { it -> it.variant = appVariant }

            uploadAllVariant.dependsOn(uploadVariant)

            uploadVariant.inputApks.forEach { file ->

                _log.info("File :" + file.absolutePath)
                val variantOutPut = project.tasks.create(
                    "upload${file.nameWithoutExtension.replace("-", "")}Variant",
                    S3Upload::class.java
                ) {

                    _log.info("Variant bType ${ appVariant.buildType.name}")

                    var lKey  = "${extension.dest}/${appVariant.applicationId}_${ appVariant.versionName}"
                    var buildType = appVariant.buildType.name.toLowerCase()
                    if(buildType == "debug"){
                        lKey += "-$buildType"
                    }
                    lKey  += ".${file.extension}"
                    it.key = lKey
                    it.file = file.absolutePath
                    it.bucket = extension.bucket
                    it.overwrite = (buildType == "debug")
                }


                variantOutPut.doFirst {
                    _log.quiet("Uploading file ${(it as S3Upload).key}")
                }

                // upload task depends on assemble
                try {
                    appVariant.assembleProvider
                } catch (e: NoSuchMethodError) {
                    @Suppress("DEPRECATION")
                    appVariant.assemble
                }?.let { variantOutPut.dependsOn(it) }
                    ?: _log.warn("Assemble task not found. Publishing APKs may not work.")

                uploadVariant.dependsOn(variantOutPut)
            }


        }
    }

}


open class S3PluginExtension {
    var bucket : String = ""
    var profile: String = ""
    var region : String = ""

    var dest : String = ""


}