package com.learncamel.routes;

import com.learncamel.CamelApplication;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.io.File;
import java.util.concurrent.Callable;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.camel.test.junit4.TestSupport.deleteDirectory;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@DirtiesContext
@RunWith(CamelSpringBootRunner.class)
@ActiveProfiles("dev")
@SpringBootTest(classes = {CamelApplication.class},
        properties = {"camel.springboot.java-routes-include-pattern=**/OrderFile*"})
public class EndToEndIntegrationTest {

    @Autowired
    private CamelContext context;

    @Autowired
    private Environment env;

    @Autowired
    private ProducerTemplate template;

    @Before
    public void cleanDir() {
        deleteDirectory("inbox");
    }

    private String body = "{\"transactionType\":\"ADD\", " +
            "\"sku\":\"%s\", \"itemDescription\":\"SamsungTV\", " +
            "\"price\":\"500\"}";

    private static int getRandomNumberInRange(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    @Test
    public void testAddOrder() {
        NotifyBuilder notify = new NotifyBuilder(context)
                .whenExactlyCompleted(1)
                .create();

        int sku = getRandomNumberInRange(0, Integer.MAX_VALUE);
        body = String.format(body, sku);
        template.sendBodyAndHeader("file:inbox", body, Exchange.FILE_NAME, "order.json");

        assertTrue(notify.matches(5, SECONDS));

        File target = new File("inbox/processed/order.json");
        assertTrue("File should be in processed directory", target.exists());
        String content = context.getTypeConverter().convertTo(String.class, target);
        assertEquals(body, content);

        await().atMost(5, SECONDS).until(latestSkuInItemRepository(), equalTo(sku));
    }

    @Test
    public void testAddInvalidOrder() {
        NotifyBuilder notify = new NotifyBuilder(context)
                .whenDone(1)
                .create();
        body = String.format(body, "");
        template.sendBodyAndHeader("file:inbox", body, Exchange.FILE_NAME, "order.json");

        assertTrue(notify.matches(5, SECONDS));

        File target = new File("inbox/error/order.json");
        assertTrue("File should be in error directory", target.exists());
        String content = context.getTypeConverter().convertTo(String.class, target);
        assertEquals(body, content);
    }

    @Test
    public void testUpdateOrder() {
        // assemble
        int sku = getRandomNumberInRange(0, Integer.MAX_VALUE);
        body = String.format(body, sku);
        template.sendBodyAndHeader("file:inbox", body, Exchange.FILE_NAME, "order.json");
        await().atMost(5, SECONDS).until(latestSkuInItemRepository(), equalTo(sku));

        //act
        String updatedBody  = "{\"transactionType\":\"UPDATE\", " +
                "\"sku\":\"%s\", \"itemDescription\":\"SamsungTV\", " +
                "\"price\":\"%s\"}";
        int updatedPrice = 750;
        updatedBody = String.format(updatedBody, sku, updatedPrice);
        template.sendBodyAndHeader("file:inbox", updatedBody, Exchange.FILE_NAME, "order.json");

        //assert
        await().atMost(5, SECONDS).until(priceOfItemWithSku(sku), equalTo(updatedPrice));
    }

    private Callable<Integer> latestSkuInItemRepository() {
        return () -> new ItemRepository().latestSku();
    }

    private Callable<Integer> priceOfItemWithSku(int sku) {
        return () -> new ItemRepository().priceOfItemWithSku(sku);
    }

    class ItemRepository {
        private JdbcTemplate jdbc;
        private DataSource ds;

        Integer latestSku() {
            ds = context.getRegistry().findByType(DataSource.class).iterator().next();
            jdbc = new JdbcTemplate(ds);
            return jdbc.queryForObject("select sku from Items order by crte_ts desc limit 1", Integer.class);
        }

        Integer priceOfItemWithSku(int sku) {
            ds = context.getRegistry().findByType(DataSource.class).iterator().next();
            jdbc = new JdbcTemplate(ds);
            return jdbc.queryForObject(String.format("select price from Items where sku ='%s'", sku), Integer.class);
        }
    }
}