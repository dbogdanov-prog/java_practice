package coursehub;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CourseImporterTest {

    /** Reader, у которого close() может падать. */
    static class BrokenReader extends Reader {
        private final StringReader inner;
        private final boolean failOnClose;
        boolean closed;

        BrokenReader(String text, boolean failOnClose) {
            this.inner = new StringReader(text);
            this.failOnClose = failOnClose;
        }

        @Override
        public int read(char[] buf, int off, int len) throws IOException {
            return inner.read(buf, off, len);
        }

        @Override
        public void close() throws IOException {
            closed = true;
            inner.close();
            if (failOnClose) {
                throw new IOException("close failed");
            }
        }
    }

    @Test
    void malformedRecord_andCloseFailure_keepMainAndSuppressed() {
        CourseImporter importer = new CourseImporter();
        List<Course> target = new ArrayList<>();
        BrokenReader reader = new BrokenReader("Java;40\nthis is bad\nSQL;20", true);

        CourseImportException ex = assertThrows(CourseImportException.class,
                () -> importer.importInto(reader, target));

        assertEquals(2, ex.getRecordNumber());                         // основное: запись №2
        assertEquals(1, ex.getSuppressed().length);                    // и ошибка close
        assertEquals("close failed", ex.getSuppressed()[0].getMessage());
        assertEquals(1, importer.getProcessed());                      // обработана одна запись
        assertTrue(target.isEmpty());                                  // ничего не опубликовано
        assertTrue(reader.closed);
    }

    @Test
    void closeFailure_afterSuccessfulRead_publishesNothing() {
        CourseImporter importer = new CourseImporter();
        List<Course> target = new ArrayList<>();
        BrokenReader reader = new BrokenReader("Java;40\nSQL;20", true);

        UncheckedIOException ex = assertThrows(UncheckedIOException.class,
                () -> importer.importInto(reader, target));

        assertEquals("close failed", ex.getCause().getMessage());
        assertEquals(2, importer.getProcessed());
        assertTrue(target.isEmpty());
    }

    @Test
    void validInput_isPublishedAndReaderClosed() {
        CourseImporter importer = new CourseImporter();
        List<Course> target = new ArrayList<>();
        BrokenReader reader = new BrokenReader("Java;40\nSQL;20", false);

        importer.importInto(reader, target);

        assertEquals(2, target.size());
        assertEquals(2, importer.getProcessed());
        assertTrue(reader.closed);
    }

    @Test
    void invalidHours_keepsCauseAndRecordNumber() {
        CourseImporter importer = new CourseImporter();
        List<Course> target = new ArrayList<>();
        Reader reader = new StringReader("Java;abc");

        CourseImportException ex = assertThrows(CourseImportException.class,
                () -> importer.importInto(reader, target));

        assertEquals(1, ex.getRecordNumber());
        assertInstanceOf(NumberFormatException.class, ex.getCause());
    }
}