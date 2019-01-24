package com.ins.gradle.plugin.android.s3.task

import com.android.build.VariantOutput
import com.android.build.gradle.api.ApkVariantOutput
import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.file.CopySpec
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs
import org.gradle.internal.logging.progress.ProgressLogger
import org.gradle.internal.logging.progress.ProgressLoggerFactory
import org.slf4j.LoggerFactory
import java.io.File
import com.github.mgk.gradle.*
import groovy.lang.Closure
import org.gradle.api.Task
import proguard.util.PrintWriterUtil.fileName



/**
 * Created by Jonathan DA ROS on 23/01/2019.
 */

open class UploadVariant : DefaultTask() {

    @get:Internal internal lateinit var variant: ApplicationVariant


    @get:Internal
    protected val progressLogger: ProgressLogger = services[ProgressLoggerFactory::class.java]
        .newOperation(javaClass)

//    @get:OutputDirectory
//    protected val outputDir by lazy { File(project.buildDir, "/uploadapk/${variant.name}/apks") }

    @get:InputFiles
    public val inputApks by lazy {
        variant.outputs.filterIsInstance<ApkVariantOutput>().filter {
            VariantOutput.OutputType.valueOf(it.outputType) == VariantOutput.OutputType.MAIN || it.filters.isNotEmpty()
        }.map { it.outputFile }
    }


    @TaskAction
    fun uploadVariant() { }




}