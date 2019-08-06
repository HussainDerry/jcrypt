/*
 * Copyright 2017 Hussain Al-Derry
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.hussainderry.task;

import com.github.hussainderry.crypto.FileDecryptorAES;
import com.github.hussainderry.model.Command;
import javafx.concurrent.Task;

import java.io.*;
import java.util.logging.Logger;

/**
 * An asynchronous decryption task.
 * uses {@link FileDecryptorAES} to decrypt the given source file and write it to the target file
 * @author Hussain Al-Derry
 */
public class AsyncDecryptionTask extends Task<Void> {

    private final Logger mLogger;
    private final String mPassword;
    private final File mSource;
    private final File mTarget;

    /**
     * @param mPassword The password to use for key generation
     * @param mSource The file to decrypt
     * @param mTarget The file to save to
     */
    public AsyncDecryptionTask(String mPassword, File mSource, File mTarget) {
        this.mLogger = Logger.getLogger(AsyncDecryptionTask.class.getName());
        this.mSource = mSource;
        this.mTarget = mTarget;
        this.mPassword = mPassword;
    }

    @Override
    protected Void call() throws Exception {
        updateMessage(Command.STARTED);
        FileDecryptorAES mFileDecryptor = new FileDecryptorAES(mPassword);
        mFileDecryptor.setProgressMonitor(this::update);
        try{
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(mTarget));
            mFileDecryptor.decrypt(new BufferedInputStream(new FileInputStream(mSource)), outputStream);
            outputStream.close();
        }catch(FileNotFoundException e){
            mLogger.severe(e.getMessage());
            updateMessage(Command.ERROR);
            return null;
        }catch(IllegalStateException e){
            if(e.getMessage().contains("Invalid password")){
                updateMessage(Command.WRONG_PASSWORD);
            }else{
                mLogger.severe(e.getMessage());
                updateMessage(Command.ERROR);
            }
            return null;
        }
        updateMessage(Command.FINISHED);
        return null;
    }

    private void update(int progress){
        this.updateProgress(progress, 100);
    }
}
