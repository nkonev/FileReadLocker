// http://examples.javacodegeeks.com/core-java/nio/filelock/create-shared-file-lock-on-file/

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

public class CreateSharedFileLockOnFile {

    public static void main(String[] args) {

        try {
            String filename = "fileToLock.dat";
            File file = new File(filename);
            file.createNewFile();

            // Creates a random access file stream to read from, and optionally
            // to write to
            FileChannel channel = new RandomAccessFile(file, "rw").getChannel();
            
            DataOutputStream out2 =
                    new DataOutputStream(
                    new BufferedOutputStream(
                    new FileOutputStream(filename)));
                    out2.writeDouble(3.14159);
                    out2.writeChars("That was pi\n");
                    out2.writeBytes("That was pi\n");
                    out2.close();

            // Acquire an exclusive lock on this channel's file ( block until
            // the region can be
            // locked, this channel is closed, or the invoking thread is
            // interrupted)
            FileLock lock = channel.lock(0, Long.MAX_VALUE, false);

            System.out.print("Press any key...");
            System.in.read();
            // Attempts to acquire an exclusive lock on this channel's file
            // (does not block, an
            // invocation always returns immediately, either having acquired a
            // lock on the requested
            // region or having failed to do so.
            try {

                lock = channel.tryLock(0, Long.MAX_VALUE, true);
            } catch (OverlappingFileLockException e) {
                // thrown when an attempt is made to acquire a lock on a a file
                // that overlaps
                // a region already locked by the same JVM or when another
                // thread is already
                // waiting to lock an overlapping region of the same file
                System.out.println("Overlapping File Lock Error: "
                        + e.getMessage());
            }

            // tells whether this lock is shared
            boolean isShared = lock.isShared();

            // release the lock
            lock.release();

            // close the channel
            channel.close();

        } catch (IOException e) {
            System.out.println("I/O Error: " + e.getMessage());
        }

    }

}
