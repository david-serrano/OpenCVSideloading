import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.process.internal.ExecAction
import org.gradle.process.internal.ExecActionFactory

import java.util.zip.*
import javax.inject.Inject

class ExtractDexTask extends DefaultTask {

    String androidSDKLocation
    String buildToolsVersion
    String srcFolder = "build/outputs/aar/"
    String zipFolder = "build/outputs/aar/temp_zip"
    String jniFolder = "src/main/jniLibs"
    String srcName
    String srcVariant = "debug"
    String srcPkgType = "aar"
    String destinationFolder = "../app/src/main/"
    String dexName

    @TaskAction
    def extractDex() {
        unzipPkg()
        File f = new File(zipFolder)
        if(f.exists()) {
            f.deleteDir()
        }
        f.mkdir()

        dexClassFile()
        createZipsForChipsets()
    }

    def createZipsForChipsets() {
        String[] chipsets = ["arm64-v8a", "armeabi-v7a"]
        for(String name in chipsets) {
            getProject().copy {
                from "${jniFolder}/$name/libopencv_java3.so"
                into "${zipFolder}/${name}"
            }
            getProject().copy {
                from "${zipFolder}/${dexName}.dex"
                into "${zipFolder}/${name}"
            }
            createZip("${zipFolder}/${name}.zip", "${zipFolder}/${name}")
        }
    }

    def createZip(String filename, String inputDir) {
        ZipOutputStream zipFile = new ZipOutputStream(new FileOutputStream(filename))
        new File(inputDir).eachFile() { file ->
            //check if file
            if (file.isFile()){
                zipFile.putNextEntry(new ZipEntry(file.name))
                def buffer = new byte[file.size()]
                file.withInputStream {
                    zipFile.write(buffer, 0, it.read(buffer))
                }
                zipFile.closeEntry()
            }
        }
        zipFile.close()
    }

    def unzipPkg() {
        getProject().copy {
            from getProject().zipTree("${srcFolder}/${srcName}-${srcVariant}.${srcPkgType}")
            into "${srcFolder}/${srcName}"
        }
    }

    def dexClassFile() {
        ExecAction execAction = getExecActionFactory().newExecAction()
        execAction.setExecutable("${androidSDKLocation}/build-tools/${buildToolsVersion}/dx.bat")
        execAction.setArgs(["--dex", "--output",
                            "${zipFolder}/${dexName}.dex",
                            "${srcFolder}/${srcName}/classes.jar"])
        execAction.execute()
    }

    @Inject
    protected ExecActionFactory getExecActionFactory() {
        throw new UnsupportedOperationException()
    }
}