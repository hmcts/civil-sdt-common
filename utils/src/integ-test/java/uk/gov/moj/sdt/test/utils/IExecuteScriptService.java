package uk.gov.moj.sdt.test.utils;

import org.springframework.core.io.Resource;

import java.util.concurrent.Future;

public interface IExecuteScriptService {

    /**
     * Execution of given script.
     * @param script script resource
     * @return boolean success/failure
     */
    boolean executeScript(Resource script);

    /**
     * Asynchronous execution of given script.
     * @param script script resource
     * @return future boolean result of an asynchronous task
     */
    Future<Boolean> runScript(Resource script);

}
