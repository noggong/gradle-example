package com.example.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Set;

public abstract class JarCount extends DefaultTask {

    // ConfigurableFileCollection <- @InputFiles

    // RegularFileProperty <- @OutputFile

    // DirectoryProperty <- @InputDirectory

    // task 의 입력
    @InputFiles
    public abstract ConfigurableFileCollection getAllJars();

    // task 의 출력
    @OutputFile
    public abstract RegularFileProperty getCountFile();

    // task 작업 구현 함수 @taskAction
    @TaskAction
    public void doCount() throws IOException {
        Set<File> jarFiles = getAllJars().getFiles();
        int count = jarFiles.size();
        File out = getCountFile().get().getAsFile();
        Files.write(out.toPath(), Collections.singleton("" + count));
    }
}
