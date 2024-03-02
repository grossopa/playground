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

package org.hamster.filetools.runner.arguments;

import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.hamster.filetools.service.FileAction;

import java.io.File;

/**
 * @author Jack Yin
 * @since 1.0
 */
@Data
public abstract class RunnerArguments {

    private final String rootFolder;
    private final String[] includes;
    private final String[] excludes;

    protected RunnerArguments(String rootFolder, String[] includes, String[] excludes) {
        this.rootFolder = rootFolder;
        this.includes = includes;
        this.excludes = excludes;
    }

    public boolean isIncluded(File fromFile, RunnerArguments arguments) {
        if (ArrayUtils.isNotEmpty(arguments.getIncludes())) {
            for (String include : arguments.getIncludes()) {
                if (fromFile.getName().contains(include)) {
                    return true;
                }
            }
            return false;
        } else if (ArrayUtils.isNotEmpty(arguments.getExcludes())) {
            for (String include : arguments.getIncludes()) {
                if (fromFile.getName().contains(include)) {
                    return false;
                }
            }
        }
        return true;
    }

    public abstract FileAction toFileAction(File fromFile);

}
