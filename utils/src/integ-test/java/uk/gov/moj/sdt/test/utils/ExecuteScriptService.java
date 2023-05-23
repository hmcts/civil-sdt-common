package uk.gov.moj.sdt.test.utils;

import java.util.concurrent.Future;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

@Service("ExecuteScriptService")
public class ExecuteScriptService implements IExecuteScriptService {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecuteScriptService.class);

    @Autowired
    protected DataSource dataSource;

    @Override
    public boolean executeScript(Resource script) {
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(script);
        boolean success = true;
        try {
            databasePopulator.execute(dataSource);
        } catch (ScriptException e) {
            success = false;
            LOGGER.error("Error executing script , script: {}. Error: {}",
                    script.getFilename(), e.getMessage(), e);
        }
        return success;
    }

    @Async
    public Future<Boolean> runScript(Resource script) {

        try {
            LOGGER.info("Working with script: {}", script.getFilename());
            this.executeScript(script);

        } catch (ScriptException e) {
            return new AsyncResult<>(false);
        }
        LOGGER.info("Done with script {}...", script.getFilename());
        return new AsyncResult<>(true);
    }
}
