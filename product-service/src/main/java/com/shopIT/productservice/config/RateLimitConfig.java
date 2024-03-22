package com.shopIT.productservice.config;

import com.shopIT.productservice.entity.TPSEntity;
import com.shopIT.productservice.repository.TPSRepository;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;

@Configuration
public class RateLimitConfig {
    @Autowired
    public ProxyManager buckets;
    @Autowired
    private TPSRepository tpsRepo;
    @Autowired
    private RedissonClient redissonClient;

    public Bucket resolveBucket(String key) {
        Supplier<BucketConfiguration> configurationSupplier = getConfigSupplier(key);
        return buckets.builder().build(key, configurationSupplier);
    }

    private Supplier<BucketConfiguration> getConfigSupplier(String key) {
        RMapCache<String, TPSEntity> cache = redissonClient.getMapCache("tpsProductCache");
        TPSEntity tpsEntity = cache.get(key);

        if (tpsEntity == null) {
            Optional<TPSEntity> tpsOpt = tpsRepo.findByUsername(key);
            // Default TPS value if not found in the database
            tpsEntity = tpsOpt.orElseGet(() -> TPSEntity.builder()
                    .username(key)
                    .tps(15)
                    .build());
            tpsRepo.save(tpsEntity);
            cache.put(key, tpsEntity);
        }

        int tps = tpsEntity.getTps();
        Refill refill = Refill.intervally(tps, Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(tps, refill);

        return () -> (BucketConfiguration.builder()
                .addLimit(limit)
                .build());
    }
}