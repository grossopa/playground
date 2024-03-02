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
import org.hamster.filetools.service.FileAction;
import org.hamster.filetools.service.RenameFileAction;

import java.io.File;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.hamster.filetools.consts.FileActionType.RENAME_FILE;

/**
 * @author Jack Yin
 * @since 1.0
 */
@Data
public class RenameFileArguments extends RunnerArguments {

    private final String strToRemove;

    public RenameFileArguments(String rootFolder, String[] includes, String[] excludes, String strToRemove) {
        super(rootFolder, includes, excludes);
        this.strToRemove = strToRemove;
    }


    @Override
    public boolean isIncluded(File fromFile, RunnerArguments arguments) {
        boolean result = super.isIncluded(fromFile, arguments);
        if (!result) {
            return false;
        }
        return containsIgnoreCase(fromFile.getName(), strToRemove);
    }

    @Override
    public FileAction toFileAction(File fromFile) {
        String newFilename = fromFile.getName().replaceAll(strToRemove, "");
        return new RenameFileAction(fromFile, newFilename, RENAME_FILE);
    }
}
