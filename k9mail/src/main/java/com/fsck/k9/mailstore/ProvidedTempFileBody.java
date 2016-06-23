package com.fsck.k9.mailstore;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.net.Uri;

import com.fsck.k9.mail.MessagingException;
import com.fsck.k9.mail.internet.SizeAware;
import com.fsck.k9.service.FileProviderInterface;


/** This is a body where the body can be accessed through a FileProvider.
 * @see FileProviderInterface
 */
public class ProvidedTempFileBody extends BinaryAttachmentBody implements SizeAware {
    private final FileProviderInterface fileProviderInterface;
    private File file;


    public ProvidedTempFileBody(FileProviderInterface fileProviderInterface, String transferEncoding) {
        this.fileProviderInterface = fileProviderInterface;
        try {
            setEncoding(transferEncoding);
        } catch (MessagingException e) {
            throw new AssertionError("setEncoding() must succeed");
        }
    }

    public OutputStream getOutputStream() throws IOException {
        file = fileProviderInterface.createProvidedFile();
        return new FileOutputStream(file);
    }

    @Override
    public InputStream getInputStream() throws MessagingException {
        try {
            return new FileInputStream(file);
        } catch (IOException ioe) {
            throw new MessagingException("Unable to open body", ioe);
        }
    }

    @Override
    public long getSize() {
        return file.length();
    }

    public Uri getProviderUri(String mimeType) throws IOException {
        return fileProviderInterface.getUriForProvidedFile(file, mimeType);
    }
}
