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

import com.github.hussainderry.crypto.FileEncryptorAES;
import com.github.hussainderry.model.Command;
import javafx.concurrent.Task;

import javax.crypto.Cipher;
import java.io.*;
import java.util.logging.Logger;

/**
 * An asynchronous encryption task.
 * uses {@link FileEncryptorAES} to encrypt the given source file and write it to the target file
 * @author Hussain Al-Derry
 */
public class AsyncEncryptionTask extends Task<Void>{

    private final Logger mLogger;
    private final String mPassword;
    private final File mSource;
    private final File mTarget;

    /**
     * @param mPassword The password to use for key generation
     * @param mSource The file to encrypt
     * @param mTarget The file to save to
     */
    public AsyncEncryptionTask(String mPassword, File mSource, File mTarget) {
        this.mSource = mSource;
        this.mTarget = mTarget;
        this.mPassword = mPassword;
        this.mLogger = Logger.getLogger(AsyncEncryptionTask.class.getName());
    }

    @Override
    protected Void call() throws Exception {
        updateMessage(Command.STARTED);
        FileEncryptorAES mEncryptorAES;

        if(checkJCEPolicy()){
            mEncryptorAES = FileEncryptorAES.createEncryptorWithHighSecurityParams(mPassword);
        }else{
            mEncryptorAES = FileEncryptorAES.createEncryptorWithMinimumSecurityParams(mPassword);
        }

        mEncryptorAES.setProgressMonitor(this::update);
        try {
            mEncryptorAES.encrypt(
                    new BufferedInputStream(new FileInputStream(mSource)),
                    new BufferedOutputStream(new FileOutputStream(mTarget)));
        } catch (FileNotFoundException e) {
            mLogger.severe(e.getMessage());
            updateMessage(Command.ERROR);
            return null;
        }
        updateMessage(Command.FINISHED);
        return null;
    }

    private void update(int progress){
        this.updateProgress(progress, 100);
    }

    private boolean checkJCEPolicy(){
        try {
            int length = Cipher.getMaxAllowedKeyLength("AES");
            return length == 256 || length == Integer.MAX_VALUE;
        } catch (Exception e){
            mLogger.warning(e.getMessage());
            return false;
        }
    }
}
