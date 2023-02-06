package io.leego.chat.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.leego.chat.client.RegistryClient;
import io.leego.chat.cluster.ClusterDiscoveryServer;
import io.leego.chat.cluster.ClusterRegistryClient;
import io.leego.chat.cluster.context.ClusterContextManager;
import io.leego.chat.cluster.context.ClusterExclusiveContextManager;
import io.leego.chat.cluster.context.ClusterSharedContextManager;
import io.leego.chat.cluster.handler.ClusterDiscoveryServerChannelInitializer;
import io.leego.chat.cluster.handler.ClusterDiscoveryServerHandler;
import io.leego.chat.cluster.handler.ClusterRegistryClientChannelInitializer;
import io.leego.chat.cluster.handler.ClusterRegistryClientHandler;
import io.leego.chat.manager.ChatManager;
import io.leego.chat.manager.ContactManager;
import io.leego.chat.manager.GroupManager;
import io.leego.chat.server.ChatServer;
import io.leego.chat.server.context.ContextManager;
import io.leego.chat.server.context.StandaloneExclusiveContextManager;
import io.leego.chat.server.context.StandaloneSharedContextManager;
import io.leego.chat.server.handler.ChatServerChannelInitializer;
import io.leego.chat.server.handler.ChatServerHandler;
import io.leego.security.SecurityManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * @author Leego Yih
 */
@Configuration
@EnableConfigurationProperties(ChatServerProperties.class)
public class ChatServerConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .enable(MapperFeature.USE_GETTERS_AS_SETTERS)
                .enable(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS)
                .enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)
                .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
                .enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .addModules(new Jdk8Module())
                .build();
    }

    @Bean
    public ChatManager chatManager(RedisConnectionFactory connectionFactory, ChatServerProperties properties) {
        return new ChatManager(connectionFactory, properties);
    }

    @Bean
    public ContactManager contactManager(RedisConnectionFactory connectionFactory, ChatServerProperties properties) {
        return new ContactManager(connectionFactory, properties);
    }

    @Bean
    public GroupManager groupManager(RedisConnectionFactory connectionFactory, ChatServerProperties properties) {
        return new GroupManager(connectionFactory, properties);
    }

    @Configuration
    @ConditionalOnProperty(value = "chat.server.cluster.enabled", havingValue = "false", matchIfMissing = false)
    public static class StandaloneConfiguration {

        @Order(0)
        @Bean(initMethod = "startup", destroyMethod = "shutdown")
        public ChatServer chatServer(ChatServerChannelInitializer channelInitializer, ChatServerProperties properties) {
            return new ChatServer(
                    properties.getNettyThreadSize(),
                    properties.getRaw().isEnabled() ? properties.getRaw().getPort() : 0,
                    properties.getWs().isEnabled() ? properties.getWs().getPort() : 0,
                    properties.getWs().getPath(),
                    channelInitializer);
        }

        @Bean
        public ChatServerChannelInitializer chatServerChannelInitializer(ChatServerHandler chatServerHandler, SecurityManager securityManager, ChatServerProperties properties) {
            return new ChatServerChannelInitializer(
                    properties.getRaw().isEnabled() ? properties.getRaw().getPort() : 0,
                    properties.getWs().isEnabled() ? properties.getWs().getPort() : 0,
                    properties.getWs().getPath(),
                    properties.getAuthTimeout(),
                    properties.getIdleTimeout(),
                    chatServerHandler,
                    securityManager);
        }

        @Bean
        public ChatServerHandler chatServerHandler(ContextManager contextManager) {
            return new ChatServerHandler(contextManager);
        }

        @Order(1)
        @Bean(initMethod = "startup", destroyMethod = "shutdown")
        public RegistryClient registryClient(ChatManager chatManager, ChatServerProperties properties) {
            return new RegistryClient(
                    properties.getRegistry().getPullPeriod(),
                    properties.getRegistry().getPushPeriod(),
                    chatManager);
        }

        @Bean
        public ContextManager contextManager(GroupManager groupManager, ChatServerProperties properties) {
            return properties.getMode() == ChatServerProperties.Mode.SHARED
                    ? new StandaloneSharedContextManager(properties.getConnectionSize(), properties.getWorkThreadSize(), groupManager)
                    : new StandaloneExclusiveContextManager(properties.getConnectionSize(), properties.getWorkThreadSize(), groupManager);
        }
    }

    @Configuration
    @ConditionalOnProperty(value = "chat.server.cluster.enabled", havingValue = "true", matchIfMissing = true)
    public static class ClusterConfiguration {

        @Order(0)
        @Bean(initMethod = "startup", destroyMethod = "shutdown")
        public ClusterDiscoveryServer clusterDiscoveryServer(ClusterDiscoveryServerChannelInitializer channelInitializer, ChatServerProperties properties) {
            return new ClusterDiscoveryServer(
                    properties.getNettyThreadSize(),
                    properties.getDiscovery().getPort(),
                    properties.getRaw().isEnabled() ? properties.getRaw().getPort() : 0,
                    properties.getWs().isEnabled() ? properties.getWs().getPort() : 0,
                    properties.getWs().getPath(),
                    channelInitializer);
        }

        @Bean
        public ClusterDiscoveryServerChannelInitializer clusterDiscoveryServerChannelInitializer(ChatServerHandler chatServerHandler, ClusterDiscoveryServerHandler clusterDiscoveryServerHandler, SecurityManager securityManager, ChatServerProperties properties) {
            return new ClusterDiscoveryServerChannelInitializer(
                    properties.getDiscovery().getPort(),
                    properties.getRaw().isEnabled() ? properties.getRaw().getPort() : 0,
                    properties.getWs().isEnabled() ? properties.getWs().getPort() : 0,
                    properties.getWs().getPath(),
                    properties.getAuthTimeout(),
                    properties.getIdleTimeout(),
                    chatServerHandler,
                    clusterDiscoveryServerHandler,
                    securityManager);
        }

        @Bean
        public ChatServerHandler chatServerHandler(ClusterContextManager clusterContextManager) {
            return new ChatServerHandler(clusterContextManager);
        }

        @Bean
        public ClusterDiscoveryServerHandler clusterDiscoveryServerHandler(ClusterContextManager clusterContextManager) {
            return new ClusterDiscoveryServerHandler(clusterContextManager);
        }

        @Order(1)
        @Bean(initMethod = "startup", destroyMethod = "shutdown")
        public ClusterRegistryClient clusterRegistryClient(ClusterRegistryClientChannelInitializer channelInitializer, ChatManager chatManager, ChatServerProperties properties) {
            return new ClusterRegistryClient(
                    properties.getNettyThreadSize(),
                    properties.getRegistry().getPullPeriod(),
                    properties.getRegistry().getPushPeriod(),
                    channelInitializer,
                    chatManager);
        }

        @Bean
        public ClusterRegistryClientChannelInitializer clusterRegistryClientChannelInitializer(ClusterRegistryClientHandler clusterRegistryClientHandler) {
            return new ClusterRegistryClientChannelInitializer(clusterRegistryClientHandler);
        }

        @Bean
        public ClusterRegistryClientHandler clusterRegistryClientHandler(ChatManager chatManager, ClusterContextManager clusterContextManager) {
            return new ClusterRegistryClientHandler(chatManager, clusterContextManager);
        }

        @Bean
        public ClusterContextManager clusterContextManager(ChatManager chatManager, GroupManager groupManager, ChatServerProperties properties) {
            return properties.getMode() == ChatServerProperties.Mode.SHARED
                    ? new ClusterSharedContextManager(properties.getConnectionSize(), properties.getWorkThreadSize(), chatManager, groupManager)
                    : new ClusterExclusiveContextManager(properties.getConnectionSize(), properties.getWorkThreadSize(), chatManager, groupManager);
        }
    }
}
