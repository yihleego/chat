package io.leego.chat.client;

import io.leego.chat.manager.ChatManager;
import io.leego.chat.util.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Leego Yih
 */
public class RegistryClient implements Executor {
    private static final Logger logger = LoggerFactory.getLogger(RegistryClient.class);
    protected int status = NEW;
    protected final Duration pullPeriod;
    protected final Duration pushPeriod;
    protected final ChatManager chatManager;
    protected final ScheduledExecutorService executor;

    public RegistryClient(Duration pullPeriod, Duration pushPeriod, ChatManager chatManager) {
        this.pullPeriod = pullPeriod;
        this.pushPeriod = pushPeriod;
        this.chatManager = chatManager;
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void startup() {
        if (status != NEW) {
            logger.warn("Cannot restart");
            return;
        }
        logger.info("Starting RegistryClient");
        synchronized (this) {
            if (status != NEW) {
                return;
            }
            long begin = System.currentTimeMillis();
            run();
            long end = System.currentTimeMillis();
            logger.info("Started RegistryClient in {} ms", end - begin);
            status = RUNNING;
        }
    }

    @Override
    public void shutdown() {
        if (status != RUNNING) {
            return;
        }
        logger.info("Stopping RegistryClient");
        synchronized (this) {
            if (status != RUNNING) {
                return;
            }
            stop();
            status = TERMINATED;
        }
        logger.info("Stopped RegistryClient");
    }

    protected void run() {
        executor.scheduleWithFixedDelay(this::register, 0, pushPeriod.toMillis(), TimeUnit.MILLISECONDS);
    }

    protected void stop() {
        if (!executor.isShutdown()) {
            executor.shutdown();
            logger.debug("Stopped executor");
        }
        unregister();
    }

    protected final void register() {
        try {
            chatManager.setInstance();
        } catch (Throwable cause) {
            logger.error("An error occurred while registering", cause);
        }
    }

    protected final void unregister() {
        try {
            chatManager.removeInstance();
        } catch (Throwable cause) {
            logger.error("An error occurred while unregistering", cause);
        }
    }
}
