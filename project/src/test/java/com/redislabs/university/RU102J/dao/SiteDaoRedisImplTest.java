package com.redislabs.university.RU102J.dao;

import com.redislabs.university.RU102J.HostPort;
import com.redislabs.university.RU102J.TestKeyManager;
import com.redislabs.university.RU102J.api.MeterReading;
import com.redislabs.university.RU102J.api.Site;
import org.junit.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertEquals;

public class SiteDaoRedisImplTest {

    private static JedisPool jedisPool;
    private static Jedis jedis;
    private static TestKeyManager keyManager;
    private Set<Site> sites;

    @BeforeClass
    public static void setUp() throws Exception {
        jedisPool = new JedisPool(HostPort.getRedisHost(), HostPort.getRedisPort());
        jedis = new Jedis(HostPort.getRedisHost(), HostPort.getRedisPort());
        keyManager = new TestKeyManager("test");
    }

    @AfterClass
    public static void tearDown() {
        jedisPool.destroy();
        jedis.close();
    }

    @After
    public void flush() {
        keyManager.deleteKeys(jedis);
    }

    @Before
    public void generateData() {
        sites = new HashSet<>();
        sites.add(new Site(1, 4.5, 3, "123 Willow St.",
                "Oakland", "CA", "94577" ));
        sites.add(new Site(2, 3.0, 2, "456 Maple St.",
                 "Oakland", "CA", "94577" ));
        sites.add(new Site(3, 4.0, 3, "789 Oak St.",
                 "Oakland", "CA", "94577" ));
    }

    /**
     * Challenge #0 Part 1. This challenge is explained in
     * the video "How to Solve a Sample Challenge"
     */
    @Test
    public void findByIdWithExistingSite() {
        SiteDaoRedisImpl dao = new SiteDaoRedisImpl(jedisPool);
        Site site = new Site(4L, 5.5, 4, "910 Pine St.",
                "Oakland", "CA", "94577");
        dao.insert(site);
        Site storedSite = dao.findById(4L);
        assertThat(storedSite, is(site));
    }

    /**
     * Challenge #0 Part 2. This challenge is explained in
     * the video "How to Solve a Sample Challenge"
     */
    @Test
    public void findByIdWithMissingSite() {
        SiteDaoRedisImpl dao = new SiteDaoRedisImpl(jedisPool);
        assertThat(dao.findById(4L), is(nullValue()));
    }

    /**
     * Challenge #1 Part 1. Use this test case to
     * implement the challenge in Chapter 1.10.
     *
     * Be sure to uncomment the @Ignore line first.
     */
    @Test
    @Ignore
    public void findAllWithMultipleSites() {
        SiteDaoRedisImpl dao = new SiteDaoRedisImpl(jedisPool);
        // Insert all sites
        for (Site site : sites) {
            dao.insert(site);
        }

        assertThat(dao.findAll(), is(sites));
    }

    /**
     * Challenge #1 Part 2. Use this test case to
     * implement the challenge in Chapter 1.10.
     *
     * Be sure to uncomment the @Ignore line first.
     */
    @Test
    @Ignore
    public void findAllWithEmptySites() {
        SiteDaoRedisImpl dao = new SiteDaoRedisImpl(jedisPool);
        assertThat(dao.findAll(), is(empty()));
    }

    @Test
    public void insert() {
        SiteDaoRedisImpl dao = new SiteDaoRedisImpl(jedisPool);
        Site site = new Site(4, 5.5, 4, "910 Pine St.",
                "Oakland", "CA", "94577");
        dao.insert(site);

        Map<String, String> siteFields = jedis.hgetAll(RedisSchema.getSiteHashKey(4L));
        assertEquals(siteFields, site.toMap());

        assertThat(jedis.sismember(RedisSchema.getSiteIDsKey(), RedisSchema.getSiteHashKey(4L)),
                is(true));
    }

    /**
     * Challenge #2.
    @Test
    public void update() {
        SiteDaoRedisImpl dao = new SiteDaoRedisImpl(jedisPool);
        Site site = new Site(4L, 5.5, 4, "910 Pine St.",
                "Oakland", "CA", "94577");
        dao.insert(site);
        ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);
        MeterReading reading = new MeterReading(site.getId(), time, 1.0, 1.0, 22.0);
        dao.update(reading);
        Site modifiedSite = dao.findById(site.getId());
        assertThat(modifiedSite.getMeterReadingCount(), is(1L));
        assert(modifiedSite.getLastReportingTime().toInstant().compareTo(time.toInstant()) >= 0);
    }
    */
}