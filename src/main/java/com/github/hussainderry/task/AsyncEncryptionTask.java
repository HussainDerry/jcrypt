package com.github.hussainderry.task;

import com.github.hussainderry.crypto.FileEncryptorAES;
import com.github.hussainderry.model.Command;
import javafx.concurrent.Task;

import javax.crypto.Cipher;
import java.io.*;

/**
 * An asynchronous encryption task.
 * uses {@link FileEncryptorAES} to encrypt the given source file and write it to the target file
 * @author Hussain Al-Derry
 */
public class AsyncEncryptionTask extends Task<Void>{

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
            e.printStackTrace();
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
            e.printStackTrace();
            return false;
        }
    }
}
