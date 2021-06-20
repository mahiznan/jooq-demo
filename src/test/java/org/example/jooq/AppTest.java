package org.example.jooq;

import org.example.jooq.config.DatasourceConfig;
import org.example.jooq.tables.*;
import org.example.jooq.tables.records.BookRecord;
import org.example.jooq.tables.Book;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jooq.impl.DSL.*;

import org.jooq.*;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Date;

public class AppTest {

    private static final Logger logger = LoggerFactory.getLogger("AppTest");
    DataSource dataSource = DatasourceConfig.createDataSource();

    public AppTest() throws IOException {
    }

    @Test
    public void createBook() {
        Author a = Author.AUTHOR;

        Result<Record1<Long>> maxAuthorId = DSL.using(dataSource, SQLDialect.MARIADB)
                .select(max(a.ID))
                .from(a).fetch();
        Assert.assertEquals(1, maxAuthorId.size());
        Long authorId = maxAuthorId.getValue(0, max(a.ID)) + 1;
        int result = DSL.using(dataSource, SQLDialect.MARIADB)
                .insertInto(Author.AUTHOR)
                .set(a.FIRST_NAME, "Rahuram")
                .set(a.LAST_NAME, "Rajan")
                .set(a.DATE_OF_BIRTH, new Date(1950, 1, 1))
                .set(a.YEAR_OF_BIRTH, 1950L)
                .set(a.ID, authorId)
                .execute();
        Assert.assertEquals(1, result);

        Language l = Language.LANGUAGE.as("l");
        Result<Record1<Long>> languageIdRes = using(dataSource, SQLDialect.MARIADB)
                .select(l.ID)
                .from(l)
                .where(l.CD.eq("en")).fetch();
        Assert.assertEquals(1, languageIdRes.size());
        Long languageId = languageIdRes.getValue(0, a.ID);

        Result<Record1<Long>> res = DSL.using(dataSource, SQLDialect.MARIADB)
                .select(max(Book.BOOK.ID))
                .from(Book.BOOK).fetch();
        Assert.assertEquals(1, res.size());

        logger.info("Max Value {}", res.getValue(0, max(Book.BOOK.ID)));

        DSL.using(dataSource, SQLDialect.MARIADB)
                .insertInto(Book.BOOK, Book.BOOK.LANGUAGE_ID, Book.BOOK.AUTHOR_ID, Book.BOOK.ID, Book.BOOK.TITLE, Book.BOOK.PUBLISHED_IN)
                .values(languageId, authorId, res.getValue(0, max(Book.BOOK.ID)) + 1, "The Third Pillar", Long.valueOf(2019))
                .execute();
    }


    @Test
    public void fetchAuthorWithBooksCount() {
        Book b = Book.BOOK.as("b");
        Author a = Author.AUTHOR.as("a");
        BookStore s = BookStore.BOOK_STORE.as("s");
        BookToBookStore t = BookToBookStore.BOOK_TO_BOOK_STORE.as("t");

        Result<Record3<String, String, Integer>> result = DSL.using(dataSource, SQLDialect.MARIADB)
                .select(a.FIRST_NAME, a.LAST_NAME, countDistinct(s.NAME))
                .from(a)
                .join(b).on(b.AUTHOR_ID.eq(a.ID))
                .join(t).on(t.BOOK_ID.eq(b.ID))
                .join(s).on(t.NAME.eq(s.NAME))
                .groupBy(a.FIRST_NAME, a.LAST_NAME)
                .orderBy(countDistinct(s.NAME).desc())
                .fetch();

        Assert.assertEquals(2, result.size());
        Assert.assertEquals("Paulo", result.getValue(0, Author.AUTHOR.FIRST_NAME));
        Assert.assertEquals("George", result.getValue(1, Author.AUTHOR.FIRST_NAME));
        Assert.assertEquals("Coelho", result.getValue(0, Author.AUTHOR.LAST_NAME));
        Assert.assertEquals("Orwell", result.getValue(1, Author.AUTHOR.LAST_NAME));
        Assert.assertEquals(Integer.valueOf(3), result.getValue(0, countDistinct(s.NAME)));
        Assert.assertEquals(Integer.valueOf(3), result.getValue(0, countDistinct(s.NAME)));
    }


    @Test
    public void fetchAllBooks() {
        Result<Record> result = DSL.using(dataSource, SQLDialect.MARIADB)
                .select().from(Author.AUTHOR).fetch();
        for (Record r : result) {
            Long id = r.getValue(Author.AUTHOR.ID);
            String firstName = r.getValue(Author.AUTHOR.FIRST_NAME);
            String lastName = r.getValue(Author.AUTHOR.LAST_NAME);
            logger.info(id + "-" + firstName + "-" + lastName);
        }
        Assert.assertTrue(result.size() > 0);
    }

    @Test
    public void fetchBookById() {
        BookRecord bookRecord = DSL.using(dataSource, SQLDialect.MARIADB)
                .selectFrom(Book.BOOK).where(Book.BOOK.ID.eq(1L)).fetchAny();
        logger.info(bookRecord.getTitle());
        Assert.assertEquals(bookRecord.getTitle(), "1984");
    }

    @Test
    public void updateBook() {
        Author a = Author.AUTHOR;

        Result<Record1<Long>> maxAuthorId = DSL.using(dataSource, SQLDialect.MARIADB)
                .select(max(a.ID))
                .from(a).fetch();
        Assert.assertEquals(1, maxAuthorId.size());
        Long authorId = maxAuthorId.getValue(0, max(a.ID)) + 1;

        int result = DSL.using(dataSource, SQLDialect.MARIADB)
                .insertInto(Author.AUTHOR)
                .set(a.FIRST_NAME, "Rajeshkumar")
                .set(a.LAST_NAME, "Muthaiah")
                .set(a.DATE_OF_BIRTH, new Date(1987, 5, 30))
                .set(a.YEAR_OF_BIRTH, 1987L)
                .set(a.ID, authorId)
                .execute();
        Assert.assertEquals(1, result);
        Result<Record1<Long>> authorResult = using(dataSource, SQLDialect.MARIADB)
                .select(a.ID)
                .from(a)
                .where(a.FIRST_NAME.eq("Rajeshkumar"))
                .fetch();
        Assert.assertEquals(1, authorResult.size());

        authorId = authorResult.getValue(0, a.ID);

        Language l = Language.LANGUAGE.as("l");
        Result<Record1<Long>> languageIdRes = using(dataSource, SQLDialect.MARIADB)
                .select(l.ID)
                .from(l)
                .where(l.CD.eq("en")).fetch();
        Assert.assertEquals(1, languageIdRes.size());
        Long languageId = languageIdRes.getValue(0, a.ID);

        Result<Record1<Long>> res = DSL.using(dataSource, SQLDialect.MARIADB)
                .select(max(Book.BOOK.ID))
                .from(Book.BOOK).fetch();
        Assert.assertEquals(1, res.size());

        logger.info("Max Value {}", res.getValue(0, max(Book.BOOK.ID)));

        DSL.using(dataSource, SQLDialect.MARIADB)
                .insertInto(Book.BOOK, Book.BOOK.LANGUAGE_ID, Book.BOOK.AUTHOR_ID, Book.BOOK.ID, Book.BOOK.TITLE, Book.BOOK.PUBLISHED_IN)
                .values(languageId, authorId, res.getValue(0, max(Book.BOOK.ID)) + 1, "The Third Pillar", Long.valueOf(2019))
                .execute();

        Book book = Book.BOOK;
        DSL.using(dataSource, SQLDialect.MARIADB)
                .update(book)
                .set(book.AUTHOR_ID, authorId)
                .where(book.TITLE.eq("The Third Pillar"))
                .execute();

        Result<Record1<Long>> bookAuthor =
                DSL
                        .using(dataSource, SQLDialect.MARIADB)
                        .select(book.AUTHOR_ID)
                        .from(book)
                        .where(book.TITLE.eq("The Third Pillar"))
                        .fetch();
        Assert.assertEquals(1, bookAuthor.size());

        Assert.assertEquals(authorId, bookAuthor.getValue(0, book.AUTHOR_ID));

    }


    @Test
    public void deleteBook() {
        Book book = Book.BOOK;
        int result = DSL.using(dataSource, SQLDialect.MARIADB)
                .delete(book)
                .where(book.TITLE.eq("The Third Pillar"))
                .execute();
        Assert.assertTrue(result > 0);

        Author author = Author.AUTHOR;
        result = DSL.using(dataSource, SQLDialect.MARIADB)
                .delete(author)
                .where(author.FIRST_NAME.eq("Rahuram"))
                .execute();
        Assert.assertTrue(result > 0);

        result = DSL.using(dataSource, SQLDialect.MARIADB)
                .delete(author)
                .where(author.FIRST_NAME.eq("Rajeshkumar"))
                .execute();
        Assert.assertTrue(result > 0);
    }


}
