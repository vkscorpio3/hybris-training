/**
 * 
 */
package org.razorfish.hazelcast.configuration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;


/**
 * @author biluo
 * 
 */
@Configuration
@EnableCaching
@Profile("hazelcast")
public class HazelcastConfiguration
{

	@Bean
	public Config config()
	{
		return new Config().addMapConfig(
				new MapConfig().setName("accepted-messages").setEvictionPolicy(EvictionPolicy.LRU).setTimeToLiveSeconds(2400))
				.setProperty("hazelcast.logging.type", "slf4j");
	}
}
