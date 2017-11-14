package com.github.hussainderry.task;

import com.github.hussainderry.crypto.FileDecryptorAES;
import com.github.hussainderry.crypto.FileEncryptorAES;
import com.github.hussainderry.model.Command;
import javafx.concurrent.Task;

import java.io.*;

/**
 * An asynchronous decryption task.
 * uses {@link FileDecryptorAES} to decrypt the given source file and write it to the target file
 * @author Hussain Al-Derry
 */
public class AsyncDecryptionTask extends Task<Void> {

    private final String mPassword;
    private final File mSource;
    private final File mTarget;

    /**
     * @param mPassword The password to use for key generation
     * @param mSource The file to decrypt
     * @param mTarget The file to save to
     */
    public AsyncDecryptionTask(String mPassword, File mSource, File mTarget) {
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
            mFileDecryptor.decrypt(
                    new BufferedInputStream(new FileInputStream(mSource)),
                    new BufferedOutputStream(new FileOutputStream(mTarget)));
        }catch(FileNotFoundException | IllegalStateException e){
            e.printStackTrace();
            if(e instanceof IllegalStateException){
                if(e.getMessage().contains("Invalid password")){
                    updateMessage(Command.WRONG_PASSWORD);
                }else{
                    updateMessage(Command.ERROR);
                }
            }else{
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
