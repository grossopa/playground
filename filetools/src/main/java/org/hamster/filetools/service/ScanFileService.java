/*
 * Copyright © 2024 the original author or authors.
 *
 * Licensed under the The MIT License (MIT) (the "License");
 *  You may obtain a copy of the License at
 *
 *         https://mit-license.org/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the “Software”), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.hamster.filetools.service;

import org.apache.commons.io.comparator.DirectoryFileComparator;
import org.hamster.filetools.model.FileActionResult;
import org.hamster.filetools.runner.arguments.RunnerArguments;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Jack Yin
 * @since 1.0
 */
@Service
public class ScanFileService {

    public List<FileActionResult> scanAndExecute(File folder, RunnerArguments arguments, Function<FileAction, FileActionResult> executor) {
        List<FileActionResult> results = new ArrayList<>();
        File[] files = folder.listFiles();
        if (files == null) {
            return results;
        }

        List<File> fileList = Arrays.stream(files).sorted(
                (f1, f2) -> new DirectoryFileComparator().compare(f2, f1)).collect(Collectors.toList());

        for (File file : fileList) {
            if (file.isFile() && arguments.isIncluded(file, arguments)) {
                FileAction fileAction = arguments.toFileAction(file);
                results.add(executor.apply(fileAction));
            } else if (file.isDirectory()) {
                results.addAll(scanAndExecute(file, arguments, executor));
            }
        }
        return results;
    }


}
