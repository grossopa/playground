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

import lombok.extern.slf4j.Slf4j;
import org.hamster.filetools.consts.FileActionResultType;
import org.springframework.stereotype.Service;

import java.io.File;

import static org.hamster.filetools.consts.FileActionResultType.*;

/**
 * @author Jack Yin
 * @since 1.0
 */
@Slf4j
@Service
public class MoveFileExecutor {
    public FileActionResultType moveFile(File fromFile, File toFile) {
        FileActionResultType resultType;
        if (toFile.exists()) {
            resultType = SKIPPED_EXISTS;
        } else {
            resultType = fromFile.renameTo(toFile) ? SUCCESS : ERROR;
        }
        return resultType;
    }
}
